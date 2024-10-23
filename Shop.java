import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import neozelda.AudioEngine;

public class Shop {
    private static BufferedImage bomb;
    private static BufferedImage bow;
    private static BufferedImage candle;
    private static BufferedImage heart;
    private static BufferedImage metalShield;
    public static BufferedImage blueMedicine;
    public static BufferedImage redMedicine;
    public static BufferedImage woodenShield;
    private static String item1;
    private int cost1;
    private String item2;
    private int cost2;
    private String item3;
    private int cost3;

    public Shop(String Item1, String item2, String item3, int cost1, int cost2, int cost3) {
        item1 = Item1;
        this.item2 = item2;
        this.item3 = item3;
        this.cost1 = cost1;
        this.cost2 = cost2;
        this.cost3 = cost3;
        try {
            if (bomb == null)
                bomb = ImageIO.read(new File("./Image files/bomb.png"));
            if (bow == null)
                bow = ImageIO.read(new File("./Image files/bow.png"));
            if (candle == null)
                candle = ImageIO.read(new File("./Image files/candle.png"));
            if (heart == null)
                heart = ImageIO.read(new File("./Image files/fullHeart.png"));
            if (metalShield == null)
                metalShield = ImageIO.read(new File("./Image files/shieldMETAL.png"));
            if (woodenShield == null)
                woodenShield = ImageIO.read(new File("./Image files/woodenShield.png"));
            if (blueMedicine == null)
                blueMedicine = ImageIO.read(new File("./Image files/blueMedicine.png"));
            if (redMedicine == null)
                redMedicine = ImageIO.read(new File("./Image files/redMedicine.png"));
        } catch (IOException ex) {

            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }

    public void drawShopPrices(Graphics g) {
        g.setColor(Color.WHITE);
        if (this.cost1 != 0) {
            Integer cost1st = this.cost1;
            g.drawString(cost1st.toString(), 200, 500 + ActiveMenu.iterationNum);
        }
        if (this.cost2 != 0) {
            Integer cost2st = this.cost2;
            g.drawString(cost2st.toString(), 400, 500 + ActiveMenu.iterationNum);
        }
        if (this.cost3 != 0) {
            Integer cost3st = this.cost3;
            g.drawString(cost3st.toString(), 600, 500 + ActiveMenu.iterationNum);
        }
    }

    public void drawShopItems(Graphics g, Driver driver) {
        if (item1.equals("bomb"))
            g.drawImage(bomb, 190, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item1.equals("metal shield"))
            g.drawImage(metalShield, 190, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item1.equals("wooden shield"))
            g.drawImage(woodenShield, 190, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        if (this.item2.equals("bow"))
            g.drawImage(bow, 390, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item2.equals("heart"))
            g.drawImage(heart, 390, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item2.equals("blue medicine"))
            g.drawImage(blueMedicine, 390, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item2.equals("red medicine"))
            g.drawImage(redMedicine, 390, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        if (this.item3.equals("candle"))
            g.drawImage(candle, 590, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item3.equals("heart"))
            g.drawImage(heart, 590, 420 + ActiveMenu.iterationNum, 38, 48, driver);
        else if (item3.equals("bomb"))
            g.drawImage(bomb, 590, 420 + ActiveMenu.iterationNum, 38, 48, driver);
    }

    public void buyItems(Player player) {
        // checks if the player hits the first item
        if (190 < player.cx + Player.unitSize / 2
                && 190 > player.cx - Player.unitSize / 2
                && 420 + Player.unitSize < player.cy + Player.unitSize / 2
                && 420 + Player.unitSize > player.cy - Player.unitSize / 2 && player.rubpees >= this.cost1
                && cost1 != 0) {
            player.rubpees -= this.cost1;
            AudioEngine.playHighlight("./Sound files/got_item_z1.wav");
            cost1 = 0;
            item1 = "";
            if (Player.location[0] == 9 && Player.location[1] == 11) {
                player.bombs += 4;
            } else if (Player.location[0] == 6 && Player.location[1] == 12) {
                // doubles the player's shield strength(gives the player the metal shield)
                player.shieldStrength = 2;
            } else if (Player.location[0] == 16 && Player.location[1] == 11) {
                // sets the player's shield strength to 1("fixes" the wooden shield
                player.shieldStrength = 1;
            }
        }
        if (390 < player.cx + Player.unitSize / 2
                && 390 > player.cx - Player.unitSize / 2
                && 420 + Player.unitSize < player.cy + Player.unitSize / 2
                && 420 + Player.unitSize > player.cy - Player.unitSize / 2 && player.rubpees >= this.cost2
                && cost2 != 0) {
            player.rubpees -= this.cost2;
            AudioEngine.playHighlight("./Sound files/got_item_z1.wav");
            cost2 = 0;
            item2 = "";
            if (Player.location[0] == 9 && Player.location[1] == 11) {
                if (!Player.hasBow)
                    Player.hasBow = true;
                else
                    player.heal(2);
            } else if (Player.location[0] == 6 && Player.location[1] == 12) {
                if (!Player.medicine.equals("blue") || !Player.medicine.equals("red"))
                    Player.medicine = "blue";
            } else if (Player.location[0] == 16 && Player.location[1] == 11)
                if (!Player.medicine.equals("red"))
                    Player.medicine = "red";
        }
        // checks if the player hit the 3rd item
        if (590 < player.cx + Player.unitSize / 2
                && 590 > player.cx - Player.unitSize / 2
                && 420 + Player.unitSize < player.cy + Player.unitSize / 2
                && 420 + Player.unitSize > player.cy - Player.unitSize / 2 && player.rubpees >= this.cost3
                && cost3 != 0) {
            player.rubpees -= this.cost3;
            AudioEngine.playHighlight("./Sound files/got_item_z1.wav");
            cost3 = 0;
            item3 = "";
            if (Player.location[0] == 9 && Player.location[1] == 11) {
                if (!Player.hasCandle)
                    Player.hasCandle = true;
                else
                    player.heal(2);
            } else if (Player.location[0] == 6 && Player.location[1] == 12) {
                player.heal(2);
            } else if (Player.location[0] == 16 && Player.location[1] == 11) {
                player.bombs += 4;
            }
        }
    }
}