package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class AndreTheBarman extends Clubgoer implements Runnable {
    private boolean running = false;
    private GridBlock barWork;
    GridBlock currentBlock = new GridBlock();

    AndreTheBarman(PeopleLocation loc, int speed) throws InterruptedException {
        super(Integer.MAX_VALUE, loc, speed);

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
        try {
            moveAcross();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void moveAcross() throws InterruptedException {
        currentBlock = club.move(currentBlock, 1, 0, this.getLocation());
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
                currentBlock = new GridBlock(club.getMaxX() + 1, club.bar_y + 1, false,
                        true, false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
