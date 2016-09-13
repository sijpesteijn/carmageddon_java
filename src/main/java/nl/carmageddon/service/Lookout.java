package nl.carmageddon.service;

import nl.carmageddon.domain.LookoutResult;

/**
 * @author Gijs Sijpesteijn
 */
public interface Lookout {

    LookoutResult start();

    void stop();
}
