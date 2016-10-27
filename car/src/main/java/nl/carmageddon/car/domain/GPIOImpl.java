package nl.carmageddon.car.domain;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * @author Gijs Sijpesteijn
 */
public class GPIOImpl implements GPIO {

    private String nr;

    public GPIOImpl(String nr) {
        this.nr = nr;
    }

    @Override
    public void start() {
        echo("/sys/class/gpio/gpio" + nr + "/value", "1");
    }

    @Override
    public void stop() {
        echo("/sys/class/gpio/gpio" + nr + "/value", "0");
    }

    @Override
    public void setDirection(String direction) {
        echo("/sys/class/gpio/gpio" + nr + "/direction", direction);
    }

    private void echo(String path, String value) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(value);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
