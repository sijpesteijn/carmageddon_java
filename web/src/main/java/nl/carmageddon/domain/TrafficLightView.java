package nl.carmageddon.domain;

import org.opencv.core.Mat;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightView implements View {
    private Mat roiMat;

    public void setRoiMat(Mat roiMat) {
        this.roiMat = roiMat;
    }

    public Mat getRoiMat() {
        return roiMat;
    }
}
