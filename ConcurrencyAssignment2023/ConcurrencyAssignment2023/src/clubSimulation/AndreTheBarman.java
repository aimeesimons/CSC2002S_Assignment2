package clubSimulation;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents Andre the Barman, who serves the patrons their drinks
 */
public class AndreTheBarman extends Thread {
    private boolean running = false;
    GridBlock currentBlock = new GridBlock();
    public PeopleLocation myLocation;
    public int movingSpeed = 0;
    public int countSteps = 0;
    Random rand = new Random();
    int x_mv = -1;
    public static ClubGrid club; // shared club
    public static AtomicBoolean paused;
    public static CountDownLatch countDownLatch;

    /**
     * This is the constructor for Andre the Barman, who serves the patrons drinks
     * 
     * @param loc            the location variable for the barman
     * @param speed          the speed at which the barman moves
     * @param club           the ClubGrid variable shared by all the patrons
     * @param paused         the AtomicBoolean variable which checks whether the
     *                       simulation is paused
     * @param countDownLatch The CountDownLatch which enables the simulation to
     *                       start
     * @throws InterruptedException
     */
    AndreTheBarman(PeopleLocation loc, int speed, ClubGrid club, AtomicBoolean paused,
            CountDownLatch countDownLatch)
            throws InterruptedException {
        this.myLocation = loc;
        this.movingSpeed = speed;
        this.club = club;
        this.paused = paused;
        this.countDownLatch = countDownLatch;
    }

    /**
     * This method checks whether there is a patron in the block just above the
     * barman
     * 
     * @return true or false depending on whether the patron is in the block above
     *         the barman.
     * @throws InterruptedException
     */
    public synchronized boolean patronInBlock() throws InterruptedException {

        if (club.whichBlock(currentBlock.getX(), club.getBar_y()).occupied()) {
            return true;
        }
        return false;

    }

    /**
     * This method calls the startBar method and initialises the barman's initial
     * position
     * 
     * @throws InterruptedException
     */
    public void Working() throws InterruptedException {
        currentBlock = club.startBar(myLocation);
        sleep(movingSpeed / 2);
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
                if (club.getCounter().getLeft() == myLocation.getID()) {// if all the patrons have come and gone
                    running = false;// stop working
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Bar is empty.");// all patrons have left

    }

    /**
     * This function is responsible for the movement of the barman
     */
    private void moveAcross() throws InterruptedException {
        synchronized (currentBlock) {
            if (currentBlock.getX() + 1 >= club.getMaxX()) {// if the barman has reached the limit of the grid, change
                                                            // direction
                x_mv = -1;
            } else if (currentBlock.getX() - 1 <= -1) {// if the barman has reached the linit of the grid, change
                                                       // direction
                x_mv = 1;
            }
            if (patronInBlock()) {// if there is a patron in the block
                Thread.sleep(1000);// serve them
            }
            currentBlock = club.move_Barman(currentBlock, x_mv, 0, myLocation);
            sleep(movingSpeed);
        }
    }

    private synchronized void checksPause() {
        synchronized (paused) {
            try {
                while (paused.get()) {
                    paused.wait();// waiting while paused is true
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSim() {
        synchronized (countDownLatch) {
            try {
                countDownLatch.await();// waiting until the latch has a value of zero.
                running = true;// conditon variable for the while loop
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
