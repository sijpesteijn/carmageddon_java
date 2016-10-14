package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightView {

    private ROI roi;
    private List<Rect> foundRectangles;

    private Mat result;

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

    public void setResult(Mat result) {
        this.result = result;
    }

    public Mat getResult() {
        return result;
    }
}
