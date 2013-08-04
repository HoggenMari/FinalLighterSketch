import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;


public class Firework {

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/30877*@* */
	/* !do not delete the line above, required for linking your tweak if you re-upload */

	ArrayList<Fire> hanabi;

	final int FIRE_COUNT = 1000;
	final float X = 200;
	final float Y = 250;

	final float G = (float) 0.04;

	private PApplet p;

	public Firework(PApplet p, PVector pos, Color c){
		this.p = p;
				   
		   hanabi = new ArrayList<Fire>();
		   
		   for(int i=0; i<FIRE_COUNT; i++){
		     float r = p.random(0,PConstants.TWO_PI);
		     float R = p.random(0,2);
		     
		     hanabi.add(new Fire(pos.x,pos.y,R*PApplet.sin(r),R*PApplet.cos(r),c));
		   }
		
	}


	public void draw(PGraphics pg)
	{
	  pg.beginDraw();
	  pg.noStroke();
	  pg.colorMode(PConstants.HSB);
	  pg.fill(0,40);
	  pg.rect(0,0,p.width,p.height);
	  	  
	  for(Fire fire : hanabi){ 
	    fire.vx += 0;
	    fire.vy += G;
	    
	    fire.x += fire.vx;
	    fire.y += fire.vy;
	    
	    if(fire.lifetime-50>0){
	      pg.noStroke();
	      pg.fill(fire.col.getRGB(), // RGB
	         fire.lifetime-50); //Alpha
	        
	      pg.fill(fire.col.getRed(), fire.col.getGreen(), fire.col.getBlue(), fire.lifetime-50);
	      pg.ellipse(fire.x,fire.y,4,4); // draw the fire
	      fire.lifetime -= 0.5; // decrease lifetime
	    }else{
	    }
	  }
	  pg.endDraw();
	  
	  PImage img = pg.get();
	  p.image(img, 0, 0);
	}
	
	public boolean isDead() {
		for(Fire fire : hanabi) {
			if(fire.lifetime-50 > 0) {
				return false;
			}
		} 
		return true;
	}

}
