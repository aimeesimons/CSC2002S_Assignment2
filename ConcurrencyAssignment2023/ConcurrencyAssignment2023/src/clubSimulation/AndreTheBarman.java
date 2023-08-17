package clubSimulation;

import java.util.Random;

public class AndreTheBarman extends Thread {
    public static ClubGrid club; // shared club
    GridBlock currentBlock;
    private Random rand;
    private int movingSpeed;

    private PeopleLocation myLocation;

    AndreTheBarman(PeopleLocation loc, int speed) {
        this.movingSpeed = speed;
        this.myLocation = loc;
        rand = new Random();
    }

}
