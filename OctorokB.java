import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class OctorokB extends Entity{
private int projectileTimer;
private int MprojectileTimer;
private BufferedImage octorokBW;
private BufferedImage octorokBS;
private BufferedImage octorokBA;
private BufferedImage octorokBD;
public OctorokB(int cx, int cy){
super(6,'w',cx,cy,2,2,0,1,(int)Math.random()*20+80,40,0);
try{     
          if(octorokBW == null){
            octorokBW = ImageIO.read(new File("./Image files/octorokBW.png"));
            octorokBS = ImageIO.read(new File("./Image files/octorokBS.png"));
            octorokBA = ImageIO.read(new File("./Image files/octorokBA.png"));
            octorokBD = ImageIO.read(new File("./Image files/octorokBD.png"));
}
}catch (IOException ex) {
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
this.projectileTimer = 240;
this.MprojectileTimer = 240;
this.Mstun =60;
this.maxInv = 30;
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
BufferedImage drawImg = (dir == 'w' ? octorokBW: dir == 's'? octorokBS: dir=='a' ? octorokBA:octorokBD);
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