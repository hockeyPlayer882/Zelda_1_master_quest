import java.awt.Color;
import java.awt.Graphics;

import neozelda.audio.AudioEngine;

import java.awt.Font;
import java.awt.FontMetrics;

public class Menu {
    public boolean gameHasStarted;
    int saveFileSelected;
    String[] fileNames;
    int animDelay = 0;
    boolean isUpper = false;
    final int ManimDelay = 40;
    int[] linkStates = { 0, 0, 0 };
    
    String[][] keyBoard = {
        { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" },
        { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p" },
        { "a", "s", "d", "f", "g", "h", "j", "k", "l", "A/a" },
        { "z", "x", "c", "v", "b", "n", "m", "!", "↵", "_" }

    };

    int[] keyBoardPos = { 0, 0 };
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
        if (!registrating) {
            String emptySaveName = "Empty. Press Enter to create a new save.";

            boolean gameWasLoaded = false;
            Player.name = fileNames[saveFileSelected];
            if (saveFileSelected == 0 && !Driver.f1.name.equals(emptySaveName)) {
                Driver.f1.loadGame(p);
                gameWasLoaded = true;
            }
            if (saveFileSelected == 1 && !Driver.f2.name.equals(emptySaveName)) {
                Driver.f2.loadGame(p);
                gameWasLoaded = true;
            }
            if (saveFileSelected == 2 && !Driver.f3.name.equals(emptySaveName)) {
                Driver.f3.loadGame(p);
                gameWasLoaded = true;
            }

            if (gameWasLoaded) {
                System.out.println(Player.level);
                if (Player.level != 0) {
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
            } else {
                registrating = true;
                if (saveFileSelected == 0)
                    Driver.f1.name = "";
                if (saveFileSelected == 1)
                    Driver.f2.name = "";
                if (saveFileSelected == 3)
                    Driver.f3.name = "";
            }
        }
    }

    public void draw(Graphics g, Player p, Driver d) {
        // Render standard menu?
        if (!gameHasStarted && !registrating)
            drawSaveSelectMenu(g, p, d);
        else if (!gameHasStarted && registrating)
            drawNewSaveMenu(g, p, d);
    }

    private void drawSaveSelectMenu(Graphics g, Player p, Driver d) {
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

            // Do the link rotating effect.
            if (animDelay <= 0) {
                linkStates[saveFileSelected] += linkStates[saveFileSelected] == 3 ? -3 : 1;
                animDelay = ManimDelay;
            }
            animDelay--;

        }
    }

    private void drawNewSaveMenu(Graphics g, Player p, Driver d) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 800);
        g.setFont(new Font("Helvetica", Font.PLAIN, 40));
        g.setColor(Color.RED);

        fileNames[0] = Driver.f1.name;
        fileNames[1] = Driver.f2.name;
        fileNames[2] = Driver.f3.name;

        // BUGFIX: Make Michael's cursor work properly.
        FontMetrics metrics = g.getFontMetrics();

        // Draw the blinking cursor.
        if (animDelay <= ManimDelay / 2) {
            g.fillRect(metrics.stringWidth(fileNames[saveFileSelected]) + 24, 50, 20, 10);

            if (animDelay <= 0)
                animDelay = ManimDelay;
        }

        // Draw the currently selected name.
        animDelay--;
        g.drawString(fileNames[saveFileSelected], 20, 50);

        // Draw the selected key.
        // TODO: Some offset math is not working properly!
        g.drawRect(keyBoardPos[1] * 75 + 20, keyBoardPos[0] * 90 + 370, 40, 40);
        
        // Draw the keyboard.
        for (int x = 0; x < keyBoard.length; x++) {
            for (int y = 0; y < keyBoard[x].length; y++) {
                g.drawString(isUpper && !(x == 2 && y == 9) ? keyBoard[x][y].toUpperCase() : keyBoard[x][y],
                        y * 75 + 30, x * 90 + 405);
            }
        }
    }

    // Michael.... you were supposed to be getting better at writing code ;-;
    public void removeKey() {
        if (saveFileSelected == 0 && Driver.f1.name.length() != 0)
            Driver.f1.name = Driver.f1.name.substring(0, Driver.f1.name.length() - 1);
        else if (saveFileSelected == 1 && Driver.f2.name.length() != 0)
            Driver.f2.name = Driver.f2.name.substring(0, Driver.f2.name.length() - 1);
        else if (saveFileSelected == 2 && Driver.f3.name.length() != 0)
            Driver.f3.name = Driver.f3.name.substring(0, Driver.f3.name.length() - 1);
        else
            return;

        // Only play the sfx if any character was actually added.
        AudioEngine.playClip("./sfx/LTTP_LowHealth.wav");
    }

    public void addKey(Player p) {
        String kLookUp = keyBoard[keyBoardPos[0]][keyBoardPos[1]];

        // Shift key.
        if (kLookUp.equals("A/a")) {
            isUpper = !isUpper;
            return;
        }

        // Enter key (finish registration).
        else if (kLookUp.equals("↵")) {
            registrating = false;
            System.out.println(Driver.f1.name);

            if (saveFileSelected == 0)
                Driver.f1.saveGame(p);
            if (saveFileSelected == 1)
                Driver.f2.saveGame(p);
            if (saveFileSelected == 2)
                Driver.f3.saveGame(p);
            
            fileNames[0] = Driver.f1.name;
            fileNames[1] = Driver.f2.name;
            fileNames[2] = Driver.f3.name;
            return;
        }

        // Underscore actually represents a space.
        if (kLookUp.equals("_"))
            kLookUp = " ";

        // Add keys.
        if (saveFileSelected == 0)
            Driver.f1.name += isUpper ? kLookUp.toUpperCase() : kLookUp;
        if (saveFileSelected == 1)
            Driver.f2.name += isUpper ? kLookUp.toUpperCase() : kLookUp;
        if (saveFileSelected == 2)
            Driver.f3.name += isUpper ? kLookUp.toUpperCase() : kLookUp;
        
        System.out.println(keyBoardPos[0] + "," + keyBoardPos[1]);
        AudioEngine.playClip("./sfx/LTTP_LowHealth.wav");
    }

    public void changeKeyBoardSelection(char dir) {
        switch (dir) {
            case 'W':
                keyBoardPos[0] += keyBoardPos[0] - 1 < 0 ? 3 : -1;
                break;
            case 'S':
                keyBoardPos[0] += keyBoardPos[0] + 1 > 3 ? -3 : 1;
                break;
            case 'A':
                keyBoardPos[1] += keyBoardPos[1] - 1 < 0 ? 9 : -1;
                break;
            case 'D':
                keyBoardPos[1] += keyBoardPos[1] + 1 > 9 ? -9 : 1;
                break;
        }  
    }
}