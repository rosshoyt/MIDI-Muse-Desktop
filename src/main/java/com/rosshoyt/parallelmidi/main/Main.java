package com.rosshoyt.parallelmidi.main;

import com.rosshoyt.parallelmidi.tools.benchmarks.BenchmarkingTimer;
import com.rosshoyt.parallelmidi.tools.file.FileUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
   private static final int DIM = 20;

   // application components
   private static JFrame application;
   private static JButton button;

   private static Label midiFileListLabel = new Label("Midi Files");
   private static Label currentDirectorylabel = new Label("TODO Curr Dir Label");
   private static ListView filesList = new ListView();
   private static VBox midiFilesComponent;



   // file and directory related fields
   private static final String DEFAULT_DIRECTORY = "/midi-files";
   private static String filesDirectory;// = DEFAULT_DIRECTORY;
   private static List<File> midiFiles;

   public static void main(String[] args) {
      Application.launch(args);
   }
   @Override
   public void start(Stage primaryStage) throws Exception {
      // initialize fields and read in the default midi files
      filesDirectory = FileUtils.getCurrentWorkingDirectory().concat(DEFAULT_DIRECTORY);
      midiFiles = new ArrayList<>();


      // initialize Application Window components
      midiFilesComponent = new VBox(currentDirectorylabel, midiFileListLabel, filesList);




      // setup algorithms
      // read files
      BenchmarkingTimer.startTimer();
      searchForMidiFiles();
      System.out.println("File search took " + BenchmarkingTimer.stopTimer() + " ms");
      addMidiFilesToFileList();
      //
      //fillGrid(grid);



      //animate();


      primaryStage.setTitle("Parallel Midi Application");


      Scene scene = new Scene(midiFilesComponent, DIM * 40, (int) (DIM * 40.4));
      primaryStage.setScene(scene);
      primaryStage.show();
//      listView = new ListView();
//
//      listView.getItems().add("Item 1");
//      listView.getItems().add("Item 2");
//      listView.getItems().add("Item 3");
//
//      HBox hbox = new HBox(listView);
//
//      Scene scene = new Scene(hbox, 300, 120);
//      primaryStage.setScene(scene);
//      primaryStage.show();
   }

//   public static void main(String[] args) {
//      // initialize fields and read in the default midi files
//      filesDirectory = FileUtils.getCurrentWorkingDirectory().concat(DEFAULT_DIRECTORY);
//      midiFiles = new ArrayList<>();
//
//
//      // initialize Application Window components
//      application = new JFrame();
//      application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      //listView = new ListView();
//      application.setSize(DIM * 40, (int) (DIM * 40.4));
//      application.setVisible(true);
//      application.repaint();
//
//
//      // setup algorithms
//      // read files
//      BenchmarkingTimer.startTimer();
//      searchForMidiFiles();
//      System.out.println("File search took " + BenchmarkingTimer.stopTimer() + " ms");
//      addMidiFilesToFileList();
//      //
//      //fillGrid(grid);
//
//
//
//
//      //animate();
//   }

   private static boolean addMidiFilesToFileList(){
      return filesList.getItems().addAll(midiFiles);
   }

   private static void searchForMidiFiles(){
      FileUtils.DirectoryScanner directoryScanner = new FileUtils.DirectoryScanner();
      midiFiles.addAll(directoryScanner.getFiles(filesDirectory));
   }



}
