package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndreTheBarman extends Clubgoer implements Runnable {
    private boolean running = false;
    private GridBlock barWork;
    GridBlock currentBlock = new GridBlock();
    public PeopleLocation myLocation;
    public int movingSpeed = 0;
    public int countSteps = 0;
    Random rand = new Random();
    public static AtomicBoolean serveDrink;
    int x_mv = -1;

    AndreTheBarman(PeopleLocation loc, int speed) throws InterruptedException {
        super(Integer.MAX_VALUE, loc, speed);
        this.myLocation = loc;
        this.movingSpeed = speed;

    }

    public synchronized boolean patronInBlock() throws InterruptedException {

        if (club.whichBlock(currentBlock.getX() - 1, club.getBar_y()).occupied()) {
            serveDrink.set(true);
            return true;
        }
        return false;

    }

    public void Working() throws InterruptedException {
        currentBlock = club.startBar(myLocation); // enter through entrance
        sleep(movingSpeed / 2); // wait a bit at door
    }

    public void run() {
        startSim();
        checksPause();
        try {
            Working();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (running) {
            try {
                moveAcross();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private synchronized void moveAcross() throws InterruptedException {
        synchronized (currentBlock) {
            synchronized (serveDrink) {
                if (currentBlock.getX() + 1 >= 20) {
                    x_mv = -1;
                } else if (currentBlock.getX() - 1 <= 0) {
                    x_mv = 1;
                }
                if (patronInBlock()) {
                    wait(1000);
                    serveDrink.notify();
                }
                currentBlock = club.move_Barman(currentBlock, x_mv, 0, myLocation);
                sleep(movingSpeed);
            }
        }

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
