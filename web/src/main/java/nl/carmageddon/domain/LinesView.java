package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class LinesView implements View {
    private Rect roi;
    private Line leftLane;
    private Line rightLane;
    private List<Line> roadLines;
    private List<Line> finishLines;
    private Mat result;
    private Point center;

    public void setRoi(Rect roi) {
        this.roi = roi;
    }

    public Rect getRoi() {
        return roi;
    }

    public void setRoadLines(List<Line> roadLines) {
        this.roadLines = roadLines;
    }

    public void setFinishLines(List<Line> finishLines) {
        this.finishLines = finishLines;
    }

    public List<Line> getRoadLines() {
        return roadLines;
    }

    public List<Line> getFinishLines() {
        return finishLines;
    }

    public void setResult(Mat result) {
        this.result = result;
    }

    public Mat getResult() {
        return result;
    }

    public Line getLeftLane() {
        return leftLane;
    }

    public void setLeftLane(Line leftLane) {
        this.leftLane = leftLane;
    }

    public Line getRightLane() {
        return rightLane;
    }

    public void setRightLane(Line rightLane) {
        this.rightLane = rightLane;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public Point getCenter() {
        return center;
    }

}
