package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class TrafficLightSettings {
    private boolean blackAndWhite;
    private HSV lowerHSVMin;
    private HSV lowerHSVMax;
    private HSV upperHSVMin;
    private HSV upperHSVMax;

    public boolean isBlackAndWhite() {
        return blackAndWhite;
    }

    public void setBlackAndWhite(boolean blackAndWhite) {
        this.blackAndWhite = blackAndWhite;
    }

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
}
