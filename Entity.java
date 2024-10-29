import java.awt.Graphics;

import neozelda.audio.AudioEngine;

//Inherited class for all enemies and player
public class Entity {
   // properties tht all entities in my game will have
   protected int speed;
   protected char dir;
   protected int cx;
   protected int cy;
   protected int hp;
   protected int Mhp;
   protected int defense;
   protected int damage;
   // if true, then the wand deals 0 damage
   public boolean isImmuneWand = false;
   // keeps track of how long an enemy will be stunned for
   public int stun;
   public int Mstun;
   // checks if the enemy is a boss
   protected boolean isBoss;
   // checks if the enemy can be defeated with a boomerang
   public boolean isWeakToBoomerang;
   // first variable sets the invincibility frame, the first number is the counter
   // and the second number is the max invincibility frames.
   protected int inv;
   protected int maxInv;
   protected int movementTimer;
   protected int MmovementTimer;
   protected static final int unitSize = 40;
   public int fireResistance;
   // keeps track of the number of enemies in a room that are needed to be
   // deafeated in order to drop a key
   public static int numKeyEnemiesAlive;
   // pretty self-explanitory, boolean to check if the specified enemy is dead or
   // not, only used for key enemies becuase its not appplicable for other enemies
   private boolean isDead;
   // quick tangeant here: if anyone finds my poor grammar skills annoying, pls do
   // me a favor and don't read these comments, ever.
   // checks if the sword is active, if not, the player can use the shield to be
   // protected from projectiles
   protected boolean shieldIsActive = true;
   // checks to see how powerful the entities shield is, 0 is no shield
   protected int shieldStrength;

   // Basic constructor for simple enemies and player
   public Entity() {
      this.speed = 6;
      this.dir = 'n';
      this.cx = 200;
      this.cy = 200;
      this.hp = 6;
      this.Mhp = 6;
      this.defense = 0;
      this.damage = 1;
      this.movementTimer = 0;
      this.MmovementTimer = 0;
      // most entities don't have a shield
      this.shieldStrength = 0;
   }

   // more complex constructor for intricate enemies
   public Entity(int speed, char dir, int cx, int cy, int hp, int Mhp, int defense, int damage, int movementTimer,
         int MmovementTimer, int shieldStrength) {
      this.speed = speed;
      this.dir = dir;
      this.cx = cx;
      this.cy = cy;
      this.hp = hp;
      this.Mhp = Mhp;
      this.defense = defense;
      this.damage = damage;
      this.movementTimer = movementTimer;
      this.MmovementTimer = MmovementTimer;
      this.shieldStrength = shieldStrength;
   }

   // calls all of the functions required for basic enemies to function
   public void callBaseFunctions(Player player, Sword sword) {
      if (this.hp > 0) {
         Boomerang.stun(this);
         this.decreaseInv();
         Wand.hurt(this);
         this.moveEntity();
         Arrow.hurt(this);
         hurtEntity(sword);
         player.hurtEntity(this);
         this.calcEnemyDir();
         for (Obstacle o : Driver.obstacles)
            o.collide(this, true);
         this.hurtEntity(sword);
         Arrow.hurt(this);
         this.hurtExplosion();
         for (Fire fire : Driver.fires)
            fire.burn(this);
      } else {
         this.despawn();
         this.moveEntity();
      }
   }

   // calls all the functions necessary for the player to function
   public void callBaseFunctions(Player player, Room room) {
      if (player.stun == 0) {
         this.decreaseInv();
         for (Obstacle obstacle : Driver.obstacles)
            obstacle.collide(this, false);

         this.moveEntity();
      }
      // only let the player move screens if they are in the overworld
      if (Player.level == 0)
         player.advanceRoom(room);
   }

   public void hurtExplosion() {
      for (Explosion explosion : Driver.explosions) {
         if (this.cx - unitSize / 2 < explosion.cx + unitSize / 2
               && this.cx + unitSize / 2 > explosion.cx - unitSize / 2
               && this.cy - unitSize / 2 < explosion.cy + unitSize / 2
               && this.cy + unitSize / 2 > explosion.cy - unitSize / 2
               && this.inv <= 0) {
            // bombs pierce defense (BOMBS OP)
            this.hp -= explosion.damage / 1;
            this.inv = 60;
         }
      }

   }

   // checks if a player and an Entity are facing each other
   public boolean checkOppositeDirs(Entity ent, Player player) {
      char Dir = (player.dir == 'e' ? player.stDir : player.dir);
      return ent.dir == 'a' && Dir == 'd' || ent.dir == 'd' && Dir == 'a' || ent.dir == 'w' && Dir == 's'
            || ent.dir == 's' && Dir == 'w';
   }

   public void hurtEntity(Entity ent) {
      // check if an entity hits another entity, if so, decrease hp according to
      // defense, rounding down
      if (this.cx - unitSize / 2 < ent.cx + unitSize / 2
            && this.cx + unitSize / 2 > ent.cx - unitSize / 2
            && this.cy - unitSize / 2 < ent.cy + unitSize / 2
            && this.cy + unitSize / 2 > ent.cy - unitSize / 2
            && this.inv <= 0) {
         // hurt entity based on damage calculations
         this.hp -= (int) (ent.damage / (1 - this.defense));
         this.inv = 60;
      }
   }

   public void hurtEntity (Sword sword) {
      if (cx - unitSize / 2 < sword.cx + sword.width / 2
            && cx + unitSize / 2 > sword.cx - sword.width / 2
            && cy - unitSize / 2 < sword.cy + sword.height / 2
            && cy + unitSize / 2 > sword.cy - sword.height / 2
            && this.inv <= 0 && Sword.damage >= (this.defense)) {
         // hurt entity based on damage calculations
         this.hp -= (int) (Sword.damage / (this.defense + 1));
         this.inv = 60;

         AudioEngine.playClip("./sfx/LOZ_Enemy_Hit.wav");
      }
      // cheat code to immediatly kill all enemies
      if (Player.name.equals("peaceful mode!"))
         hp = 0;
   }

   @SuppressWarnings("unlikely-arg-type")
   public void despawn() {
      Room.currentRoom.remove(this);
      if (this.hp <= 0) {
         if (!isDead) {
            isDead = true;
            numKeyEnemiesAlive -= 1;
         }
         // generates a random number between 0 and 20 (inclusive) if the enemy isn't a
         // boss, otherwise spawn a heart container
         int rand = 0;
         rand = (isBoss ? -10 : (int) (Math.random() * 20));
         for (Item item : Driver.items) {
            // cast items in arrayList to items
            if (rand <= 4 && item.cx > unitSize * 20 && item.type.equals("heart")) {
               item.cx = this.cx;
               item.cy = this.cy;
               break;
            } else if (rand <= 6 && rand > 4 && item.cx > unitSize * 20 && item.type.equals("rubpee")) {
               item.cx = this.cx;
               item.cy = this.cy;
               break;
            } else if (rand <= 7 && rand > 6 && item.cx > unitSize * 20 && item.type.equals("rubpee5")) {
               item.cx = this.cx;
               item.cy = this.cy;
               break;
            } else if (rand == -10 && item.type.equals("heartContainer")) {
               item.cx = this.cx;
               item.cy = this.cy;
               break;
            }
         }
         // "despawns" the enemy
         this.cx = 999;
         this.cy = 999;
         // resets any negative hp to 0
         this.hp = 0;
      }
   }

   // checks if the invincibility is above 0, which would indicate that that
   // specific entity has invincibility, if so, decrease timer until 0 is hit and
   // invincibility is removed
   public void decreaseInv() {
      if (this.inv > 0) {
         this.inv -= 1;
      }
   }

   // restores hp to a specified amount
   public void heal(int amt) {
      if (this.hp + amt <= this.Mhp) {
         this.hp += amt;
      } else {
         this.hp = this.Mhp;
      }
   }

   // algorithm for determining when and how an entity should change thier
   // direction
   public void calcEnemyDir() {
      // decreases movement timer
      this.movementTimer -= 1;
      // checks for a wall collision or if the movement timer is decreased
      if (this.movementTimer <= 0 || (this.cx - unitSize / 2) < 10 || this.cx + (unitSize / 2) > 790
            || this.cy - unitSize / 2 < 150 || this.cy + unitSize / 2 > 760) {
         this.movementTimer = (int) ((this.MmovementTimer + 1) * Math.random());
         // randomly changes the enemies position unless a collision is detected, then
         // the entity moves in the opposite direction
         int rand = (int) (4 * Math.random());
         if (this.cy - unitSize / 2 < 150) {
            rand = 2;
         } else if (this.cy + unitSize / 2 > 760) {
            rand = 1;
         } else if (this.cx - unitSize / 2 < 10) {
            rand = 4;
         } else if (this.cx + unitSize / 2 > 790) {
            rand = 3;
         }
         switch (rand) {
            case 1:
               this.dir = 'w';
               break;
            case 2:
               this.dir = 's';
               break;
            case 3:
               this.dir = 'a';
               break;
            case 4:
               this.dir = 'd';
               break;
         }
      }
   }

   public void moveEntity() {

      if (stun <= 0) {
         // called every frame, moves the entity according to its direction-> w is up, s
         // is down, a is left, and d is right
         switch (this.dir) {
            case 'w':
               this.cy -= this.speed;
               break;
            case 's':
               this.cy += this.speed;
               break;
            case 'a':
               this.cx -= this.speed;
               break;
            case 'd':
               this.cx += this.speed;
               break;
         }
      } else
         stun -= 1;
   }

   // checks if enemies can drop the key after they are defeated
   public void dropItem(Player player, Driver driver, Graphics g) {
      if (this.hp <= 0 || Player.name.equals("peaceful mode!")) {
         Item item = (Item) Driver.items.get(3);
         // spawns the boomerang if the player is in the first dungeon and in the correct
         // room
         if (Player.level == 1) {
            if (Player.location[0] == 3 && Player.location[1] == 4 && !Player.hasBoomerang) {
               if (numKeyEnemiesAlive == 0 && Room.images.size() == 0){
                  Boomerang.setImage();
                  Room.images.add(Boomerang.boomerang);
               }
            }
         }
         if (Player.level == 3) {
            if (Player.location[0] == 3 && Player.location[1] == -1 && !Player.hasRaft) {
               if (numKeyEnemiesAlive == 0 && Room.images.size() == 0)
                  Room.images.add(Room.raft);
            }
         }
         if (Player.level == 4) {
            if (Player.location[0] == -2 && Player.location[1] == -1 && !Player.hasWand) {
               if (numKeyEnemiesAlive == 0 && Room.images.size() == 0)
                  Room.images.add(Wand.wandW);
            }
         }
         if (Player.level == 2) {
            if (Player.location[0] == 2 && Player.location[1] == 3 && !Player.hasArrows) {
               if (numKeyEnemiesAlive == 0 && Room.images.size() == 0) {
                  Arrow.draw(driver, g);
                  Room.images.add(Arrow.arrowW);
               }
            }
         }
         if(Player.level == 5){
            if(Player.location[0] == -3 && Player.location[1] == -3 && !Player.hasCane && numKeyEnemiesAlive == 0 && Room.images.size() == 0){
               Room.images.add(Cane.cane);
            }

         }
         if(Player.level == 6){
            if(Player.location[0] == -4 && Player.location[1] == -3 && !Player.hasSuperBomb && numKeyEnemiesAlive == 0 && Room.images.size() == 0){
               Room.images.add(SuperBomb.superBomb);
            }

         }
         // spawns the key if all the enemies in a key room are defeated.
         if ((Player.level == 1 && ((Player.location[0] == 1 && Player.location[1] == 0 && !LoadingZone.keyArray[0][0])
               || (Player.location[0] == -1 && Player.location[1] == 3 && !LoadingZone.keyArray[0][1])))
               || (Player.level == 2
                     && ((Player.location[0] == -1 && Player.location[1] == 0 && !LoadingZone.keyArray[1][0])
                           || (Player.location[0] == -1 && Player.location[1] == 1 && !LoadingZone.keyArray[1][1])
                           || (Player.location[0] == 1 && Player.location[1] == 0 && !LoadingZone.keyArray[1][2])
                           || (Player.location[0] == 2 && Player.location[1] == 0 && !LoadingZone.keyArray[1][3])
                           || (Player.location[0] == 3 && Player.location[1] == 0 && !LoadingZone.keyArray[1][4])
                           || (Player.location[0] == 3 && Player.location[1] == 2 && !LoadingZone.keyArray[1][5])))
               || (Player.level == 3
                     && (Player.location[0] == -1 && Player.location[1] == 0 && !LoadingZone.keyArray[2][0]
                           || Player.location[0] == 0 && Player.location[1] == 1 && !LoadingZone.keyArray[2][1]
                           || Player.location[0] == 2 && Player.location[1] == 0 && !LoadingZone.keyArray[2][2]
                           || Player.location[0] == 3 && Player.location[1] == 2 && !LoadingZone.keyArray[2][3]
                           || Player.location[0] == -2 && Player.location[1] == 1 && !LoadingZone.keyArray[2][4]
                           || Player.location[0] == -2 && Player.location[1] == 0 && !LoadingZone.keyArray[2][5]))
               || (Player.level == 4
                     && ((Player.location[0] == 1 && Player.location[1] == 0 && !LoadingZone.keyArray[3][0])
                           || (Player.location[0] == 0 && Player.location[1] == 1 && !LoadingZone.keyArray[3][1])
                           || (Player.location[0] == -1 && Player.location[1] == 3 && !LoadingZone.keyArray[3][2])
                           || (Player.location[0] == 0 && Player.location[1] == 3 && !LoadingZone.keyArray[3][3])
                           || (Player.location[0] == 1 && Player.location[1] == 3 && !LoadingZone.keyArray[3][4])
                           || (Player.location[0] == 2 && Player.location[1] == 3 && !LoadingZone.keyArray[3][5])
                           || Player.location[0] == 3 && Player.location[1] == -2 && !LoadingZone.keyArray[3][6]
                           || Player.location[0] == 2 && Player.location[1] == 2 && !LoadingZone.keyArray[3][7]
                           || Player.location[0] == -2 && Player.location[1] == 2 && !LoadingZone.keyArray[3][8]))
               || (Player.level == 5
                     && (Player.location[0] == 1 && Player.location[1] == 0 && !LoadingZone.keyArray[4][0]
                           || Player.location[0] == -1 && Player.location[1] == 1 && !LoadingZone.keyArray[4][1]
                           || Player.location[0] == 2 && Player.location[1] == -1 && !LoadingZone.keyArray[4][2]
                           || Player.location[0] == 2 && Player.location[1] == -2 && !LoadingZone.keyArray[4][3]
                           || Player.location[0] == 3 && Player.location[1] == 1 && !LoadingZone.keyArray[4][4]
                           || Player.location[0] == 3 && Player.location[1] == -2 && !LoadingZone.keyArray[4][5]
                           || Player.location[0] == 4 && Player.location[1] == 0 && !LoadingZone.keyArray[4][6]
                           || Player.location[0] == 4 && Player.location[1] == -3 && !LoadingZone.keyArray[4][7]))
               || (Player.level == 6 
                     && (Player.location[0] == 1 && Player.location[1] == 0 && !LoadingZone.keyArray[5][0]
                        || Player.location[0] == 0 && Player.location[1] == 1 && !LoadingZone.keyArray[5][1]
                        || Player.location[0] == -1 && Player.location[1] == 2 && !LoadingZone.keyArray[5][2]
                        || Player.location[0] == 2 && Player.location[1] == 1 && !LoadingZone.keyArray[5][3]
                        || Player.location[0] == 2 && Player.location[1] == 0 && !LoadingZone.keyArray[5][4]
                        || Player.location[0] == 1 && Player.location[1] == -1 && !LoadingZone.keyArray[5][5]
                        || Player.location[0] == 0 && Player.location[1] == -1 && !LoadingZone.keyArray[5][6]
                        || Player.location[0] == -1 && Player.location[1] == -1 && !LoadingZone.keyArray[5][7]
                        || Player.location[0] == -2 && Player.location[1] == -1 && !LoadingZone.keyArray[5][8]
                        || Player.location[0] == -3 && Player.location[1] == -1 && !LoadingZone.keyArray[5][9]
                        || Player.location[0] == -4 && Player.location[1] == -1 && !LoadingZone.keyArray[5][10]
                        || Player.location[0] == -2 && Player.location[1] == 2 && !LoadingZone.keyArray[5][11]))
               || (Player.level == 7 
                  && ((Player.location[0] == -1 && Player.location[1] == 1 && !LoadingZone.keyArray[6][0])
                  || (Player.location[0] == -2 && Player.location[1] == 1 && !LoadingZone.keyArray[6][1])
                  || (Player.location[0] == -3 && Player.location[1] == 1 && !LoadingZone.keyArray[6][2])
                  || (Player.location[0] == -4 && Player.location[1] == 1 && !LoadingZone.keyArray[6][3])
                  || (Player.location[0] == -4 && Player.location[1] == 2 && !LoadingZone.keyArray[6][4])
                  || (Player.location[0] == -4 && Player.location[1] == 3 && !LoadingZone.keyArray[6][5])
                  || (Player.location[0] == -4 && Player.location[1] == 4 && !LoadingZone.keyArray[6][6])
                  || (Player.location[0] == -3 && Player.location[1] == 4 && !LoadingZone.keyArray[6][7])
                  || (Player.location[0] == -2 && Player.location[1] == 4 && !LoadingZone.keyArray[6][8])
                  || (Player.location[0] == -1 && Player.location[1] == 4 && !LoadingZone.keyArray[6][9])
                  || (Player.location[0] == -1 && Player.location[1] == 3 && !LoadingZone.keyArray[6][10])
                  || (Player.location[0] == -1 && Player.location[1] == 2 && !LoadingZone.keyArray[6][11])
                  
                  )
                  )
                     && item.type.equals("key")) {
            if (numKeyEnemiesAlive == 0) {
               item.cx = 400;
               item.cy = 400;
               if ((Player.level == 3 && (Player.location[0] == 0 && Player.location[1] == 1
                     || Player.location[0] == 3 && Player.location[1] == 2))
                     || (Player.level == 4 && (Player.location[0] == 0 && Player.location[1] == 1))
                  || Player.level == 5 && (Player.location[0] == 3 && Player.location[1] == 1)) {
                  item.cx = 80;
                  item.cy = 400;
               }
            }
         }
      } else
         this.despawn();
   }
}