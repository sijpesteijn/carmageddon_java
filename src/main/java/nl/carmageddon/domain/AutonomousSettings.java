package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousSettings {
    private HSV lowerHSVMin;
    private HSV lowerHSVMax;
    private HSV upperHSVMin;
    private HSV upperHSVMax;

    public void setLowerHSVMin(HSV lowerHSVMin) {
        this.lowerHSVMin = lowerHSVMin;
    }

    public void setLowerHSVMax(HSV lowerHSVMax) {
        this.lowerHSVMax = lowerHSVMax;
    }

    public void setUpperHSVMin(HSV upperHSVMin) {
        this.upperHSVMin = upperHSVMin;
    }

    public void setUpperHSVMax(HSV upperHSVMax) {
        this.upperHSVMax = upperHSVMax;
    }

    public HSV getLowerHSVMin() {
        return lowerHSVMin;
    }

    public HSV getLowerHSVMax() {
        return lowerHSVMax;
    }

    public HSV getUpperHSVMin() {
        return upperHSVMin;
    }

    public HSV getUpperHSVMax() {
        return upperHSVMax;
    }
}
