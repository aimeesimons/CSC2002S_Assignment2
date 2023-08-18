package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class AndreTheBarman extends Clubgoer implements Runnable {
    private boolean running = false;
    private GridBlock barWork;

    AndreTheBarman(PeopleLocation loc, int speed) throws InterruptedException {
        super(ClubSimulation.noClubgoers, loc, speed);
        barWork = getLocation().getLocation();

    }

    // public void serveDrink() throws InterruptedException {
    // while (patronInBlock()) {
    // wait(1000);
    // }

    // }

    // public boolean patronFound() {

    // return true;
    // }

    public boolean patronInBlock() {

        return true;
    }

    public void run() {
        startSim();
        checksPause();
        while (running) {
            checksPause();
            try {
                moveAcross();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private synchronized void moveAcross() throws InterruptedException {
        currentBlock = club.move(currentBlock, -1, 0, this.getLocation());
        sleep(this.getSpeed());
    }

    private synchronized void checksPause() {
        synchronized (paused) {
            try {
                while (paused.get()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void startSim() {
        synchronized (countDownLatch) {
            try {
                countDownLatch.await();
                running = true;
                // currentBlock = club.(this.getLocation());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
