package nl.carmageddon.car.domain;

import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Horn {
    private GPIO gpio;
    private ScheduledFuture<?> schedule;
    @Inject
    public Horn(GPIO gpio) {
        this.gpio = gpio;
    }

    public void blow() {
        if (schedule != null) {
            schedule.cancel(true);
        }
        this.gpio.stop();
        this.gpio.start();
        schedule = Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            this.gpio.stop();
        }, 3000, TimeUnit.MILLISECONDS);
    }

}
