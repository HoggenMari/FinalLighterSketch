import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Flame {

	private PApplet p;
	private PVector pos;
	int MAX_FLAME_PARTICLES = 15;
	private ArrayList<FlameParticle> flameParticles;
	private PVector ppos;
	private int ADD_MAX_FLAME_PARTICLE = 0;
	private int flameID;

	public Flame(PApplet p, int flameID) {
		this.p = p;
		this.pos = new PVector();
		this.ppos = new PVector();
		this.flameID = flameID;

		flameParticles = new ArrayList<FlameParticle>();
	}

	public void update(PVector pos) {
		this.ppos = this.pos;
		this.pos = pos;

		for (int i = 0; i < MAX_FLAME_PARTICLES; i++) {
			flameParticles.add(new FlameParticle(p, pos.x, pos.y, p.random(-2,
					2), -3, 255));
		}

		// System.out.println(ppos);
		// System.out.println(pos);

		if (ppos.x == pos.x && ppos.y == pos.y) {
			if (ADD_MAX_FLAME_PARTICLE < 5) {
				ADD_MAX_FLAME_PARTICLE += 1;
			}
			// System.out.println("TRUE");
			for (int i = 0; i < ADD_MAX_FLAME_PARTICLE; i++) {
				flameParticles.add(new FlameParticle(p, pos.x, pos.y, p.random(
						-4, 4), -6, 255));

			}
		}

		// System.out.println(flameParticles.size());
	}

	public void kill(PVector pos, int intens) {
		for (int i = 0; i < intens - 2; i++) {
			flameParticles.add(new FlameParticle(p, pos.x, pos.y, p.random(-2,
					2), -3, 100));
		}
		/*
		 * for(FlameParticle flPart : flameParticles) { flPart.decAlpha(100); }
		 */
	}

	public void draw(PGraphics pg, PImage bg) {
		// System.out.println("DRAW FLAME");
		// pg.colorMode(PConstants.RGB);

		pg.beginDraw();
		pg.noStroke();
		//pg.fill(0, 120, 120);
		//PImage bg = p.loadImage("/Users/mariushoggenmuller/Documents/bg.png");
		pg.set(0, 0, bg);
		//pg.rect(0, 0, p.width, p.height);

		for (Iterator<FlameParticle> flamePartItr = flameParticles.iterator(); flamePartItr
				.hasNext();) {
			FlameParticle fl = flamePartItr.next();
			if (fl.isDead()) {
				flamePartItr.remove();
			} else {
				fl.update();
				fl.draw(pg);
			}
		}

		for (FlameParticle fp : flameParticles) {
			fp.update();
			fp.draw(pg);
		}
		pg.endDraw();

	}

	public int getFlameID() {
		return flameID;
	}

}
