package nl.carmageddon.websocket;

import nl.carmageddon.domain.Car;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import nl.carmageddon.service.CarInstructionSender;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/car/status", configurator = CarmageddonWebsocketConfigurator.class)
public class CarStatusWebsocket implements Observer {
    private static final Logger log = LoggerFactory.getLogger(CarStatusWebsocket.class);
    private List<Session> sessions = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private Car car;

    @Inject
    public CarStatusWebsocket(Car car, CarInstructionSender carInstructionSender) throws IOException {
        this.car = car;
        this.car.addObserver(this);
        carInstructionSender.sendMessage("hello", true);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.debug("Session added " + session.toString());
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) throws IOException {
        log.debug("Session removed " + session.toString());
        try {
            sessions.remove(session);
            session.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Session removed " + session.toString());
        sessions.remove(session);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            String json = mapper.writeValueAsString(this.car);
            sessions.forEach(session -> {
                session.getAsyncRemote().sendText(json);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
