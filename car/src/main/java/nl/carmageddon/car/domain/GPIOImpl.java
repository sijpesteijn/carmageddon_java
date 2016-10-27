package nl.carmageddon.car.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Gijs Sijpesteijn
 */
public class GPIOImpl implements GPIO {
    private static final Logger logger = LoggerFactory.getLogger(GPIOImpl.class);
    private FileWriter unexportFile;
    private FileWriter exportFile;
    private int nr;

    public GPIOImpl(int nr) {
        this.nr = nr;
        try {
            unexportFile = new FileWriter("/sys/class/gpio/unexport");
            exportFile = new FileWriter("/sys/class/gpio/export");
            // Reset the port
            File exportFileCheck = new File("/sys/class/gpio/gpio" + nr);
            if (exportFileCheck.exists()) {
                unexportFile.write(nr);
                unexportFile.flush();
            }
            // Set the port for use
            exportFile.write(nr);
            exportFile.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            FileWriter commandFile = new FileWriter("/sys/class/gpio/gpio"+nr+ "/value");
            commandFile.write(1);
            commandFile.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            FileWriter commandFile = new FileWriter("/sys/class/gpio/gpio"+nr+ "/value");
            commandFile.write(0);
            commandFile.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setDirection(String direction) {
        try {
            FileWriter directionFile = new FileWriter("/sys/class/gpio/gpio"+nr+"/direction");
            directionFile.write(direction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
