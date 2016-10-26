package nl.carmageddon.domain;

import org.opencv.core.Point;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class RoadLookoutView implements View {
    private Line leftLane;
    private Line rightLane;
    private List<Line> roadLines;
    private List<Line> finishLines;
    private Point laneCenter;

    private Point finishCenter;

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

    public void setLaneCenter(Point laneCenter) {
        this.laneCenter = laneCenter;
    }

    public Point getLaneCenter() {
        return laneCenter;
    }

    public void setFinishCenter(Point finishCenter) {
        this.finishCenter = finishCenter;
    }

    public Point getFinishCenter() {
        return finishCenter;
    }
}
