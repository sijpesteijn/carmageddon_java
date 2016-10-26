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
}
