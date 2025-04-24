import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import neozelda.audio.AudioEngine;

public abstract class Wand {
   public static int cx = 999;
   public static int cy = 999;
   public static char dir = ' ';
   public static final int speed = 14;
   public static int delay = 0;
   public static BufferedImage wandW;
   private static BufferedImage wandS;
   private static BufferedImage wandA;
   private static BufferedImage wandD;
   private static BufferedImage projectileW;
   private static BufferedImage projectileS;
   private static BufferedImage projectileA;
   private static BufferedImage projectileD;

   private static void move() {
      if (!Player.isPaused) {
         cx += dir == 'a' ? -speed : dir == 'd' ? speed : 0;
         cy += dir == 'w' ? -speed : dir == 's' ? speed : 0;
         if (cx >= 840 || cx <= -Entity.unitSize || cy >= 840 || cy <= Entity.unitSize * 2) {
            cx = 999;
            dir = ' ';
            cy = 999;
         }
      }
   }

   public static void hurt(Entity other) {
      if (cx - Entity.unitSize / 2 < other.cx + Entity.unitSize / 2
            && cx + Entity.unitSize / 2 > other.cx - Entity.unitSize / 2
            && cy - Entity.unitSize / 2 < other.cy + Entity.unitSize / 2
            && cy + Entity.unitSize / 2 > other.cy - Entity.unitSize / 2
            && other.inv <= 0) {
         if (!other.isImmuneWand) {
            other.hp -= 1;
            other.inv = 60;
            // makes the enemy run in terror!!!!(makes them move in the opposite direction
            // and resets their movement timer)
            other.dir = dir;
            other.movementTimer = other.MmovementTimer;

            AudioEngine.playClip("./sfx/LOZ_Enemy_Hit.wav");
         }
         cx = 999;
         cy = 999;
      }
   }

   public static void spawn(Player player) {
      if (Player.hasWand && cx == 999) {
         delay = 15;
         dir = player.stDir;
         cx = dir == 'a' ? player.cx - Entity.unitSize * 2
               : dir == 'd' ? player.cx + Entity.unitSize * 2 : player.cx - Entity.unitSize / 2;
         cy = dir == 'w' ? player.cy - Entity.unitSize * 2
               : dir == 's' ? player.cy + Entity.unitSize * 2 : player.cy - Entity.unitSize / 2;
      }
   }

   public static void draw(Graphics g, Driver driver, Player player) {
      move();
      int CX = 0;
      int CY = 0;
      switch (dir) {
         case 'w':
            CY = player.cy - Entity.unitSize - Entity.unitSize / 2;
            CX = player.cx;
            break;
         case 's':
            CY = (player.cy + Entity.unitSize / 2) + Entity.unitSize / 2;
            CX = player.cx;
            break;
         case 'a':
            CX = player.cx - Entity.unitSize - Entity.unitSize / 2;
            CY = player.cy;
            break;
         case 'd':
            CX = player.cx + Entity.unitSize;
            CY = player.cy;
            break;
      }
      // draws the staff
      if (delay > 0) {
         if (dir == 'a') {
            g.drawImage(wandA, CX - Player.unitSize / 2, CY - Player.unitSize / 2 + ActiveMenu.iterationNum, 60, 40,
                  driver);
         } else if (dir == 'd') {
            g.drawImage(wandD, CX - Player.unitSize / 2, CY - Player.unitSize / 2 + ActiveMenu.iterationNum, 60, 40,
                  driver);
         } else if (dir == 'w') {
            g.drawImage(wandW, CX - Player.unitSize / 2, CY - Player.unitSize / 2 + ActiveMenu.iterationNum, 40, 60,
                  driver);
         } else if (dir == 's') {
            g.drawImage(wandS, CX - Player.unitSize / 2, CY - Player.unitSize / 2 + ActiveMenu.iterationNum, 40, 60,
                  driver);
         }
         delay--;
      }
      // draws the projectile the wand shoots
      if (cx <= 999 && cy <= 999)
         g.drawImage(dir == 'w' ? projectileW : dir == 's' ? projectileS : dir == 'a' ? projectileA : projectileD, cx,
               cy, Entity.unitSize, Entity.unitSize, driver);
   }

   public static void init() {
      try {
         wandW = ImageIO.read(new File("./Image files/wandW.png"));
         wandS = ImageIO.read(new File("./Image files/wandS.png"));
         wandA = ImageIO.read(new File("./Image files/wandA.png"));
         wandD = ImageIO.read(new File("./Image files/wandD.png"));
         projectileW = ImageIO.read(new File("./Image files/wandProjectileW.png"));
         projectileS = ImageIO.read(new File("./Image files/wandProjectileS.png"));
         projectileA = ImageIO.read(new File("./Image files/wandProjectileA.png"));
         projectileD = ImageIO.read(new File("./Image files/wandProjectileD.png"));
      } catch (IOException ex) {
         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }

   }
}