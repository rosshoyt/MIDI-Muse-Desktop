//package com.rosshoyt.parallelmidi.tools;
//
//
//
//
//import com.rosshoyt.analysis.midifile.tools.handlers.MetaEventHandler;
//import com.rosshoyt.analysis.midifile.tools.handlers.MidiEventHandler;
//import com.rosshoyt.analysis.midifile.tools.kaitai.StandardMidiFile;
//
//import com.rosshoyt.analysis.model.MidiFileAnalysis;
//import com.rosshoyt.analysis.model.file.FileByteData;
//import com.rosshoyt.analysis.model.file.MidiFileDetail;
//import com.rosshoyt.analysis.model.kaitai.smf.*;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Middle level file analyzer.
// * Works with one uploaded .mid/.midi file to extract useful information and return
// * SQL database-persistable analysis data (RawAnalysis)
// */
//
//public class SMFAnalyzer {
//
//
//
////   public static MidiFileDetail getMidiFileDetail(String fileName, String extension,
////                                                  MidiFileAnalysis midiFileAnalysis, byte[] fileData) {
////
////   }
////   public static _Track analyzeSMFTrack(StandardMidiFile.Track smfTrack, _Track track){
////
////      return track;
////   }
//   public static _TrackEvent analyzeSMFTrackEvent(StandardMidiFile.TrackEvent smfTrackEvent){
//      if (smfTrackEvent.eventHeader() == 255) {
//         return MetaEventHandler.handleMetaEvent(smfTrackEvent);
//      } else if (smfTrackEvent.eventHeader() == 240) {
//         System.out.println("Sysex Message Event");
//         //eventIsSupported = false;
//         // Sysex message
//      } else {
//         return MidiEventHandler.handleMidiEvent(smfTrackEvent);
//      }
//
//      return null;
//   }
//
//
//
//
//
//
//
//
//
//}