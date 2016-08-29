package nl.carmageddon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@ServerEndpoint("/status")
public class StatusWebsocket implements Observer {
    private static final Logger log = LoggerFactory.getLogger(StatusWebsocket.class);

//    @Inject
//    public StatusWebsocket(Steer steer, Engine engine) {
//        steer.addObserver(this);
//        engine.addObserver(this);
//    }

    @OnOpen
    public void onOpen(Session session) {
        log.debug("Open");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("Message");
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        log.debug("Close");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.debug("Error");
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(arg);
    }
}
