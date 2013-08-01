import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
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
	private ArrayList<Flame> flameList;
	private PGraphics pg;
	private PImage FlameImg;

	public void setup() {
		
		pg = createGraphics(1024, 1024);


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
		flameList = new ArrayList<Flame>();

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
		
		drawFlame();

	}
	
	public PImage drawFlame() {
		
		for(Lighter li : lighterList) {
			//if(li.isActive()) {
				Flame flame = new Flame(this, lighterList.indexOf(li));
				flame.setFlame(li.getPosX(), li.getPosY());
				flameList.add(flame);
			//}
		}
		
		for (Flame fl : flameList) {
			fl.draw(pg);
		}
		blur(20, pg);
		FlameImg = pg.get();
		//FlameImg.resize(160, 120);
		image(FlameImg, 0, 0);		
		
		return FlameImg;
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
