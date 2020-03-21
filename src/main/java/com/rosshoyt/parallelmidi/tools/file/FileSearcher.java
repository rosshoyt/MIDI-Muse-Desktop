package com.rosshoyt.parallelmidi.tools.file;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.newDirectoryStream;

public class FileSearcher {
   //public static final int DEFAULT_THREAD_THRESHOLD = 8;
   public static final int DEFAULT_THREAD_DEPTH_THRESHOLD = 8;
   private int threshold;
   private Set<String> extensionsToFind;


   private ForkJoinPool pool;

   private List<File> targetFiles;

   public FileSearcher(String[] extensions){
      this(extensions, DEFAULT_THREAD_DEPTH_THRESHOLD);

   }
   public FileSearcher(String[] extensions, int thread_threshold){
      threshold = thread_threshold;
      extensionsToFind = new HashSet<>(Arrays.asList(extensions));
      pool = new ForkJoinPool();
      targetFiles = Collections.synchronizedList(new ArrayList<>());
   }

   public List<File> getFilesSequentially(String root) {
      return searchSeq(Paths.get(root));
   }

   public List<File> getFilesInParallel(String root) {
      System.out.println("Searching Files in Parallel");
      pool.invoke(new FileSearch(Paths.get(root), 0));
      return targetFiles;
      //return searchPar(Paths.get(root));
   }




   public class FileSearch extends RecursiveAction {
      private int level;
      private Path path;
      private List<File> files;

      public FileSearch(Path path, int level) {
         this.path = path;
         this.level = level;
         files = new ArrayList<>();
      }

      @Override
      protected void compute() {
         // iterate through the items in directory and spawn new tasks if needed
         try (DirectoryStream<Path> ds = newDirectoryStream(path)) {
            for (Path child : ds) {
               if (isDirectory(child)) {
                  if (pool.getActiveThreadCount() < threshold)
                     invokeAll(new FileSearch(child, level + 1));
                  else {
                     // revert to sequential processing
                     try {
                        targetFiles.addAll(searchSeq(child));
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
                  if (extensionsToFind.contains(FilenameUtils.getExtension(child.toString()))) {
                     //files.add(child.toFile());
                     targetFiles.add(child.toFile());
                     System.out.println("Found target file type, adding to target file list:\n " + child);
                  }
               }
            }
         } catch(IOException e){
            e.printStackTrace();
         }
      }

   }





//   private void addTree(Path directory, Collection<File> all) throws IOException {
//      try (DirectoryStream<Path> ds = newDirectoryStream(directory)) {
//         for (Path child : ds) {
//            if (isDirectory(child)) {
//               addTreeSeq(child, all);
//            } else {
//               if (extensionsToFind.contains(FilenameUtils.getExtension(child.toString()))) {
//                  all.add(child.toFile());
//                  System.out.println("Found target file type, adding to target file list:\n " + child);
//               }
//            }
//         }
//      }
//   }



   private List<File> searchSeq(Path root) {
      List<File> all = new ArrayList<>();
      try {
         addTreeSeq(root, all);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return all;
   }

   private void addTreeSeq(Path directory, Collection<File> all) throws IOException {
      try (DirectoryStream<Path> ds = newDirectoryStream(directory)) {
         for (Path child : ds) {
            if (isDirectory(child)) {
               addTreeSeq(child, all);
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