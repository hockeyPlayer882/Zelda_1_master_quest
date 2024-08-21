import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Boss1 extends Entity {
    private int projectileTimer;
    public static BufferedImage boss1;

    public Boss1(int cx, int cy, char dir) {
        this.projectileTimer = 240;
        this.dir = dir;
        this.cx = cx;
        this.cy = cy;
        this.defense = 0;
        this.maxInv = 60;
        this.hp = 6;
        this.damage = 2;
        isBoss = true;
        try {
            boss1 = ImageIO.read(new File(".\\Image files\\boss1.png"));
        } catch (IOException ex) {

            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("When Java doesn't know how to Java .-.");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }

    public void calcDir(Player player) {
        if (this.cy <= 200)
            this.dir = 's';
        else if (this.cy >= 700)
            this.dir = 'w';
    }

    public void resetProjectileTimer() {
        this.projectileTimer = (int) (MmovementTimer + Math.random() * 80);
    }

    public void shootProjectile(Player player) {
        this.projectileTimer -= 1;
        if (projectileTimer <= 0) {
            if (this.dir != 'n') {
                // spawns 3 projectiles stronger than the players shield & moves faster than the
                // other projectiles!
                Driver.projs.add(new Projectile(this.cx, this.cy, 2, 10, 'a'));
                Driver.projs.add(new Projectile(this.cx, this.cy, 2, 10, 'a', 'w'));
                Driver.projs.add(new Projectile(this.cx, this.cy, 2, 10, 'a', 's'));
            }
            this.resetProjectileTimer();
            this.movementTimer = 0;
            calcDir(player);
        }
    }

    public void draw(Graphics g, Driver driver) {
        g.drawImage(boss1, this.cx - unitSize / 2, (this.cy - unitSize / 2) - ActiveMenu.iterationNum, unitSize,
                unitSize, driver);
    }
}