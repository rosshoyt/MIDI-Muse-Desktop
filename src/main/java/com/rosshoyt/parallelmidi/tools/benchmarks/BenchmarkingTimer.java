package com.rosshoyt.parallelmidi.tools.benchmarks;

public class BenchmarkingTimer {
   private static long startTime = 0;

   public static void startTimer(){
      startTime = System.currentTimeMillis();
   }
   public static long stopTimer(){
      return System.currentTimeMillis() - startTime;
   }
}
