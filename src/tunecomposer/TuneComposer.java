/*
 * CS 300-A, 2017S
 */
package tunecomposer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * This JavaFX app lets the user play scales.
 * @author Janet Davis 
 * @author SOLUTION - PROJECT 1
 * @since January 26, 2017
 */
public class TuneComposer extends Application {
    
     /**
     * A MidiPlayer for all notes to use.
     */
    public static final MidiPlayer PLAYER = new MidiPlayer(1,60);
    
//    private Stage primaryStage;
//    private Timeline timeline;
    private Set<Note> allNotes = new HashSet<Note>();
    private Timeline timeline;
    final private Rectangle playLine = new Rectangle(0, 0,1,1280);
    
    /**
     * Constructs a new ScalePlayer application.
     */
    public TuneComposer() {
        //PLAYER = new MidiPlayer(1,60);
    }
    
    /**
     * Plays notes that have been added.
     * Called when the Play button is clicked.
     */
    
    public void startPlaying() {
        //TODO Start red line movement
        PLAYER.stop();
        PLAYER.clear();
        for (Note note : allNotes) {
            note.schedule();
        }
        PLAYER.play();      
    }
    
    /**
     * Stops playing.
     * Called when the Stop button is clicked.
     */
    public void stopPlaying() {
        //TODO Stop line movement
        PLAYER.stop();
        timeline.stop();
        playLine.setVisible(false);
        
    }
    
    /**
     * When the user clicks the "Play scale" button, show a dialog to get the 
     * starting note and then import javafx.animation.transition.*; play the scale.
     * @param event the button click event
     */
    @FXML 
    protected void handlePlayScaleButtonAction(ActionEvent event) {
        TextInputDialog pitchDialog = new TextInputDialog("60");
        pitchDialog.setHeaderText("Give me a starting note (0-115):");
            pitchDialog.showAndWait().ifPresent(response -> {
                //playScale(Integer.parseInt(response));
            });
    }    
    
    /**
     * When the user clicks the "Stop playing" button, stop playing the scale.
     * @param event the button click event
     */
    @FXML 
    protected void handleStopPlayingButtonAction(ActionEvent event) {
        //player.stop();
        System.out.println(playLinePane.getChildren());
        stopPlaying();
    }    
    
    /**
     * When the user clicks the "Exit" menu item, exit the program.
     * @param event the menu selection event
     */
    @FXML
    protected void handleExitMenuItemAction(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML
    protected void handlePlayLine(ActionEvent event) {
        playLineMove(2000);
    }
    
    
    @FXML
    private Group background;
     
    public void initialize(){
        for(int i = 1; i < 128; i++){
            Line row = new Line(0,10*i, 2000, 10*i);
            row.setStroke(Color.LIGHTGREY);
            background.getChildren().add(row);
        }
    }
    
    @FXML
    private Group notePane;
    
    @FXML
    private BorderPane playLinePane; //I think that we can now refer to these in other functions.
    
    public void playLineMove(int endXCoordinate){
        playLine.setFill(Color.RED);
        playLinePane.getChildren().add(playLine);
        
        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        
        KeyValue keyValueX = new KeyValue(playLine.xProperty(), endXCoordinate);
        KeyValue keyScale = new KeyValue(playLine.scaleXProperty(), 2000);
 
        
        //constant speed of 100 pixels per second
        Duration duration = Duration.millis(endXCoordinate*10); //directly proportional to length of time
        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                timeline.stop();
                playLinePane.getChildren();
            }
       
        };
 
//        KeyFrame keyFrame = new KeyFrame(duration, onFinished , keyValueX);
        KeyFrame keyFrame = new KeyFrame(duration , keyValueX);
//        KeyFrame keyFrameScale = new KeyFrame(duration, keyScale);
 
        //add the keyframe to the timeline
        timeline.getKeyFrames().add(keyFrame);
//        timeline.getKeyFrames().add(keyFrameScale);
 
        timeline.play();
        
//        int duration = endXCoordinate*10;
//        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), rect);
//        tt.setFromX(0);
//        tt.setToX(200);
//        tt.setAutoReverse(false);
//
//        tt.play();
//        final Rectangle rectBasicTimeline = new Rectangle(100, 50, 100, 50);
//        playLinePane.getChildren().add(rectBasicTimeline);
//        rectBasicTimeline.setFill(Color.RED);
//
//        final Timeline timeline2 = new Timeline();
//        timeline2.setCycleCount(Timeline.INDEFINITE);
//        timeline2.setAutoReverse(true);
//        final KeyValue kv = new KeyValue(rectBasicTimeline.xProperty(), 300);
//        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
//        timeline2.getKeyFrames().add(kf);
//        timeline2.play();
    }
    
    
    /**
     * Construct the scene and start the application.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {                
        Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml"));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Scale Player");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });   
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Note note = new Note(event.getSceneX(), event.getSceneY());
                allNotes.add(note);
                note.draw();
            }
        });
        primaryStage.show();
    }
    
   
    

    /**
     * Launch the application.
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
