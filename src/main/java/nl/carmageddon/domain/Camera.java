package nl.carmageddon.domain;

import javax.inject.Singleton;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Camera {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
