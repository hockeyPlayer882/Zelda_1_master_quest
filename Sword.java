public class Sword extends Entity{
public int width;
public int height;
public static int damage;
Sword(){
damage = 1;
this.cx = 999;
this.cy = 999;
}
//keeps track of what type of sword is being used, wooden is the sword obtained at the start of the game, followed by the metal sword and lastly the magic sword
public static String type = "No_Sword";
public void spawnSword(Player player){
if(!type.equals("No_Sword")){
switch(player.stDir){
case 'w':
this.dir = 'w';
this.cy = player.cy-unitSize - unitSize/2;
this.cx = player.cx;
break;
case 's':
this.dir = 's';
this.cy = (player.cy+unitSize/2)+unitSize/2;
this.cx = player.cx;
break;
case 'a':
this.dir = 'a';
this.cx = player.cx-unitSize - unitSize/2;
this.cy = player.cy;
break;
case 'd':
this.dir = 'd';
this.cx = player.cx+unitSize;
this.cy = player.cy;
break;
}
}
}

public void despawn(){
this.cx = 9999;
this.cy = 9999;
}

}