package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightSettings {

    private ROI roi;
    private ViewType viewType;
    private boolean addFound;

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
