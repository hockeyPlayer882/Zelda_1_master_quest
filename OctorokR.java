import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class OctorokR extends Entity{
private int projectileTimer;
private int MprojectileTimer;
private BufferedImage octorokRW;
private BufferedImage octorokRS;
private BufferedImage octorokRA;
private BufferedImage octorokRD;
public OctorokR(int cx, int cy){
try{     
          if(octorokRW == null)octorokRW = ImageIO.read(new File("./Image files/octorokRW.png"));
          if(octorokRS == null)octorokRS = ImageIO.read(new File("./Image files/octorokRS.png"));
          if(octorokRA == null)octorokRA = ImageIO.read(new File("./Image files/octorokRA.png"));
          if(octorokRD == null)octorokRD = ImageIO.read(new File("./Image files/octorokRD.png"));
}catch (IOException ex) {
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
this.projectileTimer = 240;
this.MprojectileTimer = 240;
this.cx = cx;
this.cy = cy;
this.damage = 1;
this.Mstun = 80;
this.hp = 1;
this.Mhp = 1;
this.defense = 0;
this.maxInv = 15;
this.dir = 'w';
//creates a random delay for changing the enemies direction from 40 to 120
this.MmovementTimer = 40;
this.movementTimer = (int)(MmovementTimer+Math.random()*(80));
}
public int setProjectileTimer(){
return this.projectileTimer;
}
public int getMprojectileTimer(){
return this.MprojectileTimer;
}
public void resetProjectileTimer(){
this.projectileTimer = (int)(MmovementTimer+Math.random()*80);
}
public void draw(Graphics g, Driver driver){
BufferedImage drawImg = (dir == 'w' ? octorokRW: dir == 's'? octorokRS: dir=='a' ? octorokRA:octorokRD);
g.drawImage(drawImg,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
}
public void shootProjectile(){
this.projectileTimer -= 1;
if(projectileTimer <= 0){
if(this.dir != 'n') Driver.projs.add(new Projectile(this.cx,this.cy,1,7,this.dir,1,0));
this.dir = 'n';
this.resetProjectileTimer();
this.movementTimer = 0;
calcEnemyDir();
}
}
}