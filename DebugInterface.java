public class DebugInterface {
   private static boolean linked = false;
   
   public static void debugInit(boolean enabled) {
      try {
         Patcher.debug_init(enabled);
         linked = true;
      }
      catch (NoClassDefFoundError ie) {
         System.out.println("Profiler not available! Continuing...");
         linked = false;
      }
   }
   
   public static void debugMain() {
      if (linked)
         Patcher.debug_main();
   }
   
   public static void startSeg(String name) {
      if (linked)
         Patcher.seg_start(name);
   }
   
   public static void segInstant(String id) {
      if (linked)
         Patcher.seg_instant(id);
   }
   
   public static void endSeg() {
      if (linked)
         Patcher.seg_end();
   }
}