package com.rosshoyt.parallelmidi.tools.kaitai;

import com.rosshoyt.parallelmidi.tools.exceptions.UnexpectedMidiDataException;
import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStream;

import java.io.File;

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
