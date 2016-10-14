package nl.carmageddon.car.domain;

/**
 * @author Gijs Sijpesteijn
 */
public interface Pwm {
    void setPeriod(int period);

    void setPolarity(int polarity);

    void start();

    void stop();

    void setDuty(int angle);
}
