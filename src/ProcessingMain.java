import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.serial.*;

public class ProcessingMain extends PApplet {

	/*
	 * Parameter Settings Start
	 */

	static final boolean SERIAL = false;
	static final boolean SCREEN = false;
	static final String ARDUINO_DEVICE = "/dev/tty.usbmodem1a1211";

	/*
	 * Parameter Settings End
	 */

	private static final long serialVersionUID = 1L;
	static int lf = 10; // Linefeed in ASCII
	String myString = null; //Serial Output String
	Serial myPort; // Serial port you are using
	
	private LEDScreen ledScreen1;
	private LEDWall ledWall;
	
	private ArrayList<Lighter> lighterList;
	private ArrayList<Firework> firework;
	private ArrayList<Flame> flames;

	private PGraphics pg;

	Flame mouseFlame;

	public void setup() {

		pg = createGraphics(200, 400);
		pg.colorMode(HSB);

		// LEDScreen1 initialisieren
		ledScreen1 = new LEDScreen(10, 24, this);

		// LEDWall initialisieren
		ledWall = new LEDWall(this);
		ledWall.add(ledScreen1);
		ledWall.init();

		// LighterList initialisieren
		lighterList = new ArrayList<Lighter>();
		for (int i = 0; i < 4; i++) {
			lighterList
					.add(new Lighter(i, new PVector(pg.width, pg.height), 15));
		}

		// Firework initialisiren
		firework = new ArrayList<Firework>();

		// Fire initialisieren
		flames = new ArrayList<Flame>();

		// Serial Arduino initialisieren
		if (SERIAL) {
			
			  for (int i = 0; i < Serial.list().length; i++) {
			  System.out.println("Device " + i + " " + Serial.list()[i]); }
			 
			myPort = new Serial(this, ARDUINO_DEVICE, 9600);
			myPort.clear();
		}
		
		//Flame with mouse for testing
		mouseFlame = new Flame(this, 100);

		// GUI
		size(200, 400);
		background(0);

	}

	public void draw() {

		background(0);

		//PImage img1 = drawFirework();
		PImage img1 = drawFlame();
		image(img1, 0, 0);

		// Ausgabe fŸr LEDScreen
		if (SCREEN) {
			img1.resize(10, 24);
			ledScreen1.update(img1);
			ledScreen1.drawOnGui(210, 5);
			ledWall.sendDMX();
		}

	}

	public PImage drawFirework() {

		boolean fwWithID = false;

		for (Lighter lg : lighterList) {

			if (lg.getLighterState().toString() == "INIT") {
				firework.add(new Firework(this, pg, lg.getPos(), new Color(
						(int) random(0, 255), 255, 255), new Color(
						(int) random(0, 255), 255, 255), lg.getLighterID()));
				// System.out.println("MAIN - INIT");
				lg.setLighterState(Lighter.State.ACTIVE);
				System.out.println("LighterID: " + lg.getLighterID());
			}

			if (lg.getLighterState().toString() == "ACTIVE") {
				for (Firework fw : firework) {
					if (fw.getId() == lg.getLighterID()) {
						fwWithID = true;
					}
				}
				// System.out.println("MAIN - ACTIVE");
				if (!fwWithID) {
					firework.add(new Firework(this, pg, lg.getInitPos(),
							new Color((int) random(0, 255), 255, 255), lg
									.getLighterID()));
				}
			}

		}

		for (Iterator<Firework> fireItr = firework.iterator(); fireItr
				.hasNext();) {
			Firework fw = fireItr.next();
			if (fw.isDead()) {
				fireItr.remove();
			} else
				fw.draw();
		}

		PImage img = pg.get();
		return img;
	}

	public PImage drawFlame() {

		// Lighter Flame
		for (Lighter lg : lighterList) {
			if (lg.getLighterState().toString() == "INIT") {
				// System.out.println("!!!!!INIT!!!!!");
				Flame flame = new Flame(this, lg.getLighterID());
				flame.update(lg.getPos());
				flames.add(flame);
				lg.setLighterState(Lighter.State.ACTIVE);
			} else if (lg.getLighterState().toString() == "ACTIVE") {
				// System.out.println("!!!!!ACTIVE!!!!!");
				for (Flame fl : flames) {
					if (fl.getFlameID() == lg.getLighterID()) {
						fl.update(lg.getPos());
						fl.draw(pg);
					}
				}
			} else if (lg.getLighterState().toString() == "LOST") {
				// System.out.println("!!!!!LOST!!!!!");
				for (Flame fl : flames) {
					if (fl.getFlameID() == lg.getLighterID()) {
						// fl.update(lg.getLostPos());
						System.out.println("!!!!!LOST!!!!!");
						fl.kill(lg.getLostPos(), lg.getLostCounter());
						fl.draw(pg);
					}
				}
			} else if (lg.getLighterState().toString() == "IDLE") {
				for (Iterator<Flame> flameItr = flames.iterator(); flameItr
						.hasNext();) {
					Flame fl = flameItr.next();
					if (lg.getLighterID() == fl.getFlameID()) {
						System.out.println("!!!!!REMOVE!!!!!");
						flameItr.remove();
					}
				}
			}
		}

		// Mouse Flame
		mouseFlame.update(new PVector(mouseX, mouseY));

		// Draw all Flames
		for (Iterator<Flame> flameItr = flames.iterator(); flameItr.hasNext();) {
			Flame fl = flameItr.next();
			fl.draw(pg);
		}

		PImage img = pg.get();
		return img;
	}

	public void mousePressed() {

		firework.add(new Firework(this, pg, new PVector(mouseX, mouseY),
				new Color((int) random(0, 255), 255, 255), new Color(
						(int) random(0, 255), 255, 255), 100));

		mouseFlame.update(new PVector(mouseX, mouseY));
		flames.add(mouseFlame);

	}

	// Arduino LighterProtocoll: "posX,posY"-items split with ',' - tuples split
	// with ':' - end of 4 tuples is '\n'
	public void serialEvent(Serial myPort) {
		myString = myPort.readStringUntil(lf);
		if (myString != null) {
			String[] spl1 = split(myString, '\n');
			spl1 = split(spl1[0], ':');

			for (int i = 0; i < spl1.length; i++) {
				String[] spl2 = split(spl1[i], ',');
				if (spl2.length >= 2) {
					int posX = parseWithDefault(spl2[0], 0);
					int posY = parseWithDefault(spl2[1], 0);

					// Set new position of the Lighters
					lighterList.get(i).setPosition(new PVector(posX, posY));
				}
			}
		}
	}

	// Auxiliary function for parsing Arduino Data
	public static int parseWithDefault(String number, int defaultVal) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	// Auxiliary function for Blur-Effect
	void blur(float trans, PGraphics pg) {
		pg.noStroke();
		pg.fill(0, trans);
		pg.rect(0, 0, width, height);
	}

}
