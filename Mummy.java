import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Mummy extends Entity {
   private BufferedImage mummy;

   public Mummy(int cx, int cy) {
      this.cx = cx;
      this.cy = cy;
      this.hp = 3;
      this.damage = 6;
      this.Mstun = 10;
      this.cx = cx;
      this.cy = cy;
      this.Mhp = 3;
      this.defense = 1;
      this.maxInv = 30;
      this.dir = 'w';
      numKeyEnemiesAlive += 1;
      // creates a random delay for changing the enemies direction from 1 to 80
      this.MmovementTimer = 40;
      this.movementTimer = (int) (MmovementTimer + Math.random() * (80));
      try {
         mummy = ImageIO.read(new File("./Image files/mummy.png"));

      } catch (IOException ex) {

         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }

   public void draw(Graphics g, Driver driver) {
      g.drawImage(mummy, cx - unitSize / 2, cy - unitSize / 2, unitSize, unitSize, driver);
   }
}