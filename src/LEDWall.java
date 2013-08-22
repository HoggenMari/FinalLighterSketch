import hypermedia.net.UDP;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class LEDWall {

	public static final int NORMAL_MODE  = 0;
	public static final int REVERSE_MODE  = 1;

	ArrayList<LEDScreen> ledScreenList = new ArrayList<LEDScreen>();
	ArrayList<Integer> controllerList = new ArrayList<Integer>();
	ArrayList<Integer> modeList = new ArrayList<Integer>();
	
	private final String ip = "224.1.1.1";
	private final int port = 5026;
	private PApplet p;
	private UDP udp;
	private ArrayList<LEDScreen> screenList = new ArrayList<LEDScreen>();

	private int NUMBER_OF_CONTROLLERS = 2;
	private int NUMBER_OF_PORTS_IN_USE = 16;
	private int PORTS_PR_CONTROLLER = 8;
	private int LEDS_ON_PORT = 96;
	private int[] CONTROLLER_ID = { 1, 4 };
	
	public LEDWall(PApplet p) {
		this.p = p;
	}

	public void init() {
		udp = new UDP(p, 5026);
	}

	public void add(LEDScreen ledScreen, int controllerNum, int mode) {
		screenList.add(ledScreen);
		controllerList.add(controllerNum);
		modeList.add(mode);
	}
	
	public void add(LEDScreen ledScreen, int controllerNum) {
		screenList.add(ledScreen);
		controllerList.add(controllerNum);
		modeList.add(NORMAL_MODE);
	}
	
	public void add(LEDScreen ledScreen) {
		screenList.add(ledScreen);
		controllerList.add(0);
		modeList.add(NORMAL_MODE);
	}

	public void sendDMX() {

		byte[] data = new byte[2702];

		data[0] = 'Y';
		data[1] = 'T';
		data[2] = 'K';
		data[3] = 'J';

		// iterate Controller
		for (int controller = 0; controller < NUMBER_OF_CONTROLLERS; controller++) {

			int screenListIndex = 0;

			data[4] = (byte) CONTROLLER_ID[controller];
			data[5] = 0;

			data[6] = 0x57;
			data[7] = 0x05;
			int portsInUse = NUMBER_OF_PORTS_IN_USE - controller
					* PORTS_PR_CONTROLLER;
			if (portsInUse > PORTS_PR_CONTROLLER) {
				portsInUse = PORTS_PR_CONTROLLER;
			}

			data[8] = (byte) portsInUse;
			data[9] = 0;

			int dataIndex = 10;

			int channel = 0;

			data[dataIndex++] = (byte) (channel & 0xff);
			data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
			data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
			data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);

			// iterate screenList
			while (screenListIndex < screenList.size()) {

				if (controllerList.get(screenListIndex) == controller) {

					System.out.println("CONTROLLER: " + controller
							+ " SCREENLISTINDEX: " + screenListIndex);

					int ledCounter = 0;
					
					if(modeList.get(screenListIndex)==NORMAL_MODE) {
						
					// iterate x-coordinates
					for (int i_x = 0; i_x < screenList.get(screenListIndex)
							.getY().length; i_x++) {

						// iterate y-coordinates i_x (down)
						for (int i_y = 0; i_y < screenList.get(screenListIndex)
								.getY()[i_x]; i_y++) {

							setPixel(i_x, i_y, screenList.get(screenListIndex)
									.getImage(), data, dataIndex);
							dataIndex += 3;
							ledCounter++;

							// open new port if required
							if (ledCounter >= LEDS_ON_PORT) {

								channel += 2048;
								ledCounter = 0;

								data[dataIndex++] = (byte) (channel & 0xff);
								data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
								data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
								data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
							}
						}

						i_x++;

						// iterate y-coordinates i_x (up)
						for (int i_y = screenList.get(screenListIndex).getY()[i_x] - 1; i_y >= 0; i_y--) {

							setPixel(i_x, i_y, screenList.get(screenListIndex)
									.getImage(), data, dataIndex);
							dataIndex += 3;
							ledCounter++;

							// open new port if required
							if (ledCounter >= LEDS_ON_PORT) {

								channel += 2048;
								ledCounter = 0;

								data[dataIndex++] = (byte) (channel & 0xff);
								data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
								data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
								data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
							}
						}
					}
					} else {
					// iterate x-coordinates
					for (int i_x = screenList.get(screenListIndex)
							.getY().length - 1; i_x >= 0; i_x--) {

						// iterate y-coordinates i_x (down)
						for (int i_y = screenList.get(screenListIndex)
								.getY()[i_x] - 1; i_y >= 0; i_y--) {

							setPixel(i_x, i_y, screenList.get(screenListIndex)
									.getImage(), data, dataIndex);
							dataIndex += 3;
							ledCounter++;

							// open new port if required
							if (ledCounter >= LEDS_ON_PORT) {

								channel += 2048;
								ledCounter = 0;

								data[dataIndex++] = (byte) (channel & 0xff);
								data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
								data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
								data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
							}
						}

						i_x--;

						// iterate y-coordinates i_x (up)
						for (int i_y = 0; i_y < screenList.get(screenListIndex).getY()[i_x]; i_y++) {

							setPixel(i_x, i_y, screenList.get(screenListIndex)
									.getImage(), data, dataIndex);
							dataIndex += 3;
							ledCounter++;

							// open new port if required
							if (ledCounter >= LEDS_ON_PORT) {

								channel += 2048;
								ledCounter = 0;

								data[dataIndex++] = (byte) (channel & 0xff);
								data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
								data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
								data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
							}
						}
					}
					}
					
					// padding bytes for remaining leds on port
					int rest;
					if ((rest = 96 - ledCounter) != 96) {
						System.out.println("REMAIN: " + rest);
						for (int i = 0; i < rest; i++) {
							dataIndex += 3;
						}

						channel += 2048;
						ledCounter = 0;

						data[dataIndex++] = (byte) (channel & 0xff);
						data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
						data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
						data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
					}

				}

				screenListIndex++;

			}

			// Map the Pixels
			System.out.println(dataIndex);
			udp.send(data, ip, port);

		}

	}

	void setPixel(int ix, int iy, PImage image, byte data[], int dataIndex) {
		int rgb = image.get(ix, iy);

		data[dataIndex + 2] = (byte) (rgb & 0xff);
		data[dataIndex + 1] = (byte) ((rgb >> 8) & 0xff);
		data[dataIndex] = (byte) ((rgb >> 16) & 0xff);

	}

}