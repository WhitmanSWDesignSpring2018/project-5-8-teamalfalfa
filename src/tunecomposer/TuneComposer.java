/*
 * CS 300-A, 2017S
 */
package tunecomposer;

import java.io.IOException;
import java.util.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.sound.midi.ShortMessage;

/**
 * This JavaFX app lets the user play scales.
 * @author Ian[0], Ian[1], Angie, Melissa
 * @since January 26, 2017
 */
public class TuneComposer extends Application {

     /**
     * A MidiPlayer for all notes to use.
     */
    public static final MidiPlayer PLAYER = new MidiPlayer(100,60);

    /**
     * A list of instrument values to associate with MidiPlayer channels
     */
    private final int[] timbreList = new int[] {0, 6, 12, 19, 21, 24, 40, 60};

    /**
     * The set of all notes, to be played later.
     */
    private static Set<Note> allNotes;
    //NOTE: Hmm, it seems like you are duplicating information about notes.
    //      This could make things difficult later.

    /**
     * A line moves from left to right across the main pane. It crosses each
     * note as that note is played.
     */
    private static PlayLine playLine;

    /**
     * Controls the movement of playLine.
     */
    private Timeline timeline;
    
    /**
     * TODO
     */
    private boolean clickInPane = true;
    
    private boolean changeDuration = false;
    
    /**
     * TODO
     */
    private boolean isDragSelecting = false;
    
    /**
     * List of notes being selected by the selection area
     */
    private Set<Note> selectedNotes;

    /**
     * The background of the application.
     */
    @FXML
    private Group background;

    /**
     * The pane in which notes are constructed.
     */
    @FXML
    private Pane notePane;

    /**
     * The pane in which the play line is constructed and plays.
     */
    @FXML
    private AnchorPane playLinePane;

    /**
     * The line wrapped by PlayLine.
     */
    @FXML
    private Line movingLine;

    /**
     * An area for click-and-drag note selection.
     */
    private SelectionArea selection;

    /**
     * TODO
     */
    @FXML
    private Rectangle selectRect;

    /**
     * A group of sidebar radio buttons for selecting an instrument.
     */
    @FXML
    private ToggleGroup instrumentToggle;

    public TuneComposer() {
        allNotes = new HashSet<Note>();
        selectedNotes = new HashSet<Note>();
    }

    /**
     * Add the given note to the set of all notes, to be played later.
     * @param note note added to composition
     */
    public static void addNote(Note note) {
        allNotes.add(note);
    }

    /**
     * Plays notes that have been added.
     * Called when the Play button is clicked.
     */
    public void startPlaying() {
        PLAYER.stop();
        PLAYER.clear();
        for(int i=0; i<8; i++){
            PLAYER.addMidiEvent(ShortMessage.PROGRAM_CHANGE + i, timbreList[i], 0, 0, 0);
        }
        allNotes.forEach((note) -> {
            note.schedule();
        });

        PLAYER.play();
        playLine.play(Note.getNotesEnd());
    }

    /**
     * Overload version of startPlaying() which ignores an ActionEvent.
     * @param ignored not used
     */
    @FXML
    public void handleStartPlaying(ActionEvent ignored) {
        startPlaying();
    }

    /**
     * Stops playing.
     * Called when the Stop button is clicked. Does not remove notes from the
     * screen or from allNotes.
     */
    public void stopPlaying() {
        PLAYER.stop();
        playLine.stop();

    }

