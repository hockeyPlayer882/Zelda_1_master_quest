import java.awt.*;
import java.util.ArrayList;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Room {
   private int numOctorokRs = 0;
   private int numSpawnedOctorokRs = 0;
   private int numOctorokBs = 0;
   private int numSpawnedOctorokBs = 0;
   // NOTE TO SELF:: GENIUS IDEA!!!!!!!!!!!!! FORCE THE PLAYER TO FIND A KEY HIDDEN
   // IN THE OVERWORLD FOR ONE OF THE DUNGEON MAPS WHERE YOU HAVE TO FIGHT DARK
   // LINK.
   // arrayList to store objects in a specific room
   public static ArrayList<ArrayList<Entity>> currentRoom = new ArrayList<ArrayList<Entity>>();
   public static ArrayList<String> text = new ArrayList<String>();
   public static final int imagecx = 400 - Entity.unitSize / 2;
   public static final int imagecy = 400;
   public static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
   // array to store what object is to be generated in a specific room-> 0 is
   // nothing, 1 is an rock, 2 is a moveable rock, 3 is a burnable tree, 4 is
   // water(works like a rock, but visually different), 5 is a bridge(purely
   // visual), 6 is a loading zone, 7 is a red octorok, 8 is a blue octorok,9 is a
   // skeleton,10 is a boss, 11 is a triforce piece, 12 is a rock that can be
   // exploded, 13 is a swordsman, 14 is a tree(normal
   // obstacle), 15 is a burnable tree(set on fire with candle! fun!),
   // 16 is a shieldEater
   // 17,18,19,and 20 are all docks(allows the player to use the raftto cross water)
   //(17 sends the player to the up, 18 sends the player to the down, 19 sends the player left and 20 sends the player right)
   // 21 is a Mummy
   // 22 is a Wizzrobe
   // 23 is a snake
   // 24 is a rock that can be blown up with only a superbomb(BOOOOOOOM)
   public int[][] roomToBeGenerated = new int[19][18];
   // created a 10 by 10 grid that stores the lost woods maze
   public static int[][] lostWoods = new int[10][10];
   private BufferedImage rock;
   private BufferedImage rockSuperBombable;
   private BufferedImage tree;
   private BufferedImage water;
   private BufferedImage tile;
   private BufferedImage wall;
   public static BufferedImage raft;
   private int numWater;
   //private GlobalModManager modManager;

   public Room(String area/*, GlobalModManager modManager*/) {
      //this.modManager = modManager;
      try {
         if (rock == null)
            rock = ImageIO.read(new File("./Image files/rock.png"));
         if (rockSuperBombable == null)
            rockSuperBombable = ImageIO.read(new File("./Image files/rockSuperBombable.png"));
         if (water == null)
            water = ImageIO.read(new File("./Image files/water.png"));
         if (tile == null)
            tile = ImageIO.read(new File("./Image files/tile.png"));
         if (wall == null)
            wall = ImageIO.read(new File("./Image files/wall.png"));
         if (tree == null)
            tree = ImageIO.read(new File("./Image files/tree.png"));
         if (raft == null)
            raft = ImageIO.read(new File("./Image files/raft.png"));
      } catch (IOException ex) {

         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("When Java doesn't know how to Java .-.");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }


   // Room erasure and load.
   public void fillRoomArray(Player player) {
      // empty arrayLists and reset number of enemies spawned in a room
      currentRoom.clear();
      if (Player.hasBoomerang) {
         Boomerang.cx = player.cx;
         Boomerang.cy = player.cy;
         Boomerang.Return(player);
      }
      WaterMonster.isSpawned = false;
      Driver.projs.clear();
      Driver.bombs.clear();
      Driver.fires.clear();
      Driver.obstacles.clear();
      //modManager.unloadRoom();
      Fire.numFire = 0;
      Bomb.numBombs = 0;
      numWater = 0;
      Driver.explosions.clear();
      Entity.numKeyEnemiesAlive = 0;

      // Room load starts here.
      boolean skipBuiltinRoomLoad = false;//modManager.loadRoom(Player.location);
      if (skipBuiltinRoomLoad)
         return; // Our job here is done... especially if this room doesn't exist in the source.
      for (Item item : Driver.items) {
         item.cx = 999;
         item.cy = 999;
      }
      numOctorokRs = 0;
      numOctorokBs = 0;
      for (int x = 0; x < roomToBeGenerated.length; x++) {
         ArrayList<Entity> row = new ArrayList<Entity>();
         for (int y = 0; y < roomToBeGenerated[0].length; y++) {

            if (roomToBeGenerated[x][y] == 1 || roomToBeGenerated[x][y] == 12 || roomToBeGenerated[x][y] == 24) {
               String type = (Player.level <= 0 ? "rock" : "wall");
               boolean isExplodable = (roomToBeGenerated[x][y] == 12 ? true : false);
               Obstacle obstacle = new Obstacle(x * 41 + 23, y * 41 + 140, type, 0, false, true, isExplodable);
               if(roomToBeGenerated[x][y] == 24) obstacle.isSuperBombable = true;
               row.add(obstacle);
               Driver.obstacles.add(obstacle);
               continue;
            } else if (roomToBeGenerated[x][y] == 4) {
               Obstacle obstacle = new Obstacle(x * 41 + 23, y * 41 + 140, "water", 0, false, true, false);
               row.add(obstacle);
               Driver.obstacles.add(obstacle);
               row.add(obstacle);
               numWater += 1;
               continue;
            } else if (roomToBeGenerated[x][y] == 5)
               row.add(new Bridge(x * 41 + 23, y * 41 + 140));
            else if (roomToBeGenerated[x][y] == 6) {
               // standard level is a cave at -1
               int newArea = -1;
               // checks what level the player is in for spawning the loading zone
               if (Player.location[0] == 6 && Player.location[1] == 11)
                  newArea = 1;
               else if (Player.location[0] == 12 && Player.location[1] == 13)
                  newArea = 2;
               else if (Player.location[0] == -5 && Player.location[1] == 10)
                  newArea = 3;
               else if (Player.location[0] == 14 && Player.location[1] == 9)
                  newArea = 4;
               else if (Player.location[0] == 8 && Player.location[1] == 5 || Player.level == 7)
                  newArea = 7;
               else if (Player.location[0] == 10 && Player.location[1] == 10)
                  newArea = 8;
               row.add(new LoadingZone(x * 41 + 23, y * 41 + 140, newArea));
               continue;
            } else if (roomToBeGenerated[x][y] == 7) {
               row.add(new OctorokR(x * 41 + 23, y * 41 + 140));
               numOctorokRs += 1;
               continue;
            } else if (roomToBeGenerated[x][y] == 8) {
               row.add(new OctorokB(x * 41 + 23, y * 41 + 140));
               numOctorokBs += 1;
               continue;
            } else if (roomToBeGenerated[x][y] == 9) {
               row.add(new Skeleton(x * 41 + 23, y * 41 + 140));
               continue;
            } else if (roomToBeGenerated[x][y] == 10) {
               if (Player.level == 1) 
                  row.add(new Boss1(x * 41 + 23, y * 41 + 140, 's'));
               else if (Player.level == 2) 
                  row.add(new Boss2());
               else if (Player.level == 3) 
                  row.add(new Boss3(x * 41 + 23, y * 41 + 140, 10));
               else if (Player.level == 4)
                  row.add(new Boss4(x * 41 + 23, y * 41 + 140));
               else if (Player.level == 5)  
                  row.add(new Boss5(x * 41 + 23, y * 41 + 140,'w'));
               else if (Player.level == 6)  
                  row.add(new Boss6(x * 41 + 23, y * 41 + 140,20));
               else if (Player.level == 7)
                  row.add(new Boss7(x*41+23,y*41+140));

               continue;
            } else if (roomToBeGenerated[x][y] == 11)
               row.add(new TriforcePiece(x * 41 + 23, y * 41 + 140));
            else if (roomToBeGenerated[x][y] == 13)
               row.add(new Swordsman(x * 41 + 23, y * 41 + 140));
            else if (roomToBeGenerated[x][y] == 14 || roomToBeGenerated[x][y] == 15) {
               boolean burnable = (roomToBeGenerated[x][y] == 15);
               Obstacle obstacle = new Obstacle(x * 41 + 23, y * 41 + 140, "tree", 0, burnable, true, false);
               row.add(obstacle);
               Driver.obstacles.add(obstacle);
               continue;
            } else if (roomToBeGenerated[x][y] == 16)
               row.add(new ShieldEater(x * 41 + 23, y * 41 + 140));
            else if (roomToBeGenerated[x][y] >= 17 && roomToBeGenerated[x][y] <= 20)
               row.add(new Bridge(x * 41 + 23, y * 41 + 140, roomToBeGenerated[x][y] == 17 ? 'w'
                     : roomToBeGenerated[x][y] == 18 ? 's' : roomToBeGenerated[x][y] == 19 ? 'a' : 'd'));
            else if (roomToBeGenerated[x][y] == 21)
               row.add(new Mummy(x * 41 + 23, y * 41 + 140));
            else if (roomToBeGenerated[x][y] == 22)
               row.add(new Wizzrobe(x * 41 + 21, y * 41 + 140));
            else if (roomToBeGenerated[x][y] == 23)
               row.add(new Snake(x * 41 + 21, y * 41 + 140));

         }
         currentRoom.add(row);
      }
   }

   public void emptyRoom(Player player) {
      for (int x = 0; x < roomToBeGenerated.length; x++) {
         for (int y = 0; y < roomToBeGenerated[0].length; y++) {
            roomToBeGenerated[x][y] = 0;
         }
      }
      fillRoomArray(player);
   }

   @SuppressWarnings("unlikely-arg-type")
   public void drawRooms(Graphics g, Driver driver, Player player,Sword sword) {
      if (Player.level > 0 ) {
         // draws the ground tiles for the dungeon
         if(Boss7.darkTimer == 0){
            for (int x = 0; x < 18; x++) {
               for (int y = 0; y < 15; y++) {
                  g.drawImage(tile, x * Entity.unitSize + 40, y * Entity.unitSize + 135 + ActiveMenu.iterationNum,
                        Entity.unitSize, Entity.unitSize, driver);
               }
            }
         }
         else{
            g.setColor(Color.black);
            g.fillRect(40,135+ActiveMenu.iterationNum,800,800);
            
         }
         // draws the top walls accoding to their location
         LoadingZone.drawBarriers(g, driver);
      }
      if (currentRoom.size() > 0) {
         for (int x = 0; x < currentRoom.size(); x++) {
            for (int y = 0; y < currentRoom.get(x).size(); y++) {
               // draws a loading zone in its corrosponding spot
               if (currentRoom.get(x).get(y) instanceof LoadingZone) {
                  LoadingZone loadingZone = (LoadingZone) currentRoom.get(x).get(y);
                  loadingZone.enter(driver, player, this,g);
                  g.setColor(Color.BLACK);
                  g.fillRect(loadingZone.cx - Entity.unitSize / 2, loadingZone.cy - Entity.unitSize / 2, Entity.unitSize, Entity.unitSize);
                  continue;
               }
               // draws an obstacle in its corrosponding spot & waterMonster
               else if (currentRoom.get(x).get(y) instanceof Obstacle) {
                  Obstacle obstacle = (Obstacle) currentRoom.get(x).get(y);
                  if (obstacle.type.equals("rock")) {
                     g.drawImage(obstacle.isSuperBombable ? rockSuperBombable:rock, obstacle.cx - Entity.unitSize / 2, obstacle.cy - Entity.unitSize / 2, Entity.unitSize, Entity.unitSize,
                           driver);
                     if (obstacle.isExplodable || obstacle.isSuperBombable) {
                        obstacle.openExplosion(this, x, y);
                     }
                     continue;
                  } else if (obstacle.type.equals("wall")) {
                     g.drawImage(wall, obstacle.cx - Entity.unitSize / 2, obstacle.cy - Entity.unitSize / 2, Entity.unitSize, Entity.unitSize,
                           driver);
                     if (obstacle.isExplodable) {
                        obstacle.openExplosion(this, x, y);
                     }
                     continue;
                  } else if (obstacle.type.equals("water")) {
                     g.drawImage(water, obstacle.cx - Entity.unitSize / 2, obstacle.cy - Entity.unitSize / 2, Entity.unitSize, Entity.unitSize,
                           driver);
                     if (!WaterMonster.isSpawned && !Player.isPaused && player.stun == 0) {
                        if ((int) (Math.random() * Math.abs((50 - numWater))) <= 0) {
                           WaterMonster.spawn(x * 41 + 23, y * 41 + 140, numWater);
                        }
                     } else if (!Player.isPaused && player.stun == 0) {
                        WaterMonster.doThings(sword, player);
                        g.drawImage(WaterMonster.waterMonster, WaterMonster.cx - Entity.unitSize / 2,
                              WaterMonster.cy - Entity.unitSize / 2 + ActiveMenu.iterationNum, Entity.unitSize,
                              Entity.unitSize, driver);
                     }
                     continue;
                  } else if (obstacle.type.equals("tree")) {
                     g.drawImage(tree, obstacle.cx - Entity.unitSize / 2, obstacle.cy - Entity.unitSize / 2, Entity.unitSize, Entity.unitSize,
                           driver);
                     if (obstacle.isBurnable)
                        obstacle.openFire(this, x, y);
                  }
               }
               // draws a bridge in its corresponding spot
               else if (currentRoom.get(x).get(y) instanceof Bridge) {
                  Bridge bridge1 = (Bridge) currentRoom.get(x).get(y);
                  bridge1.draw(g, driver);
                  bridge1.loadDock(player, g, driver);
                  continue;
               }
               // draws a red octorok in its corrosponding spot
               else if (currentRoom.get(x).get(y) instanceof OctorokR) {
                  if (numSpawnedOctorokRs >= numOctorokRs)
                     numSpawnedOctorokRs = 0;
                  if (numSpawnedOctorokRs != numOctorokRs)
                     numSpawnedOctorokRs += 1;
                  OctorokR octorokR = (OctorokR) currentRoom.get(x).get(y);
                  octorokR.draw(g, driver);
                  if (!Player.isPaused) {
                     octorokR.callBaseFunctions(player, sword);
                     octorokR.shootProjectile();
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof OctorokB) {
                  if (numSpawnedOctorokBs >= numOctorokBs)
                     numSpawnedOctorokBs = 0;
                  if (numSpawnedOctorokBs != numOctorokBs)
                     numSpawnedOctorokBs += 1;
                  OctorokB octorokB = (OctorokB) currentRoom.get(x).get(y);
                  if (octorokB.inv % 2 == 0)
                     octorokB.draw(g, driver);
                  if (!Player.isPaused) {
                     octorokB.callBaseFunctions(player, sword);
                     octorokB.shootProjectile();
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Skeleton) {
                  Skeleton skeleton = (Skeleton) currentRoom.get(x).get(y);
                  skeleton.draw(g, driver);
                  if (!Player.isPaused) {
                     skeleton.dropItem(player,driver, g);
                     if (skeleton.hp > 0) {
                        skeleton.callBaseFunctions(player, sword);
                     } else {
                        skeleton.despawn();
                     }
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Mummy) {
                  Mummy mummy = (Mummy) currentRoom.get(x).get(y);
                  if (mummy.inv % 2 == 0)
                     mummy.draw(g, driver);
                  if (!Player.isPaused) {
                     player.hurtEntity(mummy);
                     if (mummy.hp > 0) {
                        mummy.callBaseFunctions(player, sword);
                     }
                     else {
                        mummy.dropItem(player,driver,g);
                        mummy.despawn();
                     }
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Swordsman) {
                  Swordsman swordsman = (Swordsman) currentRoom.get(x).get(y);
                  if (!Player.isPaused) {
                     swordsman.dropItem(player, driver, g);
                     if (swordsman.hp > 0) {
                        swordsman.hurtEntity(sword, player, driver, g);
                        player.hurtEntity(swordsman);
                        swordsman.hurtExplosion();
                        swordsman.calcEnemyDir();
                        Wand.hurt(swordsman);
                        swordsman.moveEntity();
                        for (Obstacle obstacle : Driver.obstacles) 
                           obstacle.collide(swordsman, true);
                     } else {
                        swordsman.despawn();
                     }
                  }
                  swordsman.draw(g,driver);
               } else if (currentRoom.get(x).get(y) instanceof Wizzrobe) {
                  Wizzrobe wizzrobe = (Wizzrobe) currentRoom.get(x).get(y);
                  if (!Player.isPaused) {
                     wizzrobe.dropItem(player, driver, g);
                     if (wizzrobe.hp > 0) {
                        wizzrobe.callBaseFunctions(sword, player,driver,g);
                     } else {

                        wizzrobe.despawn();
                     }
                  }
                  if (wizzrobe.inv % 2 == 0)
                     wizzrobe.draw(g, driver);
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof ShieldEater) {
                  ShieldEater shieldEater = (ShieldEater) currentRoom.get(x).get(y);
                  if (!Player.isPaused) {
                     if (shieldEater.hp > 0) {
                        shieldEater.hurtEntity(sword);
                        shieldEater.hurtExplosion();
                        Arrow.hurt(shieldEater);
                        Wand.hurt(shieldEater);
                        shieldEater.hurt(player);
                        Boomerang.stun(shieldEater);
                        for (Fire fire : Driver.fires) 
                           fire.burn(shieldEater);                        
                        shieldEater.calcEnemyDir();
                        shieldEater.moveEntity();
                        for (Obstacle obstacle : Driver.obstacles) 
                           obstacle.collide(shieldEater, true);
                     } else {
                        currentRoom.remove(shieldEater);
                        if (shieldEater.cx - Entity.unitSize / 2 < player.cx + Entity.unitSize / 2
                              && shieldEater.cx + Entity.unitSize / 2 > player.cx - Entity.unitSize / 2
                              && shieldEater.cy - Entity.unitSize / 2 < player.cy + Entity.unitSize / 2
                              && shieldEater.cy + Entity.unitSize / 2 > player.cy - Entity.unitSize / 2)
                           player.dir = 'n';
                        ShieldEater.playerIsStuck = false;
                        shieldEater.dropItem(player, driver, g);
                        shieldEater.despawn();
                     }
                  }
                  shieldEater.draw(g, driver);
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Snake) {
                  Snake snake = (Snake) currentRoom.get(x).get(y);
                  if (snake.inv % 2 == 0)
                     snake.draw(g, driver);
                  if (!Player.isPaused) {
                     snake.hurtEntity(player);
                     if (snake.hp > 0) {
                         snake.callBaseFunctions(player,sword);  
                     }
                     else {
                        snake.dropItem(player,driver,g);
                        snake.despawn();
                     }
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Boss1) {
                  Boss1 boss1 = (Boss1) currentRoom.get(x).get(y);
                  boss1.hurtEntity(sword);
                  boss1.decreaseInv();
                  if (!Player.isPaused) {
                     if (boss1.hp > 0) {
                        boss1.calcDir(player);
                        boss1.moveEntity();
                        boss1.shootProjectile(player);
                        player.hurtEntity(boss1);
                        if (boss1.inv % 2 == 0)
                           boss1.draw(g, driver);
                     } else if (LoadingZone.numDefeatedBosses == 0) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss1.cx;
                        heartContainer.cy = boss1.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                  }
                  continue;
               } else if (currentRoom.get(x).get(y) instanceof Boss2) {
                  Boss2 boss2 = (Boss2) currentRoom.get(x).get(y);
                  Arrow.hurt(boss2);
                  boss2.decreaseInv();
                  if (!Player.isPaused) {
                     if (boss2.hp > 0) {
                        boss2.calcDir();
                        boss2.moveEntity();
                        boss2.shootProjectile();
                        player.hurtEntity(boss2);
                        boss2.draw(g, driver);
                     } else if (LoadingZone.numDefeatedBosses <= 1) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss2.cx;
                        heartContainer.cy = boss2.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                  }
               } else if (currentRoom.get(x).get(y) instanceof Boss3) {
                  Boss3 boss3 = (Boss3) currentRoom.get(x).get(y);
                  boss3.decreaseInv();
                  if (!Player.isPaused) {
                     if (!Boss3.heads.isEmpty()) {
                        for (int i = 0; i < Boss3.heads.size(); i++) {
                           Boss3.Head head = Boss3.heads.get(i);
                           if (head.hp > 0) {
                              head.draw(g, driver, boss3);
                              Boomerang.stun(head);
                              head.hurtEntity(sword);
                              player.hurtEntity(head);
                              head.shootProjectile();
                              head.move();
                              head.calcDirs(boss3);
                              boss3.draw(g, driver);
                           } else {
                              head.despawn();
                              Boss3.heads.remove(head);
                           }
                        }

                     } else if (LoadingZone.numDefeatedBosses == 2) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss3.cx;
                        heartContainer.cy = boss3.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                     continue;
                  }
               } else if (currentRoom.get(x).get(y) instanceof Boss4) {
                  Boss4 boss4 = (Boss4) currentRoom.get(x).get(y);
                  boss4.decreaseInv();
                  if (!Player.isPaused) {
                     if (!Boss4.heads.isEmpty()) {
                        boss4.draw(g, driver);
                        boss4.calcEnemyDir();
                        boss4.moveEntity();
                        boss4.shootProjectile();
                        for (int i = 0; i < Boss4.heads.size(); i++) {
                           Boss4.Head head = Boss4.heads.get(i);
                           if (head.hp > 0) {
                              if (head.inv % 2 == 0)
                                 head.draw(g, driver);
                              Boomerang.stun(head);
                              Wand.hurt(head);
                              head.hurtEntity(sword);
                              player.hurtEntity(head);
                              head.hurtExplosion();
                              head.move(boss4);
                           } else {
                              head.despawn();
                              Boss4.heads.remove(head);
                           }
                        }

                     } else if (LoadingZone.numDefeatedBosses == 3) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss4.cx;
                        heartContainer.cy = boss4.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                     continue;
                  }
               }
               else if (currentRoom.get(x).get(y) instanceof Boss5) {
                  Boss5 boss5 = (Boss5) currentRoom.get(x).get(y);
                  boss5.hurtEntity(sword);
                  boss5.decreaseInv();
                  if (!Player.isPaused) {
                     if (boss5.hp > 0) {
                        boss5.calcEnemyDir();
                        boss5.moveEntity();
                        boss5.shootProjectile(player);
                        player.hurtEntity(boss5);
                        if(boss5.inv%2 == 0)
                           boss5.draw(g, driver);
                     } else if (LoadingZone.numDefeatedBosses == 4) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss5.cx;
                        heartContainer.cy = boss5.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                  }
                  continue;
               }else if (currentRoom.get(x).get(y) instanceof Boss6) {
                  Boss6 boss6 = (Boss6) currentRoom.get(x).get(y);
                  boss6.decreaseInv();
                  if (!Player.isPaused) {
                     if (!Boss6.heads6.isEmpty()) {
                        for (int i = 0; i < Boss6.heads6.size(); i++) {
                           Boss6.Head6 head = Boss6.heads6.get(i);
                           if (head.hp > 0) {
                              head.draw(g, driver, boss6);
                              Boomerang.stun(head);
                              head.hurtEntity(sword);
                              player.hurtEntity(head);
                              head.shootProjectile();
                              head.move();
                              head.calcDirs(boss6);
                              boss6.draw(g, driver);
                           } else {
                              head.despawn();
                              Boss6.heads6.remove(head);
                           }
                        }

                     } else if (LoadingZone.numDefeatedBosses == 5) {
                        Item heartContainer = (Item) Driver.items.get(5);
                        heartContainer.cx = boss6.cx;
                        heartContainer.cy = boss6.cy;
                        currentRoom.remove(currentRoom.get(x).get(y));
                        LoadingZone.numDefeatedBosses += 1;
                        LoadingZone.currentRoomBlock[3] = 0;
                        LoadingZone.currentRoomBlock[0] = 3;
                     }
                     continue;
                  }
               } else if(currentRoom.get(x).get(y) instanceof Boss7){
                  Boss7 boss7 = (Boss7) currentRoom.get(x).get(y);
                  if(!Player.isPaused){
                  if(boss7.hp > 0){ 
                  boss7.moveEntity();  
                  boss7.teleport(player);
                  boss7.hurtEntity(sword);
                  boss7.attack(player);
                  player.hurtEntity(boss7);
                  if(boss7.inv > 0)
                     boss7.inv--;
                  boss7.calcEnemyDir(player);
                  
                  boss7.draw(g,driver);
                  for(int i = 0; i < Boss7.darkSwords.size();i++){
                     DarkSword darkSword = Boss7.darkSwords.get(i);
                     darkSword.draw(driver,g);
                     darkSword.move();
                     darkSword.hurtPlayer(player);
                  }
                  }else if (LoadingZone.numDefeatedBosses == 6) {
                     Item heartContainer = (Item) Driver.items.get(5);
                     heartContainer.cx = boss7.cx;
                     Boss7.darkTimer = 0;
                     Boss7.darkSwords.clear();
                     heartContainer.cy = boss7.cy;
                     boss7.despawn();
                     currentRoom.remove(currentRoom.get(x).get(y));
                     LoadingZone.numDefeatedBosses += 1;
                     LoadingZone.currentRoomBlock[3] = 0;
                     LoadingZone.currentRoomBlock[0] = 3;
                  }
                  }
               
               }
               else if (currentRoom.get(x).get(y) instanceof SuperBomb){
                  if(!Player.isPaused){
                  SuperBomb superbomb = (SuperBomb) currentRoom.get(x).get(y);
                  superbomb.drawBomb(driver,g,x);
                  }
               }
               else if (currentRoom.get(x).get(y) instanceof TriforcePiece) {
                  TriforcePiece triforcePiece = (TriforcePiece) currentRoom.get(x).get(y);
                  triforcePiece.collectTriforce(this,player);
                  triforcePiece.draw(g, driver);
                  continue;
               }


            }

         }
         if (Player.level != 0)
            LoadingZone.exit(player, this);
         if (Player.level < 0 && LoadingZone.activeShop != null) {
            LoadingZone.activeShop.drawShopPrices(g);
            LoadingZone.activeShop.drawShopItems(g, driver);
            LoadingZone.activeShop.buyItems(player);
         }
      }
   }
   
   public void setText(String line1, String line2, String line3) {
      text.add(line1);
      text.add(line2);
      text.add(line3);
   }

   public void clearText() {
      text.clear();
   }

   public void drawText(Graphics g, Sword sword, ActiveMenu activeMenu) {
      for (int i = 0; i < text.size(); i++) {
         g.setColor(Color.WHITE);
         Font font = new Font("Verdana", Font.PLAIN, 40);
         g.setFont(font);
         FontMetrics fm = g.getFontMetrics();
         ;
         String label = (String) text.get(i);
         g.drawString(label, (800 - fm.stringWidth(label)) / 2,
               ((400 + i * 80 - fm.getHeight()) / 2) + fm.getAscent() + ActiveMenu.iterationNum);
      }
   }

   public void setImages(BufferedImage image) {
      images.add(image);
   }

   public void clearImages() {
      images.clear();
   }

   public void setImages(BufferedImage image1, BufferedImage image2, BufferedImage image3) {
      images.add(image1);
      images.add(image2);
      images.add(image3);
   }

   public void setShopPrices(String price1, String price2, String price3) {
      text.add(price1);
      text.add(price2);
      text.add(price3);
   }

   public static void collectImages(Player player) {
      for (int i = 0; i < images.size(); i++) {
         if (imagecx - Entity.unitSize / 2 < player.cx + Entity.unitSize / 2
               && imagecx + Entity.unitSize / 2 > player.cx - Entity.unitSize / 2
               && imagecy - Entity.unitSize / 2 < player.cy + Entity.unitSize / 2
               && imagecy + Entity.unitSize / 2 > player.cy - Entity.unitSize / 2) {
            BufferedImage image = (BufferedImage) images.get(i);
            if (!Driver.metalSwordW.equals(image) || player.Mhp >= 14) {
               Driver.itemCollected.setFramePosition(0);
               Driver.itemCollected.loop(0);
            }
            if (Driver.woodenSwordW.equals(image))
               Sword.type = "wooden";
            else if (images.get(i).equals(Boomerang.boomerang))
               Player.hasBoomerang = true;
            else if (images.get(i).equals(Arrow.arrowW))
               Player.hasArrows = true;
            else if (images.get(i).equals(raft))
               Player.hasRaft = true;
            else if (images.get(i).equals(Wand.wandW))
               Player.hasWand = true;
            else if (images.get(i).equals(Cane.cane))
               Player.hasCane = true;
            else if (images.get(i).equals(SuperBomb.superBomb))
               Player.hasSuperBomb = true;
            else if (Driver.metalSwordW.equals(image) && player.Mhp >= 14) {
               Sword.type = "metal";
               Sword.damage += 1;
            }
            if (!Driver.metalSwordW.equals(image) || player.Mhp >= 14)
               images.remove(i);
         }
      }
   }

   // takes in the players location, and refills the parts in the room to their
   // respective values
   /**
    * @param player
    */
   public void spawnRoom(Player player) {
      clearText();
      System.out.println(Player.location[0] + "," + Player.location[1]);
      for (int x = 0; x < roomToBeGenerated.length; x++) {
         for (int y = 0; y < roomToBeGenerated[0].length; y++) {
            if (Player.level == 0) {
               // starting room
               if (Player.location[0] == 10 && Player.location[1] == 10) {

                  if (y >= 0 && y <= 3 || y >= 11 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "null", "", x);
                  else if (y == 4)
                     roomToBeGenerated[x][y] = makeRow("loadingZone in room", "null", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // rooms directly up from the start
               else if (Player.location[0] == 10 && Player.location[1] == 11) {
                  if (y >= 0 && y <= 4)
                     roomToBeGenerated[x][y] = 1;
                  else if (y >= 11 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "null", "", x);
                  else if (y == 4)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "2RO", "", x);
                  else if (y == 8)
                     roomToBeGenerated[x][y] = makeRow("empty", "2RO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 rooms up from the start
               else if ((Player.location[0] == 10 && Player.location[1] == 12)) {
                  if (y >= 11)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "", "", x);
                  else if (y >= 7)
                     roomToBeGenerated[x][y] = makeRow("null", "2BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room directly on top of the previous room (opening room to something)
               else if ((Player.location[0] == 10 && Player.location[1] == 13)) {
                  if (y >= 11 || y <= 4)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "", "", x);
                  else if (y >= 6 && y <= 11)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  if (y < 2 && x != 10 && x != 11)
                     roomToBeGenerated[x][y] = 4;
                  continue;
               }
               // room above the previous room(has a dock that leads into dungeon 5 and a heart
               else if (Player.location[0] == 10 && Player.location[1] == 14) {
                  if ((y > 10 || y < 4) && (x != 10 && x != 11))
                     roomToBeGenerated[x][y] = 1;
                  else if (y <= 10 && y >= 4)
                     roomToBeGenerated[x][y] = 4;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][11] = 17;
                  roomToBeGenerated[11][11] = 17;
                  roomToBeGenerated[10][4] = 18;
                  roomToBeGenerated[11][4] = 18;
                  continue;
               }
               // room above the previous room (left is a heart, right is dungeon 5)
               else if (Player.location[0] == 10 && Player.location[1] == 15) {
                  if (y > 10 && (x != 10 && x != 11) || y == 0)
                     roomToBeGenerated[x][y] = 1;
                  else if (y <= 10)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // left of the previous room
               else if (Player.location[0] == 9 && Player.location[1] == 15) {
                  if (x == 0 || y == 0 || y > 10)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[0][5] = 12;
               }
               // 2 room to the right of the previous room(dungeon 5)
               else if (Player.location[0] == 11 && Player.location[1] == 15) {
                  if (x == 18 || y == 0 || y > 10)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  if(LoadingZone.numDefeatedBosses >= 4) roomToBeGenerated[18][5] = 12;
               }
               // room directly down from the start
               else if (Player.location[0] == 10 && Player.location[1] == 9) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "null", "", x);
                  else if (y == 4)
                     roomToBeGenerated[x][y] = makeRow("empty", "2R&BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below previous room
               else if (Player.location[0] == 10 && Player.location[1] == 8) {
                  if (y > 12)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 6 || y == 8)
                     roomToBeGenerated[x][y] = makeRow("empty", "2R&BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 11 && Player.location[1] == 8) {
                  if (y > 12)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 12 && Player.location[1] == 8) {
                  if (y > 12 || x == 18)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above previous room
               else if (Player.location[0] == 12 && Player.location[1] == 9) {
                  if (x == 18)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above previous room
               else if (Player.location[0] == 12 && Player.location[1] == 10) {
                  if (x == 18)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above previous room
               else if (Player.location[0] == 12 && Player.location[1] == 11) {

                  if (x == 18 && (y != 10 && y != 11))
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above previous room
               else if (Player.location[0] == 12 && Player.location[1] == 12) {
                  if (y == 0 || x == 18)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the room at (12,11) (lets the player use the raft to a
               // new area)
               else if (Player.location[0] == 13 && Player.location[1] == 11) {
                  if ((x == 0 || x == 1) && (y != 10 && y != 11))
                     roomToBeGenerated[x][y] = 1;
                  else if (x >= 2)
                     roomToBeGenerated[x][y] = 4;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[2][10] = 20;
                  roomToBeGenerated[2][11] = 20;
                  continue;
               }
               // entrance room to the new dock( 1 room to the right of the previous room)
               else if (Player.location[0] == 14 && Player.location[1] == 11) {
                  if ((x >= 17) && (y != 10 && y != 11))
                     roomToBeGenerated[x][y] = 1;
                  else if (x <= 16)
                     roomToBeGenerated[x][y] = 4;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[16][10] = 19;
                  roomToBeGenerated[16][11] = 19;
                  continue;
               }
               // 1 room directly right from the previous room (branch room)
               else if (Player.location[0] == 15 && Player.location[1] == 11) {
                  if (x == 0 && (y != 10 && y != 11))
                     roomToBeGenerated[x][y] = 1;
                  else if (y >= 0 && y <= 4 || y >= 13 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if ((y == 5 || y == 6) && (x == 10 || x == 13))
                     roomToBeGenerated[x][y] = y == 5 ? 7 : 8;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above the branch room at (15,11)
               else if (Player.location[0] == 15 && Player.location[1] == 12) {
                  if (x == 0 || y <= 4)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 5 || y == 6)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else if (y >= 13)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if ((y == 5 || y == 6) && (x == 10 || x == 13))
                     roomToBeGenerated[x][y] = y == 5 ? 7 : 8;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[2][4] = 12;
                  continue;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 16 && Player.location[1] == 12) {
                  if (x == 18 || y <= 4)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 5 || y == 6)
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else if (y >= 13)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if ((y == 5 || y == 6) && (x == 10 || x == 13))
                     roomToBeGenerated[x][y] = y == 5 ? 7 : 8;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[6][4] = 12;
                  continue;
               }
               // room below the previous room(has a third shop)
               else if (Player.location[0] == 16 && Player.location[1] == 11) {

                  if ((x == 2 || x == 3) && y == 4)
                     roomToBeGenerated[x][y] = 6;
                  else if ((y <= 4 || y >= 13) && x <= 6 || x >= 12)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the branch room at (15,11)
               else if (Player.location[0] == 15 && Player.location[1] == 10) {
                  if (y <= 1)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (x == 0)
                     roomToBeGenerated[x][y] = 1;
                  else if (y >= 0 && y <= 4)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "", "", x);
                  else if (y >= 13 && y <= 18)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][10] = 15;
                  continue;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 16 && Player.location[1] == 10) {
                  if (x >= 13)
                     roomToBeGenerated[x][y] = 1;
                  else if (y >= 0 && y <= 4 || y >= 13 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if ((y == 5 || y == 6) && (x >= 10 && x <= 13))
                     roomToBeGenerated[x][y] = y == 5 ? 7 : 8;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == 16 && Player.location[1] == 9) {
                  if (y >= 0 && y <= 4)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (y >= 10 && x == 0)
                     roomToBeGenerated[x][y] = 1;
                  else if (x >= 13)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room(holds a cave with advice, "secrets are hidden in
               // trees, set them on fire to reveal them."
               else if (Player.location[0] == 16 && Player.location[1] == 8) {
                  if (y == 14 && (x == 10 || x == 11))
                     roomToBeGenerated[x][y] = 6;
                  else if (x == 0 || x == 18 || y == 14)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 10 || y == 11 && (x != 0 && x != 18))
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room above and to the right of the previous room(another branch room)
               else if (Player.location[0] == 15 && Player.location[1] == 9) {
                  if (y <= 4 || (x == 18 && y >= 10) || (x == 0 && y == 14))
                     roomToBeGenerated[x][y] = 1;
                  else if (y >= 5 && y <= 7)
                     roomToBeGenerated[x][y] = makeRow("", "DANGER! " + (y == 6 ? "red" : "blue") + " octoroks", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the previous room(literally nothing here, but we're going to make
               // the player think otherwise ;] )
               else if (Player.location[0] == 15 && Player.location[1] == 8) {
                  if (y == 14 && (x == 10 || x == 11))
                     roomToBeGenerated[x][y] = 14;
                  else if (x == 0 || x == 18 || y == 14)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 10 || y == 11 && (x != 0 && x != 18))
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the left of the branch room at (15,9) (entrance to dungeon 4)
               else if (Player.location[0] == 14 && Player.location[1] == 9) {
                  if (y == 14 || y <= 4 || x == 0)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  if (ActiveMenu.numTriforcePieces >= 3)
                     roomToBeGenerated[10][10] = 15;
               }
               // rooms directly right from the start
               else if (Player.location[0] == 11 && Player.location[1] == 10) {
                  if (y >= 0 && y <= 4 || y >= 11 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("rocks on left", "", "", x);
                  else if (y == 5)
                     roomToBeGenerated[x][y] = makeRow("", "2R&BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room below the previous room
               else if (Player.location[0] == 11 && Player.location[1] == 9) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("rocks on left", "", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // rooms directly left from the start
               else if (Player.location[0] == 9 && Player.location[1] == 10) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (y > 0 && y < 4 || y >= 11 && y <= 17)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "null", "", x);
                  else if (y == 4)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "2RO", "", x);
                  else if (y == 8)
                     roomToBeGenerated[x][y] = makeRow("empty", "2R&BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room on top of previous room
               else if (Player.location[0] == 11 && Player.location[1] == 11) {
                  if (y >= 0 && y <= 4 || y >= 11 && y <= 17)
                     roomToBeGenerated[x][y] = makeRow("rocks on left", "null", "", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("", "2BO", "", x);
               }
               // room on top of previous room
               else if (Player.location[0] == 11 && Player.location[1] == 12) {
                  if (y >= 11 && y <= 18)
                     roomToBeGenerated[x][y] = makeRow("rocks on left", "", "", x);
                  else if (y == 0)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 10 || y == 9)
                     roomToBeGenerated[x][y] = makeRow("", "1 red octorok", "", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  roomToBeGenerated[10][5] = 12;
               }
               // going up? yup! room on top of previous room
               else if (Player.location[0] == 11 && Player.location[1] == 13) {
                  if (y >= 11 && y <= 15 || y <= 4){
                     roomToBeGenerated[x][y] = 1;
                     roomToBeGenerated[x][y] = makeRow("", "", "small river", x);
                     }
                  else
                     roomToBeGenerated[x][y] = makeRow("", "2R&BO", "small bridge", x);
               }
               // The second dungeon room(1 room to the left of the previous room
               else if (Player.location[0] == 12 && Player.location[1] == 13) {
                  if (x == 0) {
                     if (y >= 11 && y <= 15 || y <= 4)
                        roomToBeGenerated[x][y] = 4;
                     else
                        roomToBeGenerated[x][y] = 5;
                  } else if (y == 0 || y >= 15 || x >= 17)
                     roomToBeGenerated[x][y] = 1;
                  // only spawns level 2 if level 1 is completed& the triforce was collected...
                  else if (x == 16 && (y == 10 || y == 11) && ActiveMenu.numTriforcePieces >= 1)
                     roomToBeGenerated[x][y] = 6;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // the room 1 up and 1 left from the start(The first shop room in the game)
               else if (Player.location[0] == 9 && Player.location[1] == 11) {
                  if (y == 14)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (y >= 0 && y <= 3)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "null", "", x);
                  else if (y == 4)
                     roomToBeGenerated[x][y] = makeRow("loadingZone in room", "null", "", x);
                  else if (y == 8 || y == 10)
                     roomToBeGenerated[x][y] = makeRow("empty", "2R&BO", "", x);
                  else if (y >= 11)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "null", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // the room on top of the first shop in the game
               else if (Player.location[0] == 9 && Player.location[1] == 12) {
                  if (y == 8 || y == 10)
                     roomToBeGenerated[x][y] = makeRow("empty", "2R&BO", "", x);
                  // spawns a rock that can be exploded in the top right corner of the left group
                  // of rocks
                  else if (y == 11 && x == 6)
                     roomToBeGenerated[x][y] = 12;
                  else if (y >= 11)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "null", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 room to the left of previous room
               else if (Player.location[0] == 8 && Player.location[1] == 12) {
                  if (y == 8 || y == 10)
                     roomToBeGenerated[x][y] = makeRow("", "2RO", "small river", x);
                  else if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("", "2BO", "small bridge", x);
                  else if (y >= 11)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "", "small river", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("", "", "small river", x);
                  if (x == 0 && y != 6 && y != 7)
                     roomToBeGenerated[x][y] = 4;
               }
               // room directly left of previous room(starts path into the second shop room)
               else if (Player.location[0] == 7 && Player.location[1] == 12) {
                  if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 4;
               }
               // second shop room(1 left of previous room)
               else if (Player.location[0] == 6 && Player.location[1] == 12) {
                  if (x > 3 && y > 3 && y < 10) {
                     if (x >= 4 && y >= 4 && x <= 5 && y <= 5)
                        roomToBeGenerated[x][y] = 6;
                     else if (y == 6 && x <= 6)
                        roomToBeGenerated[x][y] = 1;
                     else if (x == 9)
                        roomToBeGenerated[x][y] = 7;
                     else if (y >= 4 && y <= 5 && x >= 10 || y >= 8 && y <= 9 && x >= 10)
                        roomToBeGenerated[x][y] = 4;
                     else
                        roomToBeGenerated[x][y] = 0;
                     continue;
                  } else
                     roomToBeGenerated[x][y] = 4;
               }
               // 1 rooom on top of previous room
               else if (Player.location[0] == 6 && Player.location[1] == 13) {
                  if (x >= 16 && y >= 2 && y <= 3)
                     roomToBeGenerated[x][y] = 0;
                  else if (x == 18)
                     roomToBeGenerated[x][y] = 1;
                  else if (x > 3 && y > 3 && y < 10) {
                     if (y >= 4 && y <= 5 && x >= 10 || y >= 8 && y <= 9 && x >= 10)
                        roomToBeGenerated[x][y] = 0;
                     else
                        roomToBeGenerated[x][y] = 0;
                     continue;
                  } else
                     roomToBeGenerated[x][y] = 1;
                  roomToBeGenerated[3][4] = 12;
               }
               // 1 room to the right of previous room
               else if (Player.location[0] == 7 && Player.location[1] == 13) {
                  if (y < 2)
                     roomToBeGenerated[x][y] = 4;
                  else if (y >= 4)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 8 && Player.location[1] == 13) {
                  if (y < 2)
                     roomToBeGenerated[x][y] = 4;
                  else if (y >= 13 && x >= 1 && x < 4)
                     roomToBeGenerated[x][y] = 0;
                  else if (y >= 4 && x <= 5)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 more room to the right of the previous room
               else if (Player.location[0] == 9 && Player.location[1] == 13) {
                  if (y < 2)
                     roomToBeGenerated[x][y] = 4;
                  else if (y > 4 && y < 8)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  if (x == 10 || x == 11)
                     roomToBeGenerated[x][1] = 12;
                  continue;
               }
               // room directly left from the shop
               else if (Player.location[0] == 8 && Player.location[1] == 11) {
                  if (y == 14)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (y >= 0 && y <= 4 || y == 18)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "null", "small river", x);
                  else if (y == 8 || y == 10)
                     roomToBeGenerated[x][y] = makeRow("empty", "2RO", "small river", x);
                  else if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("empty", "2BO", "small bridge", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("empty", "null", "small river", x);
                  if (x == 0 && y != 6 && y != 7 && y != 14)
                     roomToBeGenerated[x][y] = 4;
               }
               // 1 down from previous room
               else if (Player.location[0] == 8 && Player.location[1] == 10) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("openingMiddle", "", "", x);
                  else if (y >= 0 && y <= 4 || y == 18 || y == 8 || y == 10)
                     roomToBeGenerated[x][y] = makeRow("empty", "2BO", "small river", x);
                  else if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("empty", "2RO", "small bridge", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("empty", "null", "small river", x);
               }
               // 2 rooms directly left from the shop(room into first dungeon room)
               else if (Player.location[0] == 7 && Player.location[1] == 11) {
                  if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else
                     roomToBeGenerated[x][y] = 4;
               }
               // first dungeon room(1 left of previous room)
               else if (Player.location[0] == 6 && Player.location[1] == 11) {
                  if (x > 3 && y > 3 && y < 10) {
                     if (x >= 4 && y >= 4 && x <= 5 && y <= 5)
                        roomToBeGenerated[x][y] = 6;
                     else if (y == 6 && x <= 6)
                        roomToBeGenerated[x][y] = 1;
                     else if (x == 9)
                        roomToBeGenerated[x][y] = 8;
                     else if (y >= 4 && y <= 5 && x >= 10 || y >= 8 && y <= 9 && x >= 10)
                        roomToBeGenerated[x][y] = 4;
                     else
                        roomToBeGenerated[x][y] = 0;
                     continue;
                  } else
                     roomToBeGenerated[x][y] = 4;
               }
               // room below the previous room(opening to the lost woods)
               else if (Player.location[0] == 6 && Player.location[1] == 10) {
                  // generates a new map for the lost woods
                  if (x == 0 && y == 0)
                     MapGenerator.generate();
                  if (y == 0)
                     roomToBeGenerated[x][y] = 4;
                  else if (y > 3 && y < 11)
                     roomToBeGenerated[x][y] = makeRow("", "!!BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  if ((x == 0) && (y <= 3 || y >= 11))
                     roomToBeGenerated[x][y] = 1;
                  continue;
               }
               // room to the right of previous room
               else if (Player.location[0] == 7 && Player.location[1] == 10) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = 4;
                  else if (y > 3 && y < 11)
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == 7 && Player.location[1] == 9) {
                  if (y > 3 && y < 11)
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 6 && Player.location[1] == 9) {
                  if (x == 0)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == 6 && Player.location[1] == 8) {
                  if (x == 0)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 6)
                     roomToBeGenerated[x][y] = makeRow("", "2R&BO", "", x);
                  else if (y == 12)
                     roomToBeGenerated[x][y] = makeRow("", "2RO", "", x);
                  else if (y > 12)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room to the right of the previous room
               else if (Player.location[0] == 7 && Player.location[1] == 8) {
                  if (y > 12)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // you'll never believe it! this room is to the right of the previous room!(it's
               // another opening room to some area I don't know what to put in that area
               // though)
               else if (Player.location[0] == 8 && Player.location[1] == 8) {
                  if (y > 3 && y < 11)
                     roomToBeGenerated[x][y] = makeRow("", "!!RO", "", x);
                  else if (y > 12 && x != 10 && x != 11)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               //room below the previous room(has a bridge and leads to dungeons 6 & 7)
               else if (Player.location[0] == 8 && Player.location[1] == 7){
                  if ((y > 10 || y < 4) && (x != 10 && x != 11))
                     roomToBeGenerated[x][y] = 1;
                  else if (y <= 10 && y >= 4)
                     roomToBeGenerated[x][y] = 4;
                  else
                     roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][11] = 17;
                  roomToBeGenerated[11][11] = 17;
                  roomToBeGenerated[10][4] = 18;
                  roomToBeGenerated[11][4] = 18;
                  continue;
               }
               //room below the previous room (branches room, left leads to dungeon 6, right has a heartPiece, and down leads to dungeon 7)
               else if (Player.location[0] == 8 && Player.location[1] == 6){
                  if(y == 14 && (x < 2  || x > 16)) roomToBeGenerated[x][y] = 1;
                  else if(y==14) roomToBeGenerated[x][y] = Obstacle.superBombBlownRock ? 0:24;
                  else if( y == 0 && x != 10 && x != 11) roomToBeGenerated[x][y] = 1;
                  else if ((x == 7 || x == 13) && y <= 14) roomToBeGenerated[x][y] = x == 7 ? 7:8;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the right of the previous room
               else if (Player.location[0] == 9 && Player.location[1] == 6){
                  if(y == 10 && x == 18) roomToBeGenerated[x][y] = 12;
                  else if(y == 0 || y == 14 || x == 18) roomToBeGenerated[x][y] = 1;
                  else if(x > 1 && y < 5 && x <= 6) roomToBeGenerated[x][y] = 8;
                  else if(x > 3 && y%2 == 0) roomToBeGenerated[x][y] = 7;
                  else roomToBeGenerated[x][y] = 0;
               }
               //2 rooms to the left of the previous room 
               else if (Player.location[0] == 7 && Player.location[1] == 6){
                  if(y == 10 && x == 10 && LoadingZone.numDefeatedBosses == 5) roomToBeGenerated[x][y] = 15;
                  else if(y == 0 || y == 14 || x == 0) roomToBeGenerated[x][y] = 1;
                  else if(x <= 15 && y < 14)roomToBeGenerated[x][y] = y%2 == 0 ? 7:8;
                  else roomToBeGenerated[x][y] = 0;
               }
               //1 room to the right and below the previous room (dungeon 7)
               else if (Player.location[0] == 8 && Player.location[1] == 5){
                  if(x == 0 || y == 14 || x == 18) roomToBeGenerated[x][y] = 1;
                  else if(y == 0) roomToBeGenerated[x][y] = makeRow("openingMiddle","","",x);
                  else if (x == 10 && y == 10 && LoadingZone.numDefeatedBosses == 6) roomToBeGenerated[x][y] = 6;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room 1 below and to the left of the start room
               else if (Player.location[0] == 9 && Player.location[1] == 9) {
                  if (y == 0)
                     roomToBeGenerated[x][y] = makeRow("rocks on right", "", "", x);
                  else if (y == 3 || y == 6)
                     roomToBeGenerated[x][y] = makeRow("", "2R&BO", "", x);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the left of the previous room
               else if (Player.location[0] == 8 && Player.location[1] == 9) {
                  if (y >= 14)
                     roomToBeGenerated[x][y] = 0;
                  else if (y == 6 || y == 7)
                     roomToBeGenerated[x][y] = makeRow("", "", "small bridge", x);
                  else
                     roomToBeGenerated[x][y] = makeRow("", "", "small river", x);

               }
               // yet another room that is directly right of the previous one! what a
               // shocker!!!
               else if (Player.location[0] == 9 && Player.location[1] == 8) {
                  if (y > 12)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // code for the lost woods( a ten by 10 grid to the far left from the start the
               // leads into dungeon 3)
               else if (Player.location[0] >= -4 && Player.location[0] <= 5 && Player.location[1] >= 6
                     && Player.location[1] <= 15) {
                  // resets the number of enemies generated in a room
                  if (x == 0 && y == 0)
                     MapGenerator.numEnemiesSpawned = MapGenerator.MenemiesSpawned;
                  // gets the players location in the lost woods(map location -4,6 is 0,0 in the
                  // lost woods, so a offset is needed) and translates the nodes into
                  // roomToBeGenerated
                  roomToBeGenerated[x][y] = MapGenerator.map[Player.location[0] + 4][Player.location[1] - 6]
                        .translate(x, y);

                  // creates the borders around the lost woods
                  // checks the rooms arent entrance and exit rooms
                  if (Player.location[1] - 6 != 4) {
                     if (Player.location[0] + 4 == 0) {
                        if (x == 0)
                           roomToBeGenerated[x][y] = 14;
                     } else if (Player.location[0] + 4 == 9) {
                        if (x == 18)
                           roomToBeGenerated[x][y] = 14;
                     }
                     if (Player.location[1] - 6 == 9)
                        if (y == 0)
                           roomToBeGenerated[x][y] = 14;
                        else if (Player.location[1] - 6 == 0)
                           if (y == 18)
                              roomToBeGenerated[x][y] = 14;
                  }
               }
               // room to the right of the lost woods(3rd dungeon room to be coded)
               else if (Player.location[0] == -5 && Player.location[1] == 10) {
                  // generates a new map for the lost woods
                  if (x == 0 && y == 0)
                     MapGenerator.generate();
                  if ((x == 0) || (y <= 3 || y >= 11))
                     roomToBeGenerated[x][y] = 1;
                  // generates the 3rd dungeon
                  else if (x == 1 && y > 7 && y < 10 && ActiveMenu.numTriforcePieces >= 2)
                     roomToBeGenerated[x][y] = 6;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // if the player goes into a non-existant room, softlock them!!!!!!!!!!!!!!!
               else
                  roomToBeGenerated[x][y] = 1;
            }
            // dungeon #1
            else if (Player.level == 1) {
               // entrance room for level 1
               if (Player.location[0] == 0 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  // controls the door openings
                  // every entrance room should be open.
                  LoadingZone.currentRoomBlock[1] = 0;
                  // creates a key door on the top if the door hasn't been opened yet.
                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[0][0] ? 2 : 0);
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
               }
               // 1 room to the left of start(nothing in here except 1 enemy)
               else if (Player.location[0] == -1 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][5] = 9;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
               }
               // 1 room to the right of start(has 1 row of enemies that need to be defeated to
               // show a key
               else if (Player.location[0] == 1 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 10) {
                     roomToBeGenerated[x][y] = 9;
                  }
               }
               // the next 2 rooms up(linear with 1 row of enemies in each room)
               else if (Player.location[0] == 0 && (Player.location[1] == 2 || Player.location[1] == 1)) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 7)
                     roomToBeGenerated[x][y] = 9;
               }
               // 1 more room up(banches out with a key door at the top. Identical to starting
               // room
               else if (Player.location[0] == 0 && Player.location[1] == 3) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[0][1] ? 2 : 0);
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y >= 7 && y <= 10 && x >= 7 && x <= 10)
                     roomToBeGenerated[x][y] = 9;
               }
               // 1 room to the left and right of the room above(1 row of enemies) room to the
               // right has a key in it
               else if ((Player.location[0] == -1 || Player.location[0] == 1) && Player.location[1] == 3) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (Player.location[0] == -1)
                     LoadingZone.currentRoomBlock[3] = 0;
                  else
                     LoadingZone.currentRoomBlock[2] = 0;
                  if (y == 13)
                     roomToBeGenerated[x][y] = 9;
               }
               // 1 room up (creates a fork in the road, to the right is the boomerang, to the
               // left is the boss
               else if (Player.location[0] == 0 && Player.location[1] == 4) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y >= 7 && y <= 10 && x >= 7 && x <= 10)
                     roomToBeGenerated[x][y] = 9;
               }
               // tough path of 2 rooms with 1 row of skeletons before the boomerang room
               else if (Player.location[0] > 0 && Player.location[0] < 3 && Player.location[1] == 4) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 15)
                     roomToBeGenerated[x][y] = 9;
               }
               // boomerang room (DIFFICULT!!!!!!!!!!!)
               else if (Player.location[0] == 3 && Player.location[1] == 4) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 15 || y > 13 && y < 15)
                     roomToBeGenerated[x][y] = 9;

               }
               // begins the HARD path of 3 rooms with 2 rows of skeletons before the boss is
               // reached
               else if (Player.location[0] < 0 && Player.location[0] > -4 && Player.location[1] == 4) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 13 || x == 14)
                     roomToBeGenerated[x][y] = 9;
               }
               // rooms turn down to enter the boss room
               else if (Player.location[0] == -4 && Player.location[1] == 4) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  // if the boss is defeated, block the player from accessing the boss room
                  LoadingZone.currentRoomBlock[1] = (LoadingZone.numDefeatedBosses == 0 ? 0 : 1);
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 13 || x == 14)
                     roomToBeGenerated[x][y] = 9;
               }
               // BOSS ROOM
               else if (Player.location[0] == -4 && Player.location[1] == 3) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  if (LoadingZone.numDefeatedBosses == 0)
                     roomToBeGenerated[16][10] = 10;
               }
               // Room with the First triforce piece
               else if (Player.location[0] == -3 && Player.location[1] == 3) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               } else
                  roomToBeGenerated[x][y] = 1;
            }
            // dungeon #2
            else if (Player.level == 2) {
               // Entrance room
               if (Player.location[0] == 0 && Player.location[1] == 0) {

                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[1][0] ? 2 : 0);
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               // 1 room to the left of the start(has 1 swordsman that holds a key)
               else if (Player.location[0] == -1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][10] = 13;
               }
               // 1 room above the start(1st branch room)
               else if (Player.location[0] == 0 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[1][1] ? 2 : 0);
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 4 && x > 6 && x < 13)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the left of the previous room(has 2 swordsmen with a key)
               else if (Player.location[0] == -1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if ((x == 2 || x == 5) && y >= 1 && y <= 12)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 3 && (y == 3 || y == 4))
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the room on top of the start room(2 rooms right of the
               // previous room)
               else if (Player.location[0] == 1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 5 && (y == 2 || y == 3))
                     roomToBeGenerated[x][y] = 9;
                  else if (x == 5 && y > 1 && y < 14)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 4 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room below the previous room(This room has a row of swordsmen with a key)
               else if (Player.location[0] == 1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 5 && y > 1 && y < 10)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 4)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 rooms up from the start(branches out left and right: right to the arrows,
               // left to the boss)
               else if (Player.location[0] == 0 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 2)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 7 && (y == 5 || y == 6))
                     roomToBeGenerated[x][y] = 9;
                  else if (x == 7 && y < 15 && y > 0)
                     roomToBeGenerated[x][y] = 1;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = (!LoadingZone.keyDoor[1][4] ? 2 : 0);
                  ;
                  if (x == 5 && y > 0 && y < 10 || y == 10 && x >= 5)
                     roomToBeGenerated[x][y] = 1;
                  else if ((x == 6 || x == 7) && (y == 2 || y == 3))
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room below the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = (!LoadingZone.keyDoor[1][2] ? 2 : 0);
                  if ((x == 6 || x == 7) && (y == 2 || y == 3))
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room below the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 7 || y == 8)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room above and to the right of the previous room
               else if (Player.location[0] == 3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[1][3] ? 2 : 0);
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               // 1 room below previous room(has 2 rows of skeletons and a key)
               else if (Player.location[0] == 3 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 4 || y == 10)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 rooms above the previous room(1 row of skeletons that hold a key, this room
               // branches back into the room where a key can be used to open the door to the
               // arrows)
               else if (Player.location[0] == 3 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = (!LoadingZone.keyDoor[1][4] ? 2 : 0);
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 2 && y > 8 && y < 14)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 2 && y < 9)
                     roomToBeGenerated[x][y] = 13;
                  else if (y == 8 && x > 2)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room on to the left and on top of the previous room( has the 2 rows of
               // skeletions and 1 row of swordsmen as well as the arrows)
               else if (Player.location[0] == 2 && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 2 || y == 3)
                     roomToBeGenerated[x][y] = 9;
                  else if (y == 4)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 rooms up and 1 room the left of the start (starts path into boss room)
               else if (Player.location[0] == -1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 12)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the left of the previous room
               else if (Player.location[0] == -2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = (!LoadingZone.keyDoor[1][5] ? 2 : 0);
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 4 && (x == 10 || x == 11))
                     roomToBeGenerated[x][y] = 9;
                  else if (y == 5 && (x == 10 || x == 11))
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room below the previous room
               else if (Player.location[0] == -2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 4 && y > 0 && y <= 12)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 4 && y >= 15)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the left of the previous room(room into the boss room)
               else if (Player.location[0] == -3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = (LoadingZone.numDefeatedBosses == 1 ? 0 : 1);
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 13)
                     roomToBeGenerated[x][y] = 9;
                  else if (y == 14)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // BOSS ROOM(1 room below the previous room)
               else if (Player.location[0] == -3 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  roomToBeGenerated[x][y] = 1;
                  roomToBeGenerated[x][y] = 10;
               }
               // spawns the room with the second triforce piece
               else if (Player.location[0] == -2 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               } else
                  roomToBeGenerated[x][y] = 1;
            }
            // dungeon #3
            else if (Player.level == 3) {
               // entrance room
               if (Player.location[0] == 0 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = (!LoadingZone.keyDoor[2][0] ? 2 : 0);
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }
               // left of the previous room(has 1 shieldEater and a key)
               else if (Player.location[0] == -1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][10] = 16;
               }
               // 1 room to the right of the start(has 1 row of shieldEaters)
               else if (Player.location[0] == 1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[x][10] = 16;
               }
               // 1 room above the start room
               else if (Player.location[0] == 0 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if ((x == 3 && y >= 4 && y <= 13) || ((x == 1 || x == 2) && (y > 1 && y < 4)))
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 1 && y == 10)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;

               }
               // room to the right of the previous room
               else if (Player.location[0] == 1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = (y > 1 && y <= 4 ? 16 : y > 4 && y <= 7 ? 13 : y > 7 && y <= 12 ? 9 : 0);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room to the right of the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if ((y == 3 && (x > 1 && x < 17)) || (x == 17 && y >= 10))
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x > 1 && x < 17 && (y == 10 || y == 11))
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room 2 rooms up from the start
               else if (Player.location[0] == 0 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 9 && x > 1 && x < 17)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               } else if (Player.location[0] == 1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 11 && y > 1 && y < 17)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               } else if (Player.location[0] == 2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 11 && y > 1 && y < 17)
                     roomToBeGenerated[x][y] = 16;
                  else if (x == 10 && y > 1 && y < 17)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room to the right of the previous room (branches a vertical path and has a
               // path from the left that holds a key
               else if (Player.location[0] == 3 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if ((x == 3 && y >= 4 && y <= 13) || ((x == 1 || x == 2) && (y > 1 && y < 4)))
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 1 && y == 10)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // going back to point (0,2), room directly up from this room
               else if (Player.location[0] == 0 && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 13 && y > 1 && y < 17)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // next 2 rooms after the top branch room
               else if ((Player.location[0] == 1 || Player.location[0] == 2) && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 13 && y > 1 && y < 17)
                     roomToBeGenerated[x][y] = (Player.location[0] == 2 ? 9 : 13);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 room to the right of the previous room
               else if (Player.location[0] == 3 && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 10)
                     roomToBeGenerated[x][y] = (y > 1 && y <= 4 ? 16 : y > 4 && y <= 7 ? 13 : y > 7 && y <= 12 ? 9 : 0);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 2 rooms below the previous room
               else if (Player.location[0] == 3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = !LoadingZone.keyDoor[2][1] ? 2 : 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 5)
                     roomToBeGenerated[x][y] = (y > 1 && y <= 4 ? 16 : y > 4 && y <= 7 ? 13 : y > 7 && y <= 12 ? 9 : 0);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == 3 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = !LoadingZone.keyDoor[2][2] ? 2 : 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 10 || y == 11)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room(has the raft)
               else if (Player.location[0] == 3 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = (y > 4 && y <= 6 ? 16 : y > 6 && y <= 8 ? 13 : y > 8 && y <= 10 ? 9 : 0);
               }
               // room to the left of the branch room at (0,2) (leads into the boss room
               // eventually....)
               else if (Player.location[0] == -1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[2][3] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if ((x == 10 || x == 11) && y == 10)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room(leads back into the 1st branch room that has a
               // key when you enter from this path
               else if (Player.location[0] == -1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if ((x == 0 || x == 1) && (y == 10 || y == 11))
                     roomToBeGenerated[x][y] = x == 0 ? 13 : 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // 1 up and to the right of the previous room
               else if (Player.location[0] == -2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[2][4] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][10] = 13;
                  continue;
               }
               // 1 room below the previous room(holds a key and leads into a room with another
               // key)
               else if (Player.location[0] == -2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 7)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;

               }
               // 1 room below the previous room(holds a key)
               else if (Player.location[0] == -2 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 5)
                     roomToBeGenerated[x][y] = (y > 1 && y <= 4 ? 16 : y > 4 && y <= 7 ? 13 : y > 7 && y <= 12 ? 9 : 0);
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;

               }
               // room 2 up and 1 to the right of the previous room(starts path to the boss
               // room)
               else if (Player.location[0] == -3 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[2][5] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
                  roomToBeGenerated[10][10] = 16;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == -3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 10)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // room below the previous room
               else if (Player.location[0] == -3 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = (LoadingZone.numDefeatedBosses == 2 ? 0 : 1);
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 10 || y == 11)
                     roomToBeGenerated[x][y] = x == 10 ? 13 : 16;
                  else
                     roomToBeGenerated[x][y] = 0;
                  continue;
               }
               // BOSS ROOM
               else if (Player.location[0] == -3 && Player.location[1] == -1) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  roomToBeGenerated[16][5] = 10;
                  continue;
               }
               // Room with the third triforce piece
               else if (Player.location[0] == -2 && Player.location[1] == -1) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               } else
                  roomToBeGenerated[x][y] = 1;
               continue;
            }
            // dungeon #4
            else if (Player.level == 4) {
               // entrance room(nothing imortant here yet and nothing ever will be here that is
               // important)
               if (Player.location[0] == 0 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[3][0] ? 0 : 2;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room(row of mummies)
               else if (Player.location[0] == -1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the right of the previous room(1 mummy with a key)
               else if (Player.location[0] == 1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 10 && y == 10)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room above the entrance room (branch room & a key that can only be accessed
               // from the left entrance)
               else if (Player.location[0] == 0 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if ((x >= 1 && (y == 5 || y == 10) && x < 6) || (y >= 5 && y <= 10 && x == 6))
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 3)
                     roomToBeGenerated[x][y] = 16;
                  else if (y == 4)
                     roomToBeGenerated[x][y] = (x < 4 ? 9 : x < 10 ? 13 : 21);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // right of the branch room at (0,1)

               else if (Player.location[0] == 1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 16 && x > 0 && x < 18)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // right of the previous room
               else if (Player.location[0] == 2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if ((y == 16 || y == 17) && x > 0 && x < 18)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the previous room (branch room)
               else if (Player.location[0] == 2 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[3][1] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 1 && y > 0 && y < 18)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the previous room(leads down into a path of sheer pain that will
               // make the player question their life choices!!!!!!! also has a key:))
               else if (Player.location[0] == 2 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][2] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 12 || y == 13)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 1 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][3] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 0 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][4] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == -1 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][5] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10 || y == 10)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room (ABSOLUTE PAIN!!!!!!!!!!!!!!!!!!!!!!,
               // but gives the player the magic wand)
               else if (Player.location[0] == -2 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 1 || x == 2 || y == 10 || y == 11)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the right of the branch room at (2,0) (another branch room)
               else if (Player.location[0] == 3 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 12 && x > 0 && x < 18)
                     roomToBeGenerated[x][y] = 13;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room above the previous room
               else if (Player.location[0] == 3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 7)
                     roomToBeGenerated[x][y] = (x < 4 || x >= 10 ? 13 : 21);
                  else if (y == 6 || y == 8)
                     roomToBeGenerated[x][y] = (x < 4 ? 21 : x < 10 ? 9 : 16);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room above the previous room (left has 1 key, up leads to 4 keys)
               else if (Player.location[0] == 3 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if ((y == 12 || y == 11) && (x == 6 || x == 7))
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room(dead end, but has a key that can't be
               // accessed from this entrance)
               else if (Player.location[0] == 2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x < 18 && x >= 10 && y == 6 || y < 18 && y > 6 && x == 10)
                     roomToBeGenerated[x][y] = 1;
                  else if (x == 12 && y == 15)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x > 0 && x < 12 && y == 10)
                     roomToBeGenerated[x][y] = (x > 1 && x <= 4 ? 21 : x > 4 && x <= 7 ? 13 : x > 7 && x <= 12 ? 9 : 0);
                  else if (x > 0 && x < 12 && y == 11)
                     roomToBeGenerated[x][y] = 16;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == 0 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 12 && x > 0 && x < 18)
                     roomToBeGenerated[x][y] = 1;
                  else if (y == 13 && x == 1)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room(branch room: up is a dead end, left has
               // a key, and down leads to the boss room)
               else if (Player.location[0] == -1 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x > 0 && x < 12 && y == 11)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room(has a row of skeletons, a row of
               // mummies, and a key)
               else if (Player.location[0] == -2 && Player.location[1] == 2) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y == 11)
                     roomToBeGenerated[x][y] = 21;
                  else if (x == 3)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the branch room at (-1,2) (starts the path into the boss room and
               // left has a key)
               else if (Player.location[0] == -1 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][6] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x > 0 && x < 12 && y == 10)
                     roomToBeGenerated[x][y] = (x > 1 && x <= 4 ? 21 : x > 4 && x <= 7 ? 13 : x > 7 && x <= 12 ? 9 : 0);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room
               else if (Player.location[0] == -2 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[3][7] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the previous room(leads into the boss room)
               else if (Player.location[0] == -3 && Player.location[1] == 1) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[3][8]
                        ? (LoadingZone.numDefeatedBosses == 3 ? 0 : 3)
                        : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (y >= 1 && y < 4)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the previous room (BOSS ROOM)
               else if (Player.location[0] == -3 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  roomToBeGenerated[16][10] = 10;
               }
               // room with the fourth triforce piece
               else if (Player.location[0] == -2 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               }
               // room above the room at (3,2) (leads into pain)
               else if (Player.location[0] == 3 && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 2)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // next 4 rooms(forces the player to fight 1 row of the following enemies from
               // the leftmost room to the rightmost room (skeleton, swordsman, shieldEater,
               // and mummy), each room has a key
               else if (Player.location[0] >= -1 && Player.location[0] <= 2 && Player.location[1] == 3) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = Player.location[0] == -1 ? 1 : 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  int enemyType = Player.location[0] == -1 ? 21
                        : Player.location[0] == 0 ? 16 : Player.location[0] == 1 ? 13 : 9;
                  if (y == 12 && x > 0 && x < 18)
                     roomToBeGenerated[x][y] = 1;
                  else if (y > 0 && y < 12 && x == 10 || y == 13 && x == 1)
                     roomToBeGenerated[x][y] = enemyType;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the branch room at (3,0) has a row of skeletons
               else if (Player.location[0] == 3 && Player.location[1] == -1) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 8)
                     roomToBeGenerated[x][y] = 9;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // room below the precious room (has a mix of enemies and a key)
               else if (Player.location[0] == 3 && Player.location[1] == -2) {
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (y == 9)
                     roomToBeGenerated[x][y] = (x < 4 || x >= 10 ? 13 : 21);
                  else if (y == 10 || y == 8)
                     roomToBeGenerated[x][y] = (x < 4 ? 21 : x < 10 ? 9 : 16);
                  else
                     roomToBeGenerated[x][y] = 0;
               }
            }
            // dungeon #5
            else if (Player.level == 5) {
               // entrance room(same idea as literally every single entrance room so far, left
               // has a wizzrobe(AAAAAAAAAAAAAAAAAAAAA) and a key, right has a row of
               // wizzrobes(HELP ME!!!!!!!!!!!!!), and up has a keyDoor
               if (Player.location[0] == 0 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[4][0] ? 0:2;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }
               //room to the right of the previous room (has a single wizzrobe and a key) 
               else if (Player.location[0] == 1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x == 10 && y == 10)
                     roomToBeGenerated[x][y] = 22;
                  else
                     roomToBeGenerated[x][y] = 0;

               }
               //room to the left of the start room(has a row of wizzrobes) 
               else if (Player.location[0] == -1 && Player.location[1] == 0) {
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if (x == 10)
                     roomToBeGenerated[x][y] = 22;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               //room above the start room (open branch room, has a mixed row of shieldEaters and wizzrobes)
               else if (Player.location[0] == 0  && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[4][1] ? 0:2;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y == 4) 
                     roomToBeGenerated[x][y] = x > 14 ? 16:22;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a row of mummies and wizzrobes)
               else if(Player.location[0] == -1 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x  == 5) 
                     roomToBeGenerated[x][y] = 1;
                  else if(x == 4) 
                     roomToBeGenerated[x][y] = 22;
                  else if(x == 3)
                     roomToBeGenerated[x][y] = 21;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               // 1 room to the left of the branch room above the start room (has every enemy found in dungeons so far)
               else if(Player.location[0] == 1 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = y <= 3 ? 22 : y <= 7 ? 21: y <= 10 ? 16: y <= 13 ? 13:9;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (branch room)
               else if(Player.location[0] == 2 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  
                  roomToBeGenerated[x][y] = 0;
               }
               //room below the branch room at (2,1), (leads down to keys, but doesn't have a key in this room)
               else if(Player.location[0] == 2 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 10 || y == 11) roomToBeGenerated[x][y] = x < 6 ? 22 : x <= 12 ? y == 10 ? 21:13
                  : y == 10 ? 16:9;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (has a key and leads down into another key)
               else if(Player.location[0] == 2 && Player.location[1] == -1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if((x == 10 || x == 11) && (y == 10 || y == 11)) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (has a bunch of swordmen, skeletons, and a key)
               else if(Player.location[0] == 2 && Player.location[1] == -2){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 14) roomToBeGenerated[x][y] = 9;
                  else if(y > 15) roomToBeGenerated[x][y] = 13;
                  else roomToBeGenerated[x][y] = 0;
               }
               // room to the left of the branch room at (2,1) (dead end, but has a key that can only be acessed from this entrance)
               else if(Player.location[0] == 3 && Player.location[1] == 1){
               LoadingZone.currentRoomBlock[0] = 0;
               LoadingZone.currentRoomBlock[1] = 0;
               LoadingZone.currentRoomBlock[2] = 0;
               LoadingZone.currentRoomBlock[3] = 1;
               if ((x == 3 && y >= 4 && y <= 13) || ((x == 1 || x == 2) && (y > 1 && y < 4)))
                  roomToBeGenerated[x][y] = 1;
               else if(x == 15) roomToBeGenerated[x][y] = y <= 3 ? 22 : y <= 7 ? 21: y <= 10 ? 16: y <= 13 ? 13:9;
               else roomToBeGenerated[x][y] = 0;
               }
               //the 2 rooms above and then to the left of the branch room at(2,1)
               else if ((Player.location[0] == 2 || Player.location[0] == 3) && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = Player.location[0] == 2 ? 1:0;
                  LoadingZone.currentRoomBlock[3] = Player.location[0] == 2 ? 0:1;
                  if(y == 0) roomToBeGenerated[x][y] = Player.location[0] == 2 ? 22:21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the right and below the branch room at (2,1) (another branch room and 1 wizzrobe)
               else if (Player.location[0] == 3 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y == 10 && x == 10) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the branch room at (3,0) (has a row of wizzrobes)
               else if (Player.location[0] == 3 && Player.location[1] == -1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 10) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (has a wall that prevents entrance from below, a wizzrobe, a shieldEater, and a key)
               else if (Player.location[0] == 3 && Player.location[1] == -2){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 9) roomToBeGenerated[x][y] = 22;
                  else if (y == 10) roomToBeGenerated[x][y] = 16;
                  else if (y == 11) roomToBeGenerated[x][y] = 1;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the branch room at (3,0) (has a row of wizzrobes and a key)
               else if (Player.location[0] == 4 && Player.location[1] == 0){
               LoadingZone.currentRoomBlock[0] = 1;
               LoadingZone.currentRoomBlock[1] = 0;
               LoadingZone.currentRoomBlock[2] = 0;
               LoadingZone.currentRoomBlock[3] = 1;
               if(y == 0) roomToBeGenerated[x][y] = 22;
               else roomToBeGenerated[x][y] = 0;
               }
               //next 2 rooms below the previous room (has 1 room with a row of mummies, and the other has a row of sworfdsmen and a row of skeletons)
               else if (Player.location[0] == 4 && (Player.location[1] == -1 || Player.location[1] == -2)){
               LoadingZone.currentRoomBlock[0] = 0;
               LoadingZone.currentRoomBlock[1] = 0;
               LoadingZone.currentRoomBlock[2] = 1;
               LoadingZone.currentRoomBlock[3] = 1;
               if(y == 10) roomToBeGenerated[x][y] = Player.location[1] == -2 ? 13:21;
               else if (y == 11 && Player.location[1] == -2 ) roomToBeGenerated[x][y] = 9;
               else roomToBeGenerated[x][y] = 0;
               }  
               //room to the below the previous room (has a row of wizzrobes, a row of swordsmen, and a key as well as starts the path to the cane of invincibility)
               else if (Player.location[0] == 4 && Player.location[1] == -3){
               LoadingZone.currentRoomBlock[0] = 0;
               LoadingZone.currentRoomBlock[1] = 1;
               LoadingZone.currentRoomBlock[2] = 0;
               LoadingZone.currentRoomBlock[3] = 1;
               if(y == 15) roomToBeGenerated[x][y] = 22;
               else if (y == 14) roomToBeGenerated[x][y] = 13;
               else roomToBeGenerated[x][y] = 0;
               }  
               //room to the left of the previous room (nothing here, up is a dead end, and left leads to death and the cane of invincibility)
               else if (Player.location[0] == 3 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[4][2] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }  
               //these next couple of rooms are torturous to the player, but lead to the cane of invincibility
               else if (Player.location[0] == 2 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[4][3] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = 9;
                  else roomToBeGenerated[x][y] = 0;
               }
               else if (Player.location[0] == 1 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[4][4] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = 13;
                  else roomToBeGenerated[x][y] = 0;
               }
               else if (Player.location[0] == 0 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[4][5] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = 16;
                  else roomToBeGenerated[x][y] = 0;
               }
               else if (Player.location[0] == -1 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[4][6] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = 21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the room with the cane of incinvibility
               else if (Player.location[0] == -2 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //absolute torture for the player, but gives the the cane of invincibility
               else if (Player.location[0] == -3 && Player.location[1] == -3){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y == 0 || y == 1) roomToBeGenerated[x][y] = x <= 6 ? 22: x <= 10 ? 21:16;
                  else if(y == 2) roomToBeGenerated[x][y] = x <= 6 && x >= 10 ? 9:13;
                  else roomToBeGenerated[x][y] = 0;
               }
               //going a completely different route, 1 room above the branch room at (0,1) (left leads to the boss, right is a dead end)
               else if (Player.location[0] == 0 && Player.location[1] == 2 ){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10 && y == 10)roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the right of the previous room (dead end, literally nothing here)
               else if (Player.location[0] == 1 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x >= 18 && y >= 14) roomToBeGenerated[x][y] = y <= 15 ? 22:21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //2 rooms to the left of the previous room (leads to the boss)
               else if (Player.location[0] == -1 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10 || x == 11) roomToBeGenerated[x][y] = x == 10 ? 13:9;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (left is a dead end, right leads to the boss)
               else if (Player.location[0] == -2 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[4][7] ? 0:2;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room(literal dead end)
               else if (Player.location[0] == -3 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 0) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the right and below the previous room (has a row of wizzrobes leads to the boss)
               else if (Player.location[0] == -2 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 10) roomToBeGenerated[x][y] = 22;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (has a row of mummies and leads to the boss room)
               else if (Player.location[0] == -2 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 16) roomToBeGenerated[x][y] = 21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a colums of shieldEaters and the room above the boss room
               else if(Player.location[0] == -3 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.numDefeatedBosses == 4 ? 0:1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 0) roomToBeGenerated[x][y] = 16;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (BOSS ROOM)
               else if(Player.location[0] == -3 && Player.location[1] == -1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  if(x == 10 && y == 10) roomToBeGenerated[x][y] = 10;
                  else roomToBeGenerated[x][y] = 0;
               }
               // Room with the fourth triforce piece
               else if (Player.location[0] == -2 && Player.location[1] == -1) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               }
            }
            //dungeon #6
            else if (Player.level == 6){
               //start room (branches off in all 4 directions: down is to leave, right leads to a key and a snake, left leads to a row of snakes and nothing, and up progresses into the dungeon)
               if(Player.location[0] == 0 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a single snake)
               else if(Player.location[0] == -1 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10 && y == 10)
                     roomToBeGenerated[x][y] = 23;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               //room to the right of the previous room (has a column of snakes and a key)
               else if(Player.location[0] == 1 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x == 10 && y < 14)
                     roomToBeGenerated[x][y] = 23;
                  else
                     roomToBeGenerated[x][y] = 0;
               }
               //room above the start room (right and up are separated from the left and down by a wall, has a key that can only be acessed from the other side of the wall)
               else if(Player.location[0] == 0 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if((x >= 7 && y == 10) || (y <= 10 && x == 7)) roomToBeGenerated[x][y] = 1;
                  else if((y == 3) && (x == 10 || x == 2)) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room to the left of the previous room (has a row of snakes and progresses the dungeon)
               else if(Player.location[0] == -1 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 2 && y < 14) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room above the previous room(has a key, left is death and leads to the boss, right leads to more thingies)
               else if(Player.location[0] == -1 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = LoadingZone.keyDoor[5][0]  ? 0:2;
                  if(x == 2 && y < 14) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room to the left of the previous room(empty branch room)
               else if(Player.location[0] == 0 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y]  = 0;
               }
               //room to the right of the branch room at (0,1) (witteraw dead end)
               else if(Player.location[0] == 1 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y < 14 && (x == 10 || x == 11 || x == 12)) roomToBeGenerated[x][y] = x == 10 ? 9:x == 11 ? 21:23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room to the right of the branch room at (0,2) (has a key door and a row of skeletons)
               else if(Player.location[0] == 1 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = LoadingZone.keyDoor[5][1] ? 0 : 2; 
                  if(y < 14 && x == 10) roomToBeGenerated[x][y] = 9;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room to the right of the previous room (has a row of snakes and a key door)
               else if(Player.location[0] == 2 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[5][2] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1; 
                  if(y < 14 && x == 18) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room below the previous room (has a key and a key door below it as well as a row of swordsmen)
               else if(Player.location[0] == 2 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[5][3] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1; 
                  if(x > 0 && y == 10) roomToBeGenerated[x][y] = 13;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room below the previous room (has a key and a key door down as well as a row of snakes)
               else if(Player.location[0] == 2 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[5][4] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y < 14 && x == 18) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //room below the previous room (empty, down has a key door and has multiple key doors leading to the super bomb, left has a bunch of keys)
               else if(Player.location[0] == 2 && Player.location[1] == -1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[5][5] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (empty, left leads to the superbomb)
               else if(Player.location[0] == 2 && Player.location[1] == -2){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //next 6 rooms to the left of the previous rooms (has a row of enemies in the pattern below and each room has a key)
               //In addition, this is the next 6 rooms to the left of the branch room above the previous room
               else if (Player.location[1] == -1 || Player.location[1] == -2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = Player.location[1] == -2 && Player.location[0] == -4 ? 0:1;
                  LoadingZone.currentRoomBlock[2] = Player.location[0] == -4 ? 1: Player.location[1] == -2 ? Player.location[0] == 1 ? LoadingZone.keyDoor[5][6] ? 0:2:Player.location[0] == 0 ? LoadingZone.keyDoor[5][7] ? 0:2:Player.location[0] == -1 ? LoadingZone.keyDoor[5][8] ? 0:2:Player.location[0] == -2 ? LoadingZone.keyDoor[5][9] ? 0:2:Player.location[0] == -3 ? LoadingZone.keyDoor[5][10] ? 0:2:1:0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y < 14 && x == 10) roomToBeGenerated[x][y] = Player.location[0] == -4 ? 23 : Player.location[0] == -3 ? 22 : Player.location[0] == -2 ? 21 : Player.location[0] == -1 ? 16 : Player.location[0] == 0 ? 13:9;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the farther room in the set of six above (has a whoooooooooooole bunch of enemies and the superbomb(BOOOOOOOOOOOOOOOOOM!!!!!!!!!!!!!!!!!!!!))
               //note there is a glitch where using the superbomb in the room will allow the superbomb to infinetely respawn, but i'm too lazy to fix it :)
               else if (Player.location[0] == -4 && Player.location[1] == -3 ){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x < 14 && y > 5) roomToBeGenerated[x][y] = y <= 5 ? 23 : y <=7 ? 22 : y <= 9 ? 21 : y <= 11 ? 16 : y <= 13 ? 13:9;
                  else roomToBeGenerated[x][y]  = 0;
               }
               //going a completely different direction: room to the left of the branch room at (-1,2)
               //leads to the boss room
               //has a bunch of enemies and a key
               else if (Player.location[0] == -2 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y < 16) roomToBeGenerated[x][y] = x < 4 ? 9 : x < 6 ? 21 : x < 8 ? 23: x < 10 ? 13:0;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room, has a key door on the right and a row of mummies that are on at the bottom of the room
               //continues the path to the boss room
               else if (Player.location[0] == -2 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[5][11] ? 0:2;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 15) roomToBeGenerated[x][y] = 21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a row of snakes)
               //below is the boss room!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               else if (Player.location[0] == -3 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.numDefeatedBosses < 6 ? 0:1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 1) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room
               //BOSS ROOM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               else if (Player.location[0] == -3 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  roomToBeGenerated[16][5] = 10;
                  continue;
               }
               // Room with the third triforce piece
               else if (Player.location[0] == -2 && Player.location[1] == 0) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               } else
                  roomToBeGenerated[x][y] = 1;
               continue;
            }
            else if (Player.level == 7){
               //start room for dungeon #7
               if(Player.location[0] == 0 && Player.location[1] == 0){
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = LoadingZone.keyDoor[6][0] ? 0:2;
                  continue;
               }
               //room to the left of the start room (lead to a path with a bunch of keys)
               else if(Player.location[0] == -1 && Player.location[1] == 0){
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[10][10] = 13;
                  continue;
               }
               //room above the previous room (has a row of swordsmen and a key)
               else if(Player.location[0] == -1 && Player.location[1] == 1){
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[17][y] = 13;
                  continue;
               }
               //room to the left of the previous room (has snakes in the formation of the letter 'i' and a key)
               else if(Player.location[0] == -2 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 10 && (y == 9 || y >= 12) && y < 14) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
                  continue;
               }
               //room to the left of the previous room (has a variety of enemies in the formation of the letter 'H' and a key)
               else if(Player.location[0] == -3 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 8 || x == 12 || ((x <= 11 && x >= 9) && y == 10)) roomToBeGenerated[x][y] = x == 8 ? 9: x == 12 ? 13:21;
                  else roomToBeGenerated[x][y] = 0;
                  continue;
               }
               //room to the left of the previous room (has a row of shieldEaters and a key)
               else if(Player.location[0] == -4 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 3)roomToBeGenerated[x][y] = 16;
                  else roomToBeGenerated[x][y] = 0;
                  continue;
               }
               //room above the previous room and the first 2 rooms in the upper path on the left side of the skull (has a bunch of enemies and a key)
               else if(((Player.location[0] == -4 || Player.location[0] == -1) && Player.location[1] == 2) || (Player.location[0] == -1 && Player.location[1] == 3)){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y >= 5 && y <= 10) roomToBeGenerated[x][y] = y == 5 ? 13:y == 6 ? 16:y == 7 ? 23:y == 8 ? 22:y == 9 ? 21:9;
                  else roomToBeGenerated[x][y] = 0;
                  continue;
               }
               //room above the previous room (has a row of snakes and a key)
               else if(Player.location[0] == -4 && Player.location[1] == 3){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x == 1 && y > 1 && y < 14) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room above the previous room (has a row of mummies and a key)
               else if(Player.location[0] == -4 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y == 1) roomToBeGenerated[x][y] = 21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has skeletons in the shape of a "T" and a key)
               else if(Player.location[0] == -3 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(y == 1 || x == 10) roomToBeGenerated[x][y] = 9;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a mix of enemies in the shape of an "H" and a key)
               else if(Player.location[0] == -2 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 8 || x == 12 || ((x <= 11 && x >= 9) && y == 10)) roomToBeGenerated[x][y] = x == 8 ? 9: x == 12 ? 13:21;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the previous room (has a mix of enemies in the shape of an "E" and a key)
               else if(Player.location[0] == -1 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x == 4) roomToBeGenerated[x][y] = 16;
                  else if (y == 1 && x > 4) roomToBeGenerated[x][y] = 22;
                  else if (y == 8 && x > 4) roomToBeGenerated[x][y] = 21;
                  else if (y == 12 && x > 4) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room to the left of the start room (has a snake and a key door)
               else if(Player.location[0] == 1 && Player.location[1] == 0){
                  LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[6][1] ? 0:2;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(x == 10 && y == 10) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room above the previous room (empty)
               else if (Player.location[0] == 1 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = LoadingZone.keyDoor[6][2] ? 0:2;
                  roomToBeGenerated[x][y] = 0;
               }
               //next 2 rooms to the left of the previous room (also empty)
               else if ((Player.location[0] == 2 || Player.location[0] == 3) && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = Player.location[0] == 2 ? LoadingZone.keyDoor[6][3] ? 0 : 2 : LoadingZone.keyDoor[6][4] ? 0:2;
                  roomToBeGenerated[x][y] = 0;
               }
               //room to the right of the previous room (empty as a jar of peanut butter that my doggo got into)
               else if (Player.location[0] == 4 && Player.location[1] == 1){
                  LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[6][5] ? 0:2;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 0;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //next 2 rooms above the previous room (emptyyyyyy)
               else if (Player.location[0] == 4 && (Player.location[1] == 2 || Player.location[1] == 3)){
                  LoadingZone.currentRoomBlock[0] = Player.location[1] == 2 ? LoadingZone.keyDoor[6][6] ? 0 : 2 : LoadingZone.keyDoor[6][7] ? 0:2;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //room above the previous room (EMPTYYYYYYY)
               else if (Player.location[0] == 4 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[6][8] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //next room to the left of the previous room (has a set of snakes in the shape of the letter 'D')
               else if (Player.location[0] == 3 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[6][9] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  roomToBeGenerated[x][y] = 0;
                  if(y > 0 && y < 14){
                     if(x == 4 || x == 5) roomToBeGenerated[x][y] = 23;
                     else if((x == 6 || x == 7) && (y == 3 || y == 4 || y == 12 || y == 13)) roomToBeGenerated[x][y] = 23;
                     else if ((x == 7 || x == 8) && y >= 5 && y <= 12) roomToBeGenerated[x][y] = 23;
                  }
               }
               //room to the left of the previous room (has a mix of enemies in the shape of a the letter 'N"')
               else if (Player.location[0] == 2 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = LoadingZone.keyDoor[6][10] ? 0 : 2;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 3 || x == 12) roomToBeGenerated[x][y] = x == 3? 13:9;
                  else if (y == x && x > 3 && x < 12 && y < 14) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated [x][y] = 0;
               }
               //room to the left of the previous room (has a mix of enemies in the shape of the letter 'E')
               else if(Player.location[0] == 1 && Player.location[1] == 4){
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = LoadingZone.keyDoor[6][11] ? 0 : 2;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 0;
                  if(x == 4) roomToBeGenerated[x][y] = 16;
                  else if (y == 1 && x > 4 && x < 13) roomToBeGenerated[x][y] = 22;
                  else if (y == 8 && x > 4 && x < 13) roomToBeGenerated[x][y] = 21;
                  else if (y == 12 && x > 4 && x < 13) roomToBeGenerated[x][y] = 23;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (EMPTYYYYYYYYY)
               else if(Player.location[0] == 1 && Player.location[1] == 3){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room (blocked by rocks on the bottom, has a loading zone that leads to the boss)
               else if(Player.location[0] == 1 && Player.location[1] == 2){
                  LoadingZone.currentRoomBlock[0] = 0;
                  LoadingZone.currentRoomBlock[1] = 0;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if(y == 11) roomToBeGenerated[x][y] = 1;
                  else if (y == 10 && x == 10) roomToBeGenerated[x][y] = 6;
                  else roomToBeGenerated[x][y] = 0;
               }
               //room inside of the loading zone in the previous room 
               else if(Player.location[0] == -3 && Player.location[1] == -1){               
               LoadingZone.currentRoomBlock[0] = 1;
               LoadingZone.currentRoomBlock[1] = 1;
               LoadingZone.currentRoomBlock[2] = 1;
               LoadingZone.currentRoomBlock[3] = 0;
               if(x == 0 && y == 0) setText(Player.name,"Congradulations are in order","");
               if(x == 5 && y == 5) roomToBeGenerated[x][y] = 6;
               else roomToBeGenerated[x][y] = 0;
               }
               //the next couple of rooms are builup to the boss, I used strings to add to the effect ;)
               else if ((Player.location[0] >= -2 && Player.location[0] <= 1) && Player.location[1] == -1){
               LoadingZone.currentRoomBlock[0] = 1;
               LoadingZone.currentRoomBlock[1] = Player.location[0] == 1 ? 0:1;
               LoadingZone.currentRoomBlock[2] = 0;
               LoadingZone.currentRoomBlock[3] = Player.location[0] == 1 ? 1:0;
               if(x == 0 && y == 0) setText(Player.location[0] == -2 ? "Your long journey is over":Player.location[0] == -1 ? "The end is near":Player.location[0] == 0 ? "Now, prepare for the END!!!!!!!!!":"Once more,",Player.location[0] == 1 ? "darkness shall rise over Hyrule!!!":"","");
               else roomToBeGenerated[x][y] = 0;
               }
               //room below the previous room
               //BOSS ROOM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               else if (Player.location[0] == 1 && Player.location[1] == -2) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 3;
                  roomToBeGenerated[10][10] = 10;
                  continue;
               }
               // Room with the last triforce piece
               else if (Player.location[0] == 2 && Player.location[1] == -2) {
                  roomToBeGenerated[x][y] = 0;
                  LoadingZone.currentRoomBlock[0] = 1;
                  LoadingZone.currentRoomBlock[1] = 1;
                  LoadingZone.currentRoomBlock[2] = 1;
                  LoadingZone.currentRoomBlock[3] = 1;
                  if (x >= 7 && x <= 11 && y == 4)
                     roomToBeGenerated[x][y] = 1;
                  if ((x == 7 || x == 11) && y >= 4 && y <= 8)
                     roomToBeGenerated[x][y] = 1;
                  if (x == 9 && y == 6)
                     roomToBeGenerated[x][y] = 11;
               } else
                  roomToBeGenerated[x][y] = 1;
            }
            //dungeon #7
            else if(Player.level == 7){
                //entrance room
                if(Player.location[0] == 0 && Player.location[1] == 0){
                    roomToBeGenerated[x][y] = 0;
                    LoadingZone.currentRoomBlock[0] = LoadingZone.keyDoor[7][0] ? 0 : 2;
                    LoadingZone.currentRoomBlock[1] = 0;
                    LoadingZone.currentRoomBlock[2] = 0;
                    LoadingZone.currentRoomBlock[3] = LoadingZone.keyDoor[7][1] ? 0 : 2; 
                }
                //room to the left of the previous room (has a staircase that leads to 2 keys)
                else if(Player.location[0] == -1 && Player.location[1] == 0){
                    roomToBeGenerated[x][y] = 0;
                    LoadingZone.currentRoomBlock[0] = 1;
                    LoadingZone.currentRoomBlock[1] = 1;
                    LoadingZone.currentRoomBlock[2] = 1;
                    LoadingZone.currentRoomBlock[3] = 0; 
                    roomToBeGenerated[x][y] = 6;
                }
            }
         }
      }
   }

   public int makeRow(String rockFormation, String enemyFormation, String waterFormation, int x) {

      if (waterFormation.equals("small river")) {
         if (x == 4 || x == 5)
            return 4;
         else
            return makeRow(rockFormation, enemyFormation, "", x);
      } else if (waterFormation.equals("small bridge")) {
         if (x == 4 || x == 5)
            return 5;
         else
            return makeRow(rockFormation, enemyFormation, "", x);
      } else if (waterFormation.equals("long bridge")) {
         return 5;
      } else if (enemyFormation.equals("2RO")) {
         if (x == 6 || x == 10)
            return 7;
         else
            return makeRow(rockFormation, "", "", x);
      } else if (enemyFormation.equals("!!RO")) {
         if (x >= 3 && x <= 10)
            return 7;
         else if (rockFormation.equals(""))
            return 0;
         else
            return makeRow(rockFormation, "", "", x);
      } else if (enemyFormation.equals("!!BO")) {
         if (x >= 3 && x <= 10)
            return 8;
         else
            return makeRow(rockFormation, "", "", x);
      } else if (enemyFormation.equals("2R&BO")) {
         if (x == 6)
            return 7;
         else if (x == 10)
            return 8;
         else
            return makeRow(rockFormation, "", "", x);
      } else if (enemyFormation.equals("2BO")) {
         if (x == 6 || x == 10)
            return 8;
         else
            return makeRow(rockFormation, "", "", x);
      } else if (rockFormation.equals("openingMiddle")) {
         if (x >= 0 && x <= 6 || x >= 12 && x <= 19)
            return 1;
         else
            return 0;
      } else if (rockFormation.equals("rocks on left")) {
         if (x >= 0 && x <= 6)
            return 1;
         else
            return 0;
      } else if (rockFormation.equals("rocks on right")) {
         if (x >= 12 && x <= 19)
            return 1;
         else
            return 0;
      } else if (rockFormation.equals("loadingZone in room")) {
         if (x == 3 || x == 2)
            return 6;
         else if (x >= 0 && x <= 6 || x >= 12 && x <= 19)
            return 1;
         else
            return 0;
      } else
         return 0;
   }
}