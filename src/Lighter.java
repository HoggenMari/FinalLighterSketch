import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import processing.core.PVector;


public class Lighter {
	
	private int lighterID;

	static int MAX_X = 1023;
	static int MAX_Y = 1023;
	int lost = 0;

	private PVector pos, ppos, initPos, lostPos;
	private BlockingQueue<PVector> pos_queue;
	public PVector getLostPos() {
		return lostPos;
	}

	private PVector size;

	private State lighterState;
	
	public enum State {
		IDLE, INIT, ACTIVE, LOST
	}


	public Lighter(int lighterID, PVector size){
		this.lighterID = lighterID;
		this.pos = new PVector(1023,1023);
		this.pos_queue = new LinkedBlockingQueue<PVector>(10);
		this.size = size;
		lighterState = State.IDLE;
	}
	
	public void setPosition(PVector pos){
		ppos = this.pos;
		if(pos_queue.remainingCapacity()==0){
			pos_queue.remove();
			pos_queue.add(pos);
		}else pos_queue.add(pos);
		//Save previous Position
		//New position
		pos.x = (pos.x/MAX_X)*size.x;
		pos.y = (pos.y/MAX_Y)*size.y;
		this.pos = pos;
		
		//System.out.println(pos_queue.toString());
		
		if(lighterState == State.IDLE && pos.x != size.x && pos.y != size.y) {
			initPos = pos;
			lighterState = State.INIT;
			System.out.println("FLANKENWECHSEL");
			lost=0;
		}
		else if(lighterState == State.INIT && pos.x != size.x && pos.y != size.y) {
			lostPos = pos;
			lighterState = State.ACTIVE;
			System.out.println("AKTIV");
			lost=0;
		}
		/*else if(lighterState == State.INIT && pos.x == size.x && pos.y == size.y) {
			if (lost<5) {
				lost++;
			} else lighterState = State.IDLE;
		}*/
		else if(lighterState == State.ACTIVE && pos.x == size.x && pos.y == size.y){
			lighterState = State.LOST;
		}
		else if(lighterState == State.LOST && pos.x == size.x && pos.y == size.y){
			if (lost<10) {
				lost++;
			} else { 
				lighterState = State.IDLE;
				System.out.println("REMOVE");
			}
		}
	}
	
	
	@Override
	public String toString() {
		return "Lighter [lighterID=" + lighterID + ", posX=" + pos.x + ", posY="
				+ pos.y + "]";
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



}
