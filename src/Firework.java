import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Firework {

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/30877*@* */
	/*
	 * !do not delete the line above, required for linking your tweak if you
	 * re-upload
	 */

	ArrayList<FireworkParticle> hanabi;

	final int FIRE_COUNT = 1000;
	final float X = 200;
	final float Y = 250;

	private PApplet p;
	private int id;
	private PVector pos;

	private float x;
	private float y;

	private float lifetime;

	public Firework(PApplet p, PVector pos, Color c, int id) {
		this.p = p;
		this.id = id;
		this.pos = pos;

		y = p.height;
		x = pos.x;
		
		lifetime = p.random(100, 500);

		hanabi = new ArrayList<FireworkParticle>();

		for (int i = 0; i < FIRE_COUNT; i++) {
			float r = p.random(0, PConstants.TWO_PI);
			float R = p.random(0, 2);

			hanabi.add(new FireworkParticle(pos.x, pos.y, R * PApplet.sin(r), R
					* PApplet.cos(r), c, lifetime));
		}

	}

	public void draw(PGraphics pg) {
		pg.beginDraw();
		pg.noStroke();
		pg.colorMode(PConstants.HSB);
		pg.fill(0, 40);
		pg.rect(0, 0, p.width, p.height);

		//paint tail
		if (y > pos.y) {
			y += -5;
			x += p.random(-1, 1);
			pg.noStroke();
			pg.fill(0, 0, 255);
			pg.pushMatrix();
			pg.translate(x, y);
			pg.ellipse(0, 0, 10, 15);
			pg.popMatrix();
		} else {
			//paint "flower"
			for (FireworkParticle fire : hanabi) {
				fire.draw(pg);
			}
		}

		pg.endDraw();

		PImage img = pg.get();
		p.image(img, 0, 0);
	}

	public boolean isDead() {
		for (FireworkParticle fire : hanabi) {
			if (fire.lifetime - 50 > 0) {
				return false;
			}
		}
		return true;
	}

	public int getId() {
		return id;
	}

}
