package nl.carmageddon.domain;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

/**
 * @author Gijs Sijpesteijn
 */
public class PwmImpl implements Pwm {
    private static final Logger log = LoggerFactory.getLogger(PwmImpl.class);

    private final String periodPath;
    private final String polarityPath;
    private final String dutyPath;
    private final String runPath;
    private final String dirName;
    private int period;
    private int polarity;
    private int nr;
    private int duty;
    private static String path = "/sys/devices/ocp.3/";
    public static HashMap<Integer, PwmResource> PWM = new HashMap<>();

    static {
        PWM.put(22, new PwmResource(path, "pwm_test_P9_22.*"));
        PWM.put(42, new PwmResource(path,"pwm_test_P9_42.*"));
    }

    public PwmImpl(int nr) {
        this.nr = nr;
        this.dirName = getFullDirectoryName(PWM.get(nr));
        this.periodPath = this.dirName + "period";
        this.dutyPath = this.dirName + "duty";
        this.polarityPath = this.dirName + "polarity";
        this.runPath = this.dirName + "start";
    }

    private String getFullDirectoryName(PwmResource pwmResource) {
        File dir = new File(pwmResource.getPath());
        FileFilter fileFilter = new WildcardFileFilter(pwmResource.getPwm());
        File[] files = dir.listFiles(fileFilter);
        if (files.length > 0) {
            return files[0].getAbsolutePath() + "/";
        }
        log.error("No dir found for " + pwmResource);
        return "";
    }

    public void setPeriod(int period) {
        this.period = period;
        echo(this.periodPath, String.valueOf(period));
    }

    public void setPolarity(int polarity) {
        this.polarity = polarity;
        echo(this.polarityPath, String.valueOf(polarity));
    }

    public void start() {
        echo(this.runPath, "1");
    }

    public void stop() {
        echo(this.runPath, "0");
    }

    public int getPeriod() {
        return period;
    }

    public int getPolarity() {
        return polarity;
    }

    public int getNr() {
        return nr;
    }

    public void setDuty(int duty) {
        this.duty = duty;
        echo(this.dutyPath,String.valueOf(duty));
    }

    public int getDuty() {
        return duty;
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

    private static class PwmResource {
        private final String path;
        private final String pwm;

        public PwmResource(String path, String pwm) {
            this.path = path;
            this.pwm = pwm;
        }

        public String getPath() {
            return path;
        }

        public String getPwm() {
            return pwm;
        }

        @Override
        public String toString() {
            return "PwmResource{" +
                    "path='" + path + '\'' +
                    ", pwm='" + pwm + '\'' +
                    '}';
        }
    }
}
