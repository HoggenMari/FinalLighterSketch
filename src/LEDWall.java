import hypermedia.net.UDP;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class LEDWall {

	ArrayList<LEDScreen> ledScreenList = new ArrayList<LEDScreen>();
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
	private byte[] data;
	private int channel;
	private int ledCounter;
	private int dataIndex;
	private int globalPortCounter;
	private int controller;

	public LEDWall(PApplet p) {
		this.p = p;
	}

	public void init() {
		udp = new UDP(p, 5026);
	}

	public void add(LEDScreen ledScreen) {
		screenList.add(ledScreen);
	}

	public void sendDMX() {
		
		System.out.println("SEND");

		data = new byte[6000];
		int screenListIndex = 0;
		globalPortCounter = 0;

		data[0] = 'Y';
		data[1] = 'T';
		data[2] = 'K';
		data[3] = 'J';

		//for (int controller = 0; controller < NUMBER_OF_CONTROLLERS; controller++) {
			
			controller = 0;
		
			newController(controller);

			newPort(globalPortCounter);

			//for (int screenListIndex = 0; screenListIndex < screenList.size(); screenListIndex++) {
			while(screenListIndex < screenList.size()) {
				
				ledCounter = 0;
				
				System.out.println("SCREENLISTINDEX: "+screenListIndex+" CONTROLLER: "+controller);

				for (int i_x = 0; i_x < screenList.get(screenListIndex).getY().length; i_x++) {

					// System.out.println("PORT:"+port+" SCREENLISTINDEX:"+screenListIndex+" I_X:"+i_x);

					for (int i_y = 0; i_y < screenList.get(screenListIndex)
							.getY()[i_x]; i_y++) {

						setPixel(i_x, i_y, screenList.get(screenListIndex)
								.getImage(), data, dataIndex);
						dataIndex += 3;
						ledCounter++;

						//System.out.println("LEDCOUNTER: DOWN"+ledCounter);

						//reset ledCounter and increase port
						if (globalPortCounter>=PORTS_PR_CONTROLLER) {
							udp.send(data, ip, port);
							System.out.println("increase trial 1 "+globalPortCounter);
							newController(++controller);
							globalPortCounter = 0;
							newPort(globalPortCounter);
						}
						else if (ledCounter >= LEDS_ON_PORT) {
							globalPortCounter++;
							newPort(globalPortCounter);
						}
					}
					i_x++;

					for (int i_y = screenList.get(screenListIndex).getY()[i_x]-1; i_y >= 0; i_y--) {

						setPixel(i_x, i_y, screenList.get(screenListIndex)
								.getImage(), data, dataIndex);
						dataIndex += 3;
						ledCounter++;

						//System.out.println("LEDCOUNTER UP:"+ledCounter);
						
						//reset ledCounter and increase port
						if (globalPortCounter>=PORTS_PR_CONTROLLER) {
							udp.send(data, ip, port);
							System.out.println("increase trial 1 "+globalPortCounter);
							newController(++controller);
							globalPortCounter = 0;
							newPort(globalPortCounter);
						}
						else if (ledCounter >= LEDS_ON_PORT) {
							globalPortCounter++;
							newPort(globalPortCounter);
						}
					}
				}
				
				//System.out.println("DATAINDEX:"+screenListIndex+" LEDCOUNTER:"+ledCounter);
				//pad rest leds on port
				int rest = LEDS_ON_PORT-ledCounter;
				if(rest!=96) {
					System.out.println("GO");
					for(int i=0; i<rest; i++) {
						dataIndex += 3;
					}
										
					channel += 2048;
					ledCounter = 0;

					data[dataIndex++] = (byte) (channel & 0xff);
					data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
					data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
					data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
				}
				
				//System.out.println("CHANNEL: "+channel);
				
				screenListIndex++;

			}

			// Map the Pixels
			// udp.send(data, ip, port);
			udp.send(data, ip, port);
		

	}

	void setPixel(int ix, int iy, PImage image, byte data[], int dataIndex) {
		// if (((ix * LED_RES_Y) + iy) <= image.length) {
		int rgb = image.get(ix, iy);

		data[dataIndex + 2] = (byte) (rgb & 0xff);
		data[dataIndex + 1] = (byte) ((rgb >> 8) & 0xff);
		data[dataIndex] = (byte) ((rgb >> 16) & 0xff);

		// System.out.println("Pixel: "+ix+" "+iy+" "+(byte) (rgb &
		// 0xff)+" dataIndex: "+(dataIndex-14)/3);

		// }
	}
	
	void newPort(int port) {
		
		//if(globalPortCounter<PORTS_PR_CONTROLLER) {
		System.out.println("NEW PORT: "+(globalPortCounter % PORTS_PR_CONTROLLER)+" "+globalPortCounter);
		
		ledCounter = 0;

		data[dataIndex++] = (byte) (channel & 0xff);
		data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
		data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) & 0xff);
		data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
		channel += 2048;


		/*}
		else {
			udp.send(data, ip, port);
			System.out.println("SENDED");
			
			if(controller<NUMBER_OF_CONTROLLERS-1) {
			controller++;

			System.out.println("Controller increeeease");
			globalPortCounter = 0;
			ledCounter = 0;
			
			data[4] = (byte) CONTROLLER_ID[controller];
			data[5] = 0;

			data[6] = 0x57;
			data[7] = 0x05;
			int portsInUse = NUMBER_OF_PORTS_IN_USE - controller
					* PORTS_PR_CONTROLLER;
			if (portsInUse > PORTS_PR_CONTROLLER) {
				portsInUse = PORTS_PR_CONTROLLER;
			}

			System.out.println("Controller: " + controller);

			data[8] = (byte) portsInUse;
			data[9] = 0;
			
			dataIndex = 10;
			channel = 0;
			
			newPort(0);
			}
		}*/
		
	}
	
	void newController(int controller) {
		data[4] = (byte) CONTROLLER_ID[controller];
		data[5] = 0;

		data[6] = 0x57;
		data[7] = 0x05;
		int portsInUse = NUMBER_OF_PORTS_IN_USE - controller
				* PORTS_PR_CONTROLLER;
		if (portsInUse > PORTS_PR_CONTROLLER) {
			portsInUse = PORTS_PR_CONTROLLER;
		}

		System.out.println("Controller: " + controller);

		data[8] = (byte) portsInUse;
		data[9] = 0;

		dataIndex = 10;

		//System.out.println("blubb: "+controller);
		//System.out.println("PORTSINUSE :"+portsInUse);

		channel = 0;
	}

}
