import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Skeleton extends Entity{
//this class is super boring, but interesting nonetheless.
private BufferedImage skeleton;
public Skeleton(int cx, int cy){
this.cx = cx;
this.cy = cy;
this.hp = 1;
this.damage = 2;
this.Mstun = 200;//skeletons are SUPER weak to stunning, they will stay stunned for 2 seconds
this.cx = cx;
this.cy = cy;
this.Mhp = 1;
this.defense = 0;
this.maxInv = 30;
this.dir = 'w';
numKeyEnemiesAlive += 1;
//creates a random delay for changing the enemies direction from 40 to 120
this.MmovementTimer = 40;
this.movementTimer = (int)(MmovementTimer+Math.random()*(80));
 try{
             skeleton = ImageIO.read(new File(".\\Image files\\skeleton.png"));
             
   }catch (IOException ex) {
               
              System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
              System.out.println("Error details: ");
              ex.printStackTrace();
   }
}
public void draw(Graphics g, Driver driver){
   g.drawImage(skeleton,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
}
}