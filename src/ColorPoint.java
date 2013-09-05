import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class ColorPoint
{
 
 PApplet p;
 float posX;
 float posY;
 int user; 
  
  public ColorPoint(PApplet p, int user)
  {
    this.p = p;
    this.user = user;
  }


public boolean equals(int userId) {
	if (user == userId) {
		return true;
	} else return false;
}

public void setPoint(float posX, float posY)
  {
    this.posX = posX;
    this.posY = posY;
  }
  
  public void draw(PGraphics pg) {
	pg.colorMode(PConstants.HSB);
    pg.beginDraw();
    pg.stroke( p.frameCount % 256, 255, 255);
    pg.strokeWeight(50);
    pg.line(posX, posY, posX, posY);
    pg.endDraw();
  }
  
}