import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Explosion extends Entity {
    private int lifetime = 60;
    public BufferedImage explosion;

    public Explosion(int cx, int cy) {
        this.cx = cx;
        this.damage = 1;
        this.cy = cy;
        try {
            explosion = ImageIO.read(new File("./Image files/explosion.png"));
        } catch (IOException ex) {
            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }
    public void drawExplosion(Graphics g, Driver driver) {
        if (this.lifetime > 0) {
            g.drawImage(explosion, this.cx, this.cy+ActiveMenu.iterationNum, Entity.unitSize, Entity.unitSize, driver);
            if (!Player.isPaused)
                lifetime -= 1;
        } else
            Driver.explosions.remove(this);
    }
}