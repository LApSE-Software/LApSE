package drawingtagger;

import javafx.scene.shape.Line;

/**
 *
 * @author Burhanuddin
 */
public class TaggedLine {
    int id;
    Line line;
    long timeStart, timeEnd;
    
    public TaggedLine(int id, Line line, long timeStart, long timeEnd) {
        this.id = id;
        this.line = line;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
}
