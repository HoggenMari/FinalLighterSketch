import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import processing.core.PVector;


public class Lighter {
	
	private int lighterID;

	private boolean active, init;
	static int MAX_X = 1023;
	static int MAX_Y = 1023;

	private PVector pos, ppos, initPos;
	private BlockingQueue<PVector> pos_queue;
	private PVector size;


	public Lighter(int lighterID, PVector size){
		this.lighterID = lighterID;
		this.pos = new PVector(1023,1023);
		this.active = false;
		this.init = false;
		this.pos_queue = new LinkedBlockingQueue<PVector>(10);
		this.size = size;

	}
	
	public void setPosition(PVector pos){
		ppos = this.pos;
		if(pos_queue.remainingCapacity()==0){
			pos_queue.remove();
			pos_queue.add(pos);
		}
		//Save previous Position
		//New position
		pos.x = (pos.x/MAX_X)*size.x;
		pos.y = (pos.y/MAX_Y)*size.y;
		this.pos = pos;
		
		int count=0;
		
		//check last 10 Elements
		if(!pos_queue.isEmpty()) {
			PVector[] lastTen = (PVector[]) pos_queue.toArray();
			for(PVector item : lastTen) {
				if(item.x==1023 && item.y==1023) {
					count++;
				}
			}
		}
		
		//check status
		/*if(pos.x==1023 && pos.y==1023){
			active = false;
		}else {
			while( !pos_queue.isEmpty()){
				if(pos_queue.element().x==1023 && pos_queue.element().y==1023){
					init = true;
				}
			}
			active = true;
		}*/
		//System.out.println(ppos.toString());
		//System.out.println(pos.toString());
		if(ppos.x == size.x && ppos.y == size.y && pos.x != size.x && pos.y != size.y) {
			initPos = pos;
			init = true;
			System.out.println("FLANKENWECHSEL");
		}
		else if(ppos.x != size.x && ppos.y != size.y && pos.x != size.x && pos.y != size.y) {
			init = false;
			active = true;
			System.out.println("AKTIV");
		} else if(count > 6) {
			init = false;
			active = true;
		}
		else {
			init = false;
			active = false;
		}
	}
	
	
	@Override
	public String toString() {
		return "Lighter [lighterID=" + lighterID + ", posX=" + pos.x + ", posY="
				+ pos.y + "]";
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
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

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}
	
	public int getLighterID() {
		return lighterID;
	}

	public PVector getInitPos() {
		return initPos;
	}



}
