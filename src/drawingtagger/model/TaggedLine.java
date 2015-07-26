package drawingtagger.model;

import javafx.scene.shape.Line;

/**
 *
 * @author Burhanuddin
 */
public class TaggedLine {
    public int id;
    public long timeStart, timeEnd;
    public String tag;
    private final Line line;
    
    /**
     * Constructs a tagged line based on specified values.
     * @param id
     * @param line
     * @param timeStart
     * @param timeEnd
     * @param tag
     */
    public TaggedLine(int id, Line line, long timeStart, long timeEnd, String tag) {
        this.id = id;
        this.line = line;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.tag = tag;
    }
    
    /**
     * Return x-coordinate of starting point.
     * @return 
     */
    public double getStartX() {
        return line.getStartX();
    }
    
    /**
     * Return y-coordinate of starting point.
     * @return 
     */
    public double getStartY() {
        return line.getStartY();
    }
    
    /**
     * Return x-coordinate of ending point.
     * @return 
     */
    public double getEndX() {
        return line.getEndX();
    }
    
    /**
     * Return y-coordinate of ending point.
     * @return 
     */
    public double getEndY() {
        return line.getEndY();
    }
    
    /**
     * Return this class as Line.
     * @return 
     */
    public Line asLine() {
        return line;
    }
}
