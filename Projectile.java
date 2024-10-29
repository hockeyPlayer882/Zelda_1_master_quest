import java.awt.Graphics;
import java.awt.Color;

public class Projectile {
   // base damage for projectiles are 1
   public int projectileStrength = 1;
   private int speed;
   private int damage;
   public int cx;
   public int cy;
   // array for top and bottom directions(becuase projectiles can move diagnolly.
   public char[] dirs = new char[2];

   public Projectile(int cx, int cy, int projectileStrength, int speed, char dir1) {
      this.dirs[0] = dir1;
      this.dirs[1] = 'n';
      this.speed = speed;
      this.cx = cx;
      this.cy = cy;
      this.damage = 1;
      this.projectileStrength = projectileStrength;
   }

   public Projectile(int cx, int cy, int projectileStrength, int speed, char dir1, char dir2) {
      this.dirs[0] = dir1;
      this.dirs[1] = dir2;
      this.speed = speed;
      this.cx = cx;
      this.cy = cy;
      this.damage = 1;
      this.projectileStrength = projectileStrength;

   }

   public void move() {
      for (int i = 0; i < dirs.length; i++) {
         if (dirs[i] == 'w')
            this.cy += speed;
         else if (dirs[i] == 's')
            this.cy -= speed;
         else if (dirs[i] == 'a')
            this.cx -= speed;
         else if (dirs[i] == 'd')
            this.cx += speed;
      }
   }

   public void drawProjectile(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(this.cx - Entity.unitSize / 2, this.cy - Entity.unitSize / 2 + ActiveMenu.iterationNum,
            Entity.unitSize, Entity.unitSize);
   }

   public void despawn() {
      Driver.projs.remove(this);
      this.cx = 999;
      this.cy = 999;
   }

   public void hurtPlayer(Player player) {
      if (this.cx - Entity.unitSize / 2 < player.cx + Entity.unitSize / 2
            && this.cx + Entity.unitSize / 2 > player.cx - Entity.unitSize / 2
            && this.cy - Entity.unitSize / 2 < player.cy + Entity.unitSize / 2
            && this.cy + Entity.unitSize / 2 > player.cy - Entity.unitSize / 2) {
         char storePdir = '`';
         if (player.dir == 'e')
            storePdir = player.stDir;
         if (player.dir != 'n')
            player.stDir = player.dir;
         // checks if the shield is stronger than the projectile, if the shield is
         // active, and if the obstacle and entity are facing each other(opposite
         // directions), and the projectile is moving in a line

         if (dirs[1] == 'n') {
            if (player.inv <= 0 && (!player.shieldIsActive || player.shieldStrength < projectileStrength
                  || !((player.stDir == 'w' && dirs[0] == 's' || player.stDir == 's' && dirs[0] == 'w'
                        || player.stDir == 'a' && dirs[0] == 'd' || player.stDir == 'd' && dirs[0] == 'a')))) {
               // hurt player based on damage calculations

               // UPDATE: Switched to use a wrapper function (no external classes
               // should be setting player hp).
               player.hurtRawDamage((int) (this.damage / (1 - player.defense)));
            }
         }

         // calculates shield effectiveness against projectiles that move on a diagnal
         else {
            // checks if the player's shield is effective based on location calculations
            // checks if the projectile hits the left side of the player
            boolean hitLeft = player.cx - Entity.unitSize / 2 <= cx + Entity.unitSize / 2
                  && player.cx - Entity.unitSize / 2 >= (cx - Entity.unitSize / 2) - speed;
            // checks if the projectile hits the right side of the player
            boolean hitRight = player.cx + Entity.unitSize / 2 >= cx - Entity.unitSize / 2
                  && (player.cx + Entity.unitSize / 2) <= (cx - Entity.unitSize / 2) + speed;
            // checks if the projectile hits the player from the top
            boolean hitTop = player.cy - Entity.unitSize / 2 <= cy + Entity.unitSize / 2
                  && player.cy - Entity.unitSize / 2 >= (cy + Entity.unitSize / 2) - speed;
            // checks if the projectile hits the player from the bottom
            boolean hitBottom = player.cy + Entity.unitSize / 2 >= cy - Entity.unitSize / 2
                  && (player.cy + Entity.unitSize / 2) <= (cy - Entity.unitSize / 2) + speed;
            if ((hitLeft && player.stDir != 'a' ||
                  hitRight && player.stDir != 'd' ||
                  hitTop && player.stDir != 'w' ||
                  hitBottom && player.stDir != 's')
                  && !player.shieldIsActive || player.inv <= 0 && player.shieldStrength < projectileStrength) {
               // hurt player based on damage calculations
               
               // UPDATE: Switched to use a wrapper function (no external classes
               // should be setting player hp).
               player.hurtRawDamage((int) (this.damage / (1 - player.defense)));
            }
         }
         despawn();
         if (player.dir == 'e')
            player.stDir = storePdir;
      }
   }
}