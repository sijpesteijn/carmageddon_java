package nl.carmageddon.car.domain;

/**
 * @author Gijs Sijpesteijn
 */
public interface GPIO {
    void start();

    void stop();

    void setDirection(String direction);

}
