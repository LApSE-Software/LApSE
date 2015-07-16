/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawingtagger.util;

import javafx.scene.shape.CubicCurve;

/**
 *
 * @author Burhanuddin
 */
public class GT {
    
    /**
     * Return angle between between two cubic curves.
     * @param curve1
     * @param curve2
     * @return angle
     */
    public static double angle(CubicCurve curve1, CubicCurve curve2) {
        double angle1 = Math.atan2(curve1.getStartY() - curve1.getEndY(), curve1.getStartX() - curve1.getEndX());
        double angle2 = Math.atan2(curve2.getStartY() - curve2.getEndY(), curve2.getStartX() - curve2.getEndX());
        double angle = angle1 - angle2;
        if (angle < 0) {    // negative (clockwise)
            if (angle < -Math.PI) {
                return angle + Math.PI;
            } else {
                return Math.abs(angle + Math.PI);
            }
        } else {            // positive (anti-clockwise)
            if (angle > Math.PI) {
                return angle - Math.PI;
            } else {
                return -Math.abs(angle - Math.PI);
            }
        }
    }
    
    /**
     * Position both control points of cubic curve according to ratio.
     * @param curve
     * @param ratio 
     */
    public static void scale(CubicCurve curve, double ratio) {
        double x1 = curve.getStartX();
        double y1 = curve.getStartY();
        double x2 = curve.getEndX();
        double y2 = curve.getEndY();
        curve.setControlX1(x1 + (x2 - x1) * ratio);
        curve.setControlY1(y1 + (y2 - y1) * ratio);
        curve.setControlX2(x2 + (x1 - x2) * ratio);
        curve.setControlY2(y2 + (y1 - y2) * ratio);
    }
    
    /**
     * Rotate control point according to angle.
     * @param curve
     * @param angle
     * @param isFirstControlPoint 
     */
    public static void rotate(CubicCurve curve, double angle, boolean isFirstControlPoint) {
        double x1, y1, x2, y2;
        double xNew, yNew;
        double theta = Math.abs(angle);
        if (isFirstControlPoint) {
            x1 = curve.getStartX();
            y1 = curve.getStartY();
            x2 = curve.getControlX1();
            y2 = curve.getControlY1();
            if (angle < 0) {
                xNew = x1 + Math.cos(theta) * (x2 - x1) + Math.sin(theta) * (y2 - y1);
                yNew = y1 - Math.sin(theta) * (x2 - x1) + Math.cos(theta) * (y2 - y1);
            } else {
                xNew = x1 + Math.cos(theta) * (x2 - x1) - Math.sin(theta) * (y2 - y1);
                yNew = y1 + Math.sin(theta) * (x2 - x1) + Math.cos(theta) * (y2 - y1);
            }
            curve.setControlX1(xNew);
            curve.setControlY1(yNew);
        } else {
            x1 = curve.getControlX2();
            y1 = curve.getControlY2();
            x2 = curve.getEndX();
            y2 = curve.getEndY();
            if (angle < 0) {
                xNew = x2 + Math.cos(theta) * (x1 - x2) + Math.sin(theta) * (y1 - y2);
                yNew = y2 - Math.sin(theta) * (x1 - x2) + Math.cos(theta) * (y1 - y2);
            } else {
                xNew = x2 + Math.cos(theta) * (x1 - x2) - Math.sin(theta) * (y1 - y2);
                yNew = y2 + Math.sin(theta) * (x1 - x2) + Math.cos(theta) * (y1 - y2);
            }
            curve.setControlX2(xNew);
            curve.setControlY2(yNew);
        }
    }
}
