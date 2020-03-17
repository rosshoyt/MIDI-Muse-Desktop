package com.rosshoyt.parallelmidi;
/**
 * Author: Ross Hoyt
 * CPSC 5600
 * Winter Quarter 2020
 */

import com.rosshoyt.parallelmidi.scan.NoteHeatMap;
import com.rosshoyt.parallelmidi.scan.MusicScan;
import com.rosshoyt.parallelmidi.scan.NoteObservation;
import com.rosshoyt.parallelmidi.tools.benchmarks.BenchmarkingTimer;
import com.rosshoyt.parallelmidi.tools.data.PitchResults;
import com.rosshoyt.parallelmidi.tools.file.FileUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
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
   private static HashMap<Double, NoteHeatMap> heatmaps;
   private static HashMap<Double, NoteHeatMap> output;

   // Midi file reduction constants
   private static TableView resultsTable = new TableView();
   // used to display the results
   private static final String[] NOTE_NAMES = { "C", "Db", "D","Eb","E","F","Gb","G","Ab","A","Bb","B"};

   // The primary component which holds all others.
   private static VBox mainComponent;


   // Scan related fields
   private static final String SCAN_PAR = "Scan in Parallel", SCAN_SEQ = "Scan sequentially";
   private static Button parallelScanButton = new Button(SCAN_SEQ);
   private boolean parallelScan = false;




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

         startNoteScan(parallelScan ? true : false);

      });
      noteScanComponent = new HBox(PADDING_HORIZ_PX, startNoteScanButton, noteScanStatusLabel);


      // Container for all top components above heatmap display
      midiFilesComponent = new VBox(PADDING_VERT_PX, directorySelectionComponent, midiFilesList, noteScanComponent);


      // Setup heatmap display
      createAndSetSwingContent(swingNode);

      parallelScanButton.setOnAction(e -> {
         parallelScan = !parallelScan;
         parallelScanButton.setText(parallelScan ? SCAN_PAR : SCAN_SEQ);
      });


      //for(int i = 0; i < 12; i++) {
         TableColumn<String, Integer> column = new TableColumn<>("Pitch Frequencies (C, C#, D, D#, E, F, F#, G, G#, A, A#, and B)");
         column.setCellValueFactory(new PropertyValueFactory<>("pitchOccurences"));
         column.setMinWidth(450.0);
         resultsTable.getColumns().add(column);
      //}

      // Container for all components
      mainComponent = new VBox(PADDING_VERT_PX, midiFilesComponent, parallelScanButton, resultsTable);


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



   private static void startNoteScan(boolean parallel) {
      try {
         resultsTable.getItems().clear();
         // first convert the selected files into midi files
         List<Sequence> selectedMidiFiles = getMidiSequencesFromSelectedFiles();
         if (selectedMidiFiles.size() > 0) {
            noteScanStatusLabel.setText(TEXT_SPACER + "...preparing MIDI files");

            List<NoteObservation> notes = getMidiNoteList(selectedMidiFiles);

            noteScanStatusLabel.setText(TEXT_SPACER + "...scanning");

            NoteHeatMap reduction;

            BenchmarkingTimer.startTimer();
            if(parallel){
               reduction = parallelNoteScan(notes);
            } else {
               reduction = sequentialNoteScan(notes);
            }
            long time = BenchmarkingTimer.stopTimer();
            noteScanStatusLabel.setText(TEXT_SPACER + "Scan Complete! Took " + time + " ms");
            setResultsTable(reduction);
         } else
            noteScanStatusLabel.setText(TEXT_SPACER + "Select one or more files to do the note heatmap scan");
      } catch (Exception e) {
         e.printStackTrace();
         noteScanStatusLabel.setText(TEXT_SPACER + e.getMessage());
      }

   }

   private static void setResultsTable(NoteHeatMap reduction) {
      for(int i = 0; i < 12; i++)
         resultsTable.getItems().add(new PitchResults(NOTE_NAMES[i],reduction.getCell(i)));
   }

   private static List<NoteObservation> getMidiNoteList(List<Sequence> selectedMidiFiles) {
      Hashtable<Long, NoteObservation> notes = new Hashtable<>();
      for (Sequence seq : selectedMidiFiles) {
         for (Track track : seq.getTracks()) {
            //System.out.println("Starting parse of Track # " + trackNumber + ". " + track.size() + " events in track");
            //ShortMessageHandler smHandler = new ShortMessageHandler(this.sequence, trackNumber);
            //MetaMessageHandler mmHandler = new MetaMessageHandler(this.sequence);
            for (int i = 0; i < track.size(); i++) {
               MidiEvent midiEvent = track.get(i);

               long eventTick = midiEvent.getTick();
               MidiMessage midiMessage = midiEvent.getMessage();

               if (midiMessage instanceof ShortMessage) {
                  ShortMessage sm = (ShortMessage) midiMessage;
                  if (sm.getCommand() == ShortMessage.NOTE_ON)
                     // TODO Need to convert tick to realTime for timestamp ordering to be
                     //  totally accurate, although only a problem when using multiple sequences
                     notes.put(midiEvent.getTick(), new NoteObservation(sm.getData1(), midiEvent.getTick()));
               }
            }
         }
      }
      return new ArrayList<NoteObservation>(notes.values());
   }


   private static NoteHeatMap parallelNoteScan(List<NoteObservation> notes) {
      MusicScan scan = new MusicScan(notes);
      return scan.getReduction();

   }

   private static NoteHeatMap sequentialNoteScan(List<NoteObservation> notes) {
      //List<Sequence> midiFiles = new ArrayList<>();
      NoteHeatMap noteHeatMap = new NoteHeatMap();
      for(NoteObservation note : notes){
         noteHeatMap.accum(note.noteNumber);
      }
      return noteHeatMap;
   }

   private static List<Sequence> getMidiSequencesFromSelectedFiles() throws InvalidMidiDataException, IOException {
      ObservableList selectedItems = midiFilesList.getSelectionModel().getSelectedItems();
      List<Sequence> selectedFiles = new ArrayList<>();
      for(Object o: selectedItems) {
         selectedFiles.add(MidiSystem.getSequence((File)o));
      }
      return selectedFiles;
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
      NoteHeatMap display = output.get(current);
      if(display != null){
         for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[r].length; c++);
               //grid[r][c] = interpolateColor(display.getCell(r, c) / HOT_CALIB, COLD, HOT);
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
