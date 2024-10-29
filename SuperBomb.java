import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;

public class SuperBomb extends Entity{
    public float width;
    public int explosionTimer = 120;
    public static int numBombs = 0;
    public static BufferedImage superBomb;

    public SuperBomb(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
        this.width = 10;
        try {
            superBomb = ImageIO.read(new File("./Image files/superBomb.png"));
        } catch (IOException ex) {
            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }
    public static void init(){
        try {
            superBomb = ImageIO.read(new File("./Image files/superBomb.png"));
        } catch (IOException ex) {
            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }
    public void drawBomb(Driver driver, Graphics g, int X) {
        Player.hasSuperBomb = false;
        if(explosionTimer > 0) {
            explosionTimer--;
            g.drawImage(superBomb, this.cx - Entity.unitSize / 2, (this.cy - Entity.unitSize / 2) + ActiveMenu.iterationNum,
                Entity.unitSize * 2, Entity.unitSize * 2, driver);
        }
        else{
            g.setColor(new Color(255,255,0));
            g.fillOval(cx-(int)width/2,cy-(int)width/2,(int)width,(int)width);
            width*= 1.05;
            Driver.projs.clear();
            if(width >= 1600){
                for(int x = 0; x < Room.currentRoom.size();x++)
                    for(int y = 0; y < Room.currentRoom.get(x).size();y++){
                        if(Room.currentRoom.get(x).get(y)instanceof Boss6)
                            Boss6.heads6.clear();
                        if(!(Room.currentRoom.get(x).get(y) instanceof Boss7))
                            Room.currentRoom.get(x).get(y).hp = 0;
                    }
                Room.currentRoom.get(X).remove(this);
            }
        }
        
    } 
}