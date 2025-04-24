public class SpeedTimer extends Thread{
    public static int seconds;
    public static int minutes;
    public static long hours;
    public static boolean ticking = true;
    public void increment(){
        if(!Player.isPaused){
            seconds++;
            if(seconds >= 60){
                seconds = 0;
                minutes++;
            }
            if(minutes >= 60){
                minutes=0;
                hours++;
            }

        }
    }
    public void run(){
        while(true){
            if(ticking){
                increment();
                try {
                    sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void setTime(String in){
        String[] arr = in.split(";");
        hours = Long.parseLong(arr[0]);
        minutes = Integer.parseInt(arr[1]);
        seconds = Integer.parseInt(arr[2]);
    }
    public static String ToString(){
        //using semicolons becuase save file parses with colons
        return hours + ";" + minutes + ";" + seconds;
    }
    public static String ToStringWCorrectFormat(){
        return hours + ":" + minutes + ":" + seconds;
    }
    //printing thingy
    public String toString(){
        return ToString();
    }
}