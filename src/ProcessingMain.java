import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.serial.*;

public class ProcessingMain extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int lf = 10; // Linefeed in ASCII
	String myString = null;
	Serial myPort; // Serial port you are using
	int num[];
	private LEDScreen ledScreen1;
	private LEDWall ledWall;
	private ArrayList<Lighter> lighterList;
	private PGraphics pg;
	private ArrayList<Firework> firework;
	private ArrayList<Flame> flames;

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
			lighterList.add(new Lighter(i, new PVector(pg.width, pg.height)));
		}

		// Firework initialisiren
		firework = new ArrayList<Firework>();
		
		// Fire initialisieren
		flames = new ArrayList<Flame>();

		lighterList.get(0).toString();

		for (int i = 0; i < Serial.list().length; i++) {
			System.out.println("Device " + i + " " + Serial.list()[i]);
		}
		myPort = new Serial(this, Serial.list()[4], 9600);
		myPort.clear();
		num = new int[10];

		size(200, 400);

		background(0, 0, 0);

	}

	public void draw() {

		background(0);
		

		
		for (Lighter lg : lighterList) {
			if(lg.isInit()) {
				System.out.println("!!!!!INIT!!!!!");
				Flame flame = new Flame(this, lg.getLighterID());
				flame.update(lg.getPos());
				flames.add(flame);
				lg.setInit(false);
			}
			else if(lg.isActive()) {
				System.out.println("!!!!!ACTIVE!!!!!");
				for(Flame fl : flames) {
					fl.update(lg.getPos());
					fl.draw(pg);
				}
			} else {				
				for (Iterator<Flame> flameItr = flames.iterator(); flameItr
						.hasNext();) {
					Flame fl = flameItr.next();
					if(lg.getLighterID()==fl.getFlameID()) {
						System.out.println("!!!!!REMOVE!!!!!");

						flameItr.remove();
					}
				}
			}
		}

		//Flame verfolgen
		for(Flame fl : flames) {
			fl.update(new PVector(mouseX, mouseY));
		}
		
		//PImage img1 = drawFirework();
		PImage img1 = drawFlame();
		image(img1, 0, 0);

		img1.resize(10, 24);
		ledScreen1.update(img1);
		ledScreen1.drawOnGui(170, 5);
		ledWall.sendDMX();

		/*for(Firework fw :firework) {
			System.out.println(fw.getId());
		}*/
		//System.out.println(firework.size());
		System.out.println(flames.size());

	}

	public PImage drawFirework() {

		boolean fwWithID=false;

		for (Lighter lg : lighterList) {
			System.out.println(lg.toString());
			if (lg.isInit()) {
				firework.add(new Firework(this, pg, lg.getPos(), new Color(
						(int) random(0, 255), 255, 255), new Color(
								(int) random(0, 255), 255, 255), lg.getLighterID()));
				System.out.println(lg.toString() + "INIT");
				lg.setInit(false);
			}
			if (lg.isActive()) {
				for(Firework fw : firework) {
					if(fw.getId()==lg.getLighterID()){
						fwWithID=true;
					}
				}
				if(!fwWithID){
					firework.add(new Firework(this, pg, lg.getInitPos(), new Color(
					(int) random(0, 255), 255, 255), lg.getLighterID()));
					System.out.println(lg.toString() + "REACTIVATED");
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
		for (Iterator<Flame> flameItr = flames.iterator(); flameItr
				.hasNext();) {
			Flame fl = flameItr.next();
			fl.draw(pg);
		}
		PImage img = pg.get();
		return img;
	}

	public void mousePressed() {

		/*firework.add(new Firework(this, pg, new PVector(mouseX, mouseY), new Color(
				(int) random(0, 255), 255, 255), new Color(
						(int) random(0, 255), 255, 255), 100));*/

		
		Flame flame = new Flame(this, 100);
		flame.update(new PVector(mouseX, mouseY));
		flames.add(flame);

	}

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
					lighterList.get(i).setPosition(new PVector(posX, posY));
				}
				// System.out.println(lighterList.get(i).toString());
				// float num = float(list[i]);
				// num[i]=Integer.parseInt(list[i]); // Converts and prints
				// float
				// println(num[i]);
			}
		}
	}

	public static int parseWithDefault(String number, int defaultVal) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	void blur(float trans, PGraphics pg) {
		pg.noStroke();
		pg.fill(0, trans);
		pg.rect(0, 0, width, height);
	}

}
