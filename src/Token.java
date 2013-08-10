import java.awt.Color;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;


public class Token {

	private PApplet p;
	private PVector pos;
	private Color c = new Color(255,255,255);

	public Token(PApplet p, PVector pos) {
		this.p = p;
		this.pos = pos;
	}
	
	public void update(int xs) {
		this.pos.y = pos.y + xs;
	}
	
	public void draw(PGraphics pg) {
		pg.fill(0, 40);
		pg.rect(0, 0, p.width, p.height);
		pg.fill(c.getRGB());
		pg.rect(pos.x, pos.y, 80, 80);
	}

	public PVector getPos() {
		return pos;
	}

	public void setPos(PVector pos) {
		this.pos = pos;
	}
}
