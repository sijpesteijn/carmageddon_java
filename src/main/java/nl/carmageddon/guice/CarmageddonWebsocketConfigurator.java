package nl.carmageddon.guice;

import javax.websocket.server.ServerEndpointConfig;

/**
 * @author Gijs Sijpesteijn
 */
public class CarmageddonWebsocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return GuiceServletConfig.injector.getInstance(endpointClass);
    }
}
