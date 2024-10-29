import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import neozelda.audio.AudioEngine;

import java.awt.Graphics;

public abstract class Boomerang {
   public static int cx = 999;
   public static int cy = 999;
   // checks whether the boomerang has Returnd and need to move back to the player
   public static boolean hasBounced;
   // array to store the direction the bow should be in(first number is for x
   // movement, second number is for y movement)
   public static char[] dirs = { 'n', 'n' };
   public static BufferedImage boomerang;

   public static void setImage() {
      try {
         boomerang = ImageIO.read(new File("./Image files/boomerang.png"));
      } catch (IOException ex) {

         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("When Java doesn't know how to Java .-.");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }

   public static final int speed = 6;

   public static void stun(Entity ent) {
      // calculate if an enemy hits the boomerang
      if (cx - Entity.unitSize / 2 < ent.cx + Entity.unitSize / 2
            && cx + Entity.unitSize / 2 > ent.cx - Entity.unitSize / 2
            && cy - Entity.unitSize / 2 < ent.cy + Entity.unitSize / 2
            && cy + Entity.unitSize / 2 > ent.cy - Entity.unitSize / 2) {
         // is the enemy is weak to the boomerang, the enemy will instantly die(I'm
         // feeling like being nice to the player for once, just this once)
         hasBounced = true;
         if (ent.isWeakToBoomerang)
            ent.hp = 0;
         else
            ent.stun = ent.Mstun;

         // Enemy hit; make noise.
         // TODO: UNTESTED!
         AudioEngine.playClip("./sfx/LOZ_Enemy_Hit.wav");
      }
   }

   public static void Return(Player player) {
      if (player.cx > cx)
         dirs[0] = 'd';
      else
         dirs[0] = 'a';
      if (player.cy > cy)
         dirs[1] = 's';
      else
         dirs[1] = 'w';
   }

   public static void spawn(Player player) {
      dirs[0] = 'n';
      dirs[1] = 'n';
      hasBounced = false;
      switch (player.stDir) {
         case 'w':
            dirs[1] = 'w';
            cy = player.cy - Entity.unitSize - Entity.unitSize / 2;
            cx = player.cx;
            break;
         case 's':
            dirs[1] = 's';
            cy = (player.cy + Entity.unitSize / 2) + Entity.unitSize / 2;
            cx = player.cx;
            break;
         case 'a':
            dirs[0] = 'a';
            cx = player.cx - Entity.unitSize - Entity.unitSize / 2;
            cy = player.cy;
            break;
         case 'd':
            dirs[0] = 'd';
            cx = player.cx + Entity.unitSize;
            cy = player.cy;
            break;
      }
   }

   public static void despawn(Player player) {
      if (cx - Entity.unitSize / 2 < player.cx + Entity.unitSize / 2
            && cx + Entity.unitSize / 2 > player.cx - Entity.unitSize / 2
            && cy - Entity.unitSize / 2 < player.cy + Entity.unitSize / 2
            && cy + Entity.unitSize / 2 > player.cy - Entity.unitSize / 2
            && hasBounced) {
         cx = 999;
         cy = 999;
      }
   }

   public static void move(Player player) {
      if (!Player.isPaused) {
         if (hasBounced)
            Return(player);
         despawn(player);
         cx += (dirs[0] == 'a' ? -speed : dirs[0] == 'd' ? speed : 0);
         cy += (dirs[1] == 's' ? speed : dirs[1] == 'w' ? -speed : 0);
         if (cx >= 800 || cx <= 0 || cy <= 130 || cy >= 800)
            hasBounced = true;
      }
   }

   public static void draw(Graphics g, Driver driver) {
      g.drawImage(boomerang, cx - Entity.unitSize / 2, (cy - Entity.unitSize / 2) + ActiveMenu.iterationNum,
            Entity.unitSize, Entity.unitSize, driver);
   }
}