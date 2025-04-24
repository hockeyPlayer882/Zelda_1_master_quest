import java.io.File;
public class ClassFilePurger{
    public static void main(String[] args){
        //current directory
        File directory = new File(".");
        purgeDirectory(directory);
    }
    public static void purgeDirectory(File directory){
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length;i++){
            if(files[i].getName().endsWith(".class")){
                files[i].delete();
            }
            else if(files[i].isDirectory()){
                purgeDirectory(files[i]);
            }
        }
        return;
    }
}