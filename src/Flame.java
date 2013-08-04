import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;


public class Flame {
	
	private PApplet p;
	private PVector pos;
	int MAX_FLAME_PARTICLES = 100;
	private ArrayList<FlameParticle> flameParticles;

	public Flame(PApplet p) {
		this.p = p;
		
		flameParticles = new ArrayList<FlameParticle>();
	}
	
	public void update(PVector pos) {
		this.pos = pos;
				
		for(int i=0; i<MAX_FLAME_PARTICLES; i++) {
			flameParticles.add(new FlameParticle(p, pos.x, pos.y));
		}
	}
	
	public void draw(PGraphics pg) {
		System.out.println("DRAW FLAME");
		pg.beginDraw();
		pg.noStroke();
		pg.colorMode(PConstants.HSB);
		pg.fill(0, 40);
		pg.rect(0, 0, p.width, p.height);
		for(FlameParticle fp : flameParticles) {
			fp.draw(pg);
		}
		pg.endDraw();

	}

}
