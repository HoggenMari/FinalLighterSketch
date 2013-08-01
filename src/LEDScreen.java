import java.awt.Color;

import processing.core.PApplet;
import processing.core.PImage;

public class LEDScreen {
	private int[] y;
	private int xRes, yRes;
	private PApplet p;
	private PImage image;

	//Constructor for variable y-Resolution
	public LEDScreen(int[] y, PApplet p) {
		this.p = p;
		this.y = y;
		this.xRes = y.length;
		this.yRes = getMaxY(y);
	}
	
	//Constructor for rect y-Resolution
	public LEDScreen(int x, int y, PApplet p) {
		this.p = p;
		this.y = new int[x];
		for(int ix=0; ix<x; ix++) {
			this.y[ix] = y;
		}
		this.xRes = x;
		this.yRes = y;
	}
	
	private int getMaxY(int []y) {
		 int maximum = y[0];   // start with the first value
		    for (int i=1; i<y.length; i++) {
		        if (y[i] > maximum) {
		            maximum = y[i];   // new maximum
		        }
		    }
		    return maximum;
	}
	
	public void update(PImage image) {
		this.image = image;
	}

	
	public PImage getImage() {
		return image;
	}


	void setPixel(int ix, int iy, int rgb) {
		this.image.set(ix, iy, rgb);
	}
	
	void drawOnGui(int pos_x, int pos_y) {
		for (int ix = 0; ix < y.length; ix = ix + 1) {
			for (int iy = 0; iy < y[ix]; iy = iy + 1) {
				int rgb = image.get(ix, iy);
				p.fill(rgb);
				p.rect(ix * 8 + pos_x, iy * 8 + pos_y, 8, 8);
				// System.out.println("Pixel: "+ix+"x"+iy+" "+rgb);
			}
		}
	}
	
	//getter & setter
	public int[] getY() {
		return y;
	}
	
	public int getxRes() {
		return xRes;
	}

	public void setxRes(int xRes) {
		this.xRes = xRes;
	}

	public int getyRes() {
		return yRes;
	}

	public void setyRes(int yRes) {
		this.yRes = yRes;
	}

}