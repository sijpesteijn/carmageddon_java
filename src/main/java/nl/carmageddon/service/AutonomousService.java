package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousSettings;
import nl.carmageddon.domain.CPU;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class AutonomousService {
    private CPU cpu;
    private ScheduledExecutorService cpuTimer;

    @Inject
    public AutonomousService(CPU cpu) {
        this.cpu = cpu;
    }

    public boolean startRace() {
        if (!this.cpu.isRacing()) {
            // Waarom een executor? Aparte thread zodat we direct returnen.
            Runnable cpuRunner = () -> {
                this.cpu.race();
            };
            this.cpuTimer = Executors.newSingleThreadScheduledExecutor();
            this.cpuTimer.schedule(cpuRunner, 0, TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    public void addObserver(Observer observer) {
        this.cpu.addObserver(observer);
    }

    public void stopRace() {
        this.cpu.stopRacing();
    }

    public void useSettings(AutonomousSettings settings) {
        this.cpu.useSettings(settings);
    }

    public AutonomousSettings getSettings() {
        return this.cpu.getSettings();
    }
}
