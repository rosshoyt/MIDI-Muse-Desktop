package com.rosshoyt.parallelmidi.tools.parsing;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import com.rosshoyt.parallelmidi.tools.exceptions.UnexpectedMidiDataException;
import com.rosshoyt.parallelmidi.tools.parsing.kaitai.source.StandardMidiFile;
import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;


public class KaitaiMidiParser {

   public static StandardMidiFile parseMidiFile(byte[] data) throws UnexpectedMidiDataException {
      StandardMidiFile smf;
      try {
         // Kaitai Struct SMF parse
         smf = new StandardMidiFile(new ByteBufferKaitaiStream(data));
      } catch(Exception e) {
         if(e instanceof KaitaiStream.UnexpectedDataError || e instanceof KaitaiStream.UndecidedEndiannessError)
            throw new UnexpectedMidiDataException();
      }
      return null;
   }
}
