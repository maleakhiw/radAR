package radar.radar.Models.Domain;

/**
 * Created by kenneth on 12/10/17.
 */

public class AnnotationData {
    // unlike ARAnnotation, used for bookkeeping to handle overlapping annotations.

    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    public int stackingLevel;

    public AnnotationData(int offsetX, int offsetY, int width, int height, int stackingLevel) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.stackingLevel = stackingLevel;
    }
}
