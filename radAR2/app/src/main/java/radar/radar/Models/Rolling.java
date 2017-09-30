package radar.radar.Models;

/**
 * Created by kenneth on 1/10/17.
 */

// https://stackoverflow.com/questions/3793400/is-there-a-function-in-java-to-get-moving-average

public class Rolling {

    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];

    public Rolling(int size) {
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