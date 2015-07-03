package drawingtagger;

import javafx.geometry.Rectangle2D;

/**
 *
 * @author Burhanuddin
 */
public class TaggedRectangle {
    Rectangle2D rect;
    String tag;
    
    public TaggedRectangle(Rectangle2D rect, String tag) {
        this.rect = rect;
        this.tag = tag;
    }
}
