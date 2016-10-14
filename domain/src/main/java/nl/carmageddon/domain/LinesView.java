package nl.carmageddon.domain;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class LinesView {

    private Rect roi;

    private Line averageLine;

    private List<Line> roadLines;

    private List<Line> finishLines;

    private double averageX;
    private Mat result;

    public void setRoi(Rect roi) {
        this.roi = roi;
    }

    public Rect getRoi() {
        return roi;
    }

    public void setAverageLine(Line averageLine) {
        this.averageLine = averageLine;
    }

    public void setRoadLines(List<Line> roadLines) {
        this.roadLines = roadLines;
    }

    public void setFinishLines(List<Line> finishLines) {
        this.finishLines = finishLines;
    }

    public Line getAverageLine() {
        return averageLine;
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

    public void setAverageX(double averageX) {
        this.averageX = (int) averageX;
    }

    public double getAverageX() {
        return averageX;
    }
}
