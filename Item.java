import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import neozelda.AudioEngine;

public class Item extends Entity {
   // keeps track of the increment value & the correct item
   public int value;
   public String type;
   private BufferedImage rubpee5;
   private BufferedImage rubpee;
   private BufferedImage heart;
   private BufferedImage key;
   private BufferedImage heartContainer;

   // constructor
   public Item(int cx, int cy, int value, String type) {
      this.cx = cx;
      this.cy = cy;
      this.value = value;
      this.type = type;
      try {
         rubpee5 = ImageIO.read(new File("./Image files/Rubpee5.png"));
         rubpee = ImageIO.read(new File("./Image files/Rubpee.png"));
         heart = ImageIO.read(new File("./Image files/fullHeart.png"));
         key = ImageIO.read(new File("./Image files/key.png"));
         heartContainer = ImageIO.read(new File("./Image files/heartContainer.png"));

      } catch (IOException ex) {

         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }

   /**
    * collects the item if the player hits it
    * 
    * @param player needed to check if the player hits an item
    */
   public void collectItem(Player player) {
      if ((this.cx - unitSize / 2 < player.cx + unitSize / 2 && this.cx + unitSize / 2 > player.cx - unitSize / 2
            && this.cy - unitSize / 2 < player.cy + unitSize / 2 && this.cy + unitSize / 2 > player.cy - unitSize / 2)
            || !type.equals("key") && Boomerang.cx < 800 && Boomerang.cy < 800
                  && (this.cx - unitSize / 2 < Boomerang.cx + unitSize / 2
                        && this.cx + unitSize / 2 > Boomerang.cx - unitSize / 2
                        && this.cy - unitSize / 2 < Boomerang.cy + unitSize / 2
                        && this.cy + unitSize / 2 > Boomerang.cy - unitSize / 2)) {
         if (type.equals("heart")) {
            player.heal(this.value);
            AudioEngine.playClip("./sfx/LTTP_RefillHealth.wav");
         }
         if (type.equals("rubpee") || type.equals("rubpee5")) {
            player.rubpees += this.value;
         }
         if (type.equals("bombs")) {
            player.bombs += this.value;
         }
         if (type.equals("key")) {
            AudioEngine.playClip("./sfx/LTTP_Get_Key_StereoL.wav");

            player.keys += this.value;
            if (Player.level == 1) {
               if (Player.location[0] == 1 && Player.location[1] == 0)
                  LoadingZone.keyArray[0][0] = true;
               if (Player.location[0] == -1 && Player.location[1] == 3)
                  LoadingZone.keyArray[0][1] = true;
            } else if (Player.level == 2) {
               if (Player.location[0] == -1 && Player.location[1] == 0)
                  LoadingZone.keyArray[1][0] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 1)
                  LoadingZone.keyArray[1][1] = true;
               else if (Player.location[0] == 1 && Player.location[1] == 0)
                  LoadingZone.keyArray[1][2] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 0)
                  LoadingZone.keyArray[1][3] = true;
               else if (Player.location[0] == 3 && Player.location[1] == 0)
                  LoadingZone.keyArray[1][4] = true;
               else if (Player.location[0] == 3 && Player.location[1] == 2)
                  LoadingZone.keyArray[1][5] = true;
            } else if (Player.level == 3) {
               if (Player.location[0] == -1 && Player.location[1] == 0)
                  LoadingZone.keyArray[2][0] = true;
               else if (Player.location[0] == 0 && Player.location[1] == 1)
                  LoadingZone.keyArray[2][1] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 0)
                  LoadingZone.keyArray[2][2] = true;
               else if (Player.location[0] == 3 && Player.location[1] == 2)
                  LoadingZone.keyArray[2][3] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 1)
                  LoadingZone.keyArray[2][4] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 0)
                  LoadingZone.keyArray[2][5] = true;
            } else if (Player.level == 4) {
               if (Player.location[0] == 1 && Player.location[1] == 0)
                  LoadingZone.keyArray[3][0] = true;
               else if (Player.location[0] == 0 && Player.location[1] == 1)
                  LoadingZone.keyArray[3][1] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 3)
                  LoadingZone.keyArray[3][2] = true;
               else if (Player.location[0] == 0 && Player.location[1] == 3)
                  LoadingZone.keyArray[3][3] = true;
               else if (Player.location[0] == 1 && Player.location[1] == 3)
                  LoadingZone.keyArray[3][4] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 3)
                  LoadingZone.keyArray[3][5] = true;
               else if (Player.location[0] == 3 && Player.location[1] == -2)
                  LoadingZone.keyArray[3][6] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 2)
                  LoadingZone.keyArray[3][7] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 2)
                  LoadingZone.keyArray[3][8] = true;
            } else if (Player.level == 5) {
               if (Player.location[0] == 1 && Player.location[1] == 0)
                  LoadingZone.keyArray[4][0] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 1)
                  LoadingZone.keyArray[4][1] = true;
               else if (Player.location[0] == 2 && Player.location[1] == -1)
                  LoadingZone.keyArray[4][2] = true;
               else if (Player.location[0] == 2 && Player.location[1] == -2)
                  LoadingZone.keyArray[4][3] = true;
               else if (Player.location[0] == 3 && Player.location[1] == 1)
                  LoadingZone.keyArray[4][4] = true;
               else if (Player.location[0] == 3 && Player.location[1] == -2)
                  LoadingZone.keyArray[4][5] = true;
               else if (Player.location[0] == 4 && Player.location[1] == 0)
                  LoadingZone.keyArray[4][6] = true;
               else if (Player.location[0] == 4 && Player.location[1] == -3)
                  LoadingZone.keyArray[4][7] = true;
            } else if (Player.level == 6) {
               if (Player.location[0] == 1 && Player.location[1] == 0)
                  LoadingZone.keyArray[5][0] = true;
               else if (Player.location[0] == 0 && Player.location[1] == 1)
                  LoadingZone.keyArray[5][1] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 2)
                  LoadingZone.keyArray[5][2] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 1)
                  LoadingZone.keyArray[5][3] = true;
               else if (Player.location[0] == 2 && Player.location[1] == 0)
                  LoadingZone.keyArray[5][4] = true;
               else if (Player.location[0] == 1 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][5] = true;
               else if (Player.location[0] == 0 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][6] = true;
               else if (Player.location[0] == -1 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][7] = true;
               else if (Player.location[0] == -2 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][8] = true;
               else if (Player.location[0] == -3 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][9] = true;
               else if (Player.location[0] == -4 && Player.location[1] == -1)
                  LoadingZone.keyArray[5][10] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 2)
                  LoadingZone.keyArray[5][11] = true;
            } else if (Player.level == 7) {
               if (Player.location[0] == -1 && Player.location[1] == 1)
                  LoadingZone.keyArray[6][0] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 1)
                  LoadingZone.keyArray[6][1] = true;
               else if (Player.location[0] == -3 && Player.location[1] == 1)
                  LoadingZone.keyArray[6][2] = true;
               else if (Player.location[0] == -4 && Player.location[1] == 1)
                  LoadingZone.keyArray[6][3] = true;
               else if (Player.location[0] == -4 && Player.location[1] == 2)
                  LoadingZone.keyArray[6][4] = true;
               else if (Player.location[0] == -4 && Player.location[1] == 3)
                  LoadingZone.keyArray[6][5] = true;
               else if (Player.location[0] == -4 && Player.location[1] == 4)
                  LoadingZone.keyArray[6][6] = true;
               else if (Player.location[0] == -3 && Player.location[1] == 4)
                  LoadingZone.keyArray[6][7] = true;
               else if (Player.location[0] == -2 && Player.location[1] == 4)
                  LoadingZone.keyArray[6][8] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 4)
                  LoadingZone.keyArray[6][9] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 3)
                  LoadingZone.keyArray[6][10] = true;
               else if (Player.location[0] == -1 && Player.location[1] == 2)
                  LoadingZone.keyArray[6][11] = true;
            } else if (Player.location[0] == 3 && Player.location[1] == -2)
               LoadingZone.keyArray[4][6] = true;
         }
         if (type.equals("heartContainer")) {
            AudioEngine.playHighlight("./Sound files/secret_z1.wav");

            player.Mhp += this.value;
            player.heal(this.value);
            if (Player.location[0] == 9 && Player.location[1] == 12)
               LoadingZone.heartContainers[0] = true;
            else if (Player.location[0] == 11 && Player.location[1] == 12)
               LoadingZone.heartContainers[1] = true;
            else if (Player.location[0] == 6 && Player.location[1] == 13)
               LoadingZone.heartContainers[2] = true;
            else if (Player.location[0] == 9 && Player.location[1] == 13)
               LoadingZone.heartContainers[3] = true;
            else if (Player.location[0] == 15 && Player.location[1] == 10)
               LoadingZone.heartContainers[4] = true;
            else if (Player.location[0] == 15 && Player.location[1] == 12)
               LoadingZone.heartContainers[5] = true;
            else if (Player.location[0] == 16 && Player.location[1] == 12)
               LoadingZone.heartContainers[6] = true;
            else if (Player.location[0] == 9 && Player.location[1] == 15)
               LoadingZone.heartContainers[7] = true;
            else if (Player.location[0] == 9 && Player.location[1] == 6)
               LoadingZone.heartContainers[8] = true;
         }
         this.cx = 999;
         this.cy = 999;
      }
   }

   public void drawItem(Graphics g, Driver driver) {
      if (type.equals("rubpee5")) {
         g.drawImage(rubpee5, this.cx - unitSize / 2, (this.cy - unitSize / 2) + ActiveMenu.iterationNum, unitSize,
               unitSize, driver);
      } else if (type.equals("rubpee")) {
         g.drawImage(rubpee, this.cx - unitSize / 2, (this.cy - unitSize / 2) + ActiveMenu.iterationNum, unitSize,
               unitSize, driver);
      } else if (type.equals("heart")) {
         g.drawImage(heart, this.cx - unitSize / 2, (this.cy - unitSize / 2) + ActiveMenu.iterationNum, unitSize,
               unitSize, driver);
      } else if (type.equals("key")) {
         g.drawImage(key, this.cx - unitSize / 2, (this.cy - unitSize / 2) + ActiveMenu.iterationNum, unitSize,
               unitSize, driver);
      } else if (type.equals("heartContainer")) {
         g.drawImage(heartContainer, this.cx - unitSize / 2, (this.cy - unitSize / 2) + ActiveMenu.iterationNum,
               unitSize, unitSize, driver);
      }

   }

}