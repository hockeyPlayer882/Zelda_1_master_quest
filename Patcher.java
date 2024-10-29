import java.util.HashMap;

import util.Counter;

public class Patcher {
   static HashMap<String, HashMap<String, Long>> profilerCounters;
   static HashMap<String, HashMap<String, Long>> profilerMaxValues;
   static String currentSegment = "";
   static Counter instantCounter;
   static Counter segCounter;
   static Counter totalFrameCounter;
   static boolean enabled;

   // 300 frames until each print.
   static final int MAX_DELTA = 90;
   static int curCounter = 0;

   /**
    * Initialize the profiling counters for debug processing.
    */
   public static void debug_init(boolean shouldEnable) {
      profilerCounters = new HashMap<>();
      profilerMaxValues = new HashMap<>();
      instantCounter = new Counter();
      segCounter = new Counter();
      totalFrameCounter = new Counter();

      enabled = shouldEnable;
   }

   /**
    * Analyze the captured debug output.
    */
   public static void debug_main() {
      if (!enabled) return;

      curCounter = (curCounter + 1) % MAX_DELTA;

      int ms = totalFrameCounter.elapsedms();
      totalFrameCounter.reset();

      if (curCounter != 15 && ms < 24)
         return;

      Runtime runtime = Runtime.getRuntime();
      long usedMemory = runtime.totalMemory() - runtime.freeMemory();

      // Only print profiling information if the game is lagging or if the timer went off.
      System.out.println("******************** PROFILE DATA ********************");
      System.out.println("Total time for the frame: " + ms + " ms\n");
      System.out.println("Heap Usage: " + (usedMemory / 1048576) + " / " + (runtime.totalMemory() / 1048576) + " MiB");

      for (String seg : profilerCounters.keySet()) {
         HashMap<String, Long> counters = profilerCounters.get(seg);

         // Determine the total segment execution time.
         long execTime = counters.get("TOTAL EXECUTION TIME");
         counters.remove("TOTAL EXECUTION TIME");

         // Ensure we have somewhere to pull max values from.
         if (!profilerMaxValues.containsKey(seg))
            profilerMaxValues.put(seg, new HashMap<>());

         HashMap<String, Long> profileMax = profilerMaxValues.get(seg);

         // Determine the maximum segment execution time.
         long max = findAndUpdateMax(profileMax, "TOTAL EXECUTION TIME", execTime);
         System.out.printf("Segment %s (took %d us, max %d us)\n", seg, execTime / 1000, max / 1000);

         // Print each subsegment's time (and the maximum).
         for (String cid : counters.keySet()) {
            long counter = counters.get(cid);
            long imax = findAndUpdateMax(profileMax, cid, counter);

            System.out.printf("\t%s: %d us (max %d us)\n", cid, counter / 1000, imax / 1000);
         }

         System.out.println();
      }

      System.out.println("********************* END PROFILE ********************\n");
      profilerCounters.clear();
   } 

   private static long findAndUpdateMax(HashMap<String, Long> profileMax, String key, long execTime) {
      long max = execTime;

      if (profileMax.containsKey(key)) {
         if (profileMax.get(key) < execTime)
            profileMax.put(key, max);
         else
            max = profileMax.get(key);
      }
      else
         profileMax.put(key, max);

      return max;
   }

   /**
    * Record a new data segment.
    * 
    * @param name Segment name.
    */
   public static void seg_start(String name) {
      if (!enabled) return;

      profilerCounters.put(name, new HashMap<>());
      currentSegment = name;
      instantCounter.reset();
      segCounter.reset();
   }

   /**
    * Record an instantaneous part of the segment.
    *
    * @param id Recorded element id.
    */
   public static void seg_instant(String id) {
      if (!enabled) return;

      profilerCounters.get(currentSegment).put(id, instantCounter.elapsed());
      instantCounter.reset();
   }

   public static void seg_end() {
      if (!enabled) return;
      
      profilerCounters.get(currentSegment).put("TOTAL EXECUTION TIME", segCounter.elapsed());
      currentSegment = "";
   }
}