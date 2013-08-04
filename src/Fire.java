import java.awt.Color;

import processing.core.PGraphics;

public class Fire {
	float x;
	float y;
	float vx;
	float vy;

	Color col;

	float lifetime;
	final float G = (float) 0.04;

	public Fire(float x, float y, float vx, float vy, Color col, float lifetime) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.col = col;
		this.lifetime = lifetime;
	}

	public void draw(PGraphics pg) {

		vx += 0;
		vy += G;

		x += vx;
		y += vy;

		if (lifetime - 50 > 0) {
			pg.noStroke();
			pg.fill(col.getRed(), col.getGreen(), col.getBlue(), lifetime - 50);
			pg.ellipse(x, y, 4, 4); // draw the fire
			lifetime -= 0.5; // decrease lifetime
		}
	}
}