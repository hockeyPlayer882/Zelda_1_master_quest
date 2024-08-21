import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Boss5 extends Entity {
    private int projectileTimer;
    public static BufferedImage boss5;
    public boolean playerHasUsedCane = false;

    public Boss5(int cx, int cy, char dir) {
        super(10, dir, cx, cy, 12, 12, 1, 12, 100, 50, 0);
        this.projectileTimer = 240;
        this.maxInv = 60;
        isBoss = true;
        inv = 999;
        try {
            boss5 = ImageIO.read(new File(".\\Image files\\boss5.png"));
        } catch (IOException ex) {

            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("When Java doesn't know how to Java .-.");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }

    public void resetProjectileTimer() {
        this.projectileTimer = (int) (MmovementTimer + Math.random() * 80);
    }

    public void shootProjectile(Player player) {
        this.projectileTimer -= 1;
        if (projectileTimer <= 0) {
            if (this.dir != 'n') {
                for (int i = 1; i <= 10; i++) {
                    // MATH THINGIES FOR CIRCLE
                    // formula for X points, cx = middle point's cx + (radius * math.cos(degree))
                    // formula for Y points, cy = middle point's cy + (radius * math.sin(degree))
                    Driver.projs.add(new Projectile(cx + (int) ((Entity.unitSize / 1.5) * Math.cos(i * 36)),
                            cy + (int) ((Entity.unitSize / 1.5) * Math.sin(i * 36)), 1, 8, i*36 > 180 ? 'a':'d', i*36 <= 90 || i*63 >= 270 ? 'w':'s'));
                }
            }
            this.resetProjectileTimer();
            this.movementTimer = 0;
        }
        if(Cane.isActive && !playerHasUsedCane){
            playerHasUsedCane = false;
            inv = 0;
        }
        if(!playerHasUsedCane && inv == 1)
            inv = 999;
    }

    public void draw(Graphics g, Driver driver) {
        g.drawImage(boss5, this.cx - unitSize / 2, (this.cy - unitSize / 2) - ActiveMenu.iterationNum, unitSize,
                unitSize, driver);
    }
}