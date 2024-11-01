
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class saveFile {
    private TextFile saveWriter;
    private TextFile saveReader;
    public String name;
    private String path;

    public saveFile(String path, Player p) {
        this.path = path;
        name = searchFile("name");
        try {
            saveReader = new TextFile(path, "r");
            if (saveReader.readLine().equals("")) {
                name = "Empty. Press Enter to create a new save.";
                Player.name = name;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSave(Player p) {
        try {
            saveWriter = new TextFile(path, "w");
            name = "Empty. Press Enter to create a new save.";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    public String twoDtoString(boolean[][] arr) {
        String result = "";
        for (boolean[] a : arr) {
            result += "[ ";
            for (boolean b : a) {
                result += b + " ";
            }
            result += "] ";
        }
        return result;
    }

    public String searchFile(String key) {
        try {
            saveReader = new TextFile(path, "r");
        } catch (IOException e) {
            // TODO add support for seeking in the text file
            e.printStackTrace();
        }
        while (true) {
            String s = saveReader.readLineSafe();
            if (s.equals(""))
                break;
            String[] vals = s.split("[:]");
            if (vals[0].equals(key)) {
                saveReader.close();
                return vals[1].strip();
            }
        }
        return null;
    }

    public void saveGame(Player player) {
        try {

            final String n = "\n";
            saveWriter = new TextFile(path, "w");
            saveWriter.write("name:" + Player.name + n);
            saveWriter.write("rubpees:" + player.rubpees + n);
            saveWriter.write("keys:" + player.keys + n);
            saveWriter.write("bombs:" + player.bombs + n);
            saveWriter.write("keyArray:" + twoDtoString(LoadingZone.keyArray) + n);
            saveWriter.write("keyDoor:" + twoDtoString(LoadingZone.keyDoor) + n);
            saveWriter.write("triforces:" + ActiveMenu.numTriforcePieces + n);
            saveWriter.write("bombs:" + player.bombs + n);
            saveWriter.write("medicine:" + Player.medicine + n);
            saveWriter.write("maxHP:" + player.Mhp + n);
            saveWriter.write("candle:" + Player.hasCandle + n);
            saveWriter.write("bow:" + Player.hasBow + n);
            saveWriter.write("cane:" + Player.hasCane + n);
            saveWriter.write("boomerang:" + Player.hasBoomerang + n);
            saveWriter.write("arrows:" + Player.hasArrows + n);
            saveWriter.write("raft:" + Player.hasRaft + n);
            saveWriter.write("wand:" + Player.hasWand + n);
            saveWriter.write("superBomb:" + Player.hasSuperBomb + n);
            saveWriter.write("heartContainers:" + arrToString(LoadingZone.heartContainers) + n);
            saveWriter.write("numDefeatedBosses:" + LoadingZone.numDefeatedBosses + n);
            saveWriter.write("swordType:" + Sword.type + n);
            saveWriter.write("damage:" + Sword.damage + n);
            saveWriter.write("exists:" + twoDtoString(ActiveMenu.itemExists) + n);
            saveWriter.write("shield:" + player.shieldStrength + n);
            saveWriter.write("level:" + Player.level + n);
            saveWriter.write("superBombBlownRock:" + Obstacle.superBombBlownRock + n);
            saveWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String arrToString(boolean[] arr) {
        String returnS = "[ ";
        for (int x = 0; x < arr.length; x++) {
            returnS += arr[x] + " ";
        }
        return returnS + "]";
    }

    // Fine Michael... I'll make this a tangled mess too. The current engine makes it
    // hard not to... Wait who said the cheat codes *had* to be in driver.
    private void processCheatCodes(Player player) {
        // cheat codes for names... becuase why not?
        if (Player.name.equals("I am rich!"))
            player.rubpees += 999;
        else if (Player.name.equals("I like explosions!"))
            player.bombs += 4;
        else if (Player.name.equals("I am a pyromaniac!"))
            Player.hasCandle = true;
        else if (Player.name.equals("GIVE ME THE BOW,NOW!")) {
            Player.hasBow = true;
            Player.hasArrows = true;
        } 
        else if (Player.name.equals("INVINCIBLE!"))
            Player.hasCane = true;
        else if (Player.name.equals("SUPER OVERPOWERED!")) {
            Player.hasBow = true;
            Player.hasCane = true;
            Player.hasArrows = true;
            Player.hasSuperBomb = true;
            player.rubpees = 999;
            Sword.type = "metal";
            Sword.damage = 2;
            player.shieldStrength = 2;
            player.Mhp = 100;
            player.hp = 100;
            Player.medicine = "red";
            LoadingZone.numDefeatedBosses = 7;
            ActiveMenu.numTriforcePieces = 7;
            Player.hasCandle = true;
            Player.hasBoomerang = true;
            Player.hasRaft = true;
            Player.hasWand = true;
            player.bombs = 999;
            player.keys = 999;
        } 
        else if (Player.name.equals("BOOMERANG! YAY!"))
            Player.hasBoomerang = true;
        else if (Player.name.equals("I am sick!"))
            Player.medicine = "red";
        else if (Player.name.equals("METAL!!")) {
            player.shieldStrength = 2;
            Sword.type = "metal";
            Sword.damage = 2;
        } 
        else if (Player.name.equals("KEEEYS!"))
            player.keys += 999;
        else if (Player.name.equals("SUPERSTAR!")) {
            player.hp = 100;
            player.Mhp = 100;
        }
    }

    public void loadGame(Player p, Room room, ActiveMenu m) {        
        Player.name = name;
        p.hp = 6;
        p.cx = 400;
        p.inv = 1;
        p.cy = Player.level == 0 ? 400:700;
        final int[] startLocOverworld = {10,10};
        final int[] startLocDungeon = {0,0};
        p.dir = Player.level == 0 ? 's':'w';
        Player.location = Player.level == 0 ? startLocOverworld:startLocDungeon;
        room.spawnRoom(p);
        room.fillRoomArray(p);
        Player.isPaused = false;
        while (ActiveMenu.iterationNum > 0) {
            m.resumeGame(p, room);
        }
        Obstacle.superBombBlownRock = searchFile("superBombBlownRock").equals("true");
        LoadingZone.heartContainers = parseArray(searchFile("heartContainers"), 8);
        Player.hasCandle = searchFile("candle").equals("true");
        Player.medicine = searchFile("medicine");
        Player.hasBow = searchFile("bow").equals("true");
        Player.hasCane = searchFile("cane").equals("true");
        Player.hasBoomerang = searchFile("boomerang").equals("true");

        if (Player.hasBoomerang) {
            p.activeItem = "boomerang";
            Boomerang.setImage();
        }

        Player.hasArrows = searchFile("arrows").equals("true");
        Player.hasRaft = searchFile("raft").equals("true");
        Player.hasWand = searchFile("wand").equals("true");
        Player.hasSuperBomb = searchFile("superBomb").equals("true");

        p.Mhp = Integer.parseInt(searchFile("maxHP"));
        p.rubpees = Integer.parseInt(searchFile("rubpees").trim());
        p.keys = Integer.parseInt(searchFile("keys").trim());
        p.bombs = Integer.parseInt(searchFile("bombs"));

        LoadingZone.keyArray = parse2DArray(searchFile("keyArray"), 8);
        LoadingZone.keyDoor = parse2DArray(searchFile("keyDoor"), 8);
        ActiveMenu.numTriforcePieces = Integer.parseInt(searchFile("triforces"));
        LoadingZone.numDefeatedBosses = Integer.parseInt(searchFile("numDefeatedBosses"));

        Sword.type = searchFile("swordType");
        Sword.damage = Integer.parseInt(searchFile("damage"));

        p.shieldStrength = Integer.parseInt(searchFile("shield"));
        ActiveMenu.itemExists = parse2DArray(searchFile("exists"), 4);

        Player.level = Integer.parseInt(searchFile("level"));

        // Keep the legacy cheat code system!
        processCheatCodes(p);
    }

    public boolean[][] parse2DArray(String arr, int length) {
        Scanner scanner = new Scanner(arr);
        boolean[][] returnArr = new boolean[length][1];
        for (int i = 0; i < returnArr.length; i++) {
            String tok = scanner.next();
            if (!tok.equals("["))
                System.out.println("something went wrong...");

            ArrayList<String> parts = new ArrayList<String>();

            while (!tok.equals("]")) {
                tok = scanner.next();
                // Convert it to a single string.
                parts.add(tok);
            }
            returnArr[i] = parseArray(parts.toArray(new String[tok.length()]));
        }
        scanner.close();
        return returnArr;
    }

    public boolean[] parseArray(String[] arr) {
        boolean[] returnArr = new boolean[arr.length];
        for (int i = 0; i < arr.length; i++) {
            returnArr[i] = arr[i].equals("true");
        }
        return returnArr;
    }

    public boolean[] parseArray(String arr, int length) {
        if (arr == null)
            return new boolean[length];
        Scanner scanner = new Scanner(arr);
        ArrayList<String> parts = new ArrayList<String>();
        String tok = scanner.next();
        while (!tok.equals("]")) {
            tok = scanner.next();
            // Convert it to a single string.
            parts.add(tok);
        }
        scanner.close();
        return parseArray(parts.toArray(new String[length]));
    }
}