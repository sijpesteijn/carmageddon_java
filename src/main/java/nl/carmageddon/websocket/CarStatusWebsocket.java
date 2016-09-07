package nl.carmageddon.websocket;

import nl.carmageddon.domain.Car;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
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
    private Car car;
    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    public CarStatusWebsocket(Car car) {
        this.car = car;
        this.car.addObserver(this);
        this.car.getSteer().addObserver(this);
        this.car.getEngine().addObserver(this);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
//        misschien in een aparte thread. Kost zo te veel tijd.
//        if (sessions.isEmpty()) {
//            this.car.getSteer().wobbleWheels();
//        }
        String json = mapper.writeValueAsString(this.car);
        session.getAsyncRemote().sendText(json);
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping")) {
            session.getAsyncRemote().sendText("pong");
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            String json = mapper.writeValueAsString(this.car);
            for (Session session : sessions)
                session.getAsyncRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
