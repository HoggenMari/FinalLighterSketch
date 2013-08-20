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
	private int NUMBER_OF_PORTS_IN_USE = 10;
	private int PORTS_PR_CONTROLLER = 8;

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

		byte[] data = new byte[2702];

		data[0] = 'Y';
		data[1] = 'T';
		data[2] = 'K';
		data[3] = 'J';

		for (int controller = 1; controller <= NUMBER_OF_CONTROLLERS; controller++) {
			data[4] = (byte) (controller);
			data[5] = 0;

			data[6] = 0x57;
			data[7] = 0x05;
			int portsInUse = NUMBER_OF_PORTS_IN_USE - (controller - 1)
					* PORTS_PR_CONTROLLER;
			if (portsInUse > PORTS_PR_CONTROLLER) {
				portsInUse = PORTS_PR_CONTROLLER;
			}

			// System.out.println("Ports: " + portsInUse);

			data[8] = (byte) portsInUse;
			data[9] = 0;

			int dataIndex = 10;

			//System.out.println("blubb: "+controller);
			// System.out.println("PORTSINUSE :"+portsInUse);

			int channel = 0;
			int ledsOnPort = 96;

			data[dataIndex++] = (byte) (channel & 0xff);
			data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
			data[dataIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
			data[dataIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);

			for (int screenListIndex = 0; screenListIndex < screenList.size(); screenListIndex++) {

				int ledCounter = 0;

				for (int i_x = 0; i_x < screenList.get(screenListIndex).getY().length; i_x++) {

					// System.out.println("PORT:"+port+" SCREENLISTINDEX:"+screenListIndex+" I_X:"+i_x);

					for (int i_y = 0; i_y < screenList.get(screenListIndex)
							.getY()[i_x]; i_y++) {

						setPixel(i_x, i_y, screenList.get(screenListIndex)
								.getImage(), data, dataIndex);
						dataIndex += 3;
						ledCounter++;

						//System.out.println("LEDCOUNTER: DOWN"+ledCounter);

						
						if (ledCounter >= ledsOnPort) {
							
							channel += 2048;
							ledCounter = 0;
							
							data[dataIndex++] = (byte) (channel & 0xff);
							data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
							data[dataIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
							data[dataIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);
						}
					}
					i_x++;

					for (int i_y = screenList.get(screenListIndex).getY()[i_x]; i_y > 0; i_y--) {

						setPixel(i_x, i_y, screenList.get(screenListIndex)
								.getImage(), data, dataIndex);
						dataIndex += 3;
						ledCounter++;

						//System.out.println("LEDCOUNTER UP:"+ledCounter);
						
							
						if (ledCounter >= ledsOnPort) {
							
							//newPort(data, dataIndex);

							channel += 2048;
							ledCounter = 0;

							data[dataIndex++] = (byte) (channel & 0xff);
							data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
							data[dataIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
							data[dataIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);
						}
					}
				}
				
				//System.out.println("DATAINDEX:"+screenListIndex+" LEDCOUNTER:"+ledCounter);
				int remain;
				if((remain = 96-ledCounter)!=96) {
					for(int i=0; i<remain; i++) {
						dataIndex += 3;
					}
					
					//newPort(data, dataIndex);
					
					channel += 2048;
					ledCounter = 0;

					data[dataIndex++] = (byte) (channel & 0xff);
					data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
					data[dataIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
					data[dataIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);
				}

			}

			// Map the Pixels
			udp.send(data, ip, port);

		}

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
	
	void newPort(int portCounter, int channel, int ledCounter, int ledsOnPort, byte data[], int dataIndex) {
		
		//portCounter++;
		//channel += 2048;
		//ledCounter = 0;

		data[dataIndex++] = (byte) (channel & 0xff);
		data[dataIndex++] = (byte) ((channel >> 8) & 0xff);
		data[dataIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
		data[dataIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);
		
	}

}
