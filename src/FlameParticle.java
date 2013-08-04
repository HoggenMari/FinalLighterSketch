import processing.core.PApplet;
import processing.core.PGraphics;


public class FlameParticle {
	
	private float x;
	private float y;
	private PApplet p;

	public FlameParticle(PApplet p, float x, float y) {
		this.p = p;
		this.x = x;
		this.y = y;
	}

	public void draw(PGraphics pg) {
		if (y > 0) {
			System.out.println("DRAW PARTICLE");
			y += p.random(-1, -2);
			y *= p.random(0,1);
			x += p.random(-5, 5);
			pg.noStroke();
			pg.fill(255, 255, 255);
			pg.pushMatrix();
			pg.translate(x, y);
			pg.ellipse(0, 0, 3, 15);
			pg.popMatrix();
		}		
	}

}
