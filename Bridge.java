import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Bridge extends Entity{
private BufferedImage bridge;
//Yes i'm aware it says "bridge", but the "bridge" can double as a dock for the raft
public Bridge(int cx, int cy, char dir){
this.cx = cx;
this.cy = cy;
this.dir = dir;
try{
          if(bridge == null)bridge = ImageIO.read(new File(".\\Image files\\bridge.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public Bridge(int cx,int cy){
this.cx = cx;
this.cy = cy;
dir = ' ';
try{
          if(bridge == null)bridge = ImageIO.read(new File(".\\Image files\\bridge.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void draw(Graphics g, Driver driver){
      g.drawImage(bridge,this.cx-unitSize/2,this.cy-unitSize/2,unitSize,unitSize,driver);
}
public void loadDock(Player player, Graphics g, Driver driver){
   if(dir != ' '&& Player.hasRaft){
      if(this.cx-unitSize/2<player.cx+unitSize/2
   && this.cx+unitSize/2>player.cx-unitSize/2 
   && this.cy-unitSize/2<player.cy+unitSize/2 
   && this.cy+unitSize/2>player.cy-unitSize/2){
      if(player.stun == 0 && !checkOppositeDirs(this,player)){
       player.dir = this.dir;
       player.stun = 999;
       }
      else if(checkOppositeDirs(this,player)){
       player.stun = 0;
       for(int i = 0; i < 15; i++)
         player.moveEntity();
       
       player.dir = 'n';
       }
       }
   if(player.stun > 0){
   player.cx += (player.dir == 'a' ? -3: player.dir == 'd' ? 3:0);
   player.cy += (player.dir == 's' ? 3: player.dir == 'w' ? -3:0);
   
   }
   }
   }
}