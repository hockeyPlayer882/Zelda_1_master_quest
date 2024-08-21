import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public abstract class Arrow{
static int cx = 999;
static int cy = 999;
static int damage = 2;
public static char dir = 'n';
public static int speed = 7;
public static int width = 40;
public static int height = 60;
public static BufferedImage arrowW;
public static BufferedImage arrowS;
public static BufferedImage arrowA;
public static BufferedImage arrowD;
public static void move(){
switch(dir){
      case 'w':
      cy -= speed;
      break;
      case 's':
      cy += speed;
      break;
      case 'a':
      cx -= speed;
      break;
      case 'd':
      cx += speed;
      break;
      }
}
public static void hurt(Entity ent){
if(ent.cx-Entity.unitSize/2<cx+width/2
   && ent.cx+Entity.unitSize/2>cx-width/2 
   && ent.cy-Entity.unitSize/2<cy+height/2 
   && ent.cy+Entity.unitSize/2>cy-height/2
   && ent.inv <= 0){
   //checks if the entity is a boss and if so, the only boss that can be defeated with arrows(boss2) can only be defeated with a shot facing upward
   if(ent.isBoss == false || dir == 'w') {
   //hurt entity based on damage calculations
   ent.hp -= (int)(damage/(ent.defense+1));
   ent.inv = 60; 
   }  
   }
}
public static void draw(Driver driver, Graphics g){
   try{
   if(arrowW == null) arrowW = ImageIO.read(new File(".\\Image files\\arrowW.png"));
   if(arrowS == null) arrowS = ImageIO.read(new File(".\\Image files\\arrowS.png"));
   if(arrowA == null) arrowA = ImageIO.read(new File(".\\Image files\\arrowA.png"));
   if(arrowD == null) arrowD = ImageIO.read(new File(".\\Image files\\arrowD.png"));
   }
   catch (IOException ex) {
                 System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
                 System.out.println("Error details: ");
                 ex.printStackTrace();
   }
   if(dir == 'a'){
      width = 60;
      height = 40;
      g.drawImage(arrowA,cx-Entity.unitSize/2,cy-Entity.unitSize/2+ActiveMenu.iterationNum,width,height,driver);
   }
   else if (dir == 'd'){
      width = 40;
      height = 60;
      g.drawImage(arrowD,cx-Entity.unitSize/2,cy-Entity.unitSize/2+ActiveMenu.iterationNum,width,height,driver);
   }
   else if (dir == 'w'){
      width = 40;
      height = 60;
      g.drawImage(arrowW,cx-Entity.unitSize/2,cy-Entity.unitSize/2+ActiveMenu.iterationNum,width,height,driver);
   }
   else if (dir == 's'){
      width = 60;
      height = 40;
      g.drawImage(arrowS,cx-Entity.unitSize/2,cy-Entity.unitSize/2+ActiveMenu.iterationNum,width,height,driver);
   }
}
public static void spawn(Player player){
player.rubpees -= 1;
switch(player.stDir){
case 'w':
dir = 'w';
cy = player.cy-Entity.unitSize - Entity.unitSize/2;
cx = player.cx;
break;
case 's':
dir = 's';
cy = (player.cy+Entity.unitSize/2)+Entity.unitSize/2;
cx = player.cx;
break;
case 'a':
dir = 'a';
cx = player.cx-Entity.unitSize - Entity.unitSize/2;
cy = player.cy;
break;
case 'd':
dir = 'd';
cx = player.cx+Entity.unitSize;
cy = player.cy;
break;
}
}
}