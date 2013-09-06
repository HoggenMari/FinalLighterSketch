import hypermedia.net.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PImage;

public class LEDWall {

	public static final int NORMAL_MODE = 0;
	public static final int REVERSE_MODE = 1;

	ArrayList<LEDScreen> ledScreenList = new ArrayList<LEDScreen>();
	ArrayList<Integer> controllerList = new ArrayList<Integer>();
	ArrayList<Integer> modeList = new ArrayList<Integer>();

	private final String ip = "224.1.1.1";
	private final int port = 5026;
	private PApplet p;
	private UDP udp;
	private ArrayList<LEDScreen> screenList = new ArrayList<LEDScreen>();

	private int NUMBER_OF_CONTROLLERS = 1;
	private int NUMBER_OF_PORTS_IN_USE = 2;
	private int MAX_PAYLOAD = 1500;
	private int PORTS_PR_CONTROLLER = 8;
	private int LEDS_ON_PORT = 192;
	private int[] CONTROLLER_ID = { 1, 4 };
	private static final int PORT_MAP[] = new int[] { 96, 96, 96, 96, 96, 96,
			96, 96, 96, 96, 96, 96 };

	private int headCounter;

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

	public byte[] sendHelper(int ch, byte[]... data) {
		byte[] payload = new byte[1500];

		System.out.println("Aufruf");

		payload[0] = 'Y';
		payload[1] = 'T';
		payload[2] = 'K';
		payload[3] = 'J';

		for (int controller = 1; controller <= NUMBER_OF_CONTROLLERS; controller++) {
			payload[4] = (byte) (controller);
			payload[5] = 0;

			// Unknown
			payload[6] = 0x57;
			payload[7] = 0x05;
			int portsInUse = NUMBER_OF_PORTS_IN_USE - (controller - 1)
					* PORTS_PR_CONTROLLER;
			if (portsInUse > PORTS_PR_CONTROLLER) {
				portsInUse = PORTS_PR_CONTROLLER;
			}

			payload[8] = (byte) portsInUse;
			payload[9] = 0;

			int payloadIndex = 10;

			// Now map the pixels
			for (int port = 0, channel = ch; port < data.length; port++, channel += 2048) {

				System.out.println("DataLength: " + data[port].length + " "
						+ data.length);

				payload[payloadIndex++] = (byte) (channel & 0xff);
				payload[payloadIndex++] = (byte) ((channel >> 8) & 0xff);

				int ledsOnPort = PORT_MAP[port * 2] + PORT_MAP[port * 2 + 1];
				payload[payloadIndex++] = (byte) ((ledsOnPort * 3) & 0xff);
				payload[payloadIndex++] = (byte) (((ledsOnPort * 3) >> 8) & 0xff);

				for (int i = 0; i < data[port].length; i++) {
					if ((i % 10) == 0) {
						// System.out.println("Schreiben: "+data[port][i]+" in payloadIndex: "+payloadIndex);
					}
					payload[payloadIndex++] = data[port][i];
				}
			}
		}

		return payload;

	}

	public void sendDMX() {

		// System.out.println(headCounter);

		ArrayList<byte[]> packetList = new ArrayList<byte[]>();
		int plIndex = 0;

		// kleine Datenpackete
		for (int screenIndex = 0; screenIndex < screenList.size(); screenIndex++) {

			if (modeList.get(screenIndex) == NORMAL_MODE) {

				System.out.println("NORMAL");

				System.out.println("ScreenListIndex: " + screenIndex);

				packetList.add(new byte[576]);
				System.out.println("PacketListSize: " + packetList.size());
				int packetIndex = 0;
				int count = 0;

				// solange genug freie LEDs pro Port
				for (int ix = 0; ix < screenList.get(screenIndex).getY().length; ix++) {

					for (int iy = 0; iy < screenList.get(screenIndex).getY()[ix]; iy++) {
						setPixel(ix, iy,
								screenList.get(screenIndex).getImage(),
								packetList.get(plIndex), packetIndex);
						packetIndex += 3;
						count++;
						// System.out.println("Count "+count+" ix " + ix);

						// System.out.println(screenList.get(screenIndex).getImage().get(ix,
						// iy));
						// wenn grš§er als DatenPacket
						if (count >= 192
								&& (iy < screenList.get(screenIndex).getY()[ix] || ix + 1 < screenList
										.get(screenIndex).getY().length)) {
							packetList.add(new byte[576]);
							System.out.println("Increase auf: "+packetList.size()+" iy: "+iy+" ix "+ix);
							plIndex++;
							count = 0;
							packetIndex = 0;
						}

					}
					ix++;
					for (int iy = screenList.get(screenIndex).getY()[ix] - 1; iy >= 0; iy--) {
						setPixel(ix, iy,
								screenList.get(screenIndex).getImage(),
								packetList.get(plIndex), packetIndex);
						packetIndex += 3;
						count++;
						// System.out.println("Count "+count + " ix " + ix);

						// wenn grš§er als DatenPacket
						if (count >= 192
								&& (iy > 0 || ix + 1 < screenList.get(
										screenIndex).getY().length)) {
							packetList.add(new byte[576]);
							System.out.println("Increase auf: "+packetList.size()+" iy: "+iy+" ix "+ix+" length: "+screenList.get(screenIndex).getY().length);
							plIndex++;
							count = 0;
							packetIndex = 0;
						}
					}

				}

				plIndex++;

				/*
				 * packetList.add(new byte[576]); plIndex++; count = 0;
				 * packetIndex = 0;
				 */

			} else {
				
				System.out.println("REVERSE");

				System.out.println("ScreenListIndex: " + screenIndex);

				packetList.add(new byte[576]);
				System.out.println("PacketListSize: " + packetList.size());
				int packetIndex = 0;
				int count = 0;

				// solange genug freie LEDs pro Port
				for (int ix = screenList.get(screenIndex).getY().length - 1; ix >= 0; ix--) {
					
					for (int iy = screenList.get(screenIndex).getY()[ix] - 1; iy >= 0; iy--) {
						setPixel(ix, iy,
								screenList.get(screenIndex).getImage(),
								packetList.get(plIndex), packetIndex);
						packetIndex += 3;
						count++;
						// System.out.println("Count "+count+" ix " + ix);

						// System.out.println(screenList.get(screenIndex).getImage().get(ix,
						// iy));
						// wenn grš§er als DatenPacket
						if (count >= 192
								&& (iy > 0 || ix > 0)) {
							packetList.add(new byte[576]);
							System.out.println("Increase 1 auf: "+packetList.size()+" iy: "+iy+" ix "+ix);
							plIndex++;
							count = 0;
							packetIndex = 0;
						}

					}
					ix--;
					for (int iy = 0; iy < screenList.get(screenIndex).getY()[ix]; iy++) {
						setPixel(ix, iy,
								screenList.get(screenIndex).getImage(),
								packetList.get(plIndex), packetIndex);
						packetIndex += 3;
						count++;
						// System.out.println("Count "+count + " ix " + ix);

						// wenn grš§er als DatenPacket
						if (count >= 192
								&& (ix > 0)) {
							packetList.add(new byte[576]);
							System.out.println("Increase 2 auf: "+packetList.size()+" iy: "+iy+" ix "+ix+" length: "+screenList.get(screenIndex).getY().length);
							plIndex++;
							count = 0;
							packetIndex = 0;
						}
					}

				}

				plIndex++;

				
			}
		}
		System.out.println("Packete: " + packetList.size());

		/*
		 * for(int j=0; j<576; j++) { System.out.println(packetList.get(3)[j]);
		 * }
		 */

		for (int i = 0; i < packetList.size(); i += 2) {
			byte[] dataSend = sendHelper(i * 2048, packetList.get(i),
					packetList.get(i + 1));

			System.out.println("AUFRUF: " + i);

			/*
			 * for(int j=0; j<dataSend.length; j++) {
			 * System.out.println("DataIndex: "+j+"Data: "+dataSend[j]); }
			 */

			/*
			 * for(int j=0; j<576; j++) {
			 * System.out.println(packetList.get(0)[i]); }
			 */

			udp.send(dataSend, ip, port);
		}

		// for(int i=0; i<0; i++) {
		// data = sendHelper(0, 0, 0);

		// udp.send(data, ip, port);

		// data = sendHelper(2, 4096, 16);
		// udp.send(data, ip, port);

		// data = sendHelper(4, 8192, 32);
		// udp.send(data, ip, port);

		// }
	}

