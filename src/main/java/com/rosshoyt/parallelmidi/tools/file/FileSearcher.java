package com.rosshoyt.parallelmidi.tools.file;
/**
 * @author Ross Hoyt
 */

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.newDirectoryStream;

/**
 * Directory scanning class
 */
public class FileSearcher {

   public static final int DEFAULT_THREAD_THRESHOLD = 16;
   private int threshold;
   private Set<String> extensionsToFind;
   private ForkJoinPool pool;
   private List<File> targetFiles;

   public FileSearcher(String[] extensions){
      this(extensions, DEFAULT_THREAD_THRESHOLD);

   }
   public FileSearcher(String[] extensions, int thread_threshold){
      threshold = thread_threshold;
      extensionsToFind = new HashSet<>(Arrays.asList(extensions));
      pool = new ForkJoinPool();
      targetFiles = Collections.synchronizedList(new ArrayList<>());
   }

   /**
    * Attempted parallelization of directory scanning
    * @param root
    * @return list of  files matching extensions
    */
   public List<File> getFilesInParallel(String root) {
      System.out.println("Searching Files in Parallel");
      targetFiles.clear();
      pool.invoke(new FileSearch(Paths.get(root), 0));
      return targetFiles;
      //return searchPar(Paths.get(root));
   }




   public class FileSearch extends RecursiveAction {

      private int level;
      private Path path;
      private List<Path> files;
      private Queue<Path> dirs;
      public FileSearch(Path path, int level) {
         this.path = path;
         this.level = level;
         files = new ArrayList<>();
         dirs = new LinkedList<>();
         // first scan current directory
         try (DirectoryStream<Path> ds = newDirectoryStream(path)) {
            for (Path child : ds) {
               if (isDirectory(child))
                  dirs.add(child);
               // add midi files to list
               else if (extensionsToFind.contains(FilenameUtils.getExtension(child.toString())))
                  addFileToList(child);
            }
         } catch (IOException e){
            e.printStackTrace();
         }
      }

      @Override
      protected void compute() {
         // iterate through the subdirectories and spawn new tasks if needed
         System.out.println("In compute method for thread " + Thread.currentThread().getName());
         if(dirs.size() > 1) {
            for (int i = 0; i < dirs.size(); ) {
               // if there are more than 2 directories that need to be scanned, fork tasks
               if (pool.getActiveThreadCount() < threshold) {
                  invokeAll(new FileSearch(dirs.poll(), level + 1), new FileSearch(dirs.poll(), level + 1));
                  i += 2;
               } else {
                  // search sequentially
                  targetFiles.addAll(searchSeq(dirs.poll()));
                  i++;
               }
            }
         }
      }



   }
   private void addFileToList(Path p) {
      targetFiles.add(p.toFile());
      System.out.println("Thread " + Thread.currentThread().getName()
            + " Found target file type, adding to target file list:\n " + p);

   }

   /**
    * The Sequential benchmark scan
    * @param root
    * @return
    */
   public List<File> getFilesSequentially(String root) {
      return searchSeq(Paths.get(root));
   }


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
                  System.out.println(Thread.currentThread().getId() + " found target file type, adding to target file list:\n " + child);
               }
            }
         }
      }
   }
}