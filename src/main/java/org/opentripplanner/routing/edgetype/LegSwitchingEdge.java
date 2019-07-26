package org.opentripplanner.routing.edgetype;

import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Vertex;

import org.locationtech.jts.geom.LineString;
import java.util.Locale;

/**
 * This edge type has no mode and initiates another leg.
 * It is only used when splicing together paths on a search with intermediate places.
 */
public class LegSwitchingEdge extends Edge {
	private static final long serialVersionUID = 1L;
	protected Integer locationSlack;

	public LegSwitchingEdge(Vertex v1, Vertex v2, Integer locationSlack) {
        super(new Vertex(null, null, 0.0, 0.0) {}, new Vertex(null, null, 0.0, 0.0) {});
        fromv = v1;
        tov = v2;
        this.locationSlack = locationSlack;
        // Why is this code so dirty? Because we don't want this edge to be added to the edge lists.
	}

	@Override
	public State traverse(State s0) {
        RoutingRequest options = s0.getOptions();
        long t0 = s0.getTimeSeconds();

        long timeAfterSlack = (options.arriveBy
            ? t0 - this.locationSlack
            : t0 + this.locationSlack);

        StateEditor editor = s0.edit(this);
        editor.setBackMode(TraverseMode.LEG_SWITCH);
        //Forget the last pattern to allow taking the same route from an intermediate place
        editor.setLastPattern(null);
        editor.setTimeSeconds(timeAfterSlack);
        return editor.makeState();
	}

	@Override
	public double getDistance() {
		return 0;
	}

	@Override
	public LineString getGeometry() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

        @Override
        public String getName(Locale locale) {
            return this.getName();
        }

}
