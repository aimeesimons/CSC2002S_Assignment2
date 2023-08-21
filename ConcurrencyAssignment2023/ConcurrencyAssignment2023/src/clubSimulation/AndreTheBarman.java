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
    public static AtomicBoolean served;
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

        if (club.whichBlock(currentBlock.getX() - 1, club.getBar_y()).occupied()) {
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
                checksPause();
                served.set(false);
                moveAcross();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private synchronized void moveAcross() throws InterruptedException {
        synchronized (currentBlock) {
            if (currentBlock.getX() + 1 >= club.getMaxX()) {
                currentBlock = club.move_Barman(currentBlock, 1, 0, myLocation);
                x_mv = -1;
            } else if (currentBlock.getX() - 1 <= 0) {
                x_mv = 1;
            }
            if (patronInBlock()) {
                Thread.sleep(1000);
                synchronized (served) {
                    served.set(true);
                    served.notify();
                }
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
