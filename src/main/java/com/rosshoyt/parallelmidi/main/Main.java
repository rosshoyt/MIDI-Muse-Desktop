package com.rosshoyt.parallelmidi.main;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import com.rosshoyt.parallelmidi.tools.benchmarks.BenchmarkingTimer;
import com.rosshoyt.parallelmidi.tools.file.FileUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
   // Display constants
   private static final int DIM = 20;
   private static final int PADDING_HORIZ_PX = 20;
   private static final int PADDING_VERT_PX = 10;

   /* Application GUI components (in top - down window location order) */
   // Directory selection components
   private static Label currentDirectorylabel = new Label();
   private static boolean directoryHasBeenScanned = true;
   private static DirectoryChooser directoryChooser = new DirectoryChooser();
   private static Button directoryChooserButton = new Button("Select Directory");
   private static Button scanDirectoryButton = new Button("Scan Dir for Midi Files!");
   private static HBox directorySelectionComponent;

   // Midi File List
   private static Label midiFileListLabel = new Label("Midi Files " +
         "                                         " +
         "Use ⌘ (Mac) or Ctrl (Windows), or ⇧, to select multiple");
   private static ListView midiFilesList = new ListView();

   //private static Button selectFileButton = new Button("Add File");
   // TODO Create a Selected Midi File List (instead of, or in addition to, the setSelectionMode() control)
   private static Label selectedMidiFileListLabel = new Label("Selected Midi Files");
   private static ListView selectedMidiFilesList = new ListView();


   private static Button startNoteScanButton = new Button("Start Heatscan of Selected File(s)");
   private static Label noteScanStatusLabel = new Label();
   private static HBox noteScanComponent;

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
      directorySelectionComponent = new HBox(PADDING_HORIZ_PX, currentDirectorylabel, directoryChooserButton, scanDirectoryButton);
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
      midiFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      // set the note scan button/label (row just above heatmap display)
      startNoteScanButton.setOnAction(e ->{

         startNoteScan();

      });
      noteScanComponent = new HBox(PADDING_HORIZ_PX, startNoteScanButton, noteScanStatusLabel);


      // Container for all top components above heatmap display
      midiFilesComponent = new VBox(PADDING_VERT_PX, directorySelectionComponent, midiFilesList, noteScanComponent);


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

   private static void startNoteScan() {

      ObservableList selectedItems = midiFilesList.getSelectionModel().getSelectedItems();

      List<File> selectedFiles = new ArrayList<>();
      if(selectedItems.size() > 0) {
         noteScanStatusLabel.setText("             ...scanning");
         // cast objects to files
         for(Object o: selectedItems) selectedFiles.add((File)o);

         // TODO map/reduce
         try{

         }catch(Exception e){

         }
         noteScanStatusLabel.setText("             Scan Complete!");
      } else
         noteScanStatusLabel.setText("             Select one or more files to do the note heatmap scan");

   }

//   private static void clearAllFileLists() {
//
//   }

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
