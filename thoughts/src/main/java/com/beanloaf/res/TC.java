package com.beanloaf.res;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Holds constants for Thoughts.
 *
 * @author beanloaf
 */
public final class TC {

    public static final String DEFAULT_TITLE = "<untitled>";
    public static final String DEFAULT_BODY = "<description>";
    public static final String DEFAULT_TAG = "<untagged>";
    public static final String DEFAULT_DATE = "<date>";
    public static final Dimension ZERO_DIM = new Dimension(0, 0);

    public static final Map<Character, Character> SURROUND_CHARS = Map.ofEntries(
            entry('(', ')'),
            entry('[', ']'),
            entry('{', '}'),
            entry('\'', '\''),
            entry('"', '"'),
            entry('<', '>')
    );


    private TC() {

    }


    public static class Properties {
        public static final String SORT = "sort";
        public static final String PUSH = "push";
        public static final String PULL = "pull";
        public static final String DELETE = "delete";
        public static final String UNDO = "undo";
        public static final String REDO = "redo";
        public static final String NEW_FILE = "new file";
        public static final String DISCONNECTED = "disconnected";
        public static final String CONNECTED = "online";
        public static final String UNPUSHED_FILES = "unpushed files";
        public static final String UNPULLED_FILES = "unpulled files";
        public static final String TEXT = "text";
        public static final String LIST_ITEM_PRESSED = "list item pressed";
        public static final String SET_TAB_INDEX = "set tab index";
        public static final String FOCUS_TITLE_FIELD = "focus text field";
        public static final String OPEN_SETTINGS_WINDOW = "open settings window";
        public static final String EXIT = "exit";
        public static final String REFRESH = "refresh";
        public static final String CLOUD_SETTINGS = "cloud settings";
        public static final String EXPORT = "export";
        public static final String CREDITS = "credits";

        public static final String TEST = "test";

    }

    public static class Paths {

        public static final File UNSORTED_DIRECTORY_PATH = new File("thoughts/storage/unsorted/");

        public static final File SORTED_DIRECTORY_PATH = new File("thoughts/storage/sorted/");

        public static final File TRASH_DIRECTORY_PATH = new File("thoughts/storage/trash/");

        public static final File SETTINGS_DIRECTORY = new File("thoughts/settings.json");

        public static final File LOGIN_DIRECTORY = new File("thoughts/src/main/java/com/beanloaf/res/user.json");


        public static final String ICON_DIRECTORY = "thoughts/src/main/java/com/beanloaf/res/icons/";
    }

    public static class Fonts {
        public static final Font p = new Font("Lato", Font.PLAIN, 25);
        public static final Font h1 = new Font("Lato", Font.PLAIN, 50);
        public static final Font h2 = new Font("Lato", Font.PLAIN, 40);
        public static final Font h3 = new Font("Lato", Font.PLAIN, 30);
        public static final Font h4 = new Font("Lato", Font.PLAIN, 20);
        public static final Font h5 = new Font("Lato", Font.PLAIN, 15);
    }

    public static class CellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(final JList<?> list,
                                                      final Object value,
                                                      final int index,
                                                      final boolean isSelected,
                                                      final boolean cellHasFocus) {

            final JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setHorizontalAlignment(JLabel.CENTER);
            c.setPreferredSize(new Dimension(25, 25));
            c.setOpaque(true);
            return c;
        }
    }

    public static class ScrollBar extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = Color.gray;
        }

        @Override
        protected JButton createDecreaseButton(final int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(final int orientation) {
            return createZeroButton();
        }

        protected JButton createZeroButton() {
            final JButton button = new JButton();
            button.setPreferredSize(TC.ZERO_DIM);
            button.setMinimumSize(TC.ZERO_DIM);
            button.setMaximumSize(TC.ZERO_DIM);
            return button;
        }
    }

}
