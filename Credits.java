import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
public class Credits{
    private static BufferedImage creditsBlock;
    static int time = 0;
    static int speed = 3;
    static Color activeColor = Color.BLUE;
    static final int MTime = 650;
    static String[] creditsTitles = {
        "Core developer...",
        "Spell checker...",
        "Bad code writer...",
        "Code checker...",
        "Weird code writer",
        "\"Artist\"...",
        "END OF ZELDA 1 MASTER QUEST",
        "Stats",
        "Steps walked...",
        "Damage Taken...",
        "Deaths taken...",
        "Rubpees collected..."
    };
    //Empty strings used for formatting
    static String[] creditsNames = {
        "Michael Middendorf",
        "Raine Bond",
        "Michael Middendorf",
        "Raine Bond",
        "Raine Bond",
        "Michael Middendorf",
        "",
        "",
        "" +Player.stepsWalked,
        "" +Player.damageTaken,
        "" +Player.deaths,
        "" + Player.rubpeesCollected

    };
    public static void loadCredits(){
        //TODO:: get background image for credits and load in text
        try {
            creditsBlock = ImageIO.read(new File("./Image files/creditsBlock.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void drawCredits(Graphics g, Driver d){
        g.setColor(Color.BLACK);
        g.fillRect(0,0,800,800);
        //draws the blocks
        for(int i = 0; i < 800/Entity.unitSize-1;i++){
            if( i < 7 || i >= 13)
                g.drawImage(creditsBlock,i*Entity.unitSize,50-time,Entity.unitSize,Entity.unitSize,d);
        }
        int numBlocks = 35;
        for(int i = 0; i < numBlocks;i++){
            for(int y = 0; y < 2;y++){
                g.drawImage(creditsBlock, y == 0 ? 0:(760-Entity.unitSize/2), 50+i*Entity.unitSize-time,Entity.unitSize,Entity.unitSize, d);
            }
            if(i == numBlocks-1){
                for(int h = 0; h < 800/Entity.unitSize;h++)
                    g.drawImage(creditsBlock,h*Entity.unitSize,(int) ((numBlocks-0.5)*Entity.unitSize-time-Entity.unitSize/2),Entity.unitSize,Entity.unitSize,d);
                
            }
        }
        g.setColor(Color.white);
        g.setFont(new Font("verdana", Font.PLAIN, 30));
        g.drawString("Staff",350,80-time);
        for(int i = 0; i < creditsTitles.length;i++){
            //pattern: blue->green-red
            activeColor = i%3 == 1? Color.GREEN:i%3 == 2 ? Color.RED:new Color(100,100,255);
            g.setColor(activeColor);
            g.drawString(creditsTitles[i],50,200+100*i-time);
            g.drawString(creditsNames[i],430,200+100*i-time);
        }
        if(time < MTime) time+=speed;
    }
}