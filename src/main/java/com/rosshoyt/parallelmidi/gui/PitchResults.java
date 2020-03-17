package com.rosshoyt.parallelmidi.gui;
/**
 * Class used by PropertyValueFactory to display pitch
 * results of reduction in resultsTable
 *
 */
public class PitchResults {
   public static final String PITCH_STR_KEY = "pitch";

   private int pitch0,pitch1,pitch2,pitch3,pitch4,pitch5,pitch6,pitch7,pitch8,pitch9,pitch10,pitch11;

   public PitchResults(int[] pitchOccurences) {
      pitch0  = pitchOccurences[0];
      pitch1  = pitchOccurences[1];
      pitch2  = pitchOccurences[2];
      pitch3  = pitchOccurences[3];
      pitch4  = pitchOccurences[4];
      pitch5  = pitchOccurences[5];
      pitch6  = pitchOccurences[6];
      pitch7  = pitchOccurences[7];
      pitch8  = pitchOccurences[8];
      pitch9  = pitchOccurences[9];
      pitch10 = pitchOccurences[10];
      pitch11 = pitchOccurences[11];

   }

   public int getPitch0() {
      return pitch0;
   }

   public int getPitch1() {
      return pitch1;
   }

   public int getPitch2() {
      return pitch2;
   }

   public int getPitch3() {
      return pitch3;
   }

   public int getPitch4() {
      return pitch4;
   }

   public int getPitch5() {
      return pitch5;
   }

   public int getPitch6() {
      return pitch6;
   }

   public int getPitch7() {
      return pitch7;
   }

   public int getPitch8() {
      return pitch8;
   }

   public int getPitch9() {
      return pitch9;
   }

   public int getPitch10() {
      return pitch10;
   }

   public int getPitch11() {
      return pitch11;
   }

}
