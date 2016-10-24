package nl.carmageddon.service;

import com.google.inject.Inject;
import nl.carmageddon.domain.CarInstuction;
import nl.carmageddon.domain.CarmageddonSettings;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class CarInstructionSender {
    private static Logger logger = LoggerFactory.getLogger(CarInstructionSender.class);
    private ObjectMapper mapper = new ObjectMapper();
    private PrintWriter out;

    @Inject
    public CarInstructionSender(CarmageddonSettings settings) throws IOException {
        Socket socket = new Socket(settings.getBeagleBoneSettings().getBeagleBoneIp(),
                                   settings.getBeagleBoneSettings().getCarControlPort());
        out = new PrintWriter(socket.getOutputStream());
    }

    public void sendMessage(String key, Object value) {
        try {
            CarInstuction ci = new CarInstuction();
            ci.setKey(key);
            ci.setValue(value);
            out.println(mapper.writeValueAsString(ci));
            out.flush();
        } catch (IOException e) {
            logger.error("Could not send instruction: " + key + " = " + value + " " + e.getMessage());
        }
    }
}
