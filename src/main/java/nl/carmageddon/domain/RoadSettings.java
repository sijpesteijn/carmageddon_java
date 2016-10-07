package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class RoadSettings {
    private int roiHeight;
    private int cannyThreshold1;
    private int cannyThreshold2;
    private int cannyApertureSize;
    private int linesThreshold;
    private int linesMinLineSize;
    private int linesMaxLineGap;

    public int getRoiHeight() {
        return roiHeight;
    }

    public void setRoiHeight(int roiHeight) {
        this.roiHeight = roiHeight;
    }

    public int getCannyThreshold1() {
        return cannyThreshold1;
    }

    public void setCannyThreshold1(int cannyThreshold1) {
        this.cannyThreshold1 = cannyThreshold1;
    }

    public int getCannyThreshold2() {
        return cannyThreshold2;
    }

    public void setCannyThreshold2(int cannyThreshold2) {
        this.cannyThreshold2 = cannyThreshold2;
    }

    public int getCannyApertureSize() {
        return cannyApertureSize;
    }

    public void setCannyApertureSize(int cannyApertureSize) {
        this.cannyApertureSize = cannyApertureSize;
    }

    public int getLinesThreshold() {
        return linesThreshold;
    }

    public void setLinesThreshold(int linesThreshold) {
        this.linesThreshold = linesThreshold;
    }

    public int getLinesMinLineSize() {
        return linesMinLineSize;
    }

    public void setLinesMinLineSize(int linesMinLineSize) {
        this.linesMinLineSize = linesMinLineSize;
    }

    public int getLinesMaxLineGap() {
        return linesMaxLineGap;
    }

    public void setLinesMaxLineGap(int linesMaxLineGap) {
        this.linesMaxLineGap = linesMaxLineGap;
    }
}
