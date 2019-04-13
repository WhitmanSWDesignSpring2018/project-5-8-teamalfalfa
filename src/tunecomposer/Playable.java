/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Interface for an object that can be added to the composition pane and played
 * in the MidiPlayer. Implemented by Gesture and Note. 
 * @author milloypr
 */
public interface Playable {
    
    /*
    * Returns whether the Playable is selected or not.
    * @return boolean true if selected, false if not
    */
    boolean getSelected();
    
    /*
    * Return 
    */
    Bounds getBounds();
    double getX();
    double getY();
    double getWidth();
    List<Rectangle> getNodeList();
    
    /**
     * Updates a Playables's status to selected and updates the style.
     * @param selected a boolean representing if the Playable is selected or not
     */
    void setSelected(boolean selected);
    
    /**
     * When the user presses the mouse to start dragging, calculate the offset
     * between the upper-left corner of the Playable's Rectangle object and
     * where the mouse is in the Rectangle
     * @param event mouse click
     */
    void setMovingCoords(MouseEvent event);
    
    /**
     * When the user clicks near the right end of the Playable's Rectangle 
     * object, calculate the offset between the right edge of the Rectangle 
     * and where the mouse is in the Rectangle 
     * @param event mouse click
     */
    void setMovingDuration(MouseEvent event);
    
    /**
     * While the user is dragging the mouse, move the Playable's Rectangle with
     * it.
     * @param event mouse drag
     */
    void move(MouseEvent event);
    
    /**
     * While the user is dragging the mouse, move the Playable's Rectangle to
     * the proper x-coordinate. Called by move.
     * @param event mouse drag
     */
    void moveX(MouseEvent event);
    
    /**
     * While the user is dragging the mouse, move the Playable's Rectangle to
     * the proper y-coordinate. Called by move.
     * @param event mouse drag
     */
    void moveY(MouseEvent event);
    
    /**
     * When the user stops dragging the mouse, set Playable's fields to the
     * Rectangle's current location
     * @param event mouse click
     */
    void stopMoving(MouseEvent event);
    
    /**
    * Adds the Playable to the MidiPlayer.
    */
    void schedule();
    
    /**
     * Remove the Playable from the pane.
     * @param notepane the pane the Playable will be removed from.
     */
    void removeFromPane(Pane notepane);
    
 
    /**
     * Check whether the user has clicked within the last 5 pixels
     * of the Playable's Rectangle
     * @param event mouse click
     * @return true if mouse is within the last 5 pixels of the Rectangle
     */
    boolean inLastFive(MouseEvent event);
    
    /**
     * While the user is dragging the mouse, change the width of the Playable's
     * Rectangle. 
     * @param event mouse drag
     */
    void moveDuration(MouseEvent event);
    
    /**
     * When the user stops dragging the mouse, set the Playable's Rectangle 
     * width to final width.
     * @param event mouse is unclicked after drag
     */
    void stopDuration(MouseEvent event);
    
    /**
     * Recursively sets the length this Playable and all of its elements (if it
     * contains any). As the user drags the edge of this Playable, the method
     * is called on all of its elements. 
     * @param gestureX the x-coordinate of the "Daddy" Gesture
     * @param proportion the original width of the "Daddy" Gesture divided by 
     * the current width, as the user drags its edge
     */
    void setProportions(double gestureX, double proportion);
    
    Playable makeCopy();
}
