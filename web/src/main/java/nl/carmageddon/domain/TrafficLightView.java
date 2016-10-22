package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightView implements View {

    private Rect roi;
    private Mat result;
    private Mat roiMat;

    public void setResult(Mat result) {
        this.result = result;
    }

    public Mat getResult() {
        return result;
    }

    public Rect getRoi() {
        return roi;
    }

    public void setRoi(Rect roi) {
        this.roi = roi;
    }

    public void setRoiMat(Mat roiMat) {
        this.roiMat = roiMat;
    }

    public Mat getRoiMat() {
        return roiMat;
    }
}
