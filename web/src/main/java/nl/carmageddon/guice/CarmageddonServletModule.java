package nl.carmageddon.guice;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Gijs Sijpesteijn
 */
public class CarmageddonServletModule extends ServletModule {
    private static Logger logger = LoggerFactory.getLogger(CarmageddonServletModule.class);

    @Override
    protected void configureServlets() {

        String extentsion = ".dylib";
        if (System.getProperty("os.arch").contains("arm")) {
            extentsion = ".so";
        }

        try {
            Field loadedLibraryNames = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            loadedLibraryNames.setAccessible(true);
            final Vector<String> libraries = (Vector<String>) loadedLibraryNames.get(ClassLoader.getSystemClassLoader());
            String[] names = libraries.toArray(new String[] {});
            boolean loaded = false;
            for(String name : names) {
                if (name.endsWith(Core.NATIVE_LIBRARY_NAME + extentsion))
                    loaded = true;
            }
            if (!loaded) {
                logger.debug("Loading opencv native library: " + Core.NATIVE_LIBRARY_NAME + extentsion);
                try {
                    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                } catch (UnsatisfiedLinkError e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        Map parameters = new HashMap();
        parameters.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
        parameters.put("com.sun.jersey.config.property.packages", "nl.carmageddon");
        serve("/rest/*").with(GuiceContainer.class, parameters);
        this.requestStaticInjection(CarmageddonWebsocketConfigurator.class);
    }

}
