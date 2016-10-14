package nl.carmageddon.car.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import nl.carmageddon.car.domain.CarSettings;
import nl.carmageddon.car.domain.Pwm;
import nl.carmageddon.car.domain.PwmImpl;
import nl.carmageddon.car.domain.PwmMock;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Gijs Sijpesteijn
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        if (System.getProperty("os.arch").contains("arm")) {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).toInstance(new PwmImpl(22));
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).toInstance(new PwmImpl(42));
        } else {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).to(PwmMock.class);
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).to(PwmMock.class);
        }

        try {
            PropertiesConfiguration configuration = new PropertiesConfiguration("car.properties");
            CarSettings settings = new CarSettings();
            settings.setLifelinePort(configuration.getInt("lifeline.port"));
            settings.setCarStatusPort(configuration.getInt("carstatus.port"));
            settings.setCarControlPort(configuration.getInt("carcontrol.port"));
            settings.setThrotteLimit(configuration.getInt("throttle.limit"));

            bind(CarSettings.class).toInstance(settings);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("car.properties could not be found on the classpath.");
        }
    }
}
