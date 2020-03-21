package com.rosshoyt.parallelmidi.tools.file;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
   /**
    * Gets the current working directory of the application
    * @return full path string of the current working directory
    */
   public static String getCurrentWorkingDirectory(){
      return System.getProperty("user.dir");
   }
   /**
    * Method which shortens a path for display
    * TODO REFACTOR, ensure works on Windows
    * @param absPath
    * @param nDirectoriesToShow
    * @return a truncated version of the path that is easily readable
    */
   public static String getTruncatedDisplayPath(String absPath, int nDirectoriesToShow){
      if(absPath.contains("/")) {
         StringBuilder sb = new StringBuilder(absPath);
         sb.reverse();
         int count = 0, currIndex = 0;
         // TODO move currIndex bounds checking to top of while loop
         while (currIndex != -1 && count < nDirectoriesToShow) {
            if(currIndex < sb.length() - 1)
               currIndex = sb.indexOf("/", currIndex + 1);
            if(currIndex != -1) ++count;
         }
         if(currIndex < sb.length() - 1)
            sb.replace(currIndex + 1, sb.length() , "..");
         return sb.reverse().toString();
      } else // shouldn't reach this else
         return absPath;
   }

   /**
    * Overloaded method which accepts a file object
    * @param path
    * @param nDirectoriesToShow
    * @return a truncated verison of the path that is easily readable
    */
   public static String getTruncatedDisplayPath(File path, int nDirectoriesToShow){
      return getTruncatedDisplayPath(path.getAbsolutePath(), nDirectoriesToShow);
   }

   public static byte[] getByteArray(File file) throws IOException {
      InputStream targetStream = new FileInputStream(file);
      return IOUtils.toByteArray(targetStream);
   }

   public static String getExtension(File file) {
      return FilenameUtils.getExtension(file.getName());
   }

   public static String getFileNameWithoutExtension(String fullFileName) {
      return FilenameUtils.removeExtension(fullFileName);
   }

}