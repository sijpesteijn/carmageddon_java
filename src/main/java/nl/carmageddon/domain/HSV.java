package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class HSV {
    private int hue;
    private int saturation;
    private int brightness;

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
}
