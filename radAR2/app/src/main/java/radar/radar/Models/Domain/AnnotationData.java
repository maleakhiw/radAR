package radar.radar.Models.Domain;

public class AnnotationData {
    // unlike ARAnnotation, used for bookkeeping to handle overlapping annotations.

    public int userID;
    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    public int stackingLevel;

    public AnnotationData(int userID, int offsetX, int offsetY, int width, int height, int stackingLevel) {
        this.userID = userID;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.stackingLevel = stackingLevel;
    }
}
