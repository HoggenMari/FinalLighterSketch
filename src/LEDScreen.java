import processing.core.PApplet;
import processing.core.PImage;

public class LEDScreen {
	private int[] y;
	
	public int[] getY() {
		return y;
	}
	
	public int getMaxY() {
		 int maximum = y[0];   // start with the first value
		    for (int i=1; i<y.length; i++) {
		        if (y[i] > maximum) {
		            maximum = y[i];   // new maximum
		        }
		    }
		    return maximum;
	}

	private PApplet p;
	private PImage image;

	public LEDScreen(int[] y, PApplet p) {
		this.p = p;
		this.y = y;
	}
	
	public LEDScreen(int x, int y, PApplet p) {
		this.p = p;
		this.y = new int[x];
		for(int ix=0; ix<x; ix++) {
			this.y[ix] = y;
		}
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
	
	void drawOnGui(int pos_x, int pos_y, String label) {
		for (int ix = 0; ix < y.length; ix = ix + 1) {
			for (int iy = 0; iy < y[ix]; iy = iy + 1) {
				int rgb = image.get(ix, iy);
				p.fill(rgb);
				p.rect(ix * 8 + pos_x, iy * 8 + pos_y+10, 8, 8);
				// System.out.println("Pixel: "+ix+"x"+iy+" "+rgb);
			}
		}
		p.fill(0);
		p.textSize(10);
		p.text(label, pos_x, pos_y);
	}

}