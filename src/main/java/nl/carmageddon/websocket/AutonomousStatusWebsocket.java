package nl.carmageddon.websocket;

import com.sun.jersey.core.util.Base64;
import nl.carmageddon.domain.LookoutResult;
import nl.carmageddon.guice.CarmageddonWebsocketConfigurator;
import nl.carmageddon.service.AutonomousService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@ServerEndpoint(value = "/autonomous/status", configurator = CarmageddonWebsocketConfigurator.class)
public class AutonomousStatusWebsocket implements Observer{
    private static final Logger log = LoggerFactory.getLogger(AutonomousStatusWebsocket.class);
    List<Session> sessions = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();
    private AutonomousService autonomousService;

    @Inject
    public AutonomousStatusWebsocket(AutonomousService autonomousService) {
        this.autonomousService = autonomousService;
        this.autonomousService.addObserver(this);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.debug("Session added " + session.toString());
        sessions.add(session);
        String json = mapper.writeValueAsString(autonomousService.getStatus());
        session.getAsyncRemote().sendText(json);
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
            LookoutResult status = (LookoutResult) arg;
            byte[] imgBytes = status.getImgBytes();
            ByteBuffer buffer = null;
//            if (imgBytes != null) {
                File fi = new File("./src/main/resources/ready.jpg");
                imgBytes = Files.readAllBytes(fi.toPath());
                System.out.println("Send " + imgBytes.length);
                buffer = ByteBuffer.wrap(Base64.encode(imgBytes));
//            }
            String json = mapper.writeValueAsString(status);
            for (Session session : sessions) {
                session.getAsyncRemote().sendText(json);
                if (buffer != null) {
                    session.getAsyncRemote().sendBinary(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
