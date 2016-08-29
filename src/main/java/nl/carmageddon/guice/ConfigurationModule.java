package nl.carmageddon.guice;

import com.google.inject.AbstractModule;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Gijs Sijpesteijn
 */
public class ConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(Configuration.class).toInstance(new PropertiesConfiguration("application.properties"));
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("lasforce.properties could not be found on the classpath.");
        }
    }
}