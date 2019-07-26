package org.opentripplanner.routing.edgetype;

import org.opentripplanner.graph_builder.module.NearbyStopFinder;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.vertextype.TransitStop;

import org.locationtech.jts.geom.LineString;

import java.util.List;
import java.util.Locale;

/**
 * Represents a transfer between stops that does not take the street network into account.
 *
 * TODO these should really have a set of valid modes in case bike vs. walk transfers are different
 */
public class SimpleTransfer extends Edge {
    private static final long serialVersionUID = 20140408L;
    private int penaltySeconds;

    private double distance;

    private boolean isWheelchairAccessible;

    private LineString geometry;
    private List<Edge> edges;

    public SimpleTransfer(TransitStop from, TransitStop to, double distance, LineString geometry, List<Edge> edges) {
        super(from, to);
        this.distance = distance;
        this.geometry = geometry;
        this.edges = edges;
    }

    public SimpleTransfer(TransitStop from, TransitStop to, double distance, LineString geometry) {
        this(from, to, distance, geometry, null);
    }

    public SimpleTransfer(TransitStop from, NearbyStopFinder.StopAtDistance sd) {
        this(from, sd.tstop, sd.dist, sd.geom, sd.edges);
        this.penaltySeconds = sd.penaltySeconds;
        this.isWheelchairAccessible = sd.isWheelchairAccessible;
    }

    @Override
    public State traverse(State s0) {
        if (!s0.getOptions().modes.contains(TraverseMode.WALK)) {
            return null;
        }
        // Forbid taking shortcuts composed of two transfers in a row
        if (s0.backEdge instanceof SimpleTransfer) {
            return null;
        }
        if (s0.backEdge instanceof StreetTransitLink) {
            return null;
        }
        if(distance > s0.getOptions().maxTransferWalkDistance) {
            return null;
        }

        if (s0.getOptions().wheelchairAccessible && !this.isWheelchairAccessible) {
            return null;
        }
        // Only transfer right after riding a vehicle.
        RoutingRequest rr = s0.getOptions();
        double walkspeed = rr.walkSpeed;
        StateEditor se = s0.edit(this);
        se.setBackMode(TraverseMode.WALK);

        int time = (int) Math.ceil(distance / walkspeed) + 2 * StreetTransitLink.STL_TRAVERSE_COST + this.penaltySeconds;

        se.incrementTimeInSeconds(time);

        se.incrementWeight(time * rr.walkReluctance);
        se.incrementWalkDistance(distance);
        return se.makeState();
    }

    @Override
    public String getName() {
        return fromv.getName() + " => " + tov.getName();
    }

    @Override
    public String getName(Locale locale) {
        //TODO: localize
        return this.getName();
    }

    @Override
    public double weightLowerBound(RoutingRequest rr) {
        int time = (int) (distance / rr.walkSpeed);
        return (time * rr.walkReluctance);
    }

    @Override
    public double getDistance(){
    	return this.distance;
    }


    @Override
    public LineString getGeometry(){
	   return this.geometry;
   }

    public List<Edge> getEdges() { return this.edges; }

    @Override
    public String toString() {
        return "SimpleTransfer " + getName() + " (" + getDistance() + "m / wc: " + Boolean.toString(isWheelchairAccessible()) + ", p: " + penaltySeconds + ")";
    }

    public boolean isWheelchairAccessible() {
        return isWheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        isWheelchairAccessible = wheelchairAccessible;
    }
}
