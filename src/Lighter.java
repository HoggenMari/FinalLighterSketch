import java.util.Observable;


public class Lighter {
	
	private int lighterID;
	private int posX, posY;
	private boolean active;


	public Lighter(int lighterID){
		this.lighterID = lighterID;
		this.active = false;
	}
	
	public void setPosition(int posX, int posY){
		this.posX = posX;
		this.posY = posY;
		if(posX==1023 && posY==1023){
			active = false;
		}else {
			active = true;
		}
	}
	
	@Override
	public String toString() {
		return "Lighter [lighterID=" + lighterID + ", posX=" + posX + ", posY="
				+ posY + "]";
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
	

}
