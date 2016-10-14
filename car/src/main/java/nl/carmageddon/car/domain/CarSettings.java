package nl.carmageddon.car.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class CarSettings {
    private int lifelinePort;
    private int carStatusPort;
    private int carControlPort;
    private int throtteLimit;

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

    public void setThrotteLimit(int throtteLimit) {
        this.throtteLimit = throtteLimit;
    }

    public int getThrotteLimit() {
        return throtteLimit;
    }
}
