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

	public void setup() {
		
		pg = createGraphics(400, 400);


		// LEDScreen1 initialisieren
		ledScreen1 = new LEDScreen(8, 24, this);

		// LEDWall initialisieren
		ledWall = new LEDWall(this);
		ledWall.add(ledScreen1);
		ledWall.init();

		// LighterList initialisieren
		lighterList = new ArrayList<Lighter>();
		for (int i = 0; i < 4; i++) {
			lighterList.add(new Lighter(i));
		}
		
		//FlameList initialisiren
		firework = new ArrayList<Firework>();

		lighterList.get(0).toString();

		frameRate(100);
		for (int i = 0; i < Serial.list().length; i++) {
			System.out.println("Device " + i + " " + Serial.list()[i]);
		}
		myPort = new Serial(this, Serial.list()[4], 9600);
		myPort.clear();
		num = new int[10];

		size(1024, 1024);

		background(0, 0, 0);

	}

	public void draw() {

		background(0);

		
		PImage img1 = drawFirework();
		image(img1,0,0);
		
		img1.resize(24, 24);
		ledScreen1.update(img1);
		ledScreen1.drawOnGui(170, 5);
		ledWall.sendDMX();


		
	}
	
	public PImage drawFirework() {

		for (Iterator<Firework> fireItr = firework.iterator(); fireItr
				.hasNext();) {
			Firework fw = fireItr.next();
			if (fw.isDead()) {
				fireItr.remove();
			} else
				fw.draw(pg);
		}

		PImage img = pg.get();
		return img;
	}
	
	public void mousePressed() {

		firework.add(new Firework(this, new PVector(mouseX, mouseY)));

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
					lighterList.get(i).setPosition(posX, posY);
				}
				System.out.println(lighterList.get(i).toString());
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
