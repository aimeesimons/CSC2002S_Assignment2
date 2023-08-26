//M. M. Kuttel 2023 mkuttel@gmail.com
//Grid for the club

package clubSimulation;

import java.util.concurrent.atomic.AtomicBoolean;

//This class represents the club as a grid of GridBlocks
public class ClubGrid {
	private GridBlock[][] Blocks;
	private final int x;
	private final int y;
	public final int bar_y;

	private GridBlock exit;
	private GridBlock entrance; // hard coded entrance
	private GridBlock bar;
	private final static int minX = 5;// minimum x dimension
	private final static int minY = 5;// minimum y dimension

	private PeopleCounter counter;

	ClubGrid(int x, int y, int[] exitBlocks, PeopleCounter c) throws InterruptedException {
		if (x < minX)
			x = minX; // minimum x
		if (y < minY)
			y = minY; // minimum x
		this.x = x;
		this.y = y;
		this.bar_y = y - 3;
		Blocks = new GridBlock[x][y];
		this.initGrid(exitBlocks);
		entrance = Blocks[getMaxX() / 2][0];
		bar = Blocks[getMaxX() - 1][getBar_y() + 1];
		counter = c;
	}

	// initialise the grsi, creating all the GridBlocks
	private void initGrid(int[] exitBlocks) throws InterruptedException {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				boolean exit_block = false;
				boolean bar = false;
				boolean dance_block = false;
				if ((i == exitBlocks[0]) && (j == exitBlocks[1])) {
					exit_block = true;
				} else if (j >= (y - 3))
					bar = true;
				else if ((i > x / 2) && (j > 3) && (j < (y - 5)))
					dance_block = true;
				// bar is hardcoded two rows before the end of the club
				Blocks[i][j] = new GridBlock(i, j, exit_block, bar, dance_block);
				if (exit_block) {
					this.exit = Blocks[i][j];
				}
			}
		}
	}

	public synchronized int getMaxX() {
		return x;
	}

	public synchronized int getMaxY() {
		return y;
	}

	public synchronized GridBlock whereEntrance() {
		return entrance;
	}

	public synchronized boolean inGrid(int i, int j) {
		if ((i >= x) || (j >= y) || (i < 0) || (j < 0))
			return false;
		return true;
	}

	public synchronized boolean inPatronArea(int i, int j) {
		if ((i >= x) || (j > bar_y) || (i < 0) || (j < 0))
			return false;
		return true;
	}

	public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException {

		synchronized (counter) {
			counter.personArrived(); // add to counter of people waiting
			try {
				while (counter.overCapacity())// while the club is overcapacity
					counter.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (entrance) {
				while (!entrance.get(myLocation.getID())) {// while the entrance block is occupied
					entrance.wait();
				}
			}
			// entrance.get(myLocation.getID());
			counter.personEntered(); // add to counter
			myLocation.setLocation(entrance);
			myLocation.setInRoom(true);

		}
		return entrance;

	}

	/**
	 * This function sets the initial postition of the barman
	 * 
	 * @param myLocation the PeopleLocation variable of the barman
	 * @return the barman initial postition
	 * @throws InterruptedException
	 */

	public synchronized GridBlock startBar(PeopleLocation myLocation) throws InterruptedException {
		myLocation.setLocation(bar);
		myLocation.setInRoom(true);
		return bar;

	}

	public GridBlock move(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
			throws InterruptedException { // try to move in
		synchronized (entrance) {
			if (!entrance.occupied()) {
				entrance.notifyAll();
			}
		}

		int c_x = currentBlock.getX();
		int c_y = currentBlock.getY();

		int new_x = c_x + step_x; // new block x coordinates
		int new_y = c_y + step_y; // new block y coordinates

		// restrict i an j to grid
		if (!inPatronArea(new_x, new_y)) {
			// Invalid move to outside - ignore
			return currentBlock;
		}

		if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY())) // not actually moving
			return currentBlock;

		GridBlock newBlock = Blocks[new_x][new_y];

		if (!newBlock.get(myLocation.getID()))
			return currentBlock; // stay where you are

		currentBlock.release(); // must release current block
		myLocation.setLocation(newBlock);
		return newBlock;
	}

	/**
	 * This method coordinates the movement of the barman. As he is not within the
	 * patron area, this separate function which provides similar code to the move()
	 * function was created
	 * 
	 * @param currentBlock the current block of the barman
	 * @param step_x       the number of steps to move -> either -1 or 1
	 * @param step_y       the y movement -> alwasy equal to zero
	 * @param myLocation   the PeopleLocation variable of the barman
	 * @return the next block to move to
	 * @throws InterruptedException
	 */
	public GridBlock move_Barman(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
			throws InterruptedException {

		int c_x = currentBlock.getX();
		int c_y = currentBlock.getY();

		int new_x = c_x + step_x; // new block x coordinates
		int new_y = c_y + step_y; // new block y coordinates
		if (new_x <= -1 || new_x >= 20) {
			return currentBlock;
		}

		if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY())) // not actually moving
			return currentBlock;

		GridBlock newBlock = Blocks[new_x][new_y];

		if (!newBlock.get(myLocation.getID()))
			return currentBlock; // stay where you are

		currentBlock.release(); // must release current block
		myLocation.setLocation(newBlock);
		return newBlock;
	}

	public void leaveClub(GridBlock currentBlock, PeopleLocation myLocation) {
		synchronized (counter) {
			currentBlock.release();
			counter.personLeft(); // add to counter
			myLocation.setInRoom(false);
			// entrance.notifyAll();
			counter.notifyAll();// notify all patron threads to attempt to enter the club
		}

	}

	public synchronized GridBlock getExit() {
		return exit;
	}

	public GridBlock whichBlock(int xPos, int yPos) {
		if (inGrid(xPos, yPos)) {
			return Blocks[xPos][yPos];
		}
		System.out.println("block " + xPos + " " + yPos + "  not found");
		return null;
	}

	public void setExit(GridBlock exit) {
		this.exit = exit;
	}

	public int getBar_y() {
		return bar_y;
	}

	public synchronized PeopleCounter getCounter() {
		return counter;
	}

	public synchronized GridBlock[][] getBlocks() {
		return Blocks;
	}

}
