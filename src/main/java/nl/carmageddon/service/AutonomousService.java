package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class AutonomousService {
    private CPU cpu;

    @Inject
    public AutonomousService(Car car) {
        this.cpu = new CPU(car);
    }

    public boolean startRace() {
        if (!this.cpu.isRacing()) {
            new Thread(this.cpu).start();
            return true;
        }
        return false;
    }

    public AutonomousStatus getStatus() {
        return this.cpu.getStatus();
    }

    public void addObserver(Observer observer) {
        this.cpu.addObserver(observer);
    }

    public void stopRace() {
        this.cpu.racing = false;
    }

    private class CPU extends Observable implements Runnable {
        private Car car;
        private boolean racing;
        private String lastMsg = "Ready to race.";
        private boolean finished = false;

        public CPU(Car car) {
            this.car = car;
        }

        @Override
        public void run() {
            racing = true;
            while(racing && !finished) {
                notifyClients("Looking for traffic light.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!racing && !finished) {
                notifyClients("Car stopped.");
            }
        }

        private void notifyClients(String msg) {
            this.lastMsg = msg;
            setChanged();
            notifyObservers(new AutonomousStatus(car.isConnected(), racing, msg));
        }

        public AutonomousStatus getStatus() {
            return new AutonomousStatus(car.isConnected(), racing, lastMsg);
        }

        public boolean isRacing() {
            return racing;
        }
    }
}
