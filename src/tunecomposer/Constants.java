/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author milloypr
 */
public class Constants {
    
    public static final int RESOLUTION = 100;
    public static final int BEATS_PER_MINUTE = 60;
    public static final ArrayList<String> NOTE_NAMES = new ArrayList(Arrays.asList("G", "F#/Gb", 
                              "F", "E", "D#/Eb", "D", "C#/Db",
                              "C", "B", "A#/Bb", "A", "G#/Ab"));
    
    public static final int NUM_PITCHES = 128;
    public static final int MIN_VOLUME = 0;
    public static final int MAX_VOLUME = 127;
    public static final double MIN_OPACITY = 0.2;
    public static final double MAX_OPACITY = 1.0;
    public static final int MAX_PITCH = 128;
    public static final int DEFAULT_DURATION = 100;
    public static final int TRACK = 0;
    
    public static final int LINE_SPACING = 10;
    public static final int LINE_THICKNESS = 1;
    
    public static final int RECTHEIGHT = 10;
    
    /**
     * Size of Composition pane
     */
    public static final double WIDTH = DEFAULT_DURATION * 20;
    public static final double HEIGHT = NUM_PITCHES * LINE_SPACING;
    public static final int MARGIN = 5;
    
    

    
}
