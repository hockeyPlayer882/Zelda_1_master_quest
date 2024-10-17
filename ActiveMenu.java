import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.*;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ActiveMenu{
   public BufferedImage fullHeart;
   public BufferedImage halfHeart;
   public BufferedImage emptyHeart;
   public BufferedImage poisonedFullHeart;
   public BufferedImage poisonedHalfHeart;
   private BufferedImage rubpee;
   private BufferedImage bomb; 
   private BufferedImage key;
   private BufferedImage candle;
   private BufferedImage bow;

   public static int iterationNum = 0;
   public boolean isResuming = false;
   public static int numTriforcePieces;
   public int selectedItem[] = new int[2];
   //creates an array of booleans that check if each location in the items is full or  not
   public boolean itemExists[][] = new boolean[4][4];
   //only empty is needed... there is only one class and it will always be the same

   public ActiveMenu() {
      try {     
         fullHeart = ImageIO.read(new File("./Image files/fullHeart.png"));
         halfHeart = ImageIO.read(new File("./Image files/halfHeart.png"));
         poisonedFullHeart = ImageIO.read(new File("./Image files/poisonedFullHeart.png"));
         poisonedHalfHeart = ImageIO.read(new File("./Image files/poisonedHalfHeart.png"));
         emptyHeart = ImageIO.read(new File("./Image files/emptyHeart.png"));
         rubpee = ImageIO.read(new File("./Image files/Rubpee.png"));
         bomb = ImageIO.read(new File("./Image files/bomb.png"));
         candle = ImageIO.read(new File(".//Image files/candle.png"));
         key = ImageIO.read(new File("./Image files/key.png"));
         bow = ImageIO.read(new File("./Image files/bow.png"));

         if (Shop.blueMedicine == null) 
            Shop.blueMedicine = ImageIO.read(new File("./Image files/blueMedicine.png"));
         if (Shop.redMedicine == null) 
            Shop.redMedicine = ImageIO.read(new File("./Image files/redMedicine.png"));
      }
      catch (IOException ex) {
         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
   }

   public void drawItemBoxes(Player player,Sword sword,Graphics g, Driver driver){
      //creates temporary variables for the left of each image to be changed later
      int rubpeeLeft = 285;
      int bombLeft = 285;
      int keyLeft = 290;
      //draws blue boxes around the items to be activated with the Q and E keys
      g.setColor(Color.WHITE);
      Font font1 = new Font("Verdana", Font.PLAIN, 25);
      g.setFont(font1);
      g.drawString("E",405,30+iterationNum);
      g.drawString("Q",345,25+iterationNum);
      g.setColor(Color.BLUE);
      g.drawRect(390,30+iterationNum,40,80);
      g.drawRect(330,30+iterationNum,40,80);
      Font font2 = new Font("Verdana",Font.PLAIN,15);
      g.setFont(font2);
      g.setColor(Color.WHITE);

      if (Sword.type.equals("wooden")) 
         g.drawImage(Driver.woodenSwordW,390,30+iterationNum,40,80,driver);
      else if (Sword.type.equals("metal")) 
         g.drawImage(Driver.metalSwordW,390,30+iterationNum,40,80,driver);

      if (player.rubpees/10 > 0) rubpeeLeft -= 10;
      if (player.bombs/10 > 0) bombLeft -= 10;
      if (player.keys/10 > 0) keyLeft -= 10;

      g.drawImage(bomb,bombLeft,55+iterationNum,20,25,driver);
      g.drawString("X" + player.bombs,bombLeft+20,80+iterationNum);
      g.drawImage(rubpee,rubpeeLeft,80+iterationNum,25,25,driver);
      g.drawString("X" + player.rubpees,rubpeeLeft+20,100+iterationNum);
      g.drawImage(key,keyLeft,30+iterationNum,20,25,driver);
      g.drawString("X" + player.keys,15+keyLeft,55+iterationNum);
   }

   public void drawPauseMenu(Graphics g,Player player,Driver driver){
      g.setColor(Color.BLACK);
      g.fillRect(0,iterationNum-800,800,800);
      g.setColor(Color.GRAY);
      g.fillRect(200,400+iterationNum-800,400,400);
      
      g.drawPolygon(new int[] {200,400,600}, new int[] {400+iterationNum-800,300+iterationNum-800,400+iterationNum-800}, 3);
      g.setColor(Color.YELLOW);
      //draws the triforces according to how many the player has
      if (numTriforcePieces >= 1)
         g.fillPolygon(
            new int[]{200,280,285}, 
            new int[] {400+iterationNum-800,360+iterationNum-800,400+iterationNum-800},
            3
         );

      if (numTriforcePieces >= 2)
         g.fillPolygon(
            new int[]{360,400,440},
            new int[]{320+iterationNum-800,300+iterationNum-800,320+iterationNum-800},
            3
         );

      if (numTriforcePieces >= 3)
         g.fillPolygon(
            new int[]{515,520,600},
            new int[]{400+iterationNum-800,360+iterationNum-800,400+iterationNum-800},
            3
         );

      if (numTriforcePieces >= 4)
         g.fillPolygon(
            new int[]{280,360,400},
            new int[]{360+iterationNum-800,320+iterationNum-800,360+iterationNum-800},
            3
         );
      if (numTriforcePieces >= 5)
         g.fillPolygon(
            new int[]{400,440,520},
            new int[]{360+iterationNum-800,320+iterationNum-800,360+iterationNum-800},
            3
         );
      if (numTriforcePieces >= 6)
         g.fillPolygon(
            new int[]{285,400,515},
            new int[]{400+iterationNum-800,360+iterationNum-800,400+iterationNum-800},
            3
         );
      if(numTriforcePieces == 7)
         g.fillPolygon(
            new int[] {200,400,600},
            new int[] {400+iterationNum-800,300+iterationNum-800,400+iterationNum-800},
            3);

      g.setColor(Color.WHITE);
      Font font = new Font("Verdana", Font.PLAIN, 25);
      g.setFont(font);
      g.drawString("TRIFORCE",335,300+iterationNum-800);
      g.drawString("Press Q for this",100,150+iterationNum-800);
      g.drawString("item",150,200+iterationNum-800);
      g.setColor(Color.BLUE);
      g.drawRect(480,75+iterationNum-800,190,160);
      g.drawRect(350,100+iterationNum-800,40,80);

      if(Player.hasBow)
         g.drawImage(bow,480,35+iterationNum-800,Entity.unitSize/2,Entity.unitSize,driver);

      if(Player.hasArrows) 
         g.drawImage(Arrow.arrowW,520,35+iterationNum-800,Entity.unitSize/2,Entity.unitSize,driver);

      if(Player.hasRaft)
         g.drawImage(Room.raft,560,35+iterationNum-800,Entity.unitSize/2,Entity.unitSize,driver);
   }

   public void handleActiveItem(Graphics g, Driver driver, Player player){
      int boxcx = 480;
      int boxcy = 75+iterationNum-800;
      boxcx += selectedItem[0]*50;

      if(selectedItem[1] == 1) 
         boxcy += 80;

      g.drawRect(boxcx,boxcy,40,80);

      if (player.activeItem.equals("boomerang") && Player.hasBoomerang) {
         g.drawImage(Boomerang.boomerang,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Boomerang.boomerang,331,31+iterationNum,38,78,driver);
      }

      else if(player.activeItem.equals("bomb") && player.bombs > 0) {
         g.drawImage(bomb,351,101+iterationNum-800,38,78,driver);
         g.drawImage(bomb,331,31+iterationNum,38,78,driver);
      }

      else if(player.activeItem.equals("candle") && Player.hasCandle){
         g.drawImage(candle,351,101+iterationNum-800,38,78,driver);
         g.drawImage(candle,331,31+iterationNum,38,78,driver);
      }

      else if (player.activeItem.equals("blue medicine") && Player.medicine.equals("blue")){
         g.drawImage(Shop.blueMedicine,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Shop.blueMedicine,331,31+iterationNum,38,78,driver);
      }

      else if (player.activeItem.equals("red medicine") && Player.medicine.equals("red")){
         g.drawImage(Shop.redMedicine,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Shop.redMedicine,331,31+iterationNum,38,78,driver);
      }

      else if (player.activeItem.equals("arrows") && Player.hasArrows && Player.hasBow){
         g.drawImage(Arrow.arrowW,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Arrow.arrowW,331,31+iterationNum,38,78,driver);
      }

      else if (player.activeItem.equals("wand") && Player.hasWand){
         g.drawImage(Wand.wandW,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Wand.wandW,331,31+iterationNum,38,78,driver);
      }
      else if (player.activeItem.equals("cane") && Player.hasCane){
         g.drawImage(Cane.cane,351,101+iterationNum-800,38,78,driver);
         g.drawImage(Cane.cane,331,31+iterationNum,38,78,driver);
      }
      else if (player.activeItem.equals("superBomb") && Player.hasSuperBomb){
         g.drawImage(SuperBomb.superBomb,351,101+iterationNum-800,38,78,driver);
         g.drawImage(SuperBomb.superBomb,331,31+iterationNum,38,78,driver);
      }

      if (player.bombs > 0){
         itemExists[1][0] = true;
         g.drawImage(bomb,481+50,76+iterationNum-800,38,78,driver);
      }
      else {
         itemExists[0][1] = false;
         player.activeItem = "NONE";
      }
      if (Player.hasSuperBomb){
         itemExists[3][0] = true;
         g.drawImage(SuperBomb.superBomb,481+150,76+iterationNum-800,38,78,driver);
      }
      else {
         itemExists[3][0] = false;
         player.activeItem = "NONE";
      }
      if(Player.medicine.equals("red")){
         itemExists[3][1] = true;
         g.drawImage(Shop.redMedicine,481+50,156+iterationNum-800,38,78,driver);
      }

      if(Player.medicine.equals("blue")){
         itemExists[1][1] = true;
         g.drawImage(Shop.blueMedicine,481+50,156+iterationNum-800,38,78,driver);
      }
      else{
         itemExists[1][1] = false;
         player.activeItem = "NONE";
      }
      if(Player.hasCandle){
         itemExists[1][0] = true;
         g.drawImage(candle,481,156+iterationNum-800,38,78,driver);
      }
      if(Player.hasBoomerang){
         itemExists[0][0] = true;
         g.drawImage(Boomerang.boomerang,481,76+iterationNum-800,38,78,driver);
      }
      if(Player.hasWand){
         itemExists[2][1] = true;
         g.drawImage(Wand.wandW,481+100,156+iterationNum-800,38,78,driver);
      }
      if(Player.hasCane){
         itemExists[3][1] = true;
         g.drawImage(Cane.cane,481+150,156+iterationNum-800,38,78,driver);
      }
      if(Player.hasArrows && Player.hasBow && player.rubpees > 0){
         itemExists[2][0] = true;
         Arrow.draw(driver,g);
         g.drawImage(Arrow.arrowW,481+100,76+iterationNum-800,38,78,driver);
      }
      else{
         itemExists[2][0] = false;
         player.activeItem = "NONE";
      }

      for(int x = 0;x<itemExists.length;x++){
         for(int y = 0;y<itemExists[0].length;y++){
            if(itemExists[x][y] == true){
               if(selectedItem[0] == 0 && selectedItem[1] == 0 && Player.hasBoomerang) 
                  player.activeItem = "boomerang";
               else if(selectedItem[0] == 1 && selectedItem[1] == 0 && player.bombs > 0) 
                  player.activeItem = "bomb";
               else if (selectedItem[0] == 0 && selectedItem[1] == 1 && Player.hasCandle) 
                  player.activeItem = "candle";
               else if (selectedItem[0] == 1 && selectedItem[1] == 1 && 
                       (Player.medicine.equals("blue") || Player.medicine.equals("red"))) 
                  player.activeItem = Player.medicine + " medicine";

               else if (selectedItem[0] == 2 && selectedItem[1] == 0 && Player.hasArrows 
                        && Player.hasBow && player.rubpees > 0) 
                  player.activeItem = "arrows";

               else if(selectedItem[0] == 2 && selectedItem[1] == 1 && Player.hasWand) 
                  player.activeItem = "wand";
               else if(selectedItem[0] == 3 && selectedItem[1] == 1 && Player.hasCane)
                  player.activeItem = "cane";
               else if(selectedItem[0] == 3 && selectedItem[1] == 0 && Player.hasSuperBomb)
                  player.activeItem = "superBomb";
                  
               else 
                  player.activeItem = "NONE";
            }
         }
      }
   }

   public void pauseGame(Player player, Room room){
      if(!isResuming){
         if(iterationNum <800){
            for (int x = 0; x < Room.currentRoom.size(); x++) {
               for (int y= 0; y < Room.currentRoom.get(x).size(); y++) {
                  Entity ent = (Entity) Room.currentRoom.get(x).get(y);
                  ent.cy += 5;
               }
            }

            player.cy += 5;
            iterationNum += 5;
         }
      }
   }

   public void resumeGame(Player player, Room room){
      if (iterationNum >0){
         for (int x = 0; x < Room.currentRoom.size(); x++) {
            for (int y= 0; y < Room.currentRoom.get(x).size(); y++) {
               Entity ent = (Entity) Room.currentRoom.get(x).get(y);
               ent.cy -= 5;
            }
         }

         player.cy -= 5;
         iterationNum -= 5;
      }
      else {
         isResuming = false;
         Player.isPaused = false;
      }
   }

   public void drawHearts(Player player, Graphics g,Driver driver){
      if(Snake.poisonTimer > 0)Snake.poisonTimer--;
      if(Snake.poisonTimer%80 == 1) player.hp--;
      for(int i = 0;i<(player.Mhp/2);i++){ 
         if (i<player.hp/2)
            g.drawImage(Snake.poisonTimer > 0 ? poisonedFullHeart:fullHeart,i*30+500-(i >= 9 ? 270:0),(i < 9 ? 90:60)+iterationNum,30,30,driver);
         else if (player.hp%2 == 1 && player.hp > i*2) 
            g.drawImage(Snake.poisonTimer > 0 ? poisonedHalfHeart:halfHeart,i*30+500-(i >= 9 ? 270:0),(i < 9 ? 90:60)+iterationNum,30,30,driver);
         else 
            g.drawImage(emptyHeart,i*30+500-(i >= 9 ? 270:0),(i < 9 ? 90:60)+iterationNum,30,30,driver);
      }
   }
}