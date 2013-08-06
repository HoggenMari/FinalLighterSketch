import java.awt.Color;

import processing.core.PGraphics;

public class FireworkParticle {
	float x;
	float y;
	float vx;
	float vy;

	Color col;

	float lifetime;
	final float G = (float) 0.04;
	private float size;
	private PGraphics pg;

	public FireworkParticle(PGraphics pg, float x, float y, float vx, float vy, Color col, float lifetime, float size) {
		this.pg = pg;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.col = col;
		this.lifetime = lifetime;
		this.size = size;
	}

	public void draw() {

		vx += 0;
		vy += G;

		x += vx;
		y += vy;

		if (lifetime - 50 > 0) {
			pg.noStroke();
			pg.fill(col.getRed(), col.getGreen(), col.getBlue(), lifetime - 50);
			pg.ellipse(x, y, size, size); // draw the fire
			lifetime -= 0.5; // decrease lifetime
		}
	}
	
	public boolean isDead(){
		if (y<pg.height){
			return false;
		} else return true;
	}
}