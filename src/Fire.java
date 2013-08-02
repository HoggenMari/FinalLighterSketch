import java.awt.Color;

public class Fire{
  float x;
  float y;
  float vx;
  float vy;
  
  Color col;
  
  float lifetime = 100;
  
  public Fire(float x, float y, float vx, float vy, Color col){
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.col = col;
  }
}