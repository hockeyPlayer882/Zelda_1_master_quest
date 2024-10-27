import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

public class Menu {
    public boolean gameHasStarted;
    int saveFileSelected;
    String[] fileNames;
    int animDelay = 0;
    boolean isUpper = false;
    final int ManimDelay = 40;
    int[] linkStates = { 0, 0, 0 };
    String[][] keyBoard = {
        {"1","2","3","4","5","6","7","8","9","0"},
        {"q","w","e","r","t","y","u","i","o","p"},
        {"a","s","d","f","g","h","j","k","l", "A/a"},
        {"z","x","c","v","b","n","m","!","end", " "}

    };
    int[] keyBoardPos = {0,0};
    boolean registrating = false;
    public Menu(String[] fileNames) {
        gameHasStarted = false;
        saveFileSelected = 0;
        Player.isPaused = true;
        this.fileNames = fileNames;
    }

    public void moveArrow(boolean isUp) {
        if (isUp)
            saveFileSelected -= saveFileSelected == 0 ? -2 : 1;
        else
            saveFileSelected += saveFileSelected == 2 ? -2 : 1;
    }

    public void selectArrow(ActiveMenu m, Player p, Room room) {
        if(!registrating){
        boolean gameWasLoaded = false;
        Player.name = fileNames[saveFileSelected];
        if (saveFileSelected == 0 && !Driver.f1.name.equals("Empty. press Enter to write in a name")) {
            Driver.f1.loadGame(p);
            gameWasLoaded = true;
        }
        if (saveFileSelected == 1 && !Driver.f2.name.equals("Empty. press Enter to write in a name")) {
            gameWasLoaded = true;
            Driver.f2.loadGame(p);
        }
        if (saveFileSelected == 2 && !Driver.f3.name.equals("Empty. press Enter to write in a name")) {
            gameWasLoaded = true;
            Driver.f3.loadGame(p);
        }
        if (gameWasLoaded) {
            System.out.println(Player.level);
        if(Player.level != 0){
            Player.location[0] = 0;
            Player.location[1] = 0;
            room.spawnRoom(p);
            room.fillRoomArray(p);
            p.cx = 400;
            p.cy = 790;
            p.dir = 'w';
        }
            gameHasStarted = true;
            Player.isPaused = false;
            while (ActiveMenu.iterationNum > 0) {
                m.resumeGame(p, room);
            }
        }
        else{
            registrating = true;
            if(saveFileSelected == 0) Driver.f1.name = "";
            if(saveFileSelected == 1) Driver.f2.name = "";
            if(saveFileSelected == 3) Driver.f3.name = "";
        }
    }
    }
    
    public void draw(Graphics g, Player p, Driver d) {
        if (!gameHasStarted && !registrating) {
            Player.isPaused = true;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 800, 800);
            g.setFont(new Font("Helvetica", Font.PLAIN, 25));
            g.setColor(Color.RED);
            fileNames[0] = Driver.f1.name;
            fileNames[1] = Driver.f2.name;
            fileNames[2] = Driver.f3.name;
            for (int i = 0; i < 3; i++) {
                int x = 100;
                int y = (int) (i * 200) + 100;

                g.drawImage(Player.linkAnimations[0][linkStates[i]], x, y, d);
                g.drawString(fileNames[i], x + 80, y + 40);
                g.fillRect(x - 80, saveFileSelected * 200 + 120, 40, 20);

                if (animDelay <= 0){
                    linkStates[saveFileSelected] += linkStates[saveFileSelected] == 3 ? -3 : 1;
                    animDelay = ManimDelay;
                }
                animDelay--;


            }
        }
        else if(!gameHasStarted && registrating){
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 800, 800);
            g.setFont(new Font("Helvetica", Font.PLAIN, 40));
            g.setColor(Color.RED);
            fileNames[0] = Driver.f1.name;
            fileNames[1] = Driver.f2.name;
            fileNames[2] = Driver.f3.name;
            if (animDelay <= ManimDelay/2){

                g.fillRect(fileNames[saveFileSelected].length()*20+30,50,20,10);
                if(animDelay <= 0)
                    animDelay = ManimDelay;
            }
            animDelay--;
            g.drawString(fileNames[saveFileSelected],20,50);
            g.drawRect(keyBoardPos[1]*75+20,keyBoardPos[0]*90+370,40,40);
            for(int x = 0; x < keyBoard.length;x++){
                for(int y = 0; y < keyBoard[x].length;y++){
                    g.drawString(isUpper && !(x == 2 && y == 9)? keyBoard[x][y].toUpperCase():keyBoard[x][y],y*75+20,x*90+400);
                }
            }
        }
    }
    public void removeKey(){
        if(saveFileSelected == 0) Driver.f1.name = Driver.f1.name.substring(0,Driver.f1.name.length()-1);
        if(saveFileSelected == 1) Driver.f2.name = Driver.f2.name.substring(0,Driver.f2.name.length()-1);
        if(saveFileSelected == 2) Driver.f3.name = Driver.f3.name.substring(0,Driver.f3.name.length()-1);
    }
    public void addKey(Player p){
        if(keyBoardPos[0] == 2 && keyBoardPos[1] == 9){
            isUpper = !isUpper;
            return;
        }
        if(keyBoardPos[0] == 3 && keyBoardPos[1] == 8){
            registrating = false;
            System.out.println(Driver.f1.name);
            if(saveFileSelected == 0) Driver.f1.saveGame(p);
            if(saveFileSelected == 1) Driver.f2.saveGame(p);
            if(saveFileSelected == 2) Driver.f3.saveGame(p);
            fileNames[0] = Driver.f1.name;
            fileNames[1] = Driver.f2.name;
            fileNames[2] = Driver.f3.name;
            return;
        }
        if(saveFileSelected == 0) 
            Driver.f1.name += isUpper ? keyBoard[keyBoardPos[0]][keyBoardPos[1]].toUpperCase():keyBoard[keyBoardPos[0]][keyBoardPos[1]];
        if(saveFileSelected == 1) 
            Driver.f2.name += isUpper ? keyBoard[keyBoardPos[0]][keyBoardPos[1]].toUpperCase():keyBoard[keyBoardPos[0]][keyBoardPos[1]];
        if(saveFileSelected == 2) 
            Driver.f3.name += isUpper ? keyBoard[keyBoardPos[0]][keyBoardPos[1]].toUpperCase():keyBoard[keyBoardPos[0]][keyBoardPos[1]];
        System.out.println(keyBoardPos[0] + "," + keyBoardPos[1]);
    }
    public void changeKeyBoardSelection(char dir){
        switch(dir){
            case 'W':
                keyBoardPos[0] += keyBoardPos[0]-1 < 0 ? 3:-1;
                break;
            case 'S':
                keyBoardPos[0] += keyBoardPos[0]+1 > 3 ? -3:1;
                break;
            case 'A':
                keyBoardPos[1] += keyBoardPos[1]-1 < 0 ? 9:-1;
                break;
            case 'D':
                keyBoardPos[1] += keyBoardPos[1]+1 > 9 ? -9:1;
                break;
        }
    }
}