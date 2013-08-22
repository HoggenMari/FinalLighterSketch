import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.serial.*;
import processing.video.*;


public class ProcessingMain extends PApplet {

	/*
	 * Parameter Settings Start
	 */

	static final boolean SERIAL = true;
	static final boolean SCREEN = true;
	static final boolean GAME = false;
	static final String ARDUINO_DEVICE = "/dev/tty.usbmodem1d1211";

	/*
	 * Parameter Settings End
	 */

	private static final long serialVersionUID = 1L;
	static int lf = 10; // Linefeed in ASCII
	String myString = null; // Serial Output String
	Serial myPort; // Serial port you are using

	private LEDScreen ledScreen1;
	private LEDWall ledWall;

	private ArrayList<Lighter> lighterList;
	private ArrayList<Firework> firework;
	private ArrayList<Flame> flames;

	private PGraphics pg;

	Flame mouseFlame;
	private Game game;
	private Capture cam;
	private LEDScreen ledScreen2;
	private LEDScreen ledScreen3;

	public void setup() {

		pg = createGraphics(320, 240);
		pg.colorMode(HSB);

		// LEDScreen1 initialisieren
		ledScreen1 = new LEDScreen(32, 24, this);
		ledScreen2 = new LEDScreen(32, 24, this);
		//ledScreen3 = new LEDScreen(32, 24, this);


		// LEDWall initialisieren
		ledWall = new LEDWall(this);
		ledWall.add(ledScreen1);
		//ledWall.add(ledScreen2, 1);
		//ledWall.add(ledScreen3, 1);
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
				System.out.println("Device " + i + " " + Serial.list()[i]);
			}
			
			try {
				myPort = new Serial(this, Serial.list()[4], 9600);
				myPort.clear();
			} catch (Exception e) {
				System.out.println("Serial konnte nicht initialisiert werden");
			}
		}

		// Flame with mouse for testing
		mouseFlame = new Flame(this, 100);

		// Game initialisieren
		if (GAME) {
			game = new Game(this, pg, 10.0);
		}

		// GUI
		size(200, 400);
		background(255);
		
		setupCam();

	}
	
	public void setupCam() {
		String[] cameras = Capture.list();
		  
		  if (cameras.length == 0) {
		    println("There are no cameras available for capture.");
		    exit();
		  } else {
		    println("Available cameras:");
		    for (int i = 0; i < cameras.length; i++) {
		      println(cameras[i]);
		    }
		    
		    // The camera can be initialized directly using an 
		    // element from the array returned by list():
		    cam = new Capture(this, cameras[0]);
		    cam.start();     
		  }
	}
	
	PImage drawCam() {
		  PImage img = new PImage();
		  if (cam.available() == true) {
		    cam.read();
		  }
		  img = cam.get();
		  // The following does the same, and is faster when just drawing the image
		  // without any additional resizing, transformations, or tint.
		  //set(0, 0, cam);
		  return img;
		}

	public void draw() {

		background(255);

		//PImage img1 = drawFirework();
		//PImage img1 = drawFirework();
		//PImage img2 = drawFirework();
		PImage img1 = drawCam();
		//PImage img1 = loadImage("/Users/mariushoggenmuller/Documents/test.png");
		//PImage img2 = loadImage("/Users/mariushoggenmuller/Documents/test2.png");
		//PImage img3 = loadImage("/Users/mariushoggenmuller/Documents/test.png");
		//img1 = rotate(img1);
		image(img1, 5, 5);
		//image(img2, 5, 40);

		// Ausgabe fŸr LEDScreen
		if (SCREEN) {
			try {
			img1.resize(32, 24);
			//img2.resize(32, 12);
			ledScreen1.update(img1);
			//ledScreen2.update(img2);
			//ledScreen3.update(img3);
			ledScreen1.drawOnGui(250, 5);
			//ledScreen2.drawOnGui(250, 200);
			//ledScreen3.drawOnGui(250, 400);
			ledWall.sendDMX();
			} catch (Exception e) {
				System.out.println(e);
			}
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

			if (lg.getLighterState().toString() == "ACTIVE"
					|| lg.getLighterState().toString() == "LOST") {
				for (Firework fw : firework) {
					if (fw.getId() == lg.getLighterID()) {
						fwWithID = true;
						System.out.println("WARTE");
					}
				}
				// System.out.println("MAIN - ACTIVE");
				if (!fwWithID) {
					firework.add(new Firework(this, pg, lg.getInitPos(),
							new Color((int) random(0, 255), 255, 255), lg
									.getLighterID()));
					System.out.println("GO");
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

		// Draw tokens
		if (GAME) {
			game.update(lighterList);
			game.draw();
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
						// System.out.println("!!!!!LOST!!!!!");
						fl.kill(lg.getLostPos(), lg.getLostCounter());
						fl.draw(pg);
					}
				}
			} else if (lg.getLighterState().toString() == "IDLE") {
				for (Iterator<Flame> flameItr = flames.iterator(); flameItr
						.hasNext();) {
					Flame fl = flameItr.next();
					if (lg.getLighterID() == fl.getFlameID()) {
						// System.out.println("!!!!!REMOVE!!!!!");
						flameItr.remove();
					}
				}
			}
		}

		// Mouse Flame
		mouseFlame.update(new PVector(mouseX, mouseY));

		// Draw tokens
		if (GAME) {
			game.update(lighterList);
			game.draw();
		}

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
		try {
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
		} catch (Exception e) {
			println("Initialization exception");
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

	PImage rotate(PImage img) {
		for (int ix = 0; ix < img.width; ix++) {
			for (int iy = 0; iy < img.height; iy++) {
				img.set(ix, iy, img.get(iy, ix));
			}
		}
		return img;
	}

}
