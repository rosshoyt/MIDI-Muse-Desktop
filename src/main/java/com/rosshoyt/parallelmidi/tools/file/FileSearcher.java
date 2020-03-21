package com.rosshoyt.parallelmidi.tools.file;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.newDirectoryStream;

public class FileSearcher {

   private static Set<String> extensionsToFind;

   private static ArrayList<File> targetFiles;


   public FileSearcher(String[] extensions){
      this.extensionsToFind = new HashSet<>(Arrays.asList(extensions));
      targetFiles = new ArrayList<>();
   }

   public List<File> getFilesSequentially(String root) {
      return getMidiFileResources(Paths.get(root));
   }

   public List<File> getFilesInParallel(String fullPath) {
      return getFilesSequentially(fullPath);
   }

   private List<File> getMidiFileResources(Path root) {
      List<File> all = new ArrayList<>();
      try {
         addTree(root, all);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return all;
   }

   private static void addTree(Path directory, Collection<File> all) throws IOException {

      try (DirectoryStream<Path> ds = newDirectoryStream(directory)) {
         for (Path child : ds) {
            if (isDirectory(child)) {
               addTree(child, all); //recursive call when not at leaf node
            } else {
               if (extensionsToFind.contains(FilenameUtils.getExtension(child.toString()))) {
                  all.add(child.toFile());
                  System.out.println("Found target file type, adding to target file list:\n " + child);
               }
            }
         }
      }
   }


}