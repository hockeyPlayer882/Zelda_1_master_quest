import java.awt.image.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class ShieldEater extends Entity{
private BufferedImage shieldEater;
public static boolean playerIsStuck = false;
private boolean playerIsStuckThis = false;
public ShieldEater(int cx, int cy){
this.cx = cx;
this.cy = cy;
this.MmovementTimer = 120;
this.Mstun = 50;
this.fireResistance = 1;
this.hp = 1;
numKeyEnemiesAlive += 1;
try{
if(shieldEater == null) shieldEater = ImageIO.read(new File(".\\Image files\\shieldEater.png"));
}
catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void hurt(Player player){
if(this.cx-unitSize/2<player.cx+unitSize/2
   && this.cx+unitSize/2>player.cx-unitSize/2 
   && this.cy-unitSize/2<player.cy+unitSize/2 
   && this.cy+unitSize/2>player.cy-unitSize/2){
   //checks if the player is using the cane, if so, this sprite will instantly die if it hits the player
   if(Cane.isActive){
      this.hp = 0;
   }
   else{
   player.dir = 's';
   //locks the player and shieldEater if the player isn't already stuck and the player is only stuck to one shieldEater
   if(!playerIsStuck || playerIsStuckThis){
   player.cx = this.cx;
   player.cy = this.cy;
   //offset used to make the shieldEater defeatable
   player.cy -= 10;
   this.speed = 0;
   this.dir = 'n';
   this.cx = player.cx ;
   this.cy = player.cy + 10;
   playerIsStuckThis = true;
   }
   playerIsStuck = true;
   if(player.inv <= 0) {
   player.hp -= this.damage;
   player.inv = 60;
   }
   else player.inv -= 1;
   //"breaks" the player's shield(makes it 0)
   player.shieldStrength = 0;
   }
}
} 
public void draw(Graphics g, Driver driver){
g.drawImage(shieldEater,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
}
}