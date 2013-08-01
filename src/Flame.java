import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Flame
{
 
 PApplet p;
 int posX;
 int posY;
 int lighter; 
  
  public Flame(PApplet p, int lighter)
  {
    this.p = p;
    this.lighter = lighter;
  }


public boolean equals(int lighterID) {
	if (lighter == lighterID) {
		return true;
	} else return false;
}

public void setFlame(int posX, int posY)
  {
    this.posX = posX;
    this.posY = posY;
  }
  
  public void draw(PGraphics pg) {
	pg.colorMode(PConstants.RGB);
    pg.beginDraw();
    pg.stroke( 255, 80, 80);
    pg.strokeWeight(100);
    pg.line(posX, posY, posX, posY);
    pg.endDraw();
  }
  
}