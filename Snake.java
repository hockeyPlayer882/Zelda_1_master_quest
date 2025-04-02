import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
public class Snake extends Entity{
    public static BufferedImage snakeA;
    public static BufferedImage snakeD;
    public static int poisonTimer = 0;
    public static int MpoisonTimer = 240;
    public Snake(int cx, int cy){
        super(10,'a',cx,cy,3,3,0,1,(int)(Math.random()*20+80),(int)Math.random()*20+80,0);
        numKeyEnemiesAlive++;        
            try{     
                    snakeA = ImageIO.read(new File("./Image files/snakeA.png"));
                    snakeD = ImageIO.read(new File("./Image files/snakeD.png"));
            }catch (IOException ex) {
                    System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
                    System.out.println("Error details: ");
                    ex.printStackTrace();
}
    }
    public void callBaseFunctions(Player player, Sword sword) {
        if (this.hp > 0) {
           Boomerang.stun(this);
           decreaseInv();
           Wand.hurt(this);
           if(dir == 'w' || dir == 's') dir = 'a';
           moveEntity();
           Arrow.hurt(this);
           hurtEntity(sword);
           calcEnemyDir();
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
    public void hurtEntity(Player player){
        if(this.cx - unitSize / 2 < player.cx + unitSize / 2
        && this.cx + unitSize / 2 > player.cx - unitSize / 2
        && this.cy - unitSize / 2 < player.cy + unitSize / 2
        && this.cy + unitSize / 2 > player.cy - unitSize / 2
        && player.inv <= 0) {
     // poison the player (AND THERES NOTHING THEY CAN DO ABOUT IT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!) (other than dodge)
     player.inv = 60;
     poisonTimer = MpoisonTimer; 
  }
    }
    public void calcEnemyDir() {
        movementTimer--;
        if(movementTimer <= 0){
            movementTimer = MmovementTimer;
            dir = dir == 'a' ? 'd':'a';
        }
        if(cx >= 700 || cx <= 100)
            dir = cx >= 700 ? 'a':'d';
    }
    public void draw(Graphics g, Driver driver){
        g.drawImage(dir == 'a' ? snakeA:snakeD,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
    }
    
}