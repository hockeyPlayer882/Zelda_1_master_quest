
import java.awt.image.*;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DarkSword extends Entity {
    private BufferedImage darkSwordW;
    private BufferedImage darkSwordS;
    private BufferedImage darkSwordA;
    private BufferedImage darkSwordD;
    private int height;
    private int width;
    public DarkSword(Boss7 boss7) {
        this.speed = 8;
        this.damage = 10;
        switch (boss7.dir) {
            case 'w':
                this.dir = 'w';
                this.cy = boss7.cy - unitSize - unitSize / 2;
                this.cx = boss7.cx;
                break;
            case 's':
                this.dir = 's';
                this.cy = (boss7.cy + unitSize / 2) + unitSize / 2;
                this.cx = boss7.cx;
                break;
            case 'a':
                this.dir = 'a';
                this.cx = boss7.cx - unitSize - unitSize / 2;
                this.cy = boss7.cy;
                break;
            case 'd':
                this.dir = 'd';
                this.cx = boss7.cx + unitSize;
                this.cy = boss7.cy;
                break;
        }
        height = dir == 'a' || dir == 'd' ? 40:60;
        width = dir == 'a' || dir == 'd' ? 60:40;
        try {
            darkSwordW = ImageIO.read(new File(".\\Image files\\darkSwordW.png"));
            darkSwordS = ImageIO.read(new File(".\\Image files\\darkSwordS.png"));
            darkSwordA = ImageIO.read(new File(".\\Image files\\darkSwordA.png"));
            darkSwordD = ImageIO.read(new File(".\\Image files\\darkSwordD.png"));
        } catch (IOException ex) {
            System.out.println("FAILURE, YOU ARE STOOOOOOOOOOOOOOOOOOPID WITH IMAGES! GET BETTER AT JAVA YOU IDIOT!");
            System.out.println("Error details: ");
            ex.printStackTrace();
        }
    }
    public void hurtPlayer (Player player) {
        if (player.cx - unitSize / 2 < this.cx + this.width / 2
              && player.cx + unitSize / 2 > this.cx - this.width / 2
              && player.cy - unitSize / 2 < this.cy + this.height / 2
              && player.cy + unitSize / 2 > this.cy - this.height / 2
              && player.inv <= 0) {
           // hurt player based on damage calculations
           player.hp -= (int) (damage / (player.defense + 1));
           player.inv = 60;
           //Poor OOP :( should have made this Player.poisonTimer
           Snake.poisonTimer = Snake.MpoisonTimer*2;

        }
     }
    public void move(){
        super.moveEntity();
        if(cx > 800+unitSize/2 || cx < 60 || cy > 800+unitSize/2 || cy < 100-unitSize/2)
            Boss7.darkSwords.remove(this);
    }
    public void draw(Driver driver, Graphics g){
        g.drawImage(dir == 'w' ? darkSwordW: dir == 's' ? darkSwordS: dir == 'a' ? darkSwordA:darkSwordD,cx,cy, width,height,driver);
    }
}