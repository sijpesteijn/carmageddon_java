package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class BeagleBoneSettings {

    private String beagleBoneIp;
    private int lifeLinePort;
    private int carStatusPort;
    private int carControlPort;

    public void setBeagleBoneIp(String beagleBoneIp) {
        this.beagleBoneIp = beagleBoneIp;
    }

    public void setLifeLinePort(int lifeLinePort) {
        this.lifeLinePort = lifeLinePort;
    }

    public void setCarStatusPort(int carStatusPort) {
        this.carStatusPort = carStatusPort;
    }

    public void setCarControlPort(int carControlPort) {
        this.carControlPort = carControlPort;
    }

    public String getBeagleBoneIp() {
        return beagleBoneIp;
    }

    public int getLifeLinePort() {
        return lifeLinePort;
    }

    public int getCarStatusPort() {
        return carStatusPort;
    }

    public int getCarControlPort() {
        return carControlPort;
    }
}
