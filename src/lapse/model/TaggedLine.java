/*
 * The MIT License
 *
 * Copyright 2015 Burhanuddin.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lapse.model;

import javafx.scene.shape.Line;

/**
 *
 * @author Burhanuddin
 */
public class TaggedLine implements Comparable<TaggedLine> {
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

    /**
     * To be used for comparable. Negative value means this line is drawn earlier,
     * zero value means both are drawn at the same time (which would be impossible),
     * positive value means this line is drawn late.
     * @param o
     * @return 
     */
    @Override
    public int compareTo(TaggedLine o) {
        return (int) (timeStart - o.timeStart);
    }
}
