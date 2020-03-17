package com.rosshoyt.parallelmidi.tools.music;

/**
 * Custom utilities for converting MIDI data
 * to more usable music information
 */
public class MusicUtils {
   public static final String[] NOTE_NAMES_FLAT  = { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
   public static final String[] NOTE_NAMES_SHARP = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
   private static final int OCTAVE_SIZE = 12;

   public enum AccidentalType { SHARP, FLAT }

   public static String getNoteFromMIDINoteNumber(int midiNoteNumber){
      return getNoteFromMIDINoteNumber(midiNoteNumber, AccidentalType.SHARP);
   }

   public static String getNoteFromMIDINoteNumber(int midiNoteNumber, AccidentalType accidental){
      int note = midiNoteNumber % OCTAVE_SIZE;
      switch (accidental) {
         case FLAT:
            return NOTE_NAMES_FLAT[note];
         default:
            return NOTE_NAMES_SHARP[note];
      }
   }

}
