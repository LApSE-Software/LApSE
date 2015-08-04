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
package lapse.shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

/**
 *
 * @author Burhanuddin
 */
public class Arrow extends Polygon {
    public double rotate;
    public float t;
    CubicCurve curve;
    Rotate rotateZ;
    
    /**
     * Creates new instance of arrow with default arrow shape.
     * @param curve
     * @param t 
     */
    public Arrow(CubicCurve curve, float t) {
        this(curve, t, 0, 0, 7.5, 22.5, 0, 18.75, -7.5, 22.5);
    }
    
    /**
     * Create new instance of arrow with custom shape.
     * @param curve
     * @param t
     * @param arg0 
     */
    public Arrow(CubicCurve curve, float t, double... arg0) {
        super(arg0);
        this.curve = curve;
        this.t = t;
        init();
    }
    
    /**
     * Initialize.
     */
    private void init() {
        setStroke(Color.GREEN);
        setFill(Color.LIGHTGREEN);
        rotateZ = new Rotate();
        rotateZ.setAxis(Rotate.Z_AXIS);
        getTransforms().addAll(rotateZ);
        update();
    }
    
    /**
     * Update position and rotation.
     */
    public void update() {
        double size = Math.max(curve.getBoundsInLocal().getWidth(), curve.getBoundsInLocal().getHeight());
        double scale = size / 4d;

        Point2D ori = eval(curve, t);
        Point2D tan = evalDt(curve, t).normalize().multiply(scale);

        setTranslateX(ori.getX());
        setTranslateY(ori.getY());

        double angle = Math.atan2(tan.getY(), tan.getX());
        angle = Math.toDegrees(angle);
        
        double offset = +90;

        rotateZ.setAngle(angle + offset);    
    }
    
    /**
    * Evaluate the cubic curve at a parameter 0<=t<=1, returns a Point2D
    * @param c the CubicCurve 
    * @param t param between 0 and 1
    * @return a Point2D 
    */
    private Point2D eval(CubicCurve c, float t){
        return new Point2D(Math.pow(1-t, 3) * c.getStartX() +
                3 * t * Math.pow(1-t, 2)* c.getControlX1() +
                3 * (1-t) * t * t * c.getControlX2() +
                Math.pow(t, 3) * c.getEndX(),
                Math.pow(1-t, 3) * c.getStartY() +
                3 * t * Math.pow(1-t, 2) * c.getControlY1() +
                3 * (1-t) * t * t * c.getControlY2() +
                Math.pow(t, 3) * c.getEndY());
    }
   
   /**
    * Evaluate the tangent of the cubic curve at a parameter 0<=t<=1, returns a Point2D
    * @param c the CubicCurve 
    * @param t param between 0 and 1
    * @return a Point2D 
    */
    private Point2D evalDt(CubicCurve c, float t){
        return new Point2D(-3 * Math.pow(1-t, 2) * c.getStartX() +
                3 * (Math.pow(1-t, 2) - 2 * t * (1-t)) * c.getControlX1() +
                3 * ((1-t) * 2 * t - t * t) * c.getControlX2() +
                3 * Math.pow(t, 2) * c.getEndX(),
                -3 * Math.pow(1-t, 2) * c.getStartY() +
                3 * (Math.pow(1-t, 2) - 2 * t * (1-t)) * c.getControlY1() +
                3 * ((1-t) * 2 * t - t * t) * c.getControlY2() +
                3 * Math.pow(t, 2) * c.getEndY());
    }
}
