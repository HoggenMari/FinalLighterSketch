import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class ColorPoint
{
 
 PApplet p;
 private ArrayList<PointParticle> pointParticles;
 float posX;
 float posY;
 int user; 
 int MAX_POINT_PARTICLES = 3;

  
  public ColorPoint(PApplet p, int user)
  {
    this.p = p;
    this.user = user;
    
	pointParticles = new ArrayList<PointParticle>();
  }


public boolean equals(int userId) {
	if (user == userId) {
		return true;
	} else return false;
}

public void setPoint(float posX, float posY)
  {
	System.out.println("posX: "+posX+" posY: "+posY);
    this.posX = ((640-posX)/640)*300;
    this.posY = (posY/480)*240;
    
    for (int i = 0; i < MAX_POINT_PARTICLES; i++) {
		pointParticles.add(new PointParticle(p, this.posX, this.posY, p.random(-2,
				2), -3, 255));
	}
  }
  
  public void draw(PGraphics pg) {
	pg.colorMode(PConstants.HSB);
    pg.beginDraw();
    pg.stroke( p.frameCount % 256, 255, 255);

    for (Iterator<PointParticle> pointPartItr = pointParticles.iterator(); pointPartItr
			.hasNext();) {
		PointParticle pt = pointPartItr.next();
		if (pt.isDead()) {
			pointPartItr.remove();
		} else {
			pt.update();
			pt.draw(pg);
		}
	}
    
    //pg.strokeWeight(70);
    //pg.line(posX, posY, posX, posY);
    pg.endDraw();
  }
  
}