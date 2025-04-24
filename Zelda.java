import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
public class Zelda extends Entity{
    private BufferedImage image;
    public static boolean gameIsOver = false;
    private int textTimer = 0;
    private final int MTextTimer = 5;
    private String activeText = "";
    int stringIndex = 0;
    int activeIndex = 0;
    boolean forward = true;
    static boolean loadCredits = false;
    int Mdelay = 50;
    int delay = Mdelay;
    boolean textDelaying = false;
    private final String[] text = {
        "Thanks, " + Player.name + "!",
        "You're the hero of Hyrule.",
        "Finally, peace can return to Hyrule",
        "This ends the story"
    };
    public Zelda(int cx, int cy){
        this.cx = cx;
        this.cy = cy;
        try {
            image = ImageIO.read(new File("./Image files/zelda.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    public void drawGameOverText(Graphics g){
        g.setFont(new Font("Verdana",Font.PLAIN,25));
        g.setColor(new Color(255,255,255));
        if(activeText.equals(text[text.length-1])){
            loadCredits = true;
            System.out.println("made it here?");
            System.out.println(loadCredits);
        }
        //add the characters one by one
        if(gameIsOver && !loadCredits){
            if(textTimer <= 0 && forward && !textDelaying){
                activeText += text[activeIndex].substring(stringIndex,stringIndex+1);
                textTimer = MTextTimer;
                stringIndex++;
            }
            else if (textTimer <= 0 && !forward && !textDelaying){
                activeText  = activeText.substring(1,activeText.length());
                stringIndex--;
                textTimer = MTextTimer;
            } 
            else textTimer--;
            if(stringIndex >= text[activeIndex].length()){
                textDelaying = true;
                if(delay == 0){
                    forward = false;
                    delay = Mdelay;
                    textDelaying = false;
                }
                else delay--;
                System.out.println(textDelaying + "," + delay);
            }
            if(!forward && activeText.length() == 1){
                forward= true;
                activeIndex++;
                activeText = "";
                stringIndex--;
            }
            g.drawString(activeText,400 - activeText.length()*10,200);
        }
    }
    public void checkGameOver(Player p){
        if(hurtEntity(p)){
            hp += 999;
            gameIsOver = true;
            p.dir = 't';
            p.cx = cx-(int) (unitSize/1.5);
            p.cy = cy-unitSize;
        }
    }
    public void draw(Graphics g, Driver d){
        g.drawImage(image,cx,cy-unitSize,unitSize,(int) (unitSize*1.5),d);
    }
}   