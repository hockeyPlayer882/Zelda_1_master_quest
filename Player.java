import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//child class of Entity, has specific methods and attributes special for the main player
public class Player extends Entity {
   // tracks the player location in both the overworld and a dungeon
   public static int[] location = { 10, 10 };
   // tracks where the player is: level 0 is the overworld, level -1 is a cave or
   // shop, and positive levels correspond to dungeon levels
   public static int level;
   // sets the amount of time the player will hold out their sword each thrust,
   // value below is the max value
   public static int attackDelay;
   public int MaxAttackDelay;
   public char stDir = 's';
   private static BufferedImage linkW;
   private static BufferedImage linkWattack;
   private static BufferedImage linkS;
   private static BufferedImage linkSattack;
   private static BufferedImage linkSMETAL;
   private static BufferedImage linkSBROKEN;
   private static BufferedImage linkA;
   private static BufferedImage linkAattack;
   private static BufferedImage linkAMETAL;
   private static BufferedImage linkABROKEN;
   private static BufferedImage linkD;
   private static BufferedImage linkDattack;
   private static BufferedImage linkDMETAL;
   private static BufferedImage linkDBROKEN;
   private static BufferedImage linkT;
   // 2D array for storing link non-attacking animations, first array is for
   // standard, and the second is for the metal shield
   private static BufferedImage[][] linkAnimations = { { linkW, linkS, linkA, linkD },
         { linkSMETAL, linkAMETAL, linkDMETAL }, { linkSBROKEN, linkABROKEN, linkDBROKEN } };
   // item variables
   public int rubpees;
   public int bombs;
   public int keys;
   // blue means the player has blue medicine, red means the player has red
   // medicine, otherwise the player has no medicine
   public static String medicine = "";
   // checks if the player has the candle(I removed the blue candle from the
   // original... its a poor mechanic lets be honest)
   public static boolean hasCandle;
   public static boolean hasBow;
   public static boolean hasCane;
   public static boolean hasBoomerang;
   public static boolean hasArrows;
   public static boolean hasRaft;
   public static boolean hasWand;
   public static boolean hasSuperBomb;
   public static boolean isPaused;
   // checks what the players secondary item is (bomb, bows, etc.)
   public String activeItem = "NONE";
   // the name of the player to be taken from the player input
   public static String name;

