package com.rosshoyt.parallelmidi.scan;

import com.rosshoyt.parallelmidi.gui.NoteHeatMap;

import java.util.HashMap;
import java.util.List;

public class MusicScan extends GeneralScan3<NoteObservation, NoteHeatMap> {
   public MusicScan(List<NoteObservation> raw) {
      super(raw, 1000);
   }

   @Override
   protected NoteHeatMap init() {
      return new NoteHeatMap();
   }

   @Override
   protected NoteHeatMap prepare(NoteObservation datum) {
      return new NoteHeatMap(datum.noteNumber);
   }

   @Override
   protected NoteHeatMap combine(NoteHeatMap left, NoteHeatMap right) {
      return NoteHeatMap.combine(left, right);
   }

   @Override
   protected void accum(NoteHeatMap hm, NoteObservation datum) {
      hm.accum(datum.noteNumber);
   }

}