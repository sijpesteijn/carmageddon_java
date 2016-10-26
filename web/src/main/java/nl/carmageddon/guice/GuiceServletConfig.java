package nl.carmageddon.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import nl.carmageddon.car.CPU;

import javax.servlet.ServletContextEvent;

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

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        CPU cpu = injector.getInstance(CPU.class);
        cpu.destroy();
    }
}