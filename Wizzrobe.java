import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
public class Wizzrobe extends Entity{
    private BufferedImage wizzrobeA;
    private BufferedImage wizzrobeD;
    //sets the timer that allows the wizzrobe to teleport and be invincible
    Random rand = new Random();
    private int MteleportTimer = 80;
    public int teleportTimer = 0;
    private int MprojectileTimer;;
    private int projectileTimer = 0;
    private char teleportDir = ' ';
    /**
     * constructs a wizzrobe at the starting cx and cy
     * @param cx the starting center x of the wizzrobe
     * @param cy the starting center y of the wizzrobe
     */
    public Wizzrobe(int cx, int cy){
    this.MmovementTimer = 100;
    this.fireResistance = 1;
    numKeyEnemiesAlive++;
    this.isImmuneWand = true;
    MprojectileTimer = rand.nextInt(50)+50;
    MmovementTimer = rand.nextInt(100)+50;
    MteleportTimer = rand.nextInt(80)+25;
    //1 heart of damage to the player
    this.damage = 2;
    this.defense = 1;
    this.hp = 2;
    this.dir = rand.nextInt(2) == 0 ? 'a':'d';
    this.cx = cx;
    this.cy = cy;
    try{     
        if(wizzrobeA == null)wizzrobeA = ImageIO.read(new File(".\\Image files\\wizzrobeA.png"));
        if(wizzrobeD == null)wizzrobeD = ImageIO.read(new File(".\\Image files\\wizzrobeD.png"));
    }catch (IOException ex) {
        System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
        System.out.println("Error details: ");
        ex.printStackTrace();
    }
    }
    /**
     * Shoots the projectiles
     * @param player player object to be used for making the projectiles always move toward the player
     */
    public void shootProjectile(Player player){
        if(projectileTimer == 0){
            Driver.projs.add(new Projectile(cx, cy, 2, 7, player.cx > cx ? 'd':'a'));
            projectileTimer = MprojectileTimer;
        }
        else projectileTimer--;
    }
    /**
     * Allows the wizzrobe to teleport through walls and be invincible
     */
    public void teleport(){
        if(movementTimer <= 0 && teleportTimer <= 0){
        this.inv = MteleportTimer;
        teleportTimer = MteleportTimer;
        movementTimer = MmovementTimer;
        MmovementTimer = rand.nextInt(100)+50;
        MteleportTimer = rand.nextInt(80)+25;
        teleportDir = rand.nextInt(2) == 0 && cy >= 700 || cy <= 200  ? 's':'w';
        }
        else if(teleportTimer <= 0)
            movementTimer--;
        if(teleportTimer > 0){
            teleportTimer--;
            if(cy <= 200 || cy >= 700)
                teleportDir = cy >= 700 ? 'w':'s';
            cy += teleportDir == 'w' ? -speed:speed;
        }
        if(cx >= 700 || cx <= 100)
            dir = cx >= 700 ? 'a':'d';
        cx += dir == 'a' ? -speed:speed;
    }
    /**
     * calls all of the major functions that are need for the wizzrobe to function(the one in Entity.java is missing certain functions needed for the wizzrobe)
     * @param sword sword for hurting the wizzrobe
     * 
     * @param player player to check if the player hits the wizzrobe
     */
    public void callBaseFunctions(Sword sword,Player player,Driver driver, Graphics g){
        if(!Player.isPaused){
        if(this.hp > 0){
        for(Fire fire : Driver.fires)
            fire.burn(this);
        Wand.hurt(this);
        Arrow.hurt(this);
        hurtEntity(sword);
        //if the can of invincibility is active, all ghosts who touch the player will instantly die
        player.hurtEntity(this);
        teleport();
        shootProjectile(player);
        hurtExplosion();
        decreaseInv();
        }
        else{
        dropItem(player,driver,g);
        despawn();
        }
        }
        }
    /**
     * draws the wizzrobe
     * @param g graphics
     * @param driver main file needed as a pointer for the graphics
     */
    public void draw(Graphics g,Driver driver){
        g.drawImage(dir == 'a' ? wizzrobeA:wizzrobeD,cx-unitSize/2,cy-unitSize/2,unitSize,unitSize,driver);
    }
}