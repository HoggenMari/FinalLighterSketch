import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import controlP5.CheckBox;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;

import SimpleOpenNI.SimpleOpenNI;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.serial.*;
import processing.video.*;

public class ProcessingMain extends PApplet {

	private static final long serialVersionUID = 1L;

	/*
	 * Parameter Settings Start
	 */

	static final boolean SERIAL = true;
	static final boolean SCREEN = true;
	static final boolean GAME = false;
	static final boolean CAM = false;
	static final boolean MOVIE = true;
	static final boolean KINECT = true;
	static final String ARDUINO_DEVICE = "/dev/tty.usbmodem1d1211";

	/*
	 * Parameter Settings End
	 */

	/* GUI */
	ControlP5 cp5;
	CheckBox checkbox;
	DropdownList d1, d2;

	File videoFolder = new File("../Videos");
	File[] videoFiles = videoFolder.listFiles();

	File imageFolder = new File("../Images");
	File[] imageFiles = imageFolder.listFiles();

	/* Serial */
	static int lf = 10; // Linefeed in ASCII
	String myString = null; // Serial Output String
	Serial myPort; // Serial port you are using

	/* Output */
	private LEDScreen ledScreen1;
	private LEDWall ledWall;

	/* ArrayList */
	private ArrayList<Lighter> lighterList;
	private ArrayList<Firework> firework;
	private ArrayList<Flame> flames;
	private ArrayList<ColorPoint> cpList;
	private ArrayList<Integer> userList;

	private PGraphics pg;

	private Flame mouseFlame;
	private Game game;
	private Capture cam;

	/* Kinect */
	private SimpleOpenNI context;
	boolean autoCalib = true;

	private Movie m;

	boolean freeze = false;

	private PImage imbg;

	private PImage img;

	private LEDScreen ledScreen2;

	private PGraphics pg2;

	public void setup() {
		
		//frameRate(25);

		cp5setup();
		
		pg = createGraphics(240, 240, JAVA2D);
		pg2 = createGraphics(240, 240, JAVA2D);
		
		pg2.beginDraw();
		pg2.background(0);
		pg2.endDraw();

		pg.colorMode(HSB);

		// LEDScreen1 initialisieren
		ledScreen1 = new LEDScreen(24, 24, this);
		ledScreen2 = new LEDScreen(8, 24, this);


		// LEDWall initialisieren
		ledWall = new LEDWall(this);
		ledWall.add(ledScreen1, 0, LEDWall.NORMAL_MODE);
		ledWall.add(ledScreen2, 0, LEDWall.NORMAL_MODE);
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
		
		// Paint initialisieren
		userList = new ArrayList<Integer>();
		cpList = new ArrayList<ColorPoint>();

		// Skeleton initialisieren
		if (KINECT) {
			setupSkeleton();
			//setupScene();
		}

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
		size(530, 270);
		background(255);

		// Cam
		if (CAM) {
			setupCam();
		}

		// Movie
		if (MOVIE) {
			m = new Movie(this,
					"/Users/mariushoggenmuller/Downloads/LazyLoop1.avi");
			m.loop();
		}

		img = loadImage("../Images/bg_small.png");

	}

	@SuppressWarnings("deprecation")
	public void cp5setup() {

		cp5 = new ControlP5(this);
		checkbox = cp5.addCheckBox("checkBox").setPosition(5, 5)
				.setColorForeground(color(120)).setColorActive(color(200))
				.setColorLabel(color(0)).setSize(15, 15).setItemsPerRow(6)
				.setSpacingColumn(45).setSpacingRow(20).addItem("Firework", 0)
				.addItem("Flame", 50).addItem("Painting", 100)
				.addItem("Webcam", 150).addItem("Image", 200)
				.addItem("Video", 255);
		
		d1 = cp5.addDropdownList("Videos").setPosition(370, 21)
				.setSize(70, 100).setBarHeight(15);

		d1.getCaptionLabel().style().setMarginTop(3);

		for (int i = 0; i < videoFiles.length; i++) {
			d1.addItem(videoFiles[i].getName().toString(), i);
		}

		d2 = cp5.addDropdownList("Images").setPosition(450, 21)
				.setSize(70, 100).setBarHeight(15);

		d2.getCaptionLabel().style().setMarginTop(3);

		for (int i = 0; i < imageFiles.length; i++) {
			d2.addItem(imageFiles[i].getName().toString(), i);
		}
	}

	/*
	 * CAM
	 */
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
			cam = new Capture(this, cameras[3]);
			cam.start();
		}
	}

	public PImage drawCam() {
		PImage img = new PImage();
		try {
			if (cam.available() == true) {
				cam.read();
			}

			img = cam.get();
		} catch (Exception e) {
			System.out.println("Fehler");
			setupCam();
		}
		return img;
	}

	public void draw() {
		
		System.out.println(Color.BLACK.getRGB());

		// println(checkbox.getArrayValue(0));
		if (!freeze) {
			background(255);

			pg.beginDraw();
			imbg = new PImage();
			// PImage imgbg =
			// loadImage("/Users/mariushoggenmuller/Documents/bg_small_black.png");
			// imbg = drawCam();
			//if(checkbox.getArrayValue(2)==0) {
			if (checkbox.getArrayValue(5) == 1.0) {
				pg.background(0);
				imbg = m.get();
				pg.set(0, 0, imbg);
			} else if (checkbox.getArrayValue(4) == 1.0) {
				//pg.background(0);
				imbg = img.get();
				for(int i=0; i<pg2.width; i++) {
					for(int j=0; j<pg2.height; j++) {
						int rgb = pg2.get(i, j);
						byte r = (byte) (rgb & 0xff);
						byte g = (byte) ((rgb >> 8) & 0xff);
						byte b = (byte) ((rgb >> 16) & 0xff);
						if(r<50 && g<50 && b<50) {
						pg.set(i, j, imbg.get(i, j));
						}
						/*if((frameCount%10)==0) {
							System.out.println("i: "+i+"j: "+j+" "+pg2.get(i, j));
						}*/
					}
				}
			} else if (checkbox.getArrayValue(3) == 1.0) {
				imbg = drawCam();
				pg.set(0, 0, imbg);
			} else {
				pg.background(0);
			}
			//}
			pg.endDraw();

			// drawFirework();
			// pg.set(0,0,imgbg);
			// drawFlame();
			if (checkbox.getArrayValue(0) == 1.0) {
				drawFirework();
			}
			if (checkbox.getArrayValue(1) == 1.0) {
				drawFlame();
			}
			if (checkbox.getArrayValue(2) == 1.0) {
				drawBlur();
			}
			// pg.set(0,0,imgbg);

			// PImage img1 = drawFlame();
			// PImage img1 = drawBlur();
			// PImage img1 = drawScene();
			// PImage img2 = drawFirework();
			// PImage img1 = drawCam();
			// PImage img1 =
			// loadImage("/Users/mariushoggenmuller/Documents/test.png");
			// PImage img2 =
			// loadImage("/Users/mariushoggenmuller/Documents/test2.png");
			// PImage img3 =
			// loadImage("/Users/mariushoggenmuller/Documents/test.png");
			// img1 = rotate(img1);
			PImage img1 = pg.get();
			image(img1, 5, 25);
			// image(img2, 5, 40);

			// Ausgabe fŸr LEDScreen
			if (SCREEN) {
				//try {
					img1.resize(24, 24);
					// image(img1, 0, 0);
					// img2.resize(32, 12);
					ledScreen1.update(img1);
					ledScreen2.update(img1);
					// ledScreen3.update(img3);
					ledScreen1.drawOnGui(250, 25);
					ledScreen2.drawOnGui(250, 250);
					// ledScreen2.drawOnGui(250, 200);
					// ledScreen3.drawOnGui(250, 400);
					if((frameCount%2)==0) {
						ledWall.sendDMX();
					}
				//} catch (Exception e) {
					//System.out.println(e);
				//}
			}

			// image(m, 200, 200);
		}

	}

	/*
	 * FIREWORK
	 */
	public void drawFirework() {

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

		// PImage img = pg.get();
		// return img;
	}

	/*
	 * FLAME
	 */
	public void drawFlame() {

		PImage bg = new PImage();

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
						fl.draw(pg, bg);
					}
				}
			} else if (lg.getLighterState().toString() == "LOST") {
				// System.out.println("!!!!!LOST!!!!!");
				for (Flame fl : flames) {
					if (fl.getFlameID() == lg.getLighterID()) {
						// fl.update(lg.getLostPos());
						// System.out.println("!!!!!LOST!!!!!");
						fl.kill(lg.getLostPos(), lg.getLostCounter());
						fl.draw(pg, bg);
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
			fl.draw(pg, bg);
		}

		// PImage img = pg.get();
		// return img;
	}

	/*
	 * KINECT
	 */
	public void setupSkeleton() {

		context = new SimpleOpenNI(this);

		// enable Depth
		if (context.enableDepth() == false) {
			System.out.println("Fehler beim initialisieren der Kinect");
		}
		context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
	}

	public void drawBlur() {
		context.update();
		image(context.depthImage(), 0, 0);

		stroke(0, 0, 255);
		strokeWeight(3);
		smooth();
		// for all users from 1 to 10
		int i;
		for (i = 1; i <= 10; i++) {
			// check if the skeleton is being tracked
			if (context.isTrackingSkeleton(i)) {
				 drawSkeleton(i); // draw the skeleton
			}
		}
		blur(10, pg);
		blur(5, pg2);
		setPoint();
		for (ColorPoint cp : cpList) {
			cp.draw(pg);
			cp.draw(pg2);
		}
		//pg = reversePGraphics(pg);
		//pg = reversePGraphics(pg);

		PImage BlurImg = pg2.get();
		//BlurImg = getReversePImage(BlurImg);
		BlurImg.resize(160, 240);
		image(BlurImg, 0,300);

	}

	void setPoint() {
		if (userList.size() > 0) {// if there are any users
			for (int user : userList) {// for each user
				if (context.isTrackingSkeleton(user)) {

					PVector jointPosLeft = new PVector();
					PVector posProjLeft = new PVector();

					context.getJointPositionSkeleton(user,
							SimpleOpenNI.SKEL_LEFT_HAND, jointPosLeft);
					context.convertRealWorldToProjective(jointPosLeft,
							posProjLeft);
					
					System.out.println("SET_POINT: "+posProjLeft.x+" "+posProjLeft.y);


					for (ColorPoint cp : cpList) {
						if (cp.equals(user)) {
							cp.setPoint(posProjLeft.x, posProjLeft.y);
						}
					}

				}
			}
		}

	}

	void drawSkeleton(int userId) {
		// draw the skeleton with the selected joints
		context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_LEFT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_LEFT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW,
				SimpleOpenNI.SKEL_LEFT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_NECK,
				SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_RIGHT_ELBOW);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW,
				SimpleOpenNI.SKEL_RIGHT_HAND);

		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER,
				SimpleOpenNI.SKEL_TORSO);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO,
				SimpleOpenNI.SKEL_LEFT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP,
				SimpleOpenNI.SKEL_LEFT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE,
				SimpleOpenNI.SKEL_LEFT_FOOT);

		context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO,
				SimpleOpenNI.SKEL_RIGHT_HIP);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP,
				SimpleOpenNI.SKEL_RIGHT_KNEE);
		context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE,
				SimpleOpenNI.SKEL_RIGHT_FOOT);
	}

	public void setupScene() {
		context = new SimpleOpenNI(this);

		// enable Scene
		if (context.enableScene() == false) {
			println("Can't open the sceneMap, maybe the camera is not connected!");
			exit();
			return;
		}

	}

	public PImage drawScene() {
		System.out.println("SceneDraw");
		context.update();
		image(context.sceneImage(), 0, 0);
		PImage SceneImg = context.sceneImage();
		SceneImg = getReversePImage(SceneImg);
		SceneImg.resize(160, 120);
		image(SceneImg, 5, 5);
		return SceneImg;
	}

	/*
	 * SIMPLE OPEN NI EVENTS
	 */
	public void onNewUser(int userId) {
		println("detected" + userId);
		userList.add(userId); // a new user was detected add the id to the list
		cpList.add(new ColorPoint(this, userId));
		if (autoCalib)
			context.requestCalibrationSkeleton(userId, true);
		else
			context.startPoseDetection("Psi", userId);
	}

	public void onLostUser(int userId) {
		println("lost: " + userId);
		// not 100% sure if users.remove(userId) will remove the element with
		// value userId or the element at index userId
		userList.remove((Integer) userId);// user was lost, remove the id from
											// the
											// list
		for (ColorPoint cp : cpList) {
			if (cp.equals(userId)) {
				cpList.remove(cp);
			}
		}
	}

	public void onExitUser(int userId) {
		println("onExitUser - userId: " + userId);
	}

	public void onReEnterUser(int userId) {
		println("onReEnterUser - userId: " + userId);
	}

	public void onStartCalibration(int userId) {
		println("onStartCalibration - userId: " + userId);
	}

	public void onEndCalibration(int userId, boolean successfull) {
		println("onEndCalibration - userId: " + userId + ", successfull: "
				+ successfull);

		if (successfull) {
			println("  User calibrated !!!");
			context.startTrackingSkeleton(userId);
		} else {
			println("  Failed to calibrate user !!!");
			println("  Start pose detection");
			context.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) {
		println("onStartPose - userId: " + userId + ", pose: " + pose);
		println(" stop pose detection");

		context.stopPoseDetection(userId);
		context.requestCalibrationSkeleton(userId, true);

	}

	public void onEndPose(String pose, int userId) {
		println("onEndPose - userId: " + userId + ", pose: " + pose);
	}

	public void mousePressed() {

		if (mouseX < pg.width + 5 && mouseY > 25) {
			firework.add(new Firework(this, pg, new PVector(mouseX, mouseY),
					new Color((int) random(0, 255), 255, 255), new Color(
							(int) random(0, 255), 255, 255), 100));

			mouseFlame.update(new PVector(mouseX, mouseY));
			flames.add(mouseFlame);
			cpList.add(new ColorPoint(this, 1000));

		}
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
		pg.rect(0, 0, pg.width, pg.height);
	}

	// Auxiliary function for reverse image
	public PImage getReversePImage(PImage image) {
		PImage reverse = new PImage(image.width, image.height);
		for (int i = 0; i < image.width; i++) {
			for (int j = 0; j < image.height; j++) {
				reverse.set(image.width - 1 - i, j, image.get(i, j));
			}
		}
		return reverse;
	}
	
	//Auxiliary function for reverse PGrahic
	public PGraphics reversePGraphics(PGraphics pg) {
		PGraphics reverse = createGraphics(pg.width, pg.height, JAVA2D);
		for (int i = 0; i < pg.width; i++) {
			for (int j = 0; j < pg.height; j++) {
				reverse.set(pg.width - 1 - i, j, pg.get(i, j));
			}
		}
	    return reverse;
		
	}

	// Called every time a new frame is available to read
	public void movieEvent(Movie m) {
		m.read();
	}

	public void keyPressed() {
		if (key == ' ') {
			System.out.println("Space");
			freeze = !freeze;
		}
	}

	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.getGroup().getName() == "Videos") {
			// check if the Event was triggered from a ControlGroup
			println("event from group : " + theEvent.getGroup().getValue()
					+ " from " + theEvent.getGroup());

			int file = (int) theEvent.getGroup().getValue();
			m.stop();
			m = new Movie(this, videoFiles[file].toString());
			m.loop();
		} else if (theEvent.getGroup().getName() == "Images") {
			// check if the Event was triggered from a ControlGroup
			println("event from group : " + theEvent.getGroup().getValue()
					+ " from " + theEvent.getGroup());

			int file = (int) theEvent.getGroup().getValue();
			img = loadImage(imageFiles[file].toString());
		}

	}
}
