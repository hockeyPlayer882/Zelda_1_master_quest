import util.Version;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.io.*;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;

public class Driver extends JPanel implements KeyListener, ActionListener {
   public static BufferedImage woodenSwordW;
   public static BufferedImage woodenSwordS;
   public static BufferedImage woodenSwordA;
   public static BufferedImage woodenSwordD;
   public static BufferedImage metalSwordW;
   public static BufferedImage metalSwordS;
   public static BufferedImage metalSwordA;
   public static BufferedImage metalSwordD;
   static Version version = new Version(1, 1,11, 0);
   // arrayList of rocks
   public static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
   public static ArrayList<Projectile> projs = new ArrayList<Projectile>();
   public static ArrayList<Bomb> bombs = new ArrayList<Bomb>();
   public static ArrayList<Explosion> explosions = new ArrayList<Explosion>();
   public static ArrayList<Fire> fires = new ArrayList<Fire>();
   Player player = new Player();
   public static String name;
   Sword sword = new Sword();
   static Menu menu;
   // Varius item refrences to be spawned when an enemy is defeated
   Item rubpee = new Item(999, 999, 1, "rubpee");
   Item rubpee5 = new Item(999, 999, 5, "rubpee5");
   Item bomb = new Item(999, 999, 4, "bomb");
   Item key = new Item(999, 999, 1, "key");
   Item heart = new Item(999, 999, 2, "heart");
   Item heartContainer = new Item(999, 999, 2, "heartContainer");
   int useableTimer = 0;
   public static ArrayList<Item> items = new ArrayList<>();
   Room room;
   // class used to store HUD for the player shown while the game is active
   ActiveMenu activeMenu = new ActiveMenu();
   public static Clip overworldLoop;
   public static Clip dungeonIntro;
   public static Clip dungeonLoop;
   public static Clip deathLoop;
   public static Clip itemCollected;
   public static Clip heartCollected;
   public static Clip openThing;
   public static Clip bossLoop;
   //public GlobalModManager modManager;

   // Fullscreen
   private static JFrame frame;
   private boolean fullscreen = false;
   public static Dimension currentResolution = new Dimension(790, 770);
   public static Point transformPoint = new Point(0, 0);
   public static void main(String[] args) throws Exception {
      System.out.println("Zelda 1 master quest " + version);

      // Added legal notice:
      System.out.println("\033[34mNOTICE: This is a fan game! The author of this game is not the"
                         + " original intellectual property owner! The works in this game"
                         + " are solely used in cases permitted by fair use. This game is"
                         + " not sponsored by, nor affiliated with, Nintendo Co. Ltd.\033[0m");

      // instantiates frame
      frame = new JFrame();
      // RSC Games: refactored the name prompt. Original code will be kept for historical
      // reasons.
      name = null;

      while (true) {
         do {
            name = JOptionPane.showInputDialog("Enter player name.");
         }
         while (name == null);

         int response = JOptionPane.showConfirmDialog(null, "Your name is " + name + ", right?");
         if (response == 0) break;
      }

      //creates the game menu ***REPLACE PARAMETER WITH data taken from the save file whe support is added
      String[] pNames = {name,"empty","empty"};
      menu = new Menu(pNames);
      /*
      // vvvvvv Original code below vvvvvv
      // instantiates frame
      JFrame frame = new JFrame();
      // gets the user name from a prompt in the JOptionPane
      name = JOptionPane.showInputDialog("Enter Name");
      // if the player doesn't enter a name, avoid a crash by setting the name to an
      // empty string
      if (name == null)
         name = "";
      int response = JOptionPane.showConfirmDialog(null, "Your name is " + name + ", right?");
      if (response != 0) {
         name = JOptionPane.showInputDialog("Enter Name (you don't get another chance :X)");
      }
       */
      Wand.init();
      SuperBomb.init();
      Cane.setImages();

      DebugInterface.debugInit();
      Driver driver = new Driver(frame);
      driver.setFocusable(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(true);
      frame.add(driver);
      frame.pack();
      frame.setSize(790, 770);
      frame.setMinimumSize(new Dimension(790, 770));
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      frame.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent event) {
            currentResolution = new Dimension(frame.getWidth(), frame.getHeight()); 
         }
      });

      // TODO: Michael -- You need to fix these things:
      // TODO: -- DungeonthemeIntro (still not working?)
      // TODO: -- Add Black Mist to the lost woods
      // TODO: -- Add The Goddess Appears to any secret caves.
      // TODO: -- Add Menu to the game start menu.
      // TODO: -- Spinning in the menu is a bit janky and may require a rewrite
      File overworldthemeLoop = new File("new_ost/07-Hyrule Field.wav");
      File dungeonthemeLoop = new File("new_ost/12-Lost Ancient Ruins.wav");
      File dungeonthemeIntro = new File("new_ost/12-Lost Ancient Ruins-Intro.wav");
      File deathThemeLoop = new File("./Sound files/you_died_loop.wav");
      File itemCollectedTheme = new File("./Sound files/got_item_z1.wav");
      File openThingTheme = new File("./Sound files/secret_z1.wav");
      File bossThemeLoop = new File("./new_ost/Anger of the Guardians.wav");
      File heartCollectedTheme = new File("./Sound files/got_heart_z1.wav");
      dungeonIntro = loadClip(dungeonthemeIntro);
      overworldLoop = loadClip(overworldthemeLoop);
      dungeonLoop = loadClip(dungeonthemeLoop);
      deathLoop = loadClip(deathThemeLoop);
      bossLoop = loadClip(bossThemeLoop);
      itemCollected = loadClip(itemCollectedTheme);
      heartCollected = loadClip(heartCollectedTheme);
      openThing = loadClip(openThingTheme);
   }

   static Clip loadClip(File path) throws Exception {
      AudioInputStream stream = AudioSystem.getAudioInputStream(path);
      AudioFormat format = stream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      Clip clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      return clip;
   }

   public Driver(JFrame frame) throws Exception {
      /*// TELEPORTS PLAYER TO SPECIFIC LOCATIONS! NOT FOR RELEASE! TESTING ONLY!!
      Player.location[0] = 8;
      Player.location[1] = 5;
      */
      Player.name = name;
      // cheat codes for names... becuase why not?
      if (Player.name.equals("I am rich!"))
         player.rubpees += 999;
      else if (Player.name.equals("I like explosions!"))
         player.bombs += 4;
      else if (Player.name.equals("I am a pyromaniac!"))
         Player.hasCandle = true;
      else if (Player.name.equals("GIVE ME THE BOW,NOW!")) {
         Player.hasBow = true;
         Player.hasArrows = true;
      }
      else if (Player.name.equals("INVINCIBLE!"))
         Player.hasCane = true; 
      else if (Player.name.equals("SUPER OVERPOWERED!")) {
         Player.hasBow = true;
         Player.hasCane = true;
         Player.hasArrows = true;
         Player.hasSuperBomb = true;
         player.rubpees = 999;
         Sword.type = "metal";
         Sword.damage = 2;
         player.shieldStrength = 2;
         player.Mhp = 100;
         player.hp = 100;
         Player.medicine = "red";
         LoadingZone.numDefeatedBosses = 7;
         ActiveMenu.numTriforcePieces = 7;
         Player.hasCandle = true;
         Player.hasBoomerang = true;
         Player.hasRaft = true;
         Player.hasWand = true;
         player.bombs = 999;
         player.keys = 999;
      } 
      else if (Player.name.equals("BOOMERANG! YAY!"))
         Player.hasBoomerang = true;
      else if (Player.name.equals("I am sick!"))
         Player.medicine = "red";
      else if (Player.name.equals("METAL!!")) {
         player.shieldStrength = 2;
         Sword.type = "metal";
         Sword.damage = 2;
      } 
      else if (Player.name.equals("KEEEYS!"))
         player.keys += 999;
      else if (Player.name.equals("SUPERSTAR!")) {
         player.hp = 100;
         player.Mhp = 100;
      }
      try {
         woodenSwordW = ImageIO.read(new File("./Image files/woodenSwordW.png"));
         woodenSwordS = ImageIO.read(new File("./Image files/woodenSwordS.png"));
         woodenSwordA = ImageIO.read(new File("./Image files/woodenSwordA.png"));
         woodenSwordD = ImageIO.read(new File("./Image files/woodenSwordD.png"));

         metalSwordW = ImageIO.read(new File("./Image files/metalSwordW.png"));
         metalSwordS = ImageIO.read(new File("./Image files/metalSwordS.png"));
         metalSwordA = ImageIO.read(new File("./Image files/metalSwordA.png"));
         metalSwordD = ImageIO.read(new File("./Image files/metalSwordD.png"));
      } 
      catch (IOException ex) {
         // handle exception... or not. I mean, it's not like anyone is ever going to get
         // this exception, let alone actually know what it was talking about or read any
         // of the amazing comments braught to you buy a random teenager who gets easily
         // bored in his classes and procrastonates and gives doesn't know how to spell
         // "procrastonate", unless he did whereas he would now be very confused
         System.err.println("Something's about to be... peacefully beheaded.");
      }

      //GameContext context = new GameContext(frame);
      //this.modManager = new GlobalModManager(context); // Runs mod loader (ZModLoader)

      //this.modManager.initMods();
      // check for key inputs
      addKeyListener(this);
      Timer timer = new Timer(10, this);
      timer.start();
      
      // creates the room(should be world, but im dumb) that the player is in
      this.room = new Room("starting area");
      room.spawnRoom(player);
      room.fillRoomArray(player);
      //toggleFullScreen();
   }

   // timer function to be called every frame
   public void actionPerformed(ActionEvent e) {
      // repaints the canvas

      repaint();

      // handles the music
         if (Player.level != 0) {
            if (Player.level > 0) {
               if (player.dir != 't') {
                     if ((Player.level == 1 && Player.location[0] == -4 && Player.location[1] == 3
                           || Player.level == 2 && Player.location[0] == -3 && Player.location[1] == 0
                           || Player.location[0] == -3 && Player.location[1] == -1 && Player.level == 3
                           || Player.level == 4 && Player.location[0] == -3 && Player.location[1] == 0
                           || Player.level == 5 && Player.location[0] == -3 && Player.location[1] == -1)) {
                           bossLoop.loop(Clip.LOOP_CONTINUOUSLY);
                           dungeonLoop.stop();
                           dungeonIntro.setFramePosition(0);
                           dungeonLoop.setFramePosition(0);
                           dungeonIntro.setFramePosition(0);
                           dungeonIntro.stop();
                     } else {
                        bossLoop.stop();
                        bossLoop.setFramePosition(0);
                        overworldLoop.stop();
                        overworldLoop.setFramePosition(0);
                        if(dungeonIntro.getFramePosition() == dungeonIntro.getFrameLength()){
                           dungeonLoop.loop(Clip.LOOP_CONTINUOUSLY);
                           dungeonIntro.stop();
                        }
                        else dungeonIntro.start(); 
                  
                     }
                  }
               } else if(bossLoop != null){
                  bossLoop.stop();
                  bossLoop.setFramePosition(0);
                  dungeonLoop.stop();
                  dungeonLoop.setFramePosition(0);
                  overworldLoop.stop();
                  overworldLoop.setFramePosition(0);
                  dungeonIntro.setFramePosition(0);
                  dungeonIntro.stop();
               }
            }
          else if ( dungeonLoop != null) {
            dungeonLoop.stop();
            dungeonLoop.setFramePosition(0);
            overworldLoop.loop(Clip.LOOP_CONTINUOUSLY);

      } 
         // debugger
         DebugInterface.debugMain();
      
   }

   public void keyTyped(KeyEvent e) {}

   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_F11)
         toggleFullScreen();

      // sets the position to null when a key is released, stopping the player
      if (player.dir != 't' && player.stun == 0) {
         if (player.dir != 'n' && player.dir != 'e')
            player.stDir = player.dir;
         if (player.dir != 'e')
            player.dir = 'n';
      }
      Cane.deactivate(player);
   }

   // NOTE: Part of the RSC Games fullscreen patches.
   public void toggleFullScreen() {
      fullscreen = !fullscreen;

      if (fullscreen) {
         frame.setVisible(false);
         frame.dispose();
         frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         frame.setUndecorated(true);
         frame.setVisible(true);
      }
      else {
         //frame.dispose();
         frame.setVisible(false);
         frame.dispose();
         frame.setExtendedState(JFrame.NORMAL);
         frame.setSize(new Dimension(790, 770));
         frame.setUndecorated(false);
         frame.setVisible(true);
      }
   }

   // NOTE: Part of the RSC Games fullscreen patches.
   public void updateOffset(Graphics2D g) {
      //g.setTransform(AffineTransform.getTranslateInstance(0, 0));
      Point nres = new Point((int)currentResolution.getWidth(), (int)currentResolution.getHeight());
      Point gres = new Point(790, 770);
      Point p = new Point((nres.x / 2 - gres.x / 2), (nres.y / 2 - gres.y / 2));
      transformPoint = p;

      g.translate(p.getX(), p.getY());
   }

   // NOTE: Part of the RSC Games fullscreen patch.
   private void drawBorders(Graphics2D g) {
      g.setTransform(AffineTransform.getTranslateInstance(0, 0));

      Point topLeft = transformPoint;
      Point bottomRight = new Point((int)(topLeft.getX() + 790), (int)(topLeft.getY() + 770));
      g.setColor(Color.black);
      g.fillRect(0, 0, (int)topLeft.getX(), (int)currentResolution.getHeight());
      g.fillRect(0, (int)bottomRight.getY(), (int)currentResolution.getWidth(), (int)currentResolution.getHeight());
      g.fillRect((int)bottomRight.getX(), 0, (int)currentResolution.getWidth(), (int)currentResolution.getHeight());
      g.fillRect(0, 0, (int)currentResolution.getWidth(), (int)topLeft.getY());

      // Draw a small line around the viewport.
      g.setColor(Color.white);
      g.drawRect((int)topLeft.getX() - 1, (int)topLeft.getY() - 1, (int)(bottomRight.getX() - topLeft.getX()), (int)(bottomRight.getY() - topLeft.getY()));
   }

   public void keyPressed(KeyEvent e) {
      if (player.dir != 't') {
         /* sets the player direction to the player input, e is attack, q is an item, and
         // f is pause
         //w->up, s-> down, a->left, d->right
         // key codes--- W->87, S->83,A->65,D->68, E->69,Q->81 H->72 K->75 R->82 F->70 ENTER->10
         P->80*/
         if(!menu.gameHasStarted){
            switch (e.getKeyCode()){
               case KeyEvent.VK_W:
                  menu.moveArrow(true);
                  break;
               case KeyEvent.VK_S:
                  menu.moveArrow(false);
                  break;
               case KeyEvent.VK_ENTER:
                  menu.selectArrow(activeMenu,player,room);
                  break;

            }
         }
         else if (Player.attackDelay <= 0 && !Player.isPaused && player.stun == 0 && Wand.delay <= 0) {
            switch (e.getKeyCode()) {
               case KeyEvent.VK_W:
                  player.dir = 'w';
                  break;
               case KeyEvent.VK_S:
                  player.dir = 's';
                  break;
               case KeyEvent.VK_A:
                  player.dir = 'a';
                  break;
               case KeyEvent.VK_D:
                  player.dir = 'd';
                  break;
               case KeyEvent.VK_E:
                  if (player.dir != 'n' && player.dir != 'e')
                     player.stDir = player.dir;
                  // stops the player from moving
                  player.dir = 'e';
                  player.attack(sword);
                  break;
               case KeyEvent.VK_ENTER:
                  if (player.dir != 'n' && player.dir != 'e')
                     player.stDir = player.dir;
                  // stops the player from moving
                  player.dir = 'e';
                  player.attack(sword);
                  break;
               case KeyEvent.VK_Q:
                  if (player.dir != 'n' && player.dir != 'e')
                     player.stDir = player.dir;
                  // stops the player from moving
                  player.dir = 'e';
                  if (player.activeItem.equals("bomb") && Bomb.numBombs < 2 && useableTimer <= 0) {
                     useableTimer = 10;
                     Bomb.numBombs += 1;
                     player.bombs -= 1;
                     if (player.stDir == 'w')
                        bombs.add(new Bomb(player.cx, player.cy - Player.unitSize));
                     else if (player.stDir == 's')
                        bombs.add(new Bomb(player.cx, player.cy + Player.unitSize));
                     else if (player.stDir == 'a')
                        bombs.add(new Bomb(player.cx - Player.unitSize, player.cy));
                     else if (player.stDir == 'd')
                        bombs.add(new Bomb(player.cx + Player.unitSize, player.cy));
                  } else if (player.activeItem.equals("candle") && Fire.numFire < 2 && useableTimer <= 0) {
                     useableTimer = 10;
                     Fire.numFire += 1;
                     if (player.stDir == 'w')
                        fires.add(new Fire(player.cx, player.cy - Player.unitSize, 'w'));
                     else if (player.stDir == 's')
                        fires.add(new Fire(player.cx, player.cy + Player.unitSize, 's'));
                     else if (player.stDir == 'a')
                        fires.add(new Fire(player.cx - Player.unitSize, player.cy, 'a'));
                     else if (player.stDir == 'd')
                        fires.add(new Fire(player.cx + Player.unitSize, player.cy, 'd'));
                  } else if (player.activeItem.equals("boomerang") && Player.hasBoomerang
                        && (Boomerang.cx > 800 || Boomerang.cx < 0 || Boomerang.cy > 800 || Boomerang.cy < 0))
                     Boomerang.spawn(player);
                  else if (player.activeItem.equals("blue medicine") && Player.medicine.equals("blue")) {
                     Player.medicine = "";
                     player.heal(player.Mhp);
                  } else if (player.activeItem.equals("wand") && Player.hasWand && Wand.delay <= 0) {
                     Wand.spawn(player);
                  } else if (player.activeItem.equals("red medicine") && Player.medicine.equals("red")) {
                     Player.medicine = "blue";
                     player.activeItem.equals("blue medicine");
                     player.heal(player.Mhp);
                  } else if (player.activeItem.equals("arrows") && Player.hasArrows
                        && (Arrow.cx > 800 || Arrow.cx < 0 || Arrow.cy > 800 || Arrow.cy < 0) && Player.hasBow
                        && player.rubpees > 0) {

                     Arrow.spawn(player);
                  } else if (player.activeItem.equals("cane") && Player.hasCane && Cane.coolDownTimer <= 0 && Cane.activeTimer <= 0)
                     Cane.activate(player);
                     else if (player.activeItem.equals("superBomb") && Player.hasSuperBomb){
                        //add new bomb to currentRoom
                        if (player.stDir == 'w')
                        Room.currentRoom.get(Room.currentRoom.size()-1).add(new SuperBomb(player.cx, player.cy - Player.unitSize));
                     else if (player.stDir == 's')
                        Room.currentRoom.get(Room.currentRoom.size()-1).add(new SuperBomb(player.cx, player.cy + Player.unitSize));
                     else if (player.stDir == 'a')
                        Room.currentRoom.get(Room.currentRoom.size()-1).add(new SuperBomb(player.cx - Player.unitSize, player.cy));
                     else if (player.stDir == 'd')
                        Room.currentRoom.get(Room.currentRoom.size()-1).add(new SuperBomb(player.cx + Player.unitSize, player.cy));
                     }
                  break;
               case KeyEvent.VK_F:
                  Player.isPaused = true;
                  break;
            }
         } else if (!activeMenu.isResuming && Player.isPaused) {
            switch (e.getKeyCode()) {
               case 68:
                  if (activeMenu.selectedItem[0] == activeMenu.itemExists[0].length-1) {
                     if (activeMenu.selectedItem[1] == 0)
                        activeMenu.selectedItem[1] = 1;
                     else
                        activeMenu.selectedItem[1] = 0;
                     activeMenu.selectedItem[0] = 0;
                  } else
                     activeMenu.selectedItem[0] += 1;
                  break;
               case 65:
                  if (activeMenu.selectedItem[0] == 0) {
                     if (activeMenu.selectedItem[1] == 1)
                        activeMenu.selectedItem[1] = 0;
                     else
                        activeMenu.selectedItem[1] = 1;
                     activeMenu.selectedItem[0] = activeMenu.itemExists[0].length-1;
                  } else
                     activeMenu.selectedItem[0] -= 1;
                  break;
               case 70:
                  activeMenu.isResuming = true;
                  break;
            }
         }
      }
   }

   public void paintComponent(Graphics g) {
      try {
         updateOffset((Graphics2D)g);
         render(g);
         drawBorders((Graphics2D)g);
      }
      catch (Exception ie) {
         System.out.println("FATAL! UNHANDLED EXCEPTION HIT BOTTOM OF CALL STACK!");
         System.out.print("Exception in thread " + Thread.currentThread().getName() + " ");
         ie.printStackTrace();
         System.exit(1);
      }
   }

   /**
    * Internal function. Allows easier exception handling on render
    * code failure.
    *
    * @param g Graphics to draw to.
    */
   private void render(Graphics g) {
      if (useableTimer > 0)
         useableTimer -= 1;

      DebugInterface.startSeg("JFrame repaint");
      super.paintComponent(g);
      DebugInterface.endSeg();

      if (player.hp > 0) {
         if (Player.isPaused)
            activeMenu.pauseGame(player, room);
         if (Player.isPaused && activeMenu.isResuming)
            activeMenu.resumeGame(player, room);

         // adds refrence items to arrayList
         DebugInterface.startSeg("Image collection");
         if (items.size() < 6) {
            items.add(rubpee);
            items.add(rubpee5);
            items.add(bomb);
            items.add(key);
            items.add(heart);
            items.add(heartContainer);
         }
         Room.collectImages(player);
         DebugInterface.endSeg();

         // Background to be redrawn
         DebugInterface.startSeg("Background repaint");
         if (Player.level == 0)
            g.setColor(new Color(252, 216, 168));
         else if (Player.level == -1)
            g.setColor(Color.BLACK);

         g.fillRect(0, 0, Player.unitSize * 200, Player.unitSize * 200);
         DebugInterface.endSeg();

         // in-game window to show active item, health, and amount of collectibles
         DebugInterface.startSeg("UI repaint");
         g.setColor(Color.BLACK);
         g.fillRect(0, 0 + ActiveMenu.iterationNum, Player.unitSize * 200, Player.unitSize * (30 / 8));
         activeMenu.drawItemBoxes(player, sword, g, this);
         activeMenu.drawHearts(player, g, this);
         activeMenu.handleActiveItem(g, this, player);

         // draws the pause menu
         activeMenu.drawPauseMenu(g, player, this);
         activeMenu.handleActiveItem(g, this, player);
         DebugInterface.endSeg();

         // draws enemies and objects
         DebugInterface.startSeg("Room draw");
         room.drawRooms(g, this, player,sword);
         DebugInterface.endSeg();

         // draws in game text shown in certain rooms in levels
         DebugInterface.startSeg("Text render");
         room.drawText(g, sword, activeMenu);
         for (int i = 0; i < Room.images.size(); i++) {
            g.drawImage((BufferedImage) Room.images.get(0), 400 - Player.unitSize / 2,
                  400 + ActiveMenu.iterationNum, 40, 60, this);
            DebugInterface.segInstant("Text");
         }
         DebugInterface.endSeg();
         // draws the bombs
         DebugInterface.startSeg("Bomb sim");
         for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            bomb.drawBomb(this, g);
            if(!Player.isPaused)
               bomb.explode();
            DebugInterface.segInstant("Bomb entity");
         }

         // Sim bomb explosion and render it.
         for (int i = 0; i < explosions.size(); i++) {
            Explosion explosion = explosions.get(i);
            explosion.drawExplosion(g, this);
            DebugInterface.segInstant("Explosion entity");
         }
         DebugInterface.endSeg();
         // draws the arrow & makes the arrow move
         Arrow.move();
         Arrow.draw(this, g);
         // draws the fire!!!!!!!!!!!!!!!!!
         DebugInterface.startSeg("Fire sim");
         for (int i = 0; i < fires.size(); i++) {
            Fire fire = (Fire) fires.get(i);
            fire.drawFire(g, this);
            DebugInterface.segInstant("Flame");
         }
         DebugInterface.endSeg();
         // draws the boomerang
         DebugInterface.startSeg("boomerang sim");
         if (Boomerang.cx < 900) {
            Boomerang.move(player);
            Boomerang.draw(g, this);
         }
         DebugInterface.endSeg();
         // draws the projectiles
         DebugInterface.startSeg("Enemy projectile sim");
         for (int i = 0; i < projs.size(); i++) {
            Projectile proj = (Projectile) projs.get(i);
            if (proj.cx < 840 && proj.cx > -40 && proj.cy > -40 && proj.cy < 840 && proj.dirs[0] != 'n') {
               if (!Player.isPaused) {
                  proj.move();
                  proj.hurtPlayer(player);
               }
               proj.drawProjectile(g);
            } else
               proj.despawn();
            DebugInterface.segInstant("Projectile");
         }
         DebugInterface.endSeg();

         //does all of the cool stuff for the player
         Cane.doThings(player);
         // draws the player
         DebugInterface.startSeg("Player sim");
         player.draw(g, this);
         // Sword object
         if (sword.dir == 'a') {
            sword.width = 60;
            sword.height = 40;
            g.drawImage((Sword.type.equals("wooden") ? woodenSwordA : metalSwordA), sword.cx - Player.unitSize / 2,
                  sword.cy - Player.unitSize / 2 + ActiveMenu.iterationNum, 60, 40, this);
         } else if (sword.dir == 'd') {
            sword.width = 40;
            sword.height = 60;
            g.drawImage((Sword.type.equals("wooden") ? woodenSwordD : metalSwordD), sword.cx - Player.unitSize / 2,
                  sword.cy - Player.unitSize / 2 + ActiveMenu.iterationNum, 60, 40, this);
         } else if (sword.dir == 'w') {
            sword.width = 40;
            sword.height = 60;
            g.drawImage((Sword.type.equals("wooden") ? woodenSwordW : metalSwordW), sword.cx - Player.unitSize / 2,
                  sword.cy - Player.unitSize / 2 + ActiveMenu.iterationNum, 40, 60, this);
         } else if (sword.dir == 's') {
            sword.width = 60;
            sword.height = 40;
            g.drawImage((Sword.type.equals("wooden") ? woodenSwordS : metalSwordS), sword.cx - Player.unitSize / 2,
                  sword.cy - Player.unitSize / 2 + ActiveMenu.iterationNum, 40, 60, this);
         }
         DebugInterface.endSeg();
         // draws the magic wand(and moves it)
         Wand.draw(g, this, player);
         // Mod render
         //modManager.render();
         // only let the objects move if the game isn't paused
         // GAME UPDATE CODE HERE!!!!!!
         DebugInterface.startSeg("Update");
         if (Player.isPaused == false) {
            player.callBaseFunctions(player,room);
            DebugInterface.segInstant("callBaseFunctions");
            player.decreaseAtkDel(sword);
            DebugInterface.segInstant("decreaseAtkDel");
            rubpee.collectItem(player);
            DebugInterface.segInstant("collectItem(rubpee)");
            rubpee5.collectItem(player);
            DebugInterface.segInstant("collectItem(rubpee5)");
            heart.collectItem(player);
            DebugInterface.segInstant("collectItem(heart)");
            key.collectItem(player);
            DebugInterface.segInstant("collectItem(key)");
            heartContainer.collectItem(player);
            DebugInterface.segInstant("collectItem(heartContainer)");
            //modManager.tick();
            DebugInterface.segInstant("modtick()");
         }
         else{
            menu.draw(g,player,this);
         }
         // draw items
         rubpee.drawItem(g, this);
         DebugInterface.segInstant("rubpee.draw");
         rubpee5.drawItem(g, this);
         DebugInterface.segInstant("rupbee5.draw");
         heart.drawItem(g, this);
         DebugInterface.segInstant("heart.draw");
         key.drawItem(g, this);
         DebugInterface.segInstant("key.draw");
         heartContainer.drawItem(g, this);
         DebugInterface.endSeg();
         if(player.activeItem.equals("cane"))
            Cane.drawCoolDownBox(g,this);
         if(Player.name.equals("SUPERSTAR!") || Player.name.equals("SUPER OVERPOWERED!"))
            player.heal(1);
         /*
          * //Random Stuff not important to the legend of Zelda, just fun coding thingies
          * int R = (int)(254*Math.random()+1);
          * int G = (int)(254*Math.random()+1);
          * int B = (int)(254*Math.random()+1);
          * int x = (int)(800*Math.random()+1);
          * int y = (int)(800*Math.random()+1);
          * //decides the shape that will be created--- 1 is a line, 2 is a circle, and 3
          * is a rectangle
          * int shapeType = (int)(2*Math.random()+1);
          * Color color = new Color(R,G,B);
          * g.setColor(color);
          * if(shapeType == 1){
          * g.drawLine(400,400,x,y);
          * }
          * else if (shapeType ==2){
          * g.drawOval(400-(x/2),400-(y/2),x,y);
          * }
          * else{
          * g.drawRect(400-(x/2),400-(y/2),x,y);
          * }
          */
         } else {
         deathLoop.loop(Clip.LOOP_CONTINUOUSLY);
         g.setColor(Color.BLACK);
         g.fillRect(0, 0, 790, 790);
         g.setColor(Color.RED);
         Font font = new Font("Verdana", Font.PLAIN, 80);
         g.setFont(font);
         g.drawString("YOU DIED!", 120, 400);
         Player.level = -9999;
      }
   }
}