package org.opentripplanner.routing.algorithm.raptor.transit.request;

import com.conveyal.r5.otp2.api.transit.TripPatternInfo;
import org.opentripplanner.routing.algorithm.raptor.transit.TripPattern;
import org.opentripplanner.routing.algorithm.raptor.transit.TripPatternForDate;
import org.opentripplanner.routing.algorithm.raptor.transit.TripSchedule;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of all the TripSchedules active on a range of consecutive days. The outer list of tripSchedulesByDay
 * refers to days in order.
 */
public class TripPatternForDates implements TripPatternInfo<TripSchedule> {

    private final TripPattern tripPattern;

    private final TripPatternForDate[] tripPatternForDates;

    private final int[] offsets;

    private final int numberOfTripSchedules;

    TripPatternForDates(TripPattern tripPattern, List<TripPatternForDate> tripPatternForDates, List<Integer> offsets) {
        this.tripPattern = tripPattern;
        this.tripPatternForDates = tripPatternForDates.toArray(new TripPatternForDate[]{});
        this.offsets = offsets.stream().mapToInt(i -> i).toArray();
        this.numberOfTripSchedules = Arrays.stream(this.tripPatternForDates).mapToInt(TripPatternForDate::numberOfTripSchedules).sum();
    }

    public TripPattern getTripPattern() {
        return tripPattern;
    }

    @Override public int stopIndex(int stopPositionInPattern) {
        return this.tripPattern.stopIndex(stopPositionInPattern);
    }

    @Override public int numberOfStopsInPattern() {
        return tripPattern.getStopIndexes().length;
    }

    @Override public TripSchedule getTripSchedule(int index) {
        for (int i = 0; i < tripPatternForDates.length; i++) {
            TripPatternForDate tripPatternForDate = tripPatternForDates[i];
            if (index < tripPatternForDate.numberOfTripSchedules()) {
                return new TripScheduleWithOffset(tripPatternForDate.getTripSchedule(index), offsets[i]);
            }
            index -= tripPatternForDate.numberOfTripSchedules();
        }
        throw new IndexOutOfBoundsException("Index out of bound: " + index);
    }

    @Override public int numberOfTripSchedules() {
        return numberOfTripSchedules;
    }
}
