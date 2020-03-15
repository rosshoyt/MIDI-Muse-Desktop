package com.rosshoyt.parallelmidi.tools.file;

import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUtils {
   /**
    * The extensions of different of midi files.
    * (.mid is most common)
    */
   private static final String[] fileExtensions = {"mid", "smf"};

   private static final Set<String> SUPPORTED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(fileExtensions));


   // Utilities

   public static String getCurrentWorkingDirectory(){
      String currDir = System.getProperty("user.dir");
      System.out.println("Current working Directory = " + currDir);
      return currDir;

// other option for finding current working directory
//      Path currentRelativePath = Paths.get("");
//      String s = currentRelativePath.toAbsolutePath().toString();
//      System.out.println("Current relative path is: " + s);
   }
   public static boolean fileExtensionIsSupported(File file) {
      return SUPPORTED_FILE_EXTENSIONS.contains(Files.getFileExtension(file.getName()));
   }

   public static boolean isSupportedExtension(String extension) {
      return SUPPORTED_FILE_EXTENSIONS.contains(extension);
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


   public static class DirectoryScanner {
      //private Path root;
      private static Set<String> extensionsToFind;
      private static ArrayList<File> targetFiles;
      //private static Set<String> MIDI_FILE_EXTENSION;

      // TODO consolidate various constructors

      public DirectoryScanner(){
         //this.root = Paths.get(root);
         this.extensionsToFind = SUPPORTED_FILE_EXTENSIONS; // TODO remove duplicate field
         targetFiles = new ArrayList<>();
      }

//      public DirectoryScanner(String root, String extension) {
//         this.root = Paths.get(root);
//         extensionsToFind = new HashSet<>();
//         extensionsToFind.add(extension);
//         targetFiles = new ArrayList<>();
//      }
//
//      public DirectoryScanner(String root, String[] extensions) {
//         this.root = Paths.get(root);
//         extensionsToFind = new HashSet<>(Arrays.asList(extensions));
//         targetFiles = new ArrayList<>();
//      }
//
//      public DirectoryScanner(String root, Set<String> extensionsToFind) {
//         this.root = Paths.get(root);
//         this.extensionsToFind = extensionsToFind;
//         targetFiles = new ArrayList<>();
//      }

      public List<File> getFiles(String root) {
         return getMidiFileResources(Paths.get(root));
      }

      private List<File> getMidiFileResources(Path root) {
         System.out.println("\nScanning for resource folder for midi files:");
         List<File> all = new ArrayList<>();
         try {
            addTree(root, all);
         } catch (IOException e) {
            e.printStackTrace();
         }

         return all;
      }

      private static void addTree(Path directory, Collection<File> all)
            throws IOException {

         try (DirectoryStream<Path> ds = java.nio.file.Files.newDirectoryStream(directory)) {
            for (Path child : ds) {
               boolean isDirectory = false;

               if (java.nio.file.Files.isDirectory(child)) {
                  //System.out.println("In directory " + child);
                  isDirectory = true;
                  addTree(child, all); //recursive call when not at leaf node
               } else {
                  isDirectory = false;
                  if (extensionsToFind.contains(FilenameUtils.getExtension(child.toString()))) {
                     all.add(child.toFile());
                     System.out.println("Found target file type, adding to target file list:\n " + child);
                  } else {
                     //System.out.println("Other file found: " + child);
                  }
               }
            }
         }
      }

   }
}