package drawingtagger.model;

import javafx.geometry.Rectangle2D;

/**
 *
 * @author Burhanuddin
 */
public class TaggedRectangle {
    public Rectangle2D rect;
    public String tag;
    
    /**
     * Constructs a tagged rectangle based on specified values.
     * @param rect
     * @param tag 
     */
    public TaggedRectangle(Rectangle2D rect, String tag) {
        this.rect = rect;
        this.tag = tag;
    }
}
