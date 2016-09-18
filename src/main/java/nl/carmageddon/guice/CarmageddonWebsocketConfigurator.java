package nl.carmageddon.guice;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author Gijs Sijpesteijn
 */
public class CarmageddonWebsocketConfigurator extends ServerEndpointConfig.Configurator {

    @Inject
    private static Injector injector;

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injector.getInstance(endpointClass);
    }
}
