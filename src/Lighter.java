import processing.core.PVector;

public class Lighter {

	private int lighterID;

	static int MAX_X = 1023;
	static int MAX_Y = 1023;
	int lostCounter = 50;

	private PVector pos, ppos, initPos, lostPos;

	private PVector size;

	private State lighterState;

	private int delay;

	public enum State {
		IDLE, INIT, ACTIVE, LOST
	}

	public Lighter(int lighterID, PVector size, int delay) {
		this.lighterID = lighterID;
		this.pos = new PVector(1023, 1023);
		this.size = size;
		lighterState = State.IDLE;
		this.delay = delay;
	}

	public void setPosition(PVector pos) {
		// Save previous Position
		ppos = this.pos;

		// Normalize & Save new position
		pos.x = (pos.x / MAX_X) * size.x;
		pos.y = (pos.y / MAX_Y) * size.y;
		this.pos = pos;

		/*
		 * Change Lighter States
		 */
		// IDLE -> INIT
		if (lighterState == State.IDLE && pos.x != size.x && pos.y != size.y) {
			initPos = pos;
			lighterState = State.INIT;
			System.out.println("FLANKENWECHSEL");
			lostCounter = delay;
			lostPos = pos;
		}
		// ACTIVE -> ACTIVE
		else if (lighterState == State.ACTIVE && pos.x != size.x
				&& pos.y != size.y) {
			lostPos = pos;
			lighterState = State.ACTIVE;
			System.out.println("AKTIV");
			lostCounter = delay;
			System.out.println(lostPos);
		}
		// ACTIVE -> LOST
		else if (lighterState == State.ACTIVE && pos.x == size.x
				&& pos.y == size.y) {
			lighterState = State.LOST;
			System.out.println("LOST");
		}
		// LOST -> IDLE
		else if (lighterState == State.LOST && pos.x == size.x
				&& pos.y == size.y) {
			if (lostCounter > 0) {
				lostCounter--;
			} else {
				lighterState = State.IDLE;
				System.out.println("REMOVE");
			}
		}
		// LOST -> ACTIVE
		else if (lighterState == State.LOST && pos.x != size.x
				&& pos.y != size.y) {
			lighterState = State.ACTIVE;
			lostCounter = delay;
			System.out.println("LOST->ACTIVE");
		}
	}

	@Override
	public String toString() {
		return "Lighter [lighterID=" + lighterID + ", posX=" + pos.x
				+ ", posY=" + pos.y + "]";
	}

	public PVector getPos() {
		return pos;
	}

	public void setPos(PVector pos) {
		this.pos = pos;
	}

	public PVector getPpos() {
		return ppos;
	}

	public void setPpos(PVector ppos) {
		this.ppos = ppos;
	}

	public int getLighterID() {
		return lighterID;
	}

	public PVector getInitPos() {
		return initPos;
	}

	public State getLighterState() {
		return lighterState;
	}

	public void setLighterState(State lighterState) {
		this.lighterState = lighterState;
	}

	public PVector getLostPos() {
		return lostPos;
	}

	public int getLostCounter() {
		return lostCounter;
	}

}
