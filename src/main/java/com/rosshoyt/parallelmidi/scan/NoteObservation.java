package com.rosshoyt.parallelmidi.scan;

import java.io.Serializable;

public class NoteObservation implements Serializable {
   private static final long serialVersionUID = 1L;
   /**
    * The Timestamp of the Note event
    */
   public long tickOn;
   /**
    * Midi Pitches are between 0-127
    */
   public int noteNumber;
}
