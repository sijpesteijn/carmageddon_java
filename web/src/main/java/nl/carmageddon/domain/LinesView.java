package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class LinesView implements View {
    private Rect roi;
    private Line averageLine;
    private Line leftLane;
    private Line rightLane;
    private List<Line> roadLines;
    private List<Line> finishLines;
    private double averageX;
    private Mat result;
    private int angle;
    private FinishLine finishLine;
    private PCA leftPca;
    private PCA rightPca;
    private PCA finishPca;

    public void setRoi(Rect roi) {
        this.roi = roi;
    }

    public Rect getRoi() {
        return roi;
    }

    public void setAverageLine(Line averageLine) {
        this.averageLine = averageLine;
    }

    @Deprecated
    public void setRoadLines(List<Line> roadLines) {
        this.roadLines = roadLines;
    }

    public void setFinishLines(List<Line> finishLines) {
        this.finishLines = finishLines;
    }

    public Line getAverageLine() {
        return averageLine;
    }

    @Deprecated
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

    public void setAverageX(double averageX) {
        this.averageX = (int) averageX;
    }

    public double getAverageX() {
        return averageX;
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

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public FinishLine getFinishLine() {
        return finishLine;
    }

    public void setFinishLine(FinishLine finishLine) {
        this.finishLine = finishLine;
    }

    public boolean hasLines() {
        return leftLane == null && rightLane == null;
    }

    public void setLeftPca(PCA leftPca) {
        this.leftPca = leftPca;
    }

    public void setRightPca(PCA rightPca) {
        this.rightPca = rightPca;
    }

    public void setFinishPca(PCA finishPca) {
        this.finishPca = finishPca;
    }

    public PCA getLeftPca() {
        return leftPca;
    }

    public PCA getRightPca() {
        return rightPca;
    }

    public PCA getFinishPca() {
        return finishPca;
    }
}