    /**
     * When the user clicks the "Stop playing" button, stop playing the scale.
     * @param event the button click event
     */
    @FXML
    protected void handleStopPlaying(ActionEvent event) {
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

    /**
     * Initializes FXML. Called automatically.
     * (1) adds 127 grey lines to background
     * (2) initializes the playLine(set to invisible)
     */
    public void initialize() {
        // Add grey lines to background
        for(int i = 1; i < 128; i++){
            Line row = new Line(0,10*i, 2000, 10*i);
            row.getStyleClass().add("row-divider");
            background.getChildren().add(row);
        }

        playLine = new PlayLine(movingLine);

        // Let mouse events go through to notePane
        playLinePane.setMouseTransparent(true);
        //NOTE: Good that you figured this out!

        selection = new SelectionArea(selectRect);
    }

    /**
     * Get the instrument currently selected in the sidebar.
     * @return the selected instrument
     */
    private Instrument getInstrument() {
        RadioButton selectedButton = (RadioButton)instrumentToggle.getSelectedToggle();
        String instrument = selectedButton.getText();
        switch(instrument) {
            case "Piano":           return Instrument.PIANO;
            case "Harpsichord":     return Instrument.HARPSICHORD;
            case "Marimba":         return Instrument.MARIMBA;
            case "Church Organ":    return Instrument.CHURCH_ORGAN;
            case "Accordion":       return Instrument.ACCORDION;
            case "Guitar":          return Instrument.GUITAR;
            case "Violin":          return Instrument.VIOLIN;
            case "French Horn":     return Instrument.FRENCH_HORN;
            default:
                throw new IllegalArgumentException("Unrecognized Instrument");
        }
    }

    /**
     * Construct a note from a click. Called via FXML.
     * @param event a mouse click
     */
    public void handleClick(MouseEvent event) {
        if (playLine.isPlaying()) {
            stopPlaying();
        }
        else if (isDragSelecting){
            isDragSelecting = false;
            selection.endRectangle();
            selectedNotes.clear();
        }
        else if (clickInPane) {
            if (! event.isControlDown()) {
                selectAll(false);
            }
            Instrument instrument = getInstrument();
            Note note = new Note(event.getX(), event.getY(), instrument);
            allNotes.add(note);
            notePane.getChildren().add(note.getRectangle());
            
            note.getRectangle().setOnMousePressed((MouseEvent pressedEvent) -> {
                handleNoteClick(pressedEvent, note);
                handleNotePress(pressedEvent, note);
            });
            
            note.getRectangle().setOnMouseDragged((MouseEvent dragEvent) -> {
                handleNoteDrag(dragEvent);
            });
            
            note.getRectangle().setOnMouseReleased((MouseEvent releaseEvent) -> {
                handleNoteStopDragging(releaseEvent);
            });
        }
        clickInPane = true;
    }
    
    private void handleNoteClick(MouseEvent event, Note note) {
        clickInPane = false;
        boolean control = event.isControlDown();
        boolean selected = note.getSelected();
        if (! control && ! selected) {
            selectAll(false);
            note.setSelected(true);
        } else if ( control && ! selected) {
            note.setSelected(true);
        } else if (control && selected) {
            note.setSelected(false);
        }
    }
    
    private void handleNotePress(MouseEvent event, Note note) {
        changeDuration = note.inLastFive(event);
        allNotes.forEach((n) -> {
            if (n.getSelected()) {
                if (changeDuration) {
                    n.setMovingDuration(event);
                } else {
                    n.setMovingCoords(event);
                }
            }
        });
    }
    
    private void handleNoteDrag(MouseEvent event) {
        allNotes.forEach((n) -> {
            if (n.getSelected()) {
                if (changeDuration) {
                    n.moveDuration(event);
                } else {
                    n.moveNote(event);
                }
            }
        });
    }
    
    private void handleNoteStopDragging(MouseEvent event) {
        clickInPane = false;
        allNotes.forEach((n) -> {
            if (n.getSelected()) {
                if (changeDuration) {
                    n.stopDuration(event);
                } else {
                    n.stopMoving(event);
                }
            }
        });
        changeDuration = false;
    }

    public void startDrag(MouseEvent event) {

        if (clickInPane) {
            handleSelectionStartDrag(event);
        }
    }

    public void continueDrag(MouseEvent event) {
        if (clickInPane) {
            handleSelectionContinueDrag(event);
        }
    }
    
    private void handleSelectionStartDrag(MouseEvent event) {
        // TODO If the ctrl key isn't pressed, deselect all notes.
        //      If the ctrl key is pressed, add to the existing selection.
        isDragSelecting = true;
        // Put first rectangle corner at cursor
        selection.startRectangle(event.getX(), event.getY());

        if(!event.isControlDown()){
            selectAll(false);
        }
    }
    
    private void handleSelectionContinueDrag(MouseEvent event) {
        // TODO Select all notes within the rectangle and deselect others.
        // We need a container of sorta-selected notes that fall within the
        // rectangle but aren't fully selected yet.

        // TODO Update the width and height of the rectangle.
        selection.update(event.getX(), event.getY());

        allNotes.forEach((note) -> {
            Rectangle rect = note.getRectangle();
            double horizontal = selectRect.getX() + selectRect.getWidth();
            double vertical = selectRect.getY() + selectRect.getHeight();

            if((rect.getX() > selectRect.getX() && rect.getX() < horizontal) 
            && (rect.getY() > selectRect.getY() && rect.getY() < vertical)){
                if(!note.getSelected()){
                    selectedNotes.add(note);
                    note.setSelected(true);
                }
            } else{
                if(selectedNotes.contains(note)){
                    note.setSelected(false);
                    selectedNotes.remove(note); 
                }
            }
        });

        //detect note in rectangle
        //add to list
        //select it

        // TODO It might make sense to have a selectRect class with its own
        // properties, so that we can keep track of how far the mouse has been
        // dragged.
        // Alternatively, we can get the coordinates of the rectangle's first
        // corner and compare those values to the click coordinates to
        // calculate the rectangle's new width and height.
    }

    /**
     * TODO
     * @param event 
     */
    @FXML
    void handleDelete(ActionEvent event) {
        Collection toDelete = new ArrayList();
        allNotes.forEach((note) -> {
            if (note.getSelected()) {
                toDelete.add(note);
                notePane.getChildren().remove(note.getRectangle());
            }
        });
        allNotes.removeAll(toDelete);
    }
    
    /**
     * TODO
     * @param event 
     */
    @FXML
    void handleSelectAll(ActionEvent event) {
        selectAll(true);
    }
    
    /**
     * TODO
     */
    private void selectAll(boolean selected) {
        allNotes.forEach((note) -> {
            note.setSelected(selected);
        });
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
        scene.getStylesheets().add("tunecomposer.css");
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
