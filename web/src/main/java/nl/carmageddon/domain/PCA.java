package nl.carmageddon.domain;

import org.opencv.core.Point;

/**
 * @author Gijs Sijpesteijn
 */
public class PCA {
    private double angle;
    private Point center;
    private Point axisX;
    private Point axisY;

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Point getAxisX() {
        return axisX;
    }

    public void setAxisX(Point axisX) {
        this.axisX = axisX;
    }

    public Point getAxisY() {
        return axisY;
    }

    public void setAxisY(Point axisY) {
        this.axisY = axisY;
    }
}
