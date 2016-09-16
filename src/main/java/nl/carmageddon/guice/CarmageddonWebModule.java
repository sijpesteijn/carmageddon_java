package nl.carmageddon.guice;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import nl.carmageddon.domain.Pwm;
import nl.carmageddon.domain.PwmImpl;
import nl.carmageddon.domain.PwmMock;
import org.opencv.core.Core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Gijs Sijpesteijn
 */
public class CarmageddonWebModule extends ServletModule {

    @Override
    protected void configureServlets() {

        if (System.getProperty("os.arch").contains("arm")) {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).toInstance(new PwmImpl(22));
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).toInstance(new PwmImpl(42));
        } else {
            bind(Pwm.class).annotatedWith(Names.named("PWM22")).to(PwmMock.class);
            bind(Pwm.class).annotatedWith(Names.named("PWM42")).to(PwmMock.class);
        }

        try {
            Field loadedLibraryNames = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            loadedLibraryNames.setAccessible(true);
            final Vector<String> libraries = (Vector<String>) loadedLibraryNames.get(ClassLoader.getSystemClassLoader());
            String[] names = libraries.toArray(new String[] {});
            boolean loaded = false;
            for(String name : names) {
                if (name.endsWith(Core.NATIVE_LIBRARY_NAME + ".dylib"))
                    loaded = true;
            }
            if (!loaded) {
                System.out.println("Loading opencv native library.");
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map parameters = new HashMap();
        parameters.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
        parameters.put("com.sun.jersey.config.property.packages", "nl.carmageddon");
        serve("/rest/*").with(GuiceContainer.class, parameters);
    }

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(CarmageddonWebsocketConfigurator.class);
    }
}
