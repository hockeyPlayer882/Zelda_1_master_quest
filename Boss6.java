import java.util.ArrayList;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Color;
public class Boss6 extends Entity{ 
public static ArrayList<Head6> heads6 = new ArrayList<Head6>();
public static BufferedImage boss6;
Random rand = new Random();
public class Head6 extends Entity{
   private int projectileTimer;
   private int MprojectileTimer;
   private BufferedImage head6;
   public Head6(int cx, int cy){
   this.projectileTimer = 140;
   this.MprojectileTimer = 70;
   this.hp = 10;
   this.damage = 5;
   this.speed = 12;
   this.MmovementTimer = 20;
   this.cx = cx-unitSize;
   this.cy = cy-unitSize/2;
   this.defense = 1;
   this.isWeakToBoomerang = false;
   int Rand = rand.nextInt(3);
   this.dir = (Rand == 0 ? 'w': Rand == 1 ? 'a':'s');
   try{
             head6 = ImageIO.read(new File(".\\Image files\\head6.png"));
   }catch (IOException ex) {
               
              System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
              System.out.println("When Java doesn't know how to Java .-.");
              System.out.println("Error details: ");
              ex.printStackTrace();
   }
   }
   public void draw(Graphics g,Driver driver, Boss6 boss6){
      if(inv %2 == 0)g.drawImage(head6,this.cx-unitSize/2,(this.cy-unitSize/2)-ActiveMenu.iterationNum,unitSize,unitSize,driver);
      g.setColor(new Color(127,127,127));
      g.drawLine(this.cx,this.cy,boss6.cx,boss6.cy);
   }
   public void calcDirs(Boss6 boss6){
   //decreases movement timer
  this.movementTimer-= 1;
  //checks for a length collision or if the movement timer is decreased
  if(this.movementTimer<= 0 ||((this.cx-unitSize/2) < (boss6.cx-unitSize/2)-unitSize*3) || this.cx+(unitSize/2)>boss6.cx-unitSize/2||this.cy-unitSize/2<150||this.cy+unitSize/2>760){

  this.movementTimer = (int)((this.MmovementTimer+1)*Math.random());
  //randomly changes the enemies position unless a collision is detected, then the entity moves in the opposite direction
  int rand = (int)(4*Math.random());
  if(this.cy-unitSize/2<150){
  rand = 2;
  }
  else if(this.cy+unitSize/2>760){
  rand = 1;
  }
  else if ((this.cx-unitSize/2) < (boss6.cx-unitSize/2)-unitSize*3){
  rand = 4;
  }
  else if (this.cx+unitSize/2>(boss6.cx-unitSize/2)){
  rand = 3;
  }
  
  switch(rand){
  case 1:
  this.dir ='w';
  break;
  case 2:
  this.dir = 's';
  break;
  case 3:
  this.dir = 'a';
  break;
  case 4:
  this.dir = 'd';
  break;
  }
  }
  }
   public void move(){
   if(inv > 0) inv--;
   this.cy += this.dir == 'w' ? -speed: this.dir == 's' ? speed:0;
   this.cx += this.dir == 'd' ? speed: this.dir == 'a' ? -speed:0;
   }
   public void shootProjectile(){
   projectileTimer -= 1;
   if(projectileTimer <= 0){
   projectileTimer = MprojectileTimer;
   Driver.projs.add(new Projectile(cx,cy,1,5,'a'));
   }
   }
}
public Boss6(int cx, int cy, int numHeads){
heads6.clear();
for(int i = 0;i < numHeads; i++) heads6.add(new Head6(cx,cy+(i*10)));
this.cx = cx;
this.cy = cy;
this.defense = 0;
this.maxInv = 60;
this.damage = 6;
isBoss = true;
try{
          boss6 = ImageIO.read(new File(".\\Image files\\boss6.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void draw(Graphics g, Driver driver){
g.drawImage(boss6,this.cx,(this.cy-(int)(unitSize*1.5))-ActiveMenu.iterationNum,unitSize,unitSize*6,driver);

}
}