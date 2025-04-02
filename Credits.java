import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Font;
public class Credits{
    private static BufferedImage creditsImage;
    static int time = 0;
    static int speed = 5;

    public static void loadCredits(){
        //TODO:: get background image for credits and load in text
        try {
            creditsImage = ImageIO.read(new File(".\\Image files\\creditsImage.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void drawCredits(Graphics g, Driver d){
        g.drawImage(creditsImage,0,time,800,50000,d);
    }
}