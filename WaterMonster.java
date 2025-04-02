import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class WaterMonster extends Entity {
   private static int lifetime = 30;
   public static BufferedImage waterMonster;
   public static boolean isSpawned = false;
   public static int cx;
   public static int cy;
   public static int hp = 1;
   public static int damage = 1;
   public static char dir1;
   public static char dir2;
   private static int NumWater;

   public static void spawn(int CX, int CY, int numWater) {
      try {
         waterMonster = ImageIO.read(new File("./Image files/waterMonster.png"));
      } catch (IOException ex) {

         System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
         System.out.println("Error details: ");
         ex.printStackTrace();
      }
      cx = CX;
      cy = CY;
      hp = 1;
      lifetime = numWater * 200;
      isSpawned = true;
      NumWater = numWater;
   }

   public static void doThings(Sword sword, Player player) {
      if (isSpawned) {
         attack(player);
         hurt(sword);
         Despawn();
      }
   }

   public static void hurt(Sword sword) {
      if (cx - unitSize / 2 < sword.cx + sword.width / 2
            && cx + unitSize / 2 > sword.cx - sword.width / 2
            && cy - unitSize / 2 < sword.cy + sword.height / 2
            && cy + unitSize / 2 > sword.cy - sword.height / 2) {
         hp -= Sword.damage;
      }
   }

   public static void Despawn() {
      if (hp <= 0) {
         cx = 999;
         cy = 999;
         isSpawned = false;
         int rand = (int) (Math.random() * 20);
         for (Item item : Driver.items) {
            // cast items in arrayList to items
            if (rand <= 4 && item.cx > unitSize * 20 && item.type.equals("heart")) {
               item.cx = cx;
               item.cy = cy;
               break;
            } else if (rand <= 6 && rand > 4 && item.cx > unitSize * 20 && item.type.equals("rubpee")) {
               item.cx = cx;
               item.cy = cy;
               break;
            } else if (rand <= 7 && rand > 6 && item.cx > unitSize * 20 && item.type.equals("rubpee5")) {
               item.cx = cx;
               item.cy = cy;
               break;
            }
         }
      } else if (lifetime <= 0) {
         cx = 999;
         cy = 999;
         isSpawned = false;
      }
   }

   public static void attack(Player player) {
      if (lifetime % (100 * NumWater) == 0 || lifetime == 1) {
         // make direction calculations
         if (player.cx > cx)
            dir1 = 'd';
         else
            dir1 = 'a';
         if (player.cy > cy)
            dir2 = 'w';
         else
            dir2 = 's';
         Driver.projs.add(new Projectile(cx, cy, 2, 5, dir1, dir2,0,0));
      }
      lifetime -= 1;
   }
}