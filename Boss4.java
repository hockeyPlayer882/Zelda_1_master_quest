import java.util.ArrayList;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.util.Random;
public class Boss4 extends Entity{ 
private int projectileTimer;
private int MprojectileTimer; 
public static ArrayList<Head> heads = new ArrayList<Head>();
public static BufferedImage boss4;
Random rand = new Random();
public class Head extends Entity{
   private BufferedImage head;
   public static int headNumCounter = 0;
   public static int headNum;
   public Head(int cx, int cy){
   this.hp = 3;
   //nothing yet can deal 3 damage, to the player has to use bombs or the magic rod
   this.defense = 3;
   //big ouchie to the person who gets hit by this boss.....
   this.damage = 6;
   this.speed = 10;
   headNumCounter++;
   headNum = headNumCounter;
   this.cx = headNum == 1 ? cx-unitSize:headNum == 3 ? cx+unitSize:cx;
   this.cy = headNum == 2 ? cy-unitSize:headNum == 4 ? cy+unitSize:cy;
   try{
             head = ImageIO.read(new File(".\\Image files\\headBoss4" + (headNum == 1 ? "A":headNum == 2 ? "W":headNum == 3 ? "D":"S") + ".png"));
   }catch (IOException ex) {
               
              System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
              System.out.println("When Java doesn't know how to Java .-.");
              System.out.println("Error details: ");
              ex.printStackTrace();
   }
   }
   public void draw(Graphics g,Driver driver){
      g.drawImage(head,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
   }
   public void move(Boss4 boss4){
   this.dir = boss4.dir;
   if(inv > 0) inv--;
   this.cy += this.dir == 'w' ? -speed: this.dir == 's' ? speed:0;
   this.cx += this.dir == 'd' ? speed: this.dir == 'a' ? -speed:0;
   }
   }
public Boss4(int cx, int cy){
this.projectileTimer = 60;
this.MprojectileTimer = 10;
heads.clear();
for(int i = 0;i < 4; i++) heads.add(new Head(cx,cy));
this.cx = cx;
this.cy = cy;
this.defense = 0;
this.maxInv = 60;
this.speed = 10;
this.MmovementTimer = 10;
//BEEEEEEEG ouchie if you get hit with the main body
this.damage = 12;
isBoss = true;
try{
          boss4 = ImageIO.read(new File(".\\Image files\\boss4.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}

public void shootProjectile(){
projectileTimer -= 1;
if(projectileTimer <= 0){
projectileTimer = MprojectileTimer;
int Rand = rand.nextInt(4);
Driver.projs.add(new Projectile(cx,cy,7,3, Rand == 0 ? 'w': Rand == 1 ? 's' : Rand == 2 ? 'a':'d'));
}
}
public void draw(Graphics g, Driver driver){
//decrease invincibility frames
if(inv > 0) inv--;
g.drawImage(boss4,this.cx,(this.cy-unitSize/2)-ActiveMenu.iterationNum,unitSize,unitSize,driver);

}
}