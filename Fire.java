import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import neozelda.audio.AudioEngine;

public class Fire extends Entity {
   private int lifetime = 30;
   private BufferedImage fire;
   public static int numFire = 0;
   private boolean persistant = false;
   public Fire(int cx, int cy, char dir, boolean persistant) {
      this.cx = cx;
      this.cy = cy;
      this.persistant = persistant;
      this.dir = dir;
      this.speed = 3;
      try {
         fire = ImageIO.read(new File("./Image files/fire.png"));
      } catch (IOException ex) {
         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }

   public void burn(Entity ent) {
      if (this.cx - unitSize / 2 < ent.cx + unitSize / 2
            && this.cx + unitSize / 2 > ent.cx - unitSize / 2
            && this.cy - unitSize / 2 < ent.cy + unitSize / 2
            && this.cy + unitSize / 2 > ent.cy - unitSize / 2
            && ent.inv <= 0 && ent.fireResistance == 0) {
         // if the enemy isn't immune to fire(fireResistance = 0, deal 1 point of damage
         ent.hp -= this.damage / (1 - ent.fireResistance);
         ent.inv = 60;

         AudioEngine.playClip("./sfx/LOZ_Enemy_Hit.wav");
      }
   }

   public void drawFire(Graphics g, Driver driver) {
      if(!persistant)
         this.lifetime -= 1;
      if (lifetime % 10 == 0)
         this.speed -= 1;
      this.moveEntity();
      if (lifetime > 0)
         g.drawImage(fire, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
      else {
         if (Fire.numFire > 0)
            Fire.numFire -= 1;
         Driver.fires.remove(this);
         this.cx = 999;
         this.cy = 999;
      }
   }
}