package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightSettings {

    private HSV lowerHSVMin;
    private HSV lowerHSVMax;
    private HSV upperHSVMin;
    private HSV upperHSVMax;
    private Dimension minDimension;
    private Dimension maxDimension;
    private ROI roi;
    private ViewType viewType;
    private boolean addFound;

    public HSV getLowerHSVMin() {
        return lowerHSVMin;
    }

    public void setLowerHSVMin(HSV lowerHSVMin) {
        this.lowerHSVMin = lowerHSVMin;
    }

    public HSV getLowerHSVMax() {
        return lowerHSVMax;
    }

    public void setLowerHSVMax(HSV lowerHSVMax) {
        this.lowerHSVMax = lowerHSVMax;
    }

    public HSV getUpperHSVMin() {
        return upperHSVMin;
    }

    public void setUpperHSVMin(HSV upperHSVMin) {
        this.upperHSVMin = upperHSVMin;
    }

    public HSV getUpperHSVMax() {
        return upperHSVMax;
    }

    public void setUpperHSVMax(HSV upperHSVMax) {
        this.upperHSVMax = upperHSVMax;
    }

    public Dimension getMinDimension() {
        return minDimension;
    }

    public void setMinDimension(Dimension minDimension) {
        this.minDimension = minDimension;
    }

    public Dimension getMaxDimension() {
        return maxDimension;
    }

    public void setMaxDimension(Dimension maxDimension) {
        this.maxDimension = maxDimension;
    }

    public nl.carmageddon.domain.ROI getRoi() {
        return roi;
    }

    public void setRoi(nl.carmageddon.domain.ROI roi) {
        this.roi = roi;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public boolean isAddFound() {
        return addFound;
    }

    public void setAddFound(boolean addFound) {
        this.addFound = addFound;
    }
}