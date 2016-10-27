package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class RoadSettings {
    private boolean showFinishLines;
    private boolean showRoadLines;
    private int roiHeight;
    private LineSettings laneLineSettings;
    private LineSettings finishLineSettings;
    private int minDistance2FinishLine;
    private int breakVelocity;
    private int steeringSpeed;
    private int straightSpeed;
    private int minSideDistance;

    public void setShowFinishLines(boolean showFinishLines) {
        this.showFinishLines = showFinishLines;
    }

    public boolean isShowFinishLines() {
        return showFinishLines;
    }

    public boolean isShowRoadLines() {
        return showRoadLines;
    }

    public void setShowRoadLines(boolean showRoadLines) {
        this.showRoadLines = showRoadLines;
    }

    public void setLaneLineSettings(LineSettings laneLineSettings) {
        this.laneLineSettings = laneLineSettings;
    }

    public void setFinishLineSettings(LineSettings finishLineSettings) {
        this.finishLineSettings = finishLineSettings;
    }

    public LineSettings getLaneLineSettings() {
        return laneLineSettings;
    }

    public LineSettings getFinishLineSettings() {
        return finishLineSettings;
    }

    public int getRoiHeight() {
        return roiHeight;
    }

    public void setRoiHeight(int roiHeight) {
        this.roiHeight = roiHeight;
    }

    public int getMinDistance2FinishLine() {
        return minDistance2FinishLine;
    }

    public int getBreakVelocity() {
        return breakVelocity;
    }

    public int getSteeringSpeed() {
        return steeringSpeed;
    }

    public int getStraightSpeed() {
        return straightSpeed;
    }

    public void setMinDistance2FinishLine(int minDistance2FinishLine) {
        this.minDistance2FinishLine = minDistance2FinishLine;
    }

    public void setBreakVelocity(int breakVelocity) {
        this.breakVelocity = breakVelocity;
    }

    public void setSteeringSpeed(int steeringSpeed) {
        this.steeringSpeed = steeringSpeed;
    }

    public void setStraightSpeed(int straightSpeed) {
        this.straightSpeed = straightSpeed;
    }

    public void setMinSideDistance(int minSideDistance) {
        this.minSideDistance = minSideDistance;
    }

    public int getMinSideDistance() {
        return minSideDistance;
    }
}
