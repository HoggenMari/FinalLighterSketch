import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
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
					2), -3));
		}

		//System.out.println(ppos);
		//System.out.println(pos);

		if (ppos.x == pos.x && ppos.y == pos.y) {
			if(ADD_MAX_FLAME_PARTICLE < 5) {
			ADD_MAX_FLAME_PARTICLE += 1;
			}
			//System.out.println("TRUE");
			for (int i = 0; i < ADD_MAX_FLAME_PARTICLE; i++) {
				flameParticles.add(new FlameParticle(p, pos.x, pos.y, p.random(
						-4, 4), -6));

			}
		}

		//System.out.println(flameParticles.size());
	}

	public void draw(PGraphics pg) {
		//System.out.println("DRAW FLAME");
		// pg.colorMode(PConstants.RGB);

		pg.beginDraw();
		pg.noStroke();
		pg.fill(0, 40);
		pg.rect(0, 0, p.width, p.height);

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
