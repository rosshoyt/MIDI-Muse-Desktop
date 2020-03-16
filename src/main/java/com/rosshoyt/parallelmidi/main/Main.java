package com.rosshoyt.parallelmidi.main;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import com.rosshoyt.parallelmidi.tools.benchmarks.BenchmarkingTimer;
import com.rosshoyt.parallelmidi.tools.file.FileUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
   // Display constants
   private static final int DIM = 20;
   private static final int HBOX_PADDING = 10;

   // application components
   private static JButton button;


   private static Label currentDirectorylabel = new Label();
   private static boolean directoryHasBeenScanned = true;
   private static DirectoryChooser directoryChooser = new DirectoryChooser();
   private static Button directoryChooserButton = new Button("Select Directory");
   private static Button scanDirectoryButton = new Button("Scan Dir for Midi Files!");
   private static HBox directorySelectionComponent;


   private static Label midiFileListLabel = new Label("Midi Files");
   private static ListView midiFilesList = new ListView();

   private static Label selectedMidiFileListLabel = new Label("Selected Midi Files");
   private static ListView selectedMidiFilesList = new ListView();

   // container for the top half of the application (non - heatmap display portion)
   private static VBox midiFilesComponent;

   // file and directory related fields
   private static final String DEFAULT_DIRECTORY = "/midi-files";
   private static File filesDirectory;// = DEFAULT_DIRECTORY;
   private static List<File> midiFiles;

   public static void main(String[] args) {
      Application.launch(args);
   }
   @Override
   public void start(Stage primaryStage) {
      // initialize fields and application window components
      filesDirectory = new File(FileUtils.getCurrentWorkingDirectory().concat(DEFAULT_DIRECTORY));
      midiFiles = new ArrayList<>();

      // init and configure behavior of the top 'directory chooser' component
      setCurrentDirectory(filesDirectory);
      directoryChooser.setInitialDirectory(filesDirectory);
      directoryChooserButton.setOnAction(e -> {
         File selectedDirectory = directoryChooser.showDialog(primaryStage);
         if(selectedDirectory != null){
            //clearAllFileLists();
            midiFilesList.getItems().clear();
            setCurrentDirectory(selectedDirectory);
            directoryHasBeenScanned = false;
         } else{
            System.out.println("User didn't select a directory...");
         }
      });
      scanDirectoryButton.setOnAction(e -> {
         if(!directoryHasBeenScanned) {
            readInMidiFilesFromCurrentlySelectedDirectory();
         }
         else
            System.out.println("Directory was already scanned.");
      });


      directorySelectionComponent = new HBox(HBOX_PADDING, currentDirectorylabel, directoryChooserButton, scanDirectoryButton);
      // set resize behavior
      directorySelectionComponent.setHgrow(currentDirectorylabel, Priority.SOMETIMES);
      directorySelectionComponent.setHgrow(directoryChooserButton, Priority.ALWAYS);
      directorySelectionComponent.setHgrow(scanDirectoryButton, Priority.ALWAYS);

      // Set file list to only display file name, not full path
      // source: https://stackoverflow.com/questions/35834606/showing-a-list-of-only-filenames-while-having-full-file-paths-connected
      midiFilesList.setCellFactory(lv -> new ListCell<File>() {
         @Override
         protected void updateItem(File file, boolean empty) {
            super.updateItem(file, empty);
            setText(file == null ? null : file.getName());
         }
      });

      midiFilesComponent = new VBox(midiFileListLabel, midiFilesList);


      midiFilesComponent = new VBox(directorySelectionComponent, midiFilesComponent);


      // setup algorithms
      // 1. read in the default midi files
      readInMidiFilesFromCurrentlySelectedDirectory();



      //fillGrid(grid);
      //animate();

      // configure GUI and display
      primaryStage.setTitle("Parallel Midi Application");
      Scene scene = new Scene(midiFilesComponent, DIM * 40, (int) (DIM * 40.4));
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   private static void clearAllFileLists() {

   }

   private static void readInMidiFilesFromCurrentlySelectedDirectory() {
      System.out.println("Scanning Directory.");
      BenchmarkingTimer.startTimer();
      searchForMidiFiles();
      System.out.println("File search took " + BenchmarkingTimer.stopTimer() + " ms. Adding them to the list");
      addMidiFilesToFileList();
   }

   private static void setCurrentDirectory(File path){
      filesDirectory = path;
      currentDirectorylabel.setText(FileUtils.getTruncatedPathForDisplay(filesDirectory, 3));
      System.out.println("Directory to scan is set to: " + filesDirectory.getAbsolutePath());
   }

   private static void searchForMidiFiles(){
      FileUtils.DirectoryScanner directoryScanner = new FileUtils.DirectoryScanner();
      midiFiles.addAll(directoryScanner.getFiles(filesDirectory.getAbsolutePath()));
   }

   private static boolean addMidiFilesToFileList(){
      return midiFilesList.getItems().addAll(midiFiles);
   }

}
