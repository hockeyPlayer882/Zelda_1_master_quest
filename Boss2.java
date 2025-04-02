import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
public class Boss2 extends Entity{ 
private int projectileTimer;
private int MprojectileTimer; 
public static BufferedImage boss2;
public Boss2(){
this.projectileTimer = 100;
this.MprojectileTimer = 100;
this.dir = 'a';
this.cx = 400;
this.cy = 200;
this.defense = 0;
this.maxInv = 60;
this.hp = 1;
this.damage = 1;
isBoss = true;
try{
          boss2 = ImageIO.read(new File("./Image files/boss2.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void calcDir(){
if(this.cx <= 200) this.dir = 'd';
else if (this.cx >= 700) this.dir = 'a';
}
public void resetProjectileTimer(){
this.projectileTimer = (int)(MprojectileTimer+Math.random()*MprojectileTimer/10);
}
public void shootProjectile(){
this.projectileTimer -= 1;
if(projectileTimer <= 0){
if(this.dir != 'n'){
   //spawn 1 super fast projectile that absolutely destroys the player and deals WAY to much damage;
   Driver.projs.add(new Projectile(cx,cy,2,20,'w',1,0));
 }
this.resetProjectileTimer();
this.movementTimer = 0;
calcDir();
}
}
public void draw(Graphics g, Driver driver){
g.drawImage(boss2,this.cx-unitSize/2,(this.cy-unitSize/2)-ActiveMenu.iterationNum,unitSize,unitSize,driver);
}
}