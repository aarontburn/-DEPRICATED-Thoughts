package com.beanloaf.common;

import java.awt.Font;
import java.io.File;

/**
 * Holds constants for Thoughts
 * 
 * @author beanloaf
 */
public class TC {
    public static final File UNSORTED_DIRECTORY_PATH = new File("storage/unsorted/");
    public static final File SORTED_DIRECTORY_PATH = new File("storage/sorted/");

    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";

    /* Fonts */
    public static Font p = new Font("Lato", Font.PLAIN, 25);
    public static Font h1 = new Font("Lato", Font.PLAIN, 50);
    public static Font h2 = new Font("Lato", Font.PLAIN, 40);
    public static Font h3 = new Font("Lato", Font.PLAIN, 30);
    public static Font h4 = new Font("Lato", Font.PLAIN, 20);
    public static Font h5 = new Font("Lato", Font.PLAIN, 15);

}