	/*
	 * public void sendDMX() {
	 * 
	 * byte[] data = new byte[1500];
	 * 
	 * data[0] = 'Y'; data[1] = 'T'; data[2] = 'K'; data[3] = 'J';
	 * 
	 * // iterate Controller for (int controller = 0; controller <
	 * NUMBER_OF_CONTROLLERS; controller++) {
	 * 
	 * int screenListIndex = 0;
	 * 
	 * data[4] = (byte) CONTROLLER_ID[controller]; data[5] = 0;
	 * 
	 * data[6] = 0x57; data[7] = 0x05; int portsInUse = NUMBER_OF_PORTS_IN_USE -
	 * controller PORTS_PR_CONTROLLER; if (portsInUse > PORTS_PR_CONTROLLER) {
	 * portsInUse = PORTS_PR_CONTROLLER; }
	 * 
	 * data[8] = (byte) portsInUse; data[9] = 0;
	 * 
	 * int dataIndex = 10;
	 * 
	 * int channel = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff);
	 * 
	 * // iterate screenList while (screenListIndex < screenList.size()) {
	 * 
	 * if (controllerList.get(screenListIndex) == controller) {
	 * 
	 * int ledCounter = 0;
	 * 
	 * if(modeList.get(screenListIndex)==NORMAL_MODE) {
	 * 
	 * // iterate x-coordinates for (int i_x = 0; i_x <
	 * screenList.get(screenListIndex) .getY().length; i_x++) {
	 * 
	 * // iterate y-coordinates i_x (down) for (int i_y = 0; i_y <
	 * screenList.get(screenListIndex) .getY()[i_x]; i_y++) {
	 * 
	 * setPixel(i_x, i_y, screenList.get(screenListIndex) .getImage(), data,
	 * dataIndex); dataIndex += 3; ledCounter++;
	 * 
	 * // open new port if required if (ledCounter >= LEDS_ON_PORT) {
	 * 
	 * channel += 2048; ledCounter = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff); } }
	 * 
	 * i_x++;
	 * 
	 * // iterate y-coordinates i_x (up) for (int i_y =
	 * screenList.get(screenListIndex).getY()[i_x] - 1; i_y >= 0; i_y--) {
	 * 
	 * setPixel(i_x, i_y, screenList.get(screenListIndex) .getImage(), data,
	 * dataIndex); dataIndex += 3; ledCounter++;
	 * 
	 * // open new port if required if (ledCounter >= LEDS_ON_PORT) {
	 * 
	 * channel += 2048; ledCounter = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff); } }
	 * } } else { // iterate x-coordinates for (int i_x =
	 * screenList.get(screenListIndex) .getY().length - 1; i_x >= 0; i_x--) {
	 * 
	 * // iterate y-coordinates i_x (down) for (int i_y =
	 * screenList.get(screenListIndex) .getY()[i_x] - 1; i_y >= 0; i_y--) {
	 * 
	 * setPixel(i_x, i_y, screenList.get(screenListIndex) .getImage(), data,
	 * dataIndex); dataIndex += 3; ledCounter++;
	 * 
	 * // open new port if required if (ledCounter >= LEDS_ON_PORT) {
	 * 
	 * channel += 2048; ledCounter = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff); } }
	 * 
	 * i_x--;
	 * 
	 * // iterate y-coordinates i_x (up) for (int i_y = 0; i_y <
	 * screenList.get(screenListIndex).getY()[i_x]; i_y++) {
	 * 
	 * setPixel(i_x, i_y, screenList.get(screenListIndex) .getImage(), data,
	 * dataIndex); dataIndex += 3; ledCounter++;
	 * 
	 * // open new port if required if (ledCounter >= LEDS_ON_PORT) {
	 * 
	 * channel += 2048; ledCounter = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff); } }
	 * } }
	 * 
	 * // padding bytes for remaining leds on port int rest; if ((rest = 96 -
	 * ledCounter) != 96) { System.out.println("REMAIN: " + rest); for (int i =
	 * 0; i < rest; i++) { dataIndex += 3; }
	 * 
	 * channel += 2048; ledCounter = 0;
	 * 
	 * data[dataIndex++] = (byte) (channel & 0xff); data[dataIndex++] = (byte)
	 * ((channel >> 8) & 0xff); data[dataIndex++] = (byte) ((LEDS_ON_PORT * 3) &
	 * 0xff); data[dataIndex++] = (byte) (((LEDS_ON_PORT * 3) >> 8) & 0xff); }
	 * 
	 * }
	 * 
	 * screenListIndex++;
	 * 
	 * }
	 * 
	 * // Map the Pixels //System.out.println(dataIndex); udp.send(data, ip,
	 * port);
	 * 
	 * }
	 * 
	 * }
	 */

	void setPixel(int ix, int iy, PImage image, byte data[], int dataIndex) {
		int rgb = image.get(ix, iy);

		// System.out.println("Setze Pixel:" +ix+" "+iy+" "+rgb);

		data[dataIndex + 2] = (byte) (rgb & 0xff);
		data[dataIndex + 1] = (byte) ((rgb >> 8) & 0xff);
		data[dataIndex] = (byte) ((rgb >> 16) & 0xff);

	}

}