package nl.carmageddon.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author Gijs Sijpesteijn
 */
public class GuiceServletConfig extends GuiceServletContextListener {
    public static Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new ConfigurationModule(), new CarmageddonServletModule());
        return injector;
    }
}