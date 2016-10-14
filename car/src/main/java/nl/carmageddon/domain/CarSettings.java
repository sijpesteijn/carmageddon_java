package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class CarSettings {
    private int lifelinePort;
    private int carStatusPort;

    private int carControlPort;

    public int lifelinePort() {
        return lifelinePort;
    }

    public void setLifelinePort(int lifelinePort) {
        this.lifelinePort = lifelinePort;
    }

    public int carStatusPort() {
        return carStatusPort;
    }

    public void setCarStatusPort(int carStatusPort) {
        this.carStatusPort = carStatusPort;
    }

    public void setCarControlPort(int carControlPort) {
        this.carControlPort = carControlPort;
    }

    public int getCarControlPort() {
        return carControlPort;
    }
}
