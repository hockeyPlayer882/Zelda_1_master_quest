import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
public class Menu{
    public boolean gameHasStarted;
    int saveFileSelected;
    String[] fileNames;
    int[] linkStates = {0,0,0};
    public Menu(String[] fileNames){
        gameHasStarted = false;
        saveFileSelected = 0;
        Player.isPaused = true;
        this.fileNames = fileNames;
    }
    public void moveArrow(boolean isUp){
        if(isUp) saveFileSelected -= saveFileSelected == 0 ? -2:1;
        else saveFileSelected += saveFileSelected == 2 ? -2:1;
    }
    public void selectArrow(ActiveMenu m, Player p, Room room){
        Player.name = fileNames[saveFileSelected];
        gameHasStarted = true;
        Player.isPaused = false;
        while(ActiveMenu.iterationNum > 0){
            m.resumeGame(p, room);
        }
    }
    public void draw(Graphics g, Player p, Driver d){
        if(!gameHasStarted){
            Player.isPaused = true;
            g.setColor(Color.BLACK);
            g.fillRect(0,0,800,800);
            g.setFont(new Font("Helivetica",25,25));
            g.setColor(Color.RED);
            for(int i = 0; i < 3;i++){
                int x = 100;
                int y =(int) (i*200)+100;
                g.drawImage(Player.linkAnimations[0][linkStates[i]],x,y,d);
                g.drawString(fileNames[i],x+80,y+40);
                g.fillRect(x-80,saveFileSelected*200+120,40,20);
                if(System.currentTimeMillis() %10 == 0)
                    linkStates[saveFileSelected] += linkStates[saveFileSelected] == 3 ? -3:1;

            }
        }
    }
}