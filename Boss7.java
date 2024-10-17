import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

//child class of Entity, has specific methods and attributes special for the main Boss7
public class Boss7 extends Entity {
    // tracks the Boss7 location in both the overworld and a dungeon
    public static int[] location = { 10, 10 };
    // tracks where the Boss7 is: level 0 is the overworld, level -1 is a cave or
    // shop, and positive levels correspond to dungeon levels
    public static int level;
    // sets the amount of time the Boss7 will hold out their sword each thrust,
    // value below is the max value
    public static int attackDelay;
    public int MaxAttackDelay;
    public char stDir = 's';
    private static BufferedImage boss7W;
    private static BufferedImage boss7Wattack;
    private static BufferedImage boss7S;
    private static BufferedImage boss7Sattack;
    private static BufferedImage boss7A;
    private static BufferedImage boss7Aattack;
    private static BufferedImage boss7D;
    private static BufferedImage boss7Dattack;
    private final int MteleportTimer = 200;
    private int teleportTimer = 0;
    private static final int MdarkTimer = 100;
    public static int darkTimer;
    private ArrayList<Integer> turnCX = new ArrayList<Integer>();
    private ArrayList<Integer> turnCY = new ArrayList<Integer>();
    private ArrayList<String> turnDirs = new ArrayList<String>();
    private char prevDir = 's';
    public static ArrayList<DarkSword> darkSwords = new ArrayList<DarkSword>();
    // 2D array for storing boss7 non-attacking animations, first array is for
    // standard, and the second is for the metal shield
    private static BufferedImage[] boss7Animations = { boss7W, boss7S, boss7A, boss7D };

    public Boss7(int cx, int cy) {
        this.speed = 6;
        try {
            boss7Animations[0] = ImageIO.read(new File("./Image files/boss7W.png"));
            boss7Wattack = ImageIO.read(new File("./Image files/boss7Wattack.png"));
            boss7Animations[1] = ImageIO.read(new File("./Image files/boss7S.png"));
            boss7Sattack = ImageIO.read(new File("./Image files/boss7Sattack.png"));
            boss7Animations[2] = ImageIO.read(new File("./Image files/boss7A.png"));
            boss7Aattack = ImageIO.read(new File("./Image files/boss7Aattack.png"));
            boss7Animations[3] = ImageIO.read(new File("./Image files/boss7D.png"));
            boss7Dattack = ImageIO.read(new File("./Image files/boss7Dattack.png"));
        } catch (IOException ex) {
            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
        this.hp = 14;
        this.Mhp = 14;
        attackDelay = 0;
        this.damage = 8;
        this.MaxAttackDelay = 200;
        this.cx = cx;
        this.cy = cy;
        darkTimer = MdarkTimer;
    }

    public void calcEnemyDir(Player player) {
        //decrease darkTimer (for the background stuff)
        if(darkTimer > 0) 
            darkTimer--;
        if(player.dir != prevDir && player.dir != 'n' && player.dir != 'e' && player.dir != 'q'){
            if(dir == 'w' || dir == 's'){
                turnCY.add(player.cy);
                turnDirs.add(player.dir + "");
            }
            else if(dir == 'a' || dir == 'd'){
                turnCX.add(player.cx);
                turnDirs.add(player.dir + "");
            }
        }
        if(!turnCX.isEmpty() && turnCX.get(0) == cx){
            dir = turnDirs.remove(0).charAt(0);
            turnCX.remove(0);
        }
        if(!turnCY.isEmpty() && turnCY.get(0) == cy){
            dir = turnDirs.remove(0).charAt(0);
            turnCY.remove(0);
        }
        if(player.dir != 'n' && player.dir != 'e' && player.dir != 'q')
            prevDir = player.dir;
    }

    public void teleport(Player player) {
        if(teleportTimer == 0){
            dir = player.dir == 'n' || player.dir == 'e' || player.dir == 'q' ? player.stDir:player.dir;
            cx = dir == 'd' ? player.cx-150 : dir == 'a' ? player.cx+150 : player.cx;
            cy = dir == 'w' ? player.cy+150 : dir == 's' ? player.cy-150 : player.cy;
            teleportTimer = MteleportTimer;
            darkTimer = MdarkTimer;
            //reset movement logic for following the player
            turnCX.clear();
            turnCY.clear();
            turnDirs.clear();
        }
        else
            teleportTimer--;
    }

    public void attack(Player player) {
        // locks Boss7 while the sword is activated
        if (attackDelay == 0){
            darkSwords.add(new DarkSword(this));
            attackDelay = MaxAttackDelay;
        }
        decreaseAtkDel();
    }

    public void decreaseAtkDel() {
        if (attackDelay > 0) {
            attackDelay -= 1;
        }

    }

    public void draw(Graphics g, Driver driver) {
        char drawDir;
        if (this.dir == 'n')
            drawDir = this.stDir;
        else
            drawDir = this.dir;
        if (inv % 2 == 0) {
            if (drawDir == 'w')
                g.drawImage(boss7Animations[0], this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                        driver);
            else if (drawDir == 's')
                g.drawImage(boss7Animations[1], this.cx - unitSize / 2,
                        this.cy - unitSize / 2, unitSize, unitSize, driver);
            else if (drawDir == 'a')
                g.drawImage(boss7Animations[2], this.cx - unitSize / 2,
                        this.cy - unitSize / 2, unitSize, unitSize, driver);
            else if (drawDir == 'd')
                g.drawImage(boss7Animations[3], this.cx - unitSize / 2,
                        this.cy - unitSize / 2, unitSize, unitSize, driver);
            else if (drawDir == 'e') {
                if (this.stDir == 'a')
                    g.drawImage(boss7Aattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                            driver);
                else if (this.stDir == 'd')
                    g.drawImage(boss7Dattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                            driver);
                else if (this.stDir == 'w')
                    g.drawImage(boss7Wattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                            driver);
                else if (this.stDir == 's')
                    g.drawImage(boss7Sattack, this.cx - unitSize / 2, this.cy - unitSize / 2, unitSize, unitSize,
                            driver);
            }
        }
    }
}
