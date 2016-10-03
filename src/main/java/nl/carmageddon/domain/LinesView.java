package nl.carmageddon.domain;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class LinesView {

    private Rect roi;

    private List<Point> points;

    private Double maxX;

    private Double minX;

    public void setRoi(Rect roi) {
        this.roi = roi;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Rect getRoi() {
        return roi;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setMaxX(Double maxX) {
        this.maxX = maxX;
    }

    public void setMinX(Double minX) {
        this.minX = minX;
    }

    public Double getMaxX() {
        return maxX;
    }

    public Double getMinX() {
        return minX;
    }
}
