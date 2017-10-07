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
         * The reading for azimuth "wraps around" drastically past 360 degrees
         */
        if (Math.abs(input - lastValue) > 358) {    // wraparound, don't smooth!
            setLastValue(input);
            movingAverage.setAllToValue(input);
            return input;
        } else {
            return super.updateAndGetSmoothed(input);
        }
    }

}
