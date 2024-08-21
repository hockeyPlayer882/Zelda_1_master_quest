import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
public class TriforcePiece extends Entity{
private BufferedImage triforcePiece;
public static int animationTimer = 60;
public static final int ManimationTimer = 60;
public TriforcePiece(int cx, int cy){
this.cx = cx;
this.cy = cy;

try{
          triforcePiece = ImageIO.read(new File(".\\Image files\\triforcePiece.png"));
}catch (IOException ex) {
            
           System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
           System.out.println("When Java doesn't know how to Java .-.");
           System.out.println("Error details: ");
           ex.printStackTrace();
}
}
public void collectTriforce(Room room, Player player){
if(this.cx-unitSize/2<player.cx+unitSize/2 && this.cx+unitSize/2>player.cx-unitSize/2 && this.cy-unitSize/2 <player.cy+unitSize/2 && this.cy+unitSize/2 > player.cy-unitSize/2){
this.cx = 999;
this.cy = 999;
player.hp = player.Mhp;
ActiveMenu.numTriforcePieces += 1;
animationTimer = ManimationTimer;
//special case for the player's direction-> shows an animation of link holding the triforce when he collects it for 60 in game ticks;
player.dir = 't';
}
if(player.dir == 't'){
if(animationTimer >= 0) animationTimer -= 1;
else {
player.dir = 's';
player.cx = 400;
player.cy = 800;
Player.location[0] = 0;
Player.location[1] = 0;
LoadingZone.exit(player, room);
player.stDir = player.dir;
player.dir = 'n';
}
}
}
public void draw(Graphics g, Driver driver){
g.drawImage(triforcePiece,this.cx-unitSize/2,(this.cy-unitSize/2)-ActiveMenu.iterationNum,unitSize,unitSize,driver);
}
}