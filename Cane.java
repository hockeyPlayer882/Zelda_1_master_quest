import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;

public abstract class Cane {
    public static BufferedImage cane;
    public static boolean isActive = false;
    public static final int MactiveTimer = 90;
    public static int activeTimer = 0;
    public static int coolDownTimer = 0;
    public static final int McoolDown = 79;
    public static BufferedImage star;
    public static int starCX[] = new int[5];
    public static int starCY[] = new int[5];
    public static double starDeg[] = new double[5];

    /**
     * inits the images
     */
    public static void setImages() {
        try {
            if (cane == null) {
                cane = ImageIO.read(new File("./Image files/invincibility.png"));
                star = ImageIO.read(new File("./Image files/star.png"));
            }
        } catch (IOException ex) {

            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("When Java doesn't know how to Java .-.");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }

    /**
     * draws the cool down box (and the star particles that sorround the invisible
     * player)
     * 
     * @param g      graphics panel
     * @param driver the driver (for drawing the star images)
     */
    public static void drawCoolDownBox(Graphics g, Driver driver) {
        if (isActive && !Player.isPaused) {
            for (int i = 0; i < 5; i++)
                g.drawImage(star, starCX[i], starCY[i], Entity.unitSize / 2, Entity.unitSize / 2, driver);
        }
        g.setColor(Color.BLACK);
        g.fillRect(331, 31 + 79 - coolDownTimer + ActiveMenu.iterationNum, 39, coolDownTimer);
    }

    public static void activate(Player player) {
        if (coolDownTimer == 0) {
            isActive = true;
            activeTimer = MactiveTimer;
            coolDownTimer = McoolDown;
            Snake.poisonTimer = MactiveTimer;
        }
        for (int i = 0; i < 5; i++) {
            /*  MATH THINGIES FOR CIRCLE
             formula for X points, cx = middle point's cx + (radius * math.cos(degree))
             formula for Y points, cy = middle point's cy + (radius * math.sin(degree))
            */
            starDeg[i] = i + 1;
            starCX[i] = player.cx - Entity.unitSize / 4 + (int) ((Entity.unitSize / 1.5) * Math.cos(starDeg[i] * 36));
            starCY[i] = player.cy + (int) ((Entity.unitSize / 1.5) * Math.sin(starDeg[i] * 36));
        }
    }

    public static void doThings(Player player) {
        if (!Player.isPaused && player.activeItem.equals("cane")) {
            if (activeTimer > 0 && isActive) {
                player.inv = player.inv == 2 ? 2 : 3;
                
                activeTimer--;
                for (int i = 0; i < 5; i++) {
                    // MATH THINGIES FOR CIRCLE
                    // formula for X points, cx = middle point's cx + (radius * math.cos(degree))
                    // formula for Y points, cy = middle point's cy + (radius * math.sin(degree))
                    // ^^ Bro's proud of his trig. ^^
                    starDeg[i] += 0.5;
                    starCX[i] = player.cx - Entity.unitSize / 4
                            + (int) ((Entity.unitSize / 1.5) * Math.cos(starDeg[i] * 36));
                    starCY[i] = player.cy + (int) ((Entity.unitSize / 1.5) * Math.sin(starDeg[i] * 36));
                }
            } else
                deactivate(player);
            if (coolDownTimer > 0 && activeTimer <= 0)
                coolDownTimer--;
        }
    }

    public static void deactivate(Player player) {
        if (isActive && activeTimer <= 0) {
            player.inv = 0;
            isActive = false;
        }
    }
}