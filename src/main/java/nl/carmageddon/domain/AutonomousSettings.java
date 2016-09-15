package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousSettings {
    private RGB lowerRGBMin;
    private RGB lowerRGBMax;
    private RGB upperRGBMin;
    private RGB upperRGBMax;

    public void setLowerRGBMin(RGB lowerRGBMin) {
        this.lowerRGBMin = lowerRGBMin;
    }

    public void setLowerRGBMax(RGB lowerRGBMax) {
        this.lowerRGBMax = lowerRGBMax;
    }

    public void setUpperRGBMin(RGB upperRGBMin) {
        this.upperRGBMin = upperRGBMin;
    }

    public void setUpperRGBMax(RGB upperRGBMax) {
        this.upperRGBMax = upperRGBMax;
    }

    public RGB getLowerRGBMin() {
        return lowerRGBMin;
    }

    public RGB getLowerRGBMax() {
        return lowerRGBMax;
    }

    public RGB getUpperRGBMin() {
        return upperRGBMin;
    }

    public RGB getUpperRGBMax() {
        return upperRGBMax;
    }
}
