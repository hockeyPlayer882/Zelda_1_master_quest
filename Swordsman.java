import java.awt.Graphics;
import java.awt.Color;//To be removed when I have an image
/*To be added when I have images
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
*/
public class Swordsman extends Entity {
   public Swordsman(int cx, int cy) {
      this.cx = cx;
      this.cy = cy;
      this.MmovementTimer = 120;
      this.hp = 2;
      // swordsmen are immune to the wand
      isImmuneWand = true;
      numKeyEnemiesAlive += 1;
   }

   // shield calculations are needed so hurtEntity needs to be overwridden
   public void hurtEntity(Sword sword, Player player, Driver driver, Graphics g) {
      // checks for arrow collisions
      Arrow.hurt(this);
      // checks for entity collisions
      if (inv > 0)
         this.inv -= 1;
      if (cx - unitSize / 2 < sword.cx + sword.width / 2
            && cx + unitSize / 2 > sword.cx - sword.width / 2
            && cy - unitSize / 2 < sword.cy + sword.height / 2
            && cy + unitSize / 2 > sword.cy - sword.height / 2
            && this.inv <= 0 || Player.name.equals("peaceful mode!")) {
         // checks if the player hit the shield while attacking
         if (!checkOppositeDirs(this, player) || Player.name.equals("peaceful mode!")) {
            this.hp -= (int) (Sword.damage / (1 - this.defense));
            this.inv = 60;
         }
      }
   }

   public void draw(Graphics g, Driver driver) {
      g.setColor(Color.ORANGE);
      g.fillRect(cx - unitSize, cy - unitSize, unitSize, unitSize);
      g.setColor(Color.BLACK);
      if (this.dir == 'w')
         g.fillRect(cx - unitSize, cy - unitSize, unitSize, 10);
      else if (this.dir == 's')
         g.fillRect(cx - unitSize, cy + unitSize / 22, unitSize, 10);
      else if (this.dir == 'a')
         g.fillRect(cx - unitSize, cy - unitSize, 10, unitSize);
      else if (this.dir == 'd')
         g.fillRect(cx + unitSize / 22, cy - unitSize, 10, unitSize);
   }
}