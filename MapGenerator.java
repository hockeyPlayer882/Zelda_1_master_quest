import java.util.Random;

public abstract class MapGenerator {
   private static Random rand = new Random();
   public static Node[][] map = new Node[10][10];
   // the current row and column(set to the location of the starting room
   private static int row = 9;
   private static int col = 4;
   public static final int MenemiesSpawned = 6;
   public static int numEnemiesSpawned = 0;
   // keeps track of the location of the item room(contains the master sword in a
   // tree that must be lit on fire)
   public static int[] itemRoom = { 0, 0 };

   // static class that stores up to three directions for each room (each node is
   // then translated into in game rooms)
   static class Node {
      public static int numVisited = 0;
      public boolean visited = false;
      // inits an array with 2 empty directions to be filled
      public char[] dirs = { ' ', ' ', ' ' };

      public Node(char dir1, char dir2, char dir3) {
         dirs[0] = dir1;
         dirs[1] = dir2;
         dirs[2] = dir3;
      }

      public void visit() {
         this.visited = true;
         numVisited += 1;
      }

      // translates dirs into the numbers used for the room
      public int translate(int x, int y) {
         // checks the entrance and exit rooms
         if (Player.location[0] + 4 == 9 && Player.location[1] - 6 == 4 && (x >= 12 && x <= 19) && (y > 3 && y < 11))
            return 0;
         else if (Player.location[0] + 4 == 0 && Player.location[1] - 6 == 4 && (x >= 0 && x <= 6) && (y > 3 && y < 11))
            return 0;
         // loop through the dirs
         for (int i = 0; i < dirs.length; i++) {
            if (dirs[i] == 'w' && (y >= 0 && y <= 3) && (x >= 6 && x <= 12))
               return 0;
            else if ((dirs[i] == 's') && (x >= 6 && x <= 12) && (y >= 11 && y <= 18))
               return 0;
            else if ((dirs[i] == 'd') && (x >= 12 && x <= 19) && (y > 3 && y < 11))
               return 0;
            else if ((dirs[i] == 'a') && (x >= 0 && x <= 6) && (y > 3 && y < 11))
               return 0;
         }

         // add the middle section(always open)
         if (x >= 6 && x <= 12 && y > 3 && y < 11) {
            // checks for the item room and places 1 singular tree in the middle of the map
            if (itemRoom[0] == Player.location[0] + 4 && itemRoom[1] == Player.location[1] - 6 && x == 10 && y == 9) {
               return 15;
            }
            // 41% chance of spawning an enemy at each space in the middle and keep only 6
            // enemies spawned in each room maximum
            else if (rand.nextInt(100) > 59 && numEnemiesSpawned > 0) {
               numEnemiesSpawned -= 1;
               return generateRandomEnemy();
            } else
               return 0;
         }
         return 14;
      }

      private static int generateRandomEnemy() {
         // generated a random number between 1 and 4(inclusive) (higher number needed
         // for when more enemies are coded
         int num = rand.nextInt(4) + 1;
         return (num == 1 ? 7 : num == 2 ? 8 : num == 3 ? 9 : 13);
      }

      @Override
      public String toString() {
         return dirs[0] + "," + dirs[1] + "," + dirs[2];
      }

      // connects 2 nodes based on the direction parameter(the direction is the
      // direction the new path will be formed)
      public void connect(Node other, char direction) {
         this.visit();
         map[row][col] = this;
         this.dirs[(this.dirs[0] == ' ' ? 0 : this.dirs[1] == ' ' ? 1 : 2)] = direction;
         other.dirs[(other.dirs[0] == ' ' ? 0 : this.dirs[1] == ' ' ? 1 : 2)] = (direction == 'w' ? 's'
               : direction == 's' ? 'w' : direction == 'a' ? 'd' : 'a');
      }
   }

   // sets the variables to the intial state necesarry for mapgen to start
   private static void init() {
      map = new Node[10][10];
      for (int x = 0; x < map.length; x++) {
         for (int y = 0; y < map.length; y++) {
            // fills map with empty nodes
            map[x][y] = new Node(' ', ' ', ' ');
         }
      }
      // resets the number of visited nodes
      Node.numVisited = 0;
      // starting point is marked as visited
      map[9][4].visit();
      // starts map generation at point (9,4), AKA the start of the lost woods
      row = 9;
      col = 4;
   }

   // helper function: generates a random direciton
   private static char generateRandomDir() {
      int number = rand.nextInt(4) + 1;
      // keeps number in bounds of the graph
      if (number == 1 && (col == 9 || col == 10))
         number += 1;
      if (number == 2 && col == 0)
         number += 1;
      if (number == 3 && row == 0)
         number += 1;
      if (number == 4 && (row == 9 || row == 10))
         number = 1;
      return (number == 1 ? 'w' : number == 2 ? 's' : number == 3 ? 'a' : 'd');
   }

   // builds the map
   public static void generate() {
      // initializes the map
      init();
      while (Node.numVisited != 100) {
         char currentDir = generateRandomDir();
         // creates the other node to be connected(null for now)
         Node other = null;
         // makes other not null
         // checks if all of the directions fail or not
         boolean[] fail = { false, false, false, false };
         while (true) {
            if (currentDir == 'w') {
               if (col != 9) {
                  if (!map[row][col + 1].visited) {
                     other = map[row][col + 1];
                     break;
                  } else {
                     fail[0] = true;
                     currentDir = 's';
                  }
               } else {
                  currentDir = 's';
                  fail[0] = true;
               }
            }
            if (currentDir == 's') {
               if (col != 0) {
                  if (!map[row][col - 1].visited) {
                     other = map[row][col - 1];
                     break;
                  } else {
                     fail[1] = true;
                     currentDir = 'a';
                  }
               } else {
                  currentDir = 'a';
                  fail[1] = true;
               }
            }
            if (currentDir == 'a') {
               if (row != 0) {
                  if (!map[row - 1][col].visited) {
                     other = map[row - 1][col];
                     break;
                  } else {
                     currentDir = 'd';
                     fail[2] = true;
                  }
               } else {
                  currentDir = 'd';
                  fail[2] = true;
               }
            }
            if (currentDir == 'd') {
               if (row != 9) {
                  if (!map[row + 1][col].visited) {
                     other = map[row + 1][col];
                     break;
                  } else {
                     currentDir = 'w';
                     fail[3] = true;
                  }
               } else {
                  currentDir = 'w';
                  fail[3] = true;
               }
            }
            // if all directions fail, break with a other direction set to null
            if (fail[0] && fail[1] && fail[2] && fail[3])
               break;

         }
         // if all sorrounding rooms are visited(other isn't set properly), then
         // backtrack
         if (other == null) {
            // if the room backtracks to the start, end generation (theres probably a
            // workaround for this but i'm to lazy to find it
            if (row == 9 && col == 4)
               break;
            if (map[row][col].dirs[0] != ' ')
               map[row][col].visited = true;
            row += (map[row][col].dirs[0] == 'd' ? (row == 9 ? 0 : 1)
                  : map[row][col].dirs[0] == 'a' ? (row == 0 ? 0 : -1) : 0);
            col += (map[row][col].dirs[0] == 'w' ? (col == 9 ? 0 : 1)
                  : map[row][col].dirs[0] == 's' ? (col == 0 ? 0 : -1) : 0);
            continue;
         } else {
            map[row][col].connect(other, currentDir);
            // moves the row and column
            row += currentDir == 'a' ? -1 : currentDir == 'd' ? 1 : 0;
            col += currentDir == 'w' ? 1 : currentDir == 's' ? -1 : 0;
         }
      }
      placeItemRoom();

      printMap();
   }

   // places the item room in a random location(not completely though, as the tree
   // will only spawn in rooms that are a dead end)
   public static void placeItemRoom() {
      // finds the number of dead ends in the game(there is only 1 entrance/exit to
      // the room)
      int numDeadEnds = 0;
      for (int x = 0; x < map.length; x++) {
         for (int y = 0; y < map[x].length; y++) {
            if (map[x][y].dirs[0] != ' ' && map[x][y].dirs[1] == ' ' && ((x != 9 || x != 0) && x != 4))
               numDeadEnds += 1;
         }
      }
      // calculated the odds each (the +1 is for a just in case)
      int odds = (int) (100.0 / ((double) numDeadEnds)) + 1;
      for (int x = 0; x < map.length; x++) {
         for (int y = 0; y < map[x].length; y++) {
            if (map[x][y].dirs[0] != ' ' && map[x][y].dirs[1] == ' ' && ((x != 9 || x != 0) && x != 4))
               // if a random number is less than the odds, set the item room, otherwise
               // improve the odds by removing a dead end
               if (rand.nextInt(100) <= odds) {
                  itemRoom[0] = x;
                  itemRoom[1] = y;
                  break;
               } else {
                  numDeadEnds -= 1;
                  odds = (int) (100.0 / ((double) numDeadEnds)) + 1;
               }
         }
      }
   }

   public static void printMap() {
      for (int x = map.length - 1; x >= 0; x--) {
         for (int y = 0; y < map[x].length; y++) {
            Node node = map[y][x];
            //System.out.print("\"");

            boolean topOpen = false;
            boolean bottomOpen = false;
            boolean leftOpen = false;
            boolean rightOpen = false;

            for (int i = 0; i < node.dirs.length; i++) {
               if (node.dirs[i] == 'w')
                  //System.out.print("^");
                  topOpen = true;
               else if (node.dirs[i] == 's')
                  //System.out.print("v");
                  bottomOpen = true;
               else if (node.dirs[i] == 'a')
                  leftOpen = true;
                  //System.out.print("<");
               else if (node.dirs[i] == 'd')
                  rightOpen = true;
                  //System.out.print(">");
                  //System.out.print("-");
            }

            // Left side.
            System.out.print(leftOpen ? " " : "|");

            // Middle (Can be open both ways or only 1)
            System.out.print(topOpen && bottomOpen ? " " : (!topOpen ? (!bottomOpen ? "_̅" : "‾") : "_"));

            // Right side.
            System.out.print(rightOpen ? " " : "|");

            //System.out.print("\"");
         }
         System.out.println(" ");
      }
   }
}