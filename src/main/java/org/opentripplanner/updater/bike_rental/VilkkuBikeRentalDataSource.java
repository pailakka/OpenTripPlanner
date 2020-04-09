package org.opentripplanner.updater.bike_rental;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Strings;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;

/**
 * Vilkku (Kuopio, Finland) bike rental data source.
 * url: https://kaupunkipyorat.kuopio.fi/tkhs-export-map.html?format=xml
 */
public class VilkkuBikeRentalDataSource extends GenericXmlBikeRentalDataSource {
    
    private String networkName;
    
    public VilkkuBikeRentalDataSource(String networkName) {
        super("//station");
        this.networkName = Strings.isNullOrEmpty(networkName) ? "vilkku" : networkName;
    }
    
    public BikeRentalStation makeStation(Map<String, String> attributes) {
        
        // some place entries appear to actually be checked-out bikes, not stations
        if (attributes.get("bike") != null) {
            return null;
        }
        
        BikeRentalStation station = new BikeRentalStation();
        station.networks = new HashSet<>(Collections.singleton(this.networkName));
        station.id = attributes.get("name");
        station.x = getCoordinate(attributes.get("longitude"));
        station.y = getCoordinate(attributes.get("latitude"));
        station.name = new NonLocalizedString(attributes.get("name"));
        station.bikesAvailable  = getAvailableBikes(attributes);
        station.spacesAvailable = getAvailableSpaces(attributes);
        station.state = "Station on";
        return station;
    }

    private double getCoordinate(String coordinate) {
        // for some reason the API returns coordinates with ',' as decimal separator
        if (coordinate.contains(",")) {
            return Double.parseDouble(coordinate.replace(",", "."));
        }
        return Double.parseDouble(coordinate);
    }

    private int getAvailableBikes(Map<String, String> attributes) {
        int bikes = Integer.parseInt(attributes.get("freeBikes"));
        int eBikes = Integer.parseInt(attributes.get("freeEBikes"));

        return bikes + eBikes;
    }

    private int getAvailableSpaces(Map<String, String> attributes) {
        int locks = Integer.parseInt(attributes.get("freeLocks"));
        int eLocks = Integer.parseInt(attributes.get("freeELocks"));

        return locks + eLocks;
    }  
}
