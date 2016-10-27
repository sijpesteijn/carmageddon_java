package nl.carmageddon.car.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import nl.carmageddon.car.domain.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        if (System.getProperty("os.arch").contains("arm")) {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).toInstance(new PwmImpl(22));
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).toInstance(new PwmImpl(42));
            bind(GPIO.class).toInstance(new GPIOImpl(16));
        } else {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).to(PwmMock.class);
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).to(PwmMock.class);
            bind(GPIO.class).to(GPIOMock.class);
        }

        try {
            PropertiesConfiguration configuration = new PropertiesConfiguration("car.properties");
            CarSettings settings = new CarSettings();
            settings.setLifelinePort(configuration.getInt("lifeline.port"));
            settings.setCarStatusPort(configuration.getInt("carstatus.port"));
            settings.setCarControlPort(configuration.getInt("carcontrol.port"));
            settings.setThrotteLimit(configuration.getInt("throttle.limit"));

            bind(CarSettings.class).toInstance(settings);

            List<Socket> clientConnections = new ArrayList<>();
            bind(new TypeLiteral<List<Socket>>() {}).toInstance(clientConnections);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("car.properties could not be found on the classpath.");
        }
    }
}
