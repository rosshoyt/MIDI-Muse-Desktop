package com.rosshoyt.parallelmidi.tools.data;
/**
 * Class used to display results in table
 */
public class PitchResults {
   private String pitch;
   private int pitchOccurences;

   public PitchResults(String pitch, int pitchOccurences) {
      this.pitch = pitch;
      this.pitchOccurences = pitchOccurences;
   }

   public String getPitch() {
      return pitch;
   }

   public int getPitchOccurences() {
      return pitchOccurences;
   }

   public void setPitch(String pitch) {
      this.pitch = pitch;
   }

   public void setPitchOccurences(int pitchOccurences) {
      this.pitchOccurences = pitchOccurences;
   }


}
