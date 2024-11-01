import neozelda.audio.AudioEngine;

public class Obstacle extends Entity {
   public String type;
   // checks if an obstacle can be moved or not, 0 is not, 1 is moveable from the
   // left to the right, 2 is moveable from the right to the left, 3 is moveable
   // from the top to bottom, 4 is moveable from the bottom to top, and 5 is
   // moveable from any direction
   // TODO: Bro just use an enum ;)
   //enums aren't real.....
   private int moveable;
   public boolean isBurnable;
   public boolean isExplodable;
   public boolean isSuperBombable;
   public static boolean superBombBlownRock = false;
   public Obstacle(int cx, int cy, String type, int moveable, boolean isBurnable, boolean isActive,
         boolean isExplodable) {
      this.cx = cx;
      this.cy = cy;
      this.type = type;
      this.moveable = moveable;
      this.isBurnable = isBurnable;
      this.isExplodable = isExplodable;
   }

   public void openFire(Room room, int x, int y) {
      for (int i = 0; i < Driver.fires.size(); i++) {
         Fire fire = (Fire) Driver.fires.get(i);
         if (this.cx - unitSize / 2 < fire.cx + unitSize / 2
               && this.cx + unitSize / 2 > fire.cx - unitSize / 2
               && this.cy - unitSize / 2 < fire.cy + unitSize / 2
               && this.cy + unitSize / 2 > fire.cy - unitSize / 2) {
            isBurnable = false;
            Driver.fires.remove(i);
            Room.currentRoom.get(x).set(y,
                  new LoadingZone(cx, cy, Player.location[0] == 14 && Player.location[1] == 9 ? 4
                        : Player.location[0] == 7 && Player.location[1] == 6 ? 6 : -1));
            Room.currentRoom.get(x).remove(this);

            AudioEngine.playClip("./sfx/LTTP_Secret.wav");
            break;
         }
      }
   }

   private void openRock(Room room, int x, int y) {
      isExplodable = false;
      if(isSuperBombable){
         Room.currentRoom.get(x).remove(this);
         Driver.obstacles.remove(this);
         superBombBlownRock = true;
      }
      else{
      Room.currentRoom.get(x).set(y,
            new LoadingZone(cx, cy, Player.location[0] == 11 && Player.location[1] == 15 ? 5 : -1));
      Room.currentRoom.get(x).remove(this);
      Driver.obstacles.remove(this);
      }
      
      AudioEngine.playClip("./sfx/LTTP_Secret.wav");
   }

   public void openExplosion(Room room, int x, int y) {
      // becuase I coded superbombs differently from normal bombs, I make a check to
      // see if the rocks hp is 0, which is what happens when a superbombs is used
      if (this.hp == 0)
         openRock(room, x, y);
      for (Explosion explosion : Driver.explosions) {
         if (this.cx - unitSize / 2 < explosion.cx + unitSize / 2
               && this.cx + unitSize / 2 > explosion.cx - unitSize / 2
               && this.cy - unitSize / 2 < explosion.cy + unitSize / 2
               && this.cy + unitSize / 2 > explosion.cy - unitSize / 2 && !isSuperBombable) {
            Driver.explosions.remove(explosion);
            openRock(room, x, y);
            break;
         }
      }
   }

   public void collide(Entity ent, boolean isEn) {
      // collision booleans
      // checks if a collision is from the left side
      boolean hitLeft = ent.cx + unitSize / 2 <= this.cx
            && ent.cx + unitSize / 2 >= this.cx - unitSize / 2 - (unitSize / 10);
      // checks if a collision is from the right side
      boolean hitRight = ent.cx - unitSize / 2 <= this.cx + unitSize / 2
            && ent.cx + unitSize / 2 >= this.cx + unitSize / 2 + (unitSize / 20);
      // constrains left and right collisions to only the borders of the object
      boolean constrainTopBottom = ent.cy + unitSize / 2 >= this.cy - unitSize / 2
            && ent.cy - unitSize / 2 <= this.cy + unitSize / 2;

      // checks a collision from the top
      boolean hitTop = ent.cy - unitSize / 2 < this.cy
            && ent.cy + unitSize / 2 > this.cy - unitSize / 2 - (unitSize / 10);
      // checks a collision from the bottom
      boolean hitBottom = ent.cy + unitSize / 2 > this.cy
            && ent.cy - unitSize / 2 < this.cy + unitSize / 2 + (unitSize / 10);
      boolean constrainLeftRight = ent.cx - unitSize / 2 < this.cx + unitSize / 2
            && ent.cx + unitSize / 2 > this.cx - unitSize / 2;
      // if obstacle can't be moved and obsacle is in the playable area...
      if (moveable == 0) {
         // checks a collision from the left
         if (hitLeft && constrainTopBottom && ent.dir == 'd') {
            // stops entity from moving if the entity tries to move left into a given object
            if (isEn)
               ent.dir = 'a';
            else
               ent.dir = 'n';
         }
         // checks a collision from the right
         else if (hitRight && constrainTopBottom && ent.dir == 'a') {
            if (isEn)
               ent.dir = 'd';
            else
               ent.dir = 'n';
         }
         // checks a collision from the top
         else if (constrainLeftRight && hitTop && ent.dir == 's') {
            if (isEn)
               ent.dir = 'w';
            else
               ent.dir = 'n';
         }
         // checks a collision from the bottom
         else if (constrainLeftRight && hitBottom && ent.dir == 'w') {
            if (isEn)
               ent.dir = 's';
            else
               ent.dir = 'n';
         }
      }

      else {
      }
   }
}