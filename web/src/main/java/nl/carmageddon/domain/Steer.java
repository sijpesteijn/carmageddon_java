package nl.carmageddon.domain;

import javax.inject.Singleton;

/**
 * @author Gijs Sijpesteijn
 *
 *    dutyMin = 1150000 dutyMax = 1810000;
 */
@Singleton
public class Steer {
    private int angle;

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
