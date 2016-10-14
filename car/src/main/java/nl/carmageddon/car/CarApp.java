package nl.carmageddon.car;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import nl.carmageddon.car.guice.ConfigurationModule;
import nl.carmageddon.car.socket.CarControlSocket;
import nl.carmageddon.car.socket.CarStatusSocket;
import nl.carmageddon.car.socket.LifeLineSocket;

/**
 * @author Gijs Sijpesteijn
 */
public class CarApp {
    private Injector injector;
    @Inject
    private LifeLineSocket lifeLineSocket;
    @Inject
    private CarStatusSocket carStatusSocket;
    @Inject
    private CarControlSocket carControlSocket;

    public static void main(String[] args) {
        new CarApp();
    }

    public CarApp() {
        injector = Guice.createInjector(new ConfigurationModule());
        injector.injectMembers(this);
    }
}
