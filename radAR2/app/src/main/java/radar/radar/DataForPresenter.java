package radar.radar;

/**
 * Created by kenneth on 30/9/17.
 */

public class DataForPresenter {
    public double hPixelsPerDegree;
    public double vPixelsPerDegree;

    public DataForPresenter(double hPixelsPerDegree, double vPixelsPerDegree) {
        this.hPixelsPerDegree = hPixelsPerDegree;
        this.vPixelsPerDegree = vPixelsPerDegree;
    }

    @Override
    public String toString() {
        return ((Double) hPixelsPerDegree).toString() + ", " + ((Double) vPixelsPerDegree).toString();
    }
}
