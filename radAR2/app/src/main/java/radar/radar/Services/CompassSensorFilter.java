package radar.radar.Services;

/**
 * Created by kenneth on 2/10/17.
 */

public class CompassSensorFilter extends SensorFilter {
    public CompassSensorFilter() {
        super();
    }

    @Override
    public double updateAndGetSmoothed(double input) {
        /*
         * The reading for azimuth "wraps around" when you reach 180 degrees:
         * after 179 degrees, it wraps around to -180 degrees
         * Smoothing makes the movement slower and obvious to the user! To make it seamless
         * we need to let the value update instantaneously and drop all previous "samples"
         * for the filters.
         *
         * 170 degrees is a good threshold for how big the wraparound jumps are.
         * It's very unlikely for a user to be able to turn 170 degrees in the amount of time
         * it takes for the sensor to take a sample.
         */
        if (Math.abs(input - lastValue) > 170) {    // wraparound, don't smooth!

            setLastValue(input);
            movingAverage.setAllToValue(input);
            return input;
        } else {
            return super.updateAndGetSmoothed(input);
        }
    }

}
