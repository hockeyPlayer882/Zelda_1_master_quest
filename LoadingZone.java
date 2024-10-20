import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoadingZone extends Entity {
   //private static final boolean GOT_HEART = true;
   private static final boolean HEART_UNCOLLECTED = false;
   
   //private static final boolean HAS_KEY = true;
   private static final boolean HAS_NO_KEY = false;

   private static final boolean DOOR_LOCKED = false;
   private static final boolean DOOR_UNLOCKED = true;

   public String imageName;
   // boolean array, the first index of the array determines what level that player
   // is in, every index after that is used to determine if a room in the
   // corresponding level has had a key dropped, if so, that index of the array is
   // true, otherwise it is false
   public static boolean[][] keyArray = { 
      { HAS_NO_KEY, HAS_NO_KEY }, 
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY },
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY },
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY },
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY},
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY} ,
      { HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY, HAS_NO_KEY},
      {  HAS_NO_KEY, HAS_NO_KEY
        
      }
   };
   // same idea as the keyArray, but checks if the door that each key is intended
   // for(some keys can be used out of order, but it doesn't affect anything)
   public static boolean[][] keyDoor = { 
      { DOOR_LOCKED, DOOR_LOCKED }, 
      { DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED },
      { DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED },
      { DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED }, 
      { DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED},
      { DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED},
      { DOOR_LOCKED , DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED, DOOR_LOCKED},
      { DOOR_LOCKED, DOOR_LOCKED
          
      }
   };
   // checks how many bosses have been defeated
   public static int numDefeatedBosses = 0;
   private static BufferedImage topWallBLOCKED;
   private static BufferedImage topWallOPEN;
   private static BufferedImage topWallKEY;
   private static BufferedImage topWallCLOSED;
   private static BufferedImage bottomWallBLOCKED;
   private static BufferedImage bottomWallOPEN;
   private static BufferedImage bottomWallKEY;
   private static BufferedImage bottomWallCLOSED;
   private static BufferedImage leftWallBLOCKED;
   private static BufferedImage leftWallOPEN;
   private static BufferedImage leftWallKEY;
   private static BufferedImage leftWallCLOSED;
   private static BufferedImage rightWallBLOCKED;
   private static BufferedImage rightWallOPEN;
   private static BufferedImage rightWallKEY;
   private static BufferedImage rightWallCLOSED;
   private static BufferedImage[][] wallImages = {
         { topWallBLOCKED, topWallOPEN, topWallBLOCKED, topWallKEY, topWallCLOSED },
         { bottomWallBLOCKED, bottomWallOPEN, bottomWallKEY, bottomWallCLOSED },
         { leftWallBLOCKED, leftWallOPEN, leftWallKEY, leftWallCLOSED },
         { rightWallBLOCKED, rightWallOPEN, rightWallKEY, rightWallCLOSED } };
   public static Shop activeShop;
   // keeps track of what rooms are enterable-> 0 is passable, 1 is blocked(with a
   // conditionally locked door), 2 is blocked(with a key), 3 is blocked(a wall),
   // and 4 is explodable (to be coded)
   // first number is the top opening, second number is the bottom opening, the
   // third number is the left opening, and the 4th number is the right opening.
   public static int[] currentRoomBlock = { 0, 0, 0, 0 };
   // checks what the new area that the player will go to when they hit the loading
   // zone-> -1 is to a cave or shop, and all positive numbers are for
   // corresponding levels for each dungeon
   public int areaToBeEntered;
   // keeps track of which heart containers have been collected: true is collected,
   // false is not. DOES NOT COUNT HEART CONTAINERS DROPPED BY BOSSES!
   // first index is the heart container at location (9,12), second one is the
   // heart container at (11,12), the third item is at(6,13), the fourth one is the
   // heartContainer at (9,13), the fith one is a heartContainer at(15,10), the
   // sixth one is at (15,12), the seventh one is at(16,12), the eighth one is at (9,15)
   // the seventh one is at (9,6)
   public static boolean[] heartContainers = { HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED, HEART_UNCOLLECTED , HEART_UNCOLLECTED};

   public LoadingZone(int cx, int cy, int areaToBeEntered) {
      try {
         if (wallImages[0][0] == null) {
            wallImages[0][1] = ImageIO.read(new File("./Image files/topWallBLOCKED.png"));
            wallImages[0][0] = ImageIO.read(new File("./Image files/topWallOPEN.png"));
            wallImages[0][2] = ImageIO.read(new File("./Image files/topWallKEY.png"));
            wallImages[0][3] = ImageIO.read(new File("./Image files/topWallCLOSED.png"));
            wallImages[1][1] = ImageIO.read(new File("./Image files/bottomWallBLOCKED.png"));
            wallImages[1][0] = ImageIO.read(new File("./Image files/bottomWallOPEN.png"));
            wallImages[1][2] = ImageIO.read(new File("./Image files/bottomWallKEY.png"));
            wallImages[0][3] = ImageIO.read(new File("./Image files/bottomWallCLOSED.png"));
            wallImages[2][1] = ImageIO.read(new File("./Image files/leftWallBLOCKED.png"));
            wallImages[2][0] = ImageIO.read(new File("./Image files/leftWallOPEN.png"));
            wallImages[2][2] = ImageIO.read(new File("./Image files/leftWallKEY.png"));
            wallImages[0][3] = ImageIO.read(new File("./Image files/leftWallCLOSED.png"));
            wallImages[3][1] = ImageIO.read(new File("./Image files/rightWallBLOCKED.png"));
            wallImages[3][0] = ImageIO.read(new File("./Image files/rightWallOPEN.png"));
            wallImages[3][2] = ImageIO.read(new File("./Image files/rightWallKEY.png"));
            wallImages[3][3] = ImageIO.read(new File("./Image files/rightWallCLOSED.png"));
         }

      } catch (IOException ex) {
         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
      this.cx = cx;
      this.cy = cy;
      this.areaToBeEntered = areaToBeEntered;
   }

   public void enter(Driver driver, Player player, Room room, Graphics g) {
      // checks if the player hits a loading zone, the +4 or -4 is for widening the
      // zones because ArrayLists are beeing stupid and set is flat out broken.
      // Hopefully this works
      if (this.cx - 4 - unitSize / 2 < player.cx + unitSize / 2
            && this.cx + 4 + unitSize / 2 > player.cx - unitSize / 2
            && this.cy - 4 - unitSize / 2 < player.cy + unitSize / 2
            && this.cy + 4 + unitSize / 2 > player.cy - unitSize / 2) {
         room.clearText();
         player.cx = 400;
         player.cy = unitSize * 18;
         //in-dungeon loading zones
         if(Player.level == 7 && Player.location[0] == 1 && Player.location[1] == 2){
            Player.location[0] = -3;
            Player.location[1] = -1;
            room.spawnRoom(player);
            room.fillRoomArray(player);
            player.cx = 400;
            player.cy = 400;
            return;
         }
         else if (Player.level == 7 && Player.location[0] == -3 && Player.location[1] == -1){
            Player.location[0] = 1;
            Player.location[1] = 2;
            player.cx = 400;
            player.cy = 350;
            room.spawnRoom(player);
            room.fillRoomArray(player);
            return;
         }
         Player.level = areaToBeEntered;
         room.emptyRoom(player);
         for (int x = 0; x < room.roomToBeGenerated.length; x++) {
            for (int y = 0; y < room.roomToBeGenerated[0].length; y++) {
               // checks if the player is not in a dungeon room
               if (Player.level < 0) {
                  room.roomToBeGenerated[x][0] = 1;
                  room.roomToBeGenerated[0][y] = 1;
                  room.roomToBeGenerated[18][y] = 1;
               }
            }
         }
         // Entrance dungeon rooms in the game
         if (Player.location[0] == 6 && Player.location[1] == 11 
               || Player.location[0] == 12 && Player.location[1] == 13
               || Player.location[0] == -5 && Player.location[1] == 10
               || Player.location[0] == 14 && Player.location[1] == 9
               || Player.location[0] == 11 && Player.location[1] == 15
               || Player.location[0] == 7 && Player.location[1] == 6
               || Player.location[0] == 8 && Player.location[1] == 5
               || Player.location[0] == 10 && Player.location[1] == 10 && ActiveMenu.numTriforcePieces == 7) {
            // resets the player to the starting point of the map
            Player.location[0] = 0;
            Player.location[1] = 0;
            room.spawnRoom(player);
            player.cy = unitSize * 17;
         }
         room.fillRoomArray(player);   
         if (Player.location[0] == 10 && Player.location[1] == 10 && ActiveMenu.numTriforcePieces != 7) {
            if (Sword.type != "wooden") {
               room.setText( Player.name + ",", "It's dangerous to go alone,", " take this!");
               room.setImages(Driver.woodenSwordW);
               imageName = "woodenSword";
            } else
               room.setText( Player.name + ",", "REALLY?", "What were you expecting?");
         } else if (Player.location[0] >= -4 && Player.location[0] <= 5 && Player.location[1] >= 6
               && Player.location[1] <= 15) {
            if (Sword.type != "metal") {
               room.setText( Player.name + ",", "master using it and you,", " can have this");
               room.setImages(Driver.metalSwordW);
               imageName = "metal sword";
            } else
               room.setText( Player.name + ",", "REALLY?", "What were you expecting?");
         } else if (Player.location[0] == 9 && Player.location[1] == 11
               || Player.location[0] == 6 && Player.location[1] == 12
               || Player.location[0] == 16 && Player.location[1] == 11) {
            room.setText( Player.name + ",", "Buy somethin'", "Will Ya!");
            setShop(g, player, driver);
         }
         // caves with "helpful" advice
         else if (Player.location[0] == 16 & Player.location[1] == 8) {
            room.setText( Player.name + ",", "secrets are hidden in trees,", "set them on fire to find them");
         }
         // caves that can be opened with a bomb or in a tree that hold heartContainers
         else if ((Player.location[0] == 9 && Player.location[1] == 12)
               || (Player.location[0] == 11 && Player.location[1] == 12)
               || Player.location[0] == 6 && Player.location[1] == 13
               || Player.location[0] == 9 && Player.location[1] == 13
               || Player.location[0] == 15 && Player.location[1] == 10
               || Player.location[0] == 15 && Player.location[1] == 12
               || Player.location[0] == 16 && Player.location[1] == 12
               || Player.location[0] == 9 && Player.location[1] == 15
               || Player.location[0] == 9 && Player.location[1] == 6) {
            if ((Player.location[1] == 12 && ((!heartContainers[0] && Player.location[0] == 9)
                  || (!heartContainers[1] && Player.location[0] == 11)))
                  || (Player.location[0] == 6 && Player.location[1] == 13 && !heartContainers[2])
                  || (Player.location[0] == 9 && Player.location[1] == 13 && !heartContainers[3])
                  || (Player.location[0] == 15 && Player.location[1] == 10 && !heartContainers[4])
                  || Player.location[0] == 15 && Player.location[1] == 12 && !heartContainers[5]
                  || Player.location[0] == 16 && Player.location[1] == 12 && !heartContainers[6]
                  || Player.location[0] == 9 && Player.location[1] == 15 && !heartContainers[7]
                  || Player.location[0] == 9 && Player.location[1] == 6 && !heartContainers[8]) {
               room.setText( Player.name + ",", "THIS IS A SECRET", "DON'T TELL ANYONE!");
               Item heartContainer = (Item) Driver.items.get(5);
               heartContainer.cx = 400;
               heartContainer.cy = 400;
            } else
               room.setText( Player.name + ",", "REALLY?", "What were you expecting?");
         }
      }
   }

   public static void setShop(Graphics g, Player player, Driver driver) {
      String item1 = "";
      int cost1 = 0;
      String item2 = "";
      int cost2 = 0;
      String item3 = "";
      int cost3 = 0;
      if (Player.location[0] == 9 && Player.location[1] == 11) {
         item1 = "bomb";
         cost1 = 10;
         item2 = "bow";
         cost2 = 50;
         item3 = "candle";
         cost3 = 70;
         if (Player.hasBow) {
            item2 = "heart";
            cost2 = 5;
         }
         if (Player.hasCandle) {
            item3 = "heart";
            cost3 = 5;
         }
      } else if (Player.location[0] == 6 && Player.location[1] == 12) {
         item1 = "metal shield";
         cost1 = 100;
         item2 = "blue medicine";
         cost2 = 60;
         item3 = "heart";
         cost3 = 2;
      }

      else if (Player.location[0] == 16 && Player.location[1] == 11) {
         item1 = "wooden shield";
         cost1 = 40;
         item2 = "red medicine";
         cost2 = 120;
         item3 = "bomb";
         cost3 = 5;
      }
      activeShop = new Shop(item1, item2, item3, cost1, cost2, cost3);
   }
   
   // lets the player exit a cave, or move rooms in a dungeon
   public static void exit(Player player, Room room) {
      if (Player.level == -1) {
         if (player.cy + (unitSize + unitSize / 2) >= unitSize * 20 && player.dir == 's') {
            activeShop = null;
            room.clearText();
            Player.level = 0;
            if (Player.location[0] == 10 && Player.location[1] == 10
                  || Player.location[0] == 9 && Player.location[1] == 11
                  || Player.location[0] == 15 && Player.location[1] == 12
                  || Player.location[0] == 16 && Player.location[1] == 11) {
               player.cx = 100;
               player.cy = 360;
            } else if (Player.location[0] == 9 && Player.location[1] == 12) {
               player.cx = 360;
               player.cy = 440;
            } else if (Player.location[0] == 11 && Player.location[1] == 12
                  || Player.location[0] == 15 && Player.location[1] == 10
                  || Player.location[0] == 8 && Player.location[1] == 5) {
               player.cx = 400;
               player.cy = 400;
            } else if (Player.location[0] == 6 && (Player.location[1] == 12 || Player.location[1] == 13)
                  || Player.location[0] == 9 && Player.location[1] == 15) {
               player.cx = 340;
               player.cy = 380;
            } else if (Player.location[0] == 9 && Player.location[1] == 13
                  || Player.location[0] == 16 && Player.location[1] == 12) {
               player.cx = 400;
               player.cy = 350;
            } else if (Player.location[0] >= -4 && Player.location[0] <= 5 && Player.location[1] >= 6
                  && Player.location[1] <= 15) {
               player.cx = 400;
               player.cy = 400;
            } else if (Player.location[0] == 16 && Player.location[1] == 8 || Player.location[0] == 9 && Player.location[1] == 6) {
               player.cx = 400;
               player.cy = 650;
            }
            room.spawnRoom(player);
            room.clearImages();
            room.fillRoomArray(player);
         }
      }
      // allow the player to move between area in a dungeon
      else if (Player.level > 0) {
         // allows the player to leave a dungeon
         if (Player.location[0] == 0 && Player.location[1] == 0
               && player.cy + (unitSize + unitSize / 2) + 50 >= unitSize * 20 && player.dir == 's' && player.cx >= 350
               && player.cx <= 450) {
            if (Player.level == 1) {
               Player.location[0] = 6;
               Player.location[1] = 11;
               Player.level = 0;
               player.cx = 340;
               player.cy = 380;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            } else if (Player.level == 2) {
               Player.location[0] = 12;
               Player.location[1] = 13;
               Player.level = 0;
               player.cx = 540;
               player.cy = 380;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            } else if (Player.level == 3) {
               Player.location[0] = -5;
               Player.location[1] = 10;
               Player.level = 0;
               player.cx = 400;
               player.cy = 400;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            } else if (Player.level == 4) {
               Player.location[0] = 14;
               Player.location[1] = 9;
               Player.level = 0;
               player.cx = 400;
               player.cy = 400;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            } else if (Player.level == 5){
               Player.location[0] = 11;
               Player.location[1] = 15;
               Player.level = 0;
               player.cx = 400;
               player.cy = 400;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            }
            else if (Player.level == 6){
               Player.location[0] = 7;
               Player.location[1] = 6;
               Player.level = 0;
               player.cx = 400;
               player.cy = 400;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            }
            else if (Player.level == 7){
               Player.location[0] = 8;
               Player.location[1] = 5;
               Player.level = 0;
               player.cx = 400;
               player.cy = 600;
               room.spawnRoom(player);
               room.clearImages();
               room.fillRoomArray(player);
            }
         } else if (player.cy + (unitSize + unitSize / 2) + 50 >= unitSize * 20 && player.dir == 's') {
            if (player.cx >= 350 && player.cx <= 450 && currentRoomBlock[1] == 0) {
               player.cy = unitSize * 2 + (unitSize * (30 / 8));
               Player.location[1] -= 1;
               room.spawnRoom(player);
               room.fillRoomArray(player);
            }
            // handle opening locked doors from the bottom
            else if (currentRoomBlock[1] == 2 && player.keys > 0) {
               player.keys -= 1;
               currentRoomBlock[1] = 0;
               if (Player.level == 2) {
                  if (Player.location[0] == -2 && Player.location[1] == 2)
                     keyDoor[1][5] = DOOR_UNLOCKED;
               } else if (Player.level == 3) {
                  if (Player.location[0] == 3 && Player.location[1] == 1)
                     keyDoor[2][1] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 3 && Player.location[1] == 0)
                     keyDoor[2][2] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -3 && Player.location[1] == 2)
                     keyDoor[2][5] = DOOR_UNLOCKED;
               } else if (Player.level == 4) {
                  if (Player.location[0] == 2 && Player.location[1] == 0)
                     keyDoor[3][1] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -3 && Player.location[1] == 1)
                     keyDoor[3][8] = DOOR_UNLOCKED;
               }
               else if (Player.level == 5){
                  if(Player.location[0] == -2 && Player.location[1] == 2)
                     keyDoor[4][7] = DOOR_UNLOCKED;
                  
               }
               else if (Player.level == 6){
                  if(Player.location[0] == 2 && Player.location[1] == 2)
                     keyDoor[5][2] = DOOR_UNLOCKED;
                  else if(Player.location[0] == 2 && Player.location[1] == 1)
                     keyDoor[5][3] = DOOR_UNLOCKED;
                  else if(Player.location[0] == 2 && Player.location[1] == 0)
                     keyDoor[5][4] = DOOR_UNLOCKED;
                  else if(Player.location[0] == 2 && Player.location[1] == -1)
                     keyDoor[5][5] = DOOR_UNLOCKED;
               }
               else if (Player.level == 7){
                  if(Player.location[0] == 1 && Player.location[1] == 4)
                     keyDoor[6][11] = DOOR_UNLOCKED;
               }
            } else
               player.dir = 'n';
         } else if (player.cy + (unitSize / 2) <= unitSize + (unitSize * (30 / 8)) + 50 && player.dir == 'w') {
            if (player.cx >= 350 && player.cx <= 450) {
               if (currentRoomBlock[0] == 0) {
                  player.cy = (unitSize * 17);
                  Player.location[1] += 1;
                  room.spawnRoom(player);
                  room.fillRoomArray(player);
               }
               // handle opening locked doors from the top
               else if (currentRoomBlock[0] == 2 && player.keys > 0) {
                  player.keys -= 1;
                  currentRoomBlock[0] = 0;
                  if (Player.level == 1) {
                     if (Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[0][0] = DOOR_UNLOCKED;
                     else if (Player.location[0] == 0 && Player.location[1] == 3)
                        keyDoor[0][1] = DOOR_UNLOCKED;
                  } else if (Player.level == 2) {
                     if (Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[1][0] = DOOR_UNLOCKED;
                     else if (Player.location[0] == 0 && Player.location[1] == 1)
                        keyDoor[1][1] = DOOR_UNLOCKED;
                     else if (Player.location[0] == 3 && Player.location[1] == 1)
                        keyDoor[1][3] = DOOR_UNLOCKED;
                  } else if (Player.level == 3) {
                     if (Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[2][0] = DOOR_UNLOCKED;
                  } else if (Player.level == 4) {
                     if (Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[3][0] = DOOR_UNLOCKED;
                  } else if (Player.level == 5) {
                        if(Player.location[0] == 0 && Player.location[1] == 0)
                           keyDoor[4][0] = DOOR_UNLOCKED;
                        else if (Player.location[0] == 0 && Player.location[1] == 1)
                           keyDoor[4][1] = DOOR_UNLOCKED;
                  } else if (Player.level == 7){
                        if(Player.location[0] == 1 && Player.location[1] == 0)   
                           keyDoor[6][1] = DOOR_UNLOCKED;
                        else if(Player.location[0] == 4 && Player.location[1] == 1)
                           keyDoor[6][5] = DOOR_UNLOCKED;
                        else if(Player.location[0] == 4 && Player.location[1] == 2)
                           keyDoor[6][6] = DOOR_UNLOCKED;
                        else if(Player.location[0] == 4 && Player.location[1] == 3)
                           keyDoor[6][7] = DOOR_UNLOCKED;
                        
                        
                  } else if (Player.level == 8){
                      if(Player.location[0] == 0 && Player.location[1] == 0)
                          keyDoor[7][0] = DOOR_UNLOCKED;
                      
                  }
               } else
                  player.dir = 'n';
            } else
               player.dir = 'n';
         } else if (player.cx - (unitSize / 2) - 50 <= 0 && player.dir == 'a') {
            if (player.cy >= 400 && player.cy <= 500 && currentRoomBlock[2] == 0) {
               player.cx = (unitSize * 17);
               Player.location[0] -= 1;
               room.spawnRoom(player);
               room.fillRoomArray(player);
            }
            // handle opening locked doors from the left
            else if (currentRoomBlock[2] == 2 && player.keys > 0) {
               player.keys -= 1;
               currentRoomBlock[2] = 0;
               if (Player.level == 2) {
                  if (Player.location[0] == 3 && Player.location[1] == 2)
                     keyDoor[1][4] = DOOR_UNLOCKED;
               } else if (Player.level == 3) {
                  if (Player.location[0] == -1 && Player.location[1] == 2)
                     keyDoor[2][3] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -2 && Player.location[1] == 2)
                     keyDoor[2][4] = DOOR_UNLOCKED;
               } else if (Player.level == 4) {
                  if (Player.location[0] == 2 && Player.location[1] == -1)
                     keyDoor[3][2] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 1 && Player.location[1] == -1)
                     keyDoor[3][3] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 0 && Player.location[1] == -1)
                     keyDoor[3][4] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -1 && Player.location[1] == -1)
                     keyDoor[3][5] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -1 && Player.location[1] == 1)
                     keyDoor[3][6] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -2 && Player.location[1] == 1)
                     keyDoor[3][7] = DOOR_UNLOCKED;
               } else if (Player.level == 5) {
                  if (Player.location[0] == 3 && Player.location[1] == -3)
                     keyDoor[4][2] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 2 && Player.location[1] == -3)
                     keyDoor[4][3] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 1 && Player.location[1] == -3)
                     keyDoor[4][4] = DOOR_UNLOCKED;
                  else if (Player.location[0] == 0 && Player.location[1] == -3)
                     keyDoor[4][5] = DOOR_UNLOCKED;
                  else if (Player.location[0] == -1 && Player.location[1] == -3)
                     keyDoor[4][6] = DOOR_UNLOCKED;
               } else if (Player.level == 6){
                  if(Player.location[0] == 1 && Player.location[1] == -2)
                     keyDoor[5][6] = DOOR_UNLOCKED;
                  else if(Player.location[0] == 0 && Player.location[1] == -2)
                     keyDoor[5][7] = DOOR_UNLOCKED;
                  else if(Player.location[0] == -1 && Player.location[1] == -2)
                     keyDoor[5][8] = DOOR_UNLOCKED;
                  else if(Player.location[0] == -2 && Player.location[1] == -2)
                     keyDoor[5][9] = DOOR_UNLOCKED;
                  else if(Player.location[0] == -3 && Player.location[1] == -2)
                     keyDoor[5][10] = DOOR_UNLOCKED;
                  else if(Player.location[0] == -2 && Player.location[1] == 1)
                     keyDoor[5][11] = DOOR_UNLOCKED;
               } else if (Player.level == 7){
                  if(Player.location[0] == 4 && Player.location[1] == 4)
                     keyDoor[6][8] = DOOR_UNLOCKED;
                  else if(Player.location[0] == 3 && Player.location[1] == 4)
                     keyDoor[6][9] = DOOR_UNLOCKED;
                     else if(Player.location[0] == 2 && Player.location[1] == 4)
                        keyDoor[6][10] = DOOR_UNLOCKED;
               } else if (Player.level == 8){
                   //door code go here
               }

            } else
               player.dir = 'n';
         }
         // handle doors from the right
         else if (player.cx + (unitSize) + 25 >= unitSize * 20 && player.dir == 'd') {
            if (player.cy >= 400 && player.cy <= 500) {
               if (currentRoomBlock[3] == 0) {
                  player.cx = unitSize * 2;
                  Player.location[0] += 1;
                  room.spawnRoom(player);
                  room.fillRoomArray(player);
               }
               // handle opening locked doors from the right
               else if (currentRoomBlock[3] == 2 && player.keys > 0) {
                  player.keys -= 1;
                  currentRoomBlock[3] = 0;
                  if (Player.level == 2) {
                     if (Player.location[0] == 2 && Player.location[1] == 1)
                        keyDoor[1][2] = DOOR_UNLOCKED;
                  }
                  else if(Player.level == 6){
                     if(Player.location[0] == -1 && Player.location[1] == 2)
                        keyDoor[5][0] = DOOR_UNLOCKED;
                     else if(Player.location[0] == 1 && Player.location[1] == 2)
                        keyDoor[5][1] = DOOR_UNLOCKED;
                  }
                  else if(Player.level == 7){
                     if(Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[6][0] = DOOR_UNLOCKED;
                     else if(Player.location[0] == 1 && Player.location[1] == 1)
                        keyDoor[6][2] = DOOR_UNLOCKED;
                     else if(Player.location[0] == 2 && Player.location[1] == 1)
                        keyDoor[6][3] = DOOR_UNLOCKED;
                     else if(Player.location[0] == 3 && Player.location[1] == 1)
                        keyDoor[6][4] = DOOR_UNLOCKED;
                  }
                  else if(Player.level ==  8){
                      if(Player.location[0] == 0 && Player.location[1] == 0)
                        keyDoor[7][1] = DOOR_UNLOCKED;
                  }
               } else
                  player.dir = 'n';
            } else
               player.dir = 'n';
         }
      } else {
         // make the player move in rooms
         player.advanceRoom(room);
      }
   }

   public static void drawBarriers(Graphics g, Driver driver) {
      int CX = 0;
      int CY = 0;
      int width = 0;
      int height = 0;
      for (int x = 0; x < wallImages.length; x++) {
         if (x == 0) {
            CX = 0;
            CY = 120;
            width = 800;
            height = 50;
         } else if (x == 1) {
            CX = 0;
            CY = 700;
            width = 800;
            height = 50;
         } else if (x == 2) {
            CX = 0;
            CY = 120;
            width = 50;
            height = 800 - 130;
         } else if (x == 3) {
            CX = 750;
            CY = 120;
            width = 50;
            height = 800 - 130;

         }
         if (currentRoomBlock[x] == 0)
            g.drawImage(wallImages[x][0], CX, CY + ActiveMenu.iterationNum, width, height, driver);
         else if (currentRoomBlock[x] == 1)
            g.drawImage(wallImages[x][1], CX, CY + ActiveMenu.iterationNum, width, height, driver);
         else if (currentRoomBlock[x] == 2)
            g.drawImage(wallImages[x][2], CX, CY + ActiveMenu.iterationNum, width, height, driver);
         else if (currentRoomBlock[x] == 3)
            g.drawImage(wallImages[x][3], CX, CY + ActiveMenu.iterationNum, width, height, driver);
      }
   }
}