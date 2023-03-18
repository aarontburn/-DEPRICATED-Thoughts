package com.beanloaf.res;

import java.awt.Font;
import java.io.File;

/**
 * Holds constants for Thoughts
 * 
 * @author beanloaf
 */
public class TC {

    
    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";



    public class Properties {
        public static final String SORT = "sort";

        public static final String PUSH = "push";
        public static final String PULL = "pull";

        public static final String DELETE = "delete";
        public static final String UNDO = "undo";
        public static final String REDO = "redo";

        public static final String NEW_FILE = "new file";

        public static final String DISCONNECTED = "disconnected";

        public static final String ONLINE = "ONLINE";
        public static final String UNPUSHED_FILES = "unpushed files";
        public static final String UNPULLED_FILES = "unpulled files";

        public static final String TEXT = "text";

        public static final String LIST_ITEM_PRESSED = "list item pressed";
        public static final String LIST_TAB_PRESSED = "list tab pressed";

        public static final String SET_TAB_INDEX = "set tab index";

    }

    public class Paths {
        public static final File UNSORTED_DIRECTORY_PATH = new File("thoughts/storage/unsorted/");

        public static final File SORTED_DIRECTORY_PATH = new File("thoughts/storage/sorted/");

        public static final File SETTINGS_DIRECTORY = new File("thoughts/settings.json");

        public static final String ICON_DIRECTORY = "thoughts/src/main/java/com/beanloaf/res/icons/";
    }

    public class Fonts {
        public static Font p = new Font("Lato", Font.PLAIN, 25);
        public static Font h1 = new Font("Lato", Font.PLAIN, 50);
        public static Font h2 = new Font("Lato", Font.PLAIN, 40);
        public static Font h3 = new Font("Lato", Font.PLAIN, 30);
        public static Font h4 = new Font("Lato", Font.PLAIN, 20);
        public static Font h5 = new Font("Lato", Font.PLAIN, 15);
    }

}
