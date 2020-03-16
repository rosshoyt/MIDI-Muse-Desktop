package com.rosshoyt.parallelmidi;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import com.rosshoyt.parallelmidi.gui.ColoredGrid;
import com.rosshoyt.parallelmidi.gui.HeatMap;
import com.rosshoyt.parallelmidi.tools.benchmarks.BenchmarkingTimer;
import com.rosshoyt.parallelmidi.tools.file.FileUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends Application {
   // File and directory related fields
   private static final String DEFAULT_DIRECTORY = "/midi-files";
   private static File filesDirectory;
   private static List<File> midiFiles;

   // Display constants
   private static final int DIM = 20;
   private static final int PADDING_HORIZ_PX = 20;
   private static final int PADDING_VERT_PX = 10;
   private static final String TEXT_SPACER = "             ";

   // Application GUI components (in top - down window location order)
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
   //private static Label selectedMidiFileListLabel = new Label("Selected Midi Files");
   //private static ListView selectedMidiFilesList = new ListView();

   // Reduce/Scan related GUI components
   private static Button startNoteScanButton = new Button("Start Heatscan of Selected File(s)");
   private static Label noteScanStatusLabel = new Label();
   private static HBox noteScanComponent;

   // Container for the top half of the application (non - heatmap display portion)
   private static VBox midiFilesComponent;

   // Heatmap GUI related fields
   // the Swing Heatmap component from HW 5/6
   private static final SwingNode swingNode = new SwingNode();
   private static Button startButton = new Button("Start");

   private static final int SLEEP_INTERVAL = 50; // milliseconds
   private static final Color COLD = new Color(0x0a, 0x37, 0x66), HOT = Color.RED;
   private static final double HOT_CALIB = 1.0;
   private static final String REPLAY = "Replay";
   private static Color[][] grid;
   private static double current;
   private static HashMap<Double, HeatMap> heatmaps;
   private static HashMap<Double, HeatMap> output;


   // The primary component which holds all others.
   private static VBox mainComponent;




   /**
    * Launches the JavaFX application
    * @param args not used
    */
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


      // Setup heatmap display
      createAndSetSwingContent(swingNode);
//      startButton.setOnAction(e -> {
//         try {
//            startButton.setDisable(true);
//            //animate();
//            startButton.setDisable(false);
//         }catch(InterruptedException ie) {
//            ie.printStackTrace();
//         }
//      });

      // Container for all components
      mainComponent = new VBox(PADDING_VERT_PX, midiFilesComponent, startButton, swingNode);


      //grid = new Color[DIM][DIM];
      //fillGrid(grid);
      //ColoredGrid gridPanel = new ColoredGrid(grid);

      // Read in the midi files from default location
      readInMidiFilesFromCurrentlySelectedDirectory();

      //animate();

      // configure GUI and display
      primaryStage.setTitle("Parallel Midi Application");
      Scene scene = new Scene(mainComponent, DIM * 40, (int) (DIM * 40.4));
      primaryStage.setScene(scene);
      primaryStage.show();

   }



   private static List<File> getSelectedFiles(){
      ObservableList selectedItems = midiFilesList.getSelectionModel().getSelectedItems();
      List<File> selectedFiles = new ArrayList<>();
      for(Object o: selectedItems) selectedFiles.add((File)o);
      return selectedFiles;
   }
   private static void startNoteScan() {
      List<File> selectedFiles = getSelectedFiles();
      if(selectedFiles.size() > 0) {
         noteScanStatusLabel.setText(TEXT_SPACER + "...scanning");

         // TODO map/reduce
         try{
            sequentialNoteScan(selectedFiles);
         }catch(Exception e){

         }
         noteScanStatusLabel.setText(TEXT_SPACER + "Scan Complete!");
      } else
         noteScanStatusLabel.setText(TEXT_SPACER + "Select one or more files to do the note heatmap scan");

   }

   private static boolean sequentialNoteScan(List<File> selectedFiles) {
      List<Sequence> midiFiles = new ArrayList<>();

      for (File f : selectedFiles) {
         try {
            midiFiles.add(MidiSystem.getSequence(f));
         } catch (Exception e) {
            e.printStackTrace();
            noteScanStatusLabel.setText(TEXT_SPACER + e.getMessage());
            return false;
         }

      }

      return true;
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

   private void createAndSetSwingContent(final SwingNode swingNode) {
      SwingUtilities.invokeLater(() -> {
         JPanel panel = new JPanel();
         panel.add(new JButton("Click me!"));
         swingNode.setContent(panel);
      });
   }

   private static void animate() throws InterruptedException {

      for (current = 0; current < output.size(); current += 5) {
         if(output.containsKey(current)){
            fillGrid(grid);
            //application.repaint();
            Thread.sleep(SLEEP_INTERVAL);
         }
      }

      //application.repaint();
   }

   private static void fillGrid(Color[][] grid) {
      HeatMap display = output.get(current);
      if(display != null){
         for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[r].length; c++)
               grid[r][c] = interpolateColor(display.getCell(r, c) / HOT_CALIB, COLD, HOT);
      }
   }
   private static Color interpolateColor(double ratio, Color a, Color b) {
      ratio = Math.min(ratio, 1.0);
      int ax = a.getRed();
      int ay = a.getGreen();
      int az = a.getBlue();
      int cx = ax + (int) ((b.getRed() - ax) * ratio);
      int cy = ay + (int) ((b.getGreen() - ay) * ratio);
      int cz = az + (int) ((b.getBlue() - az) * ratio);
      return new Color(cx, cy, cz);
   }
}
