import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Game {

	static final int MAX_TOKEN = 1;

	ArrayList<Token> tokens = new ArrayList<Token>();

	private PApplet p;

	private PGraphics pg;

	private double percent;

	public Game(PApplet p, PGraphics pg, double percent) {
		this.p = p;
		this.pg = pg;
		this.percent = percent;
	}

	public void update(ArrayList<Lighter> lighterList) {

		for (int i = tokens.size(); i < MAX_TOKEN; i++) {
			PVector pos = new PVector(p.random(0, pg.width), p.random(0, -20));
			tokens.add(new Token(p, pos));
		}

		for (Token tk : tokens) {
			for (Lighter lg : lighterList) {
				if (lg.getLighterState().toString() != "IDLE" && lg.getLighterState().toString() != "LOST") {
					if (lg.getPos() == tk.getPos()) {
						System.out.println("Collision");
					} else {
						System.out.println("LG: " + lg.getPos());
						System.out.println("TK: " + tk.getPos());
						System.out.println(distance(lg.getPos(), tk.getPos()));
					}
				} else tk.update(0);
			}
		}

	}

	public void draw() {

		pg.beginDraw();

		for (Iterator<Token> tokenItr = tokens.iterator(); tokenItr.hasNext();) {
			Token token = tokenItr.next();
			if (token.getPos().y > pg.height) {
				tokenItr.remove();
			} else {
				token.draw(pg);
			}
		}

		for (Token tk : tokens) {
			tk.draw(pg);
		}

		pg.endDraw();

	}

	private double distance(PVector p1, PVector p2) {
		double dist = Math.sqrt((p1.x * p2.x) * (p1.x * p2.x) + (p1.y * p2.y)
				* (p1.y * p2.y));
		return dist;
	}
}
