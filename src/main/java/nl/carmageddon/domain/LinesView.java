package nl.carmageddon.domain;

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
}
