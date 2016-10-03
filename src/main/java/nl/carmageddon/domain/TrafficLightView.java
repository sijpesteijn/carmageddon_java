package nl.carmageddon.domain;

import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightView {

    private ROI roi;
    private List<Rect> foundRectangles;

    public void setRoi(ROI roi) {
        this.roi = roi;
    }

    public void setFoundRectangles(List<Rect> foundRectangles) {
        this.foundRectangles =foundRectangles;
    }

    public List<Rect> getFoundRectangles() {
        return foundRectangles;
    }

    public ROI getRoi() {
        return roi;
    }
}
