import java.util.ArrayList;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Color;
public class Boss3 extends Entity{ 
public static ArrayList<Head> heads = new ArrayList<Head>();
public static BufferedImage boss3;
Random rand = new Random();
public class Head extends Entity{
   private int projectileTimer;
   private int MprojectileTimer;
   private BufferedImage head;
   public Head(int cx, int cy){
   this.projectileTimer = 140;
   this.MprojectileTimer = 70;
   this.hp = 3;
   this.damage = 3;
   this.speed = 10;
   this.MmovementTimer = 20;
   this.cx = cx-unitSize;
   this.cy = cy-unitSize/2;
   this.defense = 0;
   this.isWeakToBoomerang = true;
   int Rand = rand.nextInt(3);
   this.dir = (Rand == 0 ? 'w': Rand == 1 ? 'a':'s');
   try{
             head = ImageIO.read(new File("./Image files/head.png"));
   }catch (IOException ex) {
               
              System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
              System.out.println("When Java doesn't know how to Java .-.");
              System.out.println("Error details: ");
              ex.printStackTrace();
   }
   }
   public void draw(Graphics g,Driver driver, Boss3 boss3){
      g.drawImage(head,this.cx-unitSize/2,(this.cy-unitSize/2)-ActiveMenu.iterationNum,unitSize,unitSize,driver);
      g.setColor(new Color(0,255,0));
      g.drawLine(this.cx,this.cy,boss3.cx,boss3.cy);
   }
   public void calcDirs(Boss3 boss3){
   //decreases movement timer
  this.movementTimer-= 1;
  //checks for a length collision or if the movement timer is decreased
  if(this.movementTimer<= 0 ||((this.cx-unitSize/2) < (boss3.cx-unitSize/2)-unitSize*3) || this.cx+(unitSize/2)>boss3.cx-unitSize/2||this.cy-unitSize/2<150||this.cy+unitSize/2>760){

  this.movementTimer = (int)((this.MmovementTimer+1)*Math.random());
  //randomly changes the enemies position unless a collision is detected, then the entity moves in the opposite direction
  int rand = (int)(4*Math.random());
  if(this.cy-unitSize/2<150){
  rand = 2;
  }
  else if(this.cy+unitSize/2>760){
  rand = 1;
  }
  else if ((this.cx-unitSize/2) < (boss3.cx-unitSize/2)-unitSize*3){
  rand = 4;
  }
  else if (this.cx+unitSize/2>(boss3.cx-unitSize/2)){
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
   if(inv > 0)inv--;
   this.cy += this.dir == 'w' ? -speed: this.dir == 's' ? speed:0;
   this.cx += this.dir == 'd' ? speed: this.dir == 'a' ? -speed:0;
   }
   public void shootProjectile(){
   projectileTimer -= 1;
   if(projectileTimer <= 0){
   projectileTimer = MprojectileTimer;
   Driver.projs.add(new Projectile(cx,cy,7,3,'a',1,0));
   }
   }
}
public Boss3(int cx, int cy, int numHeads){
heads.clear();
for(int i = 0;i < numHeads; i++) heads.add(new Head(cx,cy+(i*10)));
this.cx = cx;
this.cy = cy;
this.defense = 0;
this.maxInv = 60;
this.damage = 6;
isBoss = true;
try{
          boss3 = ImageIO.read(new File("./Image files/boss3.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void draw(Graphics g, Driver driver){
g.drawImage(boss3,this.cx,(this.cy-(int)(unitSize*1.5))-ActiveMenu.iterationNum,unitSize,unitSize*6,driver);

}
}