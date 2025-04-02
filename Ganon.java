import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Ganon extends Entity {
    // Ganon manifested sans and turned into a bullet hell boss
    public static boolean isDefeated = false;
    private int width = 160;
    int height = 160;
    private static BufferedImage waiting;
    private static BufferedImage healing;
    private static BufferedImage hurt;
    private static BufferedImage charging;
    private static BufferedImage attacking;
    private static BufferedImage OP;
    private static BufferedImage activeImage;
    private boolean spawningProjectilesHorizontally = false;
    private int projX = 0;
    private int xSafeSpot = 0;
    private boolean spawningProjectilesVertically = false;
    private int projY = 0;
    private int ySafeSpot = 0;
    private final int MteleportTimer = 80;
    private int teleportTimer = MteleportTimer;
    private int MhurtTimer = 80;
    private int hurtTimer = 0;
    private int projDelay;
    private int minHP = 20;
    private final int MprojDelay = 4;
    boolean hasCharged = false;
    Random r = new Random();
    int phaseTimer;
    int MphaseTimer = 80;

    public Ganon() {
        //TODO::: rebuff hp to normal level
        super(0, 'n', 400, 400, 2, 20, 1, 12, 10, 10, 1);
        phaseTimer = MphaseTimer;
        projDelay = MprojDelay;
        try {
            waiting = ImageIO.read(new File(".\\Image files\\ganon waiting.png"));
            healing = ImageIO.read(new File(".\\Image files\\ganon healing.png"));
            hurt = ImageIO.read(new File(".\\Image files\\ganon hurt.png"));
            charging = ImageIO.read(new File(".\\Image files\\ganon charging.png"));
            attacking = ImageIO.read(new File(".\\Image files\\ganon attacking.png"));
            OP = ImageIO.read(new File(".\\Image files\\ganon OP.png"));
        } catch (IOException e) {
            System.out.println("You a failure with images....");
            e.printStackTrace();
        }

        activeImage = waiting;

    }

    public void controlPhases() {
        phaseTimer--;
        if (phaseTimer == 0) {
            switch (r.nextInt(3)) {
                case 0:
                    // prevents the fight from being restarted by capping how much he can heal by
                    if (minHP + 2 == hp){
                        hp--;
                        controlPhases();
                        System.out.println("healing cap reached");
                    }
                    activeImage = healing;
                    heal(1);
                    break;
                case 1:
                    if (r.nextBoolean()) {
                        spawningProjectilesHorizontally = true;
                        xSafeSpot = (40 + r.nextInt(700 / 40) * 40);
                    } else {
                        spawningProjectilesVertically = true;
                        ySafeSpot = (40 + r.nextInt(700 / 40) * 40);
                    }
                    activeImage = attacking;
                    break;
                case 2:
                    activeImage = hasCharged ? OP : charging;
                    if (hasCharged) {

                        spawningProjectilesHorizontally = true;
                        xSafeSpot = (40 + r.nextInt(700 / 40) * 40);
                        spawningProjectilesVertically = true;
                        ySafeSpot = (40 + r.nextInt(700 / 40) * 40);
                        hasCharged = false;
                    } else
                        hasCharged = true;
            }
            phaseTimer = MphaseTimer;
        }
    }

    public void spawnProjectilesHorizontally() {
        if (spawningProjectilesHorizontally) {
            if (projDelay == 0) {
                if (projX != xSafeSpot && projX != xSafeSpot + 40)
                    Driver.projs.add(
                            new Projectile(projX, 140, 6, 3, 'w', (800 - projX) / 10000.0, (800 - projX) / MprojDelay));
                projX += 40;
                if (projX > 800) {
                    projX = 40;
                    spawningProjectilesHorizontally = false;
                }
            }
        }
    }

    public void spawnProjectilesVertically() {
        if (spawningProjectilesVertically) {
            if (projDelay == 0) {
                if (projY != ySafeSpot && projY != ySafeSpot + 40)
                    Driver.projs.add(
                            new Projectile(0, projY, 6, 3, 'd', (800 - projY) / 10000.0, (800 - projY) / MprojDelay));
                projY += 40;
                if (projY > 800) {
                    projY = 40;
                    spawningProjectilesVertically = false;
                }
            }
        }
    }

    public boolean hurtEntity(Player p) {
        if (p.cx - unitSize / 2 < cx + width / 2
                && p.cx + unitSize / 2 > cx - width / 2
                && p.cy - unitSize / 2 < cy + height / 2
                && p.cy + unitSize / 2 > cy - height / 2
                && p.inv <= 0) {
            p.hp -= damage;
            p.inv = 60;
            return true;
        }
        return false;
    }

    public void hurtEntity(Sword sword) {
        if (activeImage.equals(hurt)) {
            if (cx - width / 2 < sword.cx + sword.width / 2
                    && cx + width / 2 > sword.cx - sword.width / 2
                    && cy - height / 2 < sword.cy + sword.height / 2
                    && cy + height / 2 > sword.cy - sword.height / 2
                    && this.inv <= 0) {
                // hurt entity based on damage calculations
                this.hp -= (int) (Sword.damage / (this.defense + 1));
                this.inv = 60;
                // update minHP respectively
                if (hp < minHP)
                    minHP = hp;
                    System.out.println(hp + "," + minHP);
                entityHitCallback();
            }
        }
    }

    public void drawHealthBar(Graphics g, Driver d) {
        g.setColor(Color.GREEN);

        g.drawRect(0, 120, 800, 20);
        g.setColor(Color.RED);
        for (int i = 0; i < hp; i++) {
            g.fillRect(i * 40, 120, 39, 20);
        }
    }

    public void teleport(Player p) {
        teleportTimer--;
        if (teleportTimer == 0) {
            teleportTimer = MteleportTimer;
            cx = r.nextInt(width, 800 - width);
            cy = r.nextInt(height + 120, 700);
            // make sure bro doesn't spawn on the player
            while (hurtEntity(p)) {
                cx = r.nextInt(width, 800 - width);
                cy = r.nextInt(height + 120, 700);
                // undo the damage done to the player...
                p.hp += damage;
                p.inv = 0;
            }

        }
    }

    public void manipulateProjDelay() {
        if (projDelay == 0)
            projDelay = MprojDelay;
        projDelay--;
    }

    public void exist(Graphics g, Driver d, Player p, Sword sword) {
        if (!Player.isPaused) {
            if (hp > 0) {
                if (hurtTimer <= 0) {
                    controlPhases();
                    teleport(p);
                } else
                    hurtTimer--;
                hurtEntity(sword);

                if (inv % 2 == 0)
                    draw(g, d);
                hurtEntity(p);
                drawHealthBar(g, d);
                HitWithArrow(p);
                decreaseInv();
                spawnProjectilesHorizontally();
                spawnProjectilesVertically();
                manipulateProjDelay();
            } else {
                isDefeated = true;
                despawn();
                LoadingZone.currentRoomBlock[3] = 0;
                Driver.projs.clear();
            }

        }
    }

    public void HitWithArrow(Player p) {
        if (cx - width / 2 < Arrow.cx + Arrow.width / 2
                && cx + width / 2 > Arrow.cx - Arrow.width / 2
                && cy - width / 2 < Arrow.cy + Arrow.height / 2
                && cy + width / 2 > Arrow.cy - Arrow.height / 2) {
            if (hurtTimer == 0) {
                activeImage = hurt;
                hurtTimer = MhurtTimer;
            }
            if (hurtTimer < 5 && hurtTimer != 0)
                teleportTimer = 0;
            teleport(p);
            hurtTimer = 0;
            Arrow.cx = 800;
            Arrow.cy = 800;
            Arrow.dir = 'd';
        }
    }

    public void draw(Graphics g, Driver d) {
        g.drawImage(activeImage, cx - width / 2, cy - height / 2, width, height, d);
    }
}