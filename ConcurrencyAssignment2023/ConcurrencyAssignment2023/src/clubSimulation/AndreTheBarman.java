package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndreTheBarman extends Thread {
    private boolean running = false;
    private GridBlock barWork;
    GridBlock currentBlock = new GridBlock();
    public PeopleLocation myLocation;
    public int movingSpeed = 0;
    public int countSteps = 0;
    Random rand = new Random();
    public AtomicBoolean served;
    int x_mv = -1;
    public static ClubGrid club; // shared club
    public static AtomicBoolean paused;
    public static CountDownLatch countDownLatch;

    AndreTheBarman(PeopleLocation loc, int speed, AtomicBoolean served, ClubGrid club, AtomicBoolean paused,
            CountDownLatch countDownLatch)
            throws InterruptedException {
        this.myLocation = loc;
        this.movingSpeed = speed;
        this.served = served;
        this.club = club;
        this.paused = paused;
        this.countDownLatch = countDownLatch;
    }

    public synchronized boolean patronInBlock() throws InterruptedException {

        if (club.whichBlock(currentBlock.getX(), club.getBar_y()).occupied()) {
            return true;
        }
        return false;

    }

    public void Working() throws InterruptedException {
        currentBlock = club.startBar(myLocation); //
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
                checksPause();
                moveAcross();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void moveAcross() throws InterruptedException {
        synchronized (currentBlock) {
            if (currentBlock.getX() + 1 >= club.getMaxX()) {
                x_mv = -1;
            } else if (currentBlock.getX() - 1 <= -1) {
                x_mv = 1;
            }
            if (patronInBlock()) {
                Thread.sleep(1000);
            }
            currentBlock = club.move_Barman(currentBlock, x_mv, 0, myLocation);
            sleep(movingSpeed);
        }
    }

    private synchronized void checksPause() {
        synchronized (paused) {
            try {
                while (paused.get()) {
                    paused.wait();
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
