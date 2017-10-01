package radar.radar.Services;


/**
 * Created by kenneth on 1/10/17.
 */

class MovingAverage {

    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];

    public MovingAverage(int size) {
        this.size = size;
        samples = new double[size];
        for (int i = 0; i < size; i++) samples[i] = 0d;
    }

    public void add(double x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) index = 0; // cheaper than modulus
    }

    public double getAverage() {
        return total / size;
    }
}

public class SensorFilter {
    // values
    private float[] mOrientationAngles = new float[3];  // for compass
    private float[] mRotationMatrix = new float[9];     // for compass

    // moving average
    MovingAverage movingAverage = new MovingAverage(10);

    float ALPHA = 0.1f;
    protected float[] lowPass( float[] input, float[] output ) {
        if (output == null) return input;

        for (int i=0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    double lastValue = 0;
    double lowPassOne(double input, double output) {
        if (output == 0) return input;
        return output + ALPHA * (input - output);
    }

    public double updateAndGetSmoothed(double input) {
        lastValue = lowPassOne(input, lastValue);
        movingAverage.add(lastValue);
        return movingAverage.getAverage();
    }

}