   // only one player object will be created,therefore there is no need for a
   // parameter-based constructor
   public Player() {
      try {
         linkAnimations[0][0] = ImageIO.read(new File(".\\Image files\\LinkW.png"));
         linkWattack = ImageIO.read(new File(".\\Image files\\LinkWattack.png"));
         linkAnimations[0][1] = ImageIO.read(new File(".\\Image files\\LinkS.png"));
         linkSattack = ImageIO.read(new File(".\\Image files\\LinkSattack.png"));
         linkAnimations[1][0] = ImageIO.read(new File(".\\Image files\\LinkSMETAL.png"));
         linkAnimations[2][0] = ImageIO.read(new File(".\\Image files\\LinkSBROKEN.png"));
         linkAnimations[0][2] = ImageIO.read(new File(".\\Image files\\LinkA.png"));
         linkAattack = ImageIO.read(new File(".\\Image files\\LinkAattack.png"));
         linkAnimations[1][1] = ImageIO.read(new File(".\\Image files\\LinkAMETAL.png"));
         linkAnimations[2][1] = ImageIO.read(new File(".\\Image files\\LinkABROKEN.png"));
         linkAnimations[0][3] = ImageIO.read(new File(".\\Image files\\LinkD.png"));
         linkDattack = ImageIO.read(new File(".\\Image files\\LinkDattack.png"));
         linkAnimations[1][2] = ImageIO.read(new File(".\\Image files\\LinkDMETAL.png"));
         linkAnimations[2][2] = ImageIO.read(new File(".\\Image files\\LinkDBROKEN.png"));
         linkT = ImageIO.read(new File(".\\Image files\\LinkT.png"));
      } catch (IOException ex) {
         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
      location[0] = 10;
      location[1] = 10;
      level = 0;
      this.hp = 6;
      this.Mhp = 6;
      attackDelay = 0;
      // player has no weapon, therefor attack is 0
      this.damage = 0;
      this.MaxAttackDelay = 20;
      this.cx = unitSize * 10;
      this.cy = unitSize * 10;
      this.rubpees = 0;
      this.bombs = 0;
      this.keys = 0;
      this.shieldIsActive = true;
      this.shieldStrength = 1;
   }

   public void attack(Sword sword) {
      if (Wand.delay <= 0) {
         // locks player while the sword is activated
         attackDelay = this.MaxAttackDelay;
         if (attackDelay > 0) {
            sword.spawnSword(this);
         }
         if (attackDelay < 0) {
            sword.despawn();
         }
      }
   }

   public void decreaseAtkDel(Sword sword) {
      if (attackDelay > 0) {
         shieldIsActive = false;
         attackDelay -= 1;
      } else {
         shieldIsActive = true;
         sword.despawn();
      }

   }

   public void advanceRoom(Room room) {
      if (this.cx - (unitSize / 2) <= 0 && this.dir == 'a') {
         this.cx = (unitSize * 20) - unitSize / 2;
         location[0] -= 1;
         room.spawnRoom(this);
         room.fillRoomArray(this);
      } else if (this.cx + (unitSize) >= unitSize * 20 && this.dir == 'd') {
         this.cx = unitSize;
         location[0] += 1;
         room.spawnRoom(this);
         room.fillRoomArray(this);
      } else if (this.cy + (unitSize / 2) <= unitSize + (unitSize * (30 / 8)) && this.dir == 'w') {
         this.cy = (unitSize * 18);
         location[1] += 1;
         room.spawnRoom(this);
         room.fillRoomArray(this);
      } else if (this.cy + (unitSize + unitSize / 2) >= unitSize * 20 && this.dir == 's') {
         this.cy = unitSize + (unitSize * (30 / 8));
         location[1] -= 1;
         room.spawnRoom(this);
         room.fillRoomArray(this);
      }
   }

   public void draw(Graphics g, Driver driver) {
      // draws the raft(if being used)
      if (this.stun > 0)
         g.drawImage(Room.raft, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
      char drawDir;
      if (this.dir == 'n')
         drawDir = this.stDir;
      else
         drawDir = this.dir;
      // first number for the 2D array for player animations(1 is the metal shield, 0
      // is wooden shield, -1 is a broken shield
      int shieldType = (shieldStrength == 2 ? 1 : shieldStrength == 1 ? 0 : 2);
      // the shield is invisible in the upward animation, so no the animation is the
      // same regardless of what shield link has
      // creates "invincibility framed" for link, drawing him every other frame when
      // he is hit
      if (inv % 2 == 0) {
         if (drawDir == 'w')
            g.drawImage(linkAnimations[0][0], this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                  driver);
         else if (drawDir == 's')
            g.drawImage(linkAnimations[shieldType][(shieldType > 0 ? 0 : 1)], this.cx - unitSize / 2,
                  this.cy - unitSize / 2, unitSize, unitSize, driver);
         else if (drawDir == 'a')
            g.drawImage(linkAnimations[shieldType][(shieldType > 0 ? 1 : 2)], this.cx - unitSize / 2,
                  this.cy - unitSize / 2, unitSize, unitSize, driver);
         else if (drawDir == 'd')
            g.drawImage(linkAnimations[shieldType][(shieldType > 0 ? 2 : 3)], this.cx - unitSize / 2,
                  this.cy - unitSize / 2, unitSize, unitSize, driver);
         else if (drawDir == 'e') {
            if (this.stDir == 'a')
               g.drawImage(linkAattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
            else if (this.stDir == 'd')
               g.drawImage(linkDattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
            // Refrences due to lacking up/down sprites for player's attacking stance.
            else if (this.stDir == 'w')
               g.drawImage(linkWattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
            else if (this.stDir == 's')
               g.drawImage(linkSattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize, driver);
         }
      }
      if (this.dir == 't')
         g.drawImage(linkT, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize * 2, driver);
   }
}
