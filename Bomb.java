import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Bomb {
public int cx;
public int cy;
public int explosionTimer = 60;
public static int numBombs = 0;
private BufferedImage bomb;
public Bomb(int cx, int cy){
this.cx = cx;
this.cy = cy;
try{     
          bomb = ImageIO.read(new File("./Image files/bomb.png"));
}catch (IOException ex) {
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void drawBomb(Driver driver, Graphics g){
g.drawImage(bomb,this.cx-Entity.unitSize/2,(this.cy-Entity.unitSize/2)+ActiveMenu.iterationNum,Entity.unitSize,Entity.unitSize,driver);
}
public void explode(){
if(this.explosionTimer == 0){
   Driver.bombs.remove(this);
   numBombs -= 1;
   Driver.explosions.add(new Explosion(this.cx-Entity.unitSize,this.cy-Entity.unitSize));
   Driver.explosions.add(new Explosion(this.cx,this.cy-Entity.unitSize));
   Driver.explosions.add(new Explosion(this.cx+Entity.unitSize,this.cy-Entity.unitSize));
   Driver.explosions.add(new Explosion(this.cx-Entity.unitSize,this.cy));
   Driver.explosions.add(new Explosion(this.cx,this.cy));
   Driver.explosions.add(new Explosion(this.cx+Entity.unitSize,this.cy));
   Driver.explosions.add(new Explosion(this.cx-Entity.unitSize,this.cy+Entity.unitSize));
   Driver.explosions.add(new Explosion(this.cx,this.cy+Entity.unitSize));
   Driver.explosions.add(new Explosion(this.cx+Entity.unitSize,this.cy+Entity.unitSize));
   }
   else if(!Player.isPaused) this.explosionTimer -= 1;
}
}