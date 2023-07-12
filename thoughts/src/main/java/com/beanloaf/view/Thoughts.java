package com.beanloaf.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.ThoughtsFrame;
import com.beanloaf.res.theme.ThoughtsThemeDark;
import com.beanloaf.res.theme.ThoughtsThemeLight;
import com.beanloaf.textfields.SearchBar;
;
import net.miginfocom.swing.MigLayout;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beanloaf.database.FirebaseHandler;
import com.beanloaf.events.SettingsHandler;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

/**
 * @author beanloaf
 */
public class Thoughts implements PropertyChangeListener {

    public final ThoughtsPCS thoughtsPCS = ThoughtsPCS.getInstance(this);
    public ThoughtObject selectedFile;
    public ThoughtsFrame window;
    public JPanel container;

    public final List<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public final List<ThoughtObject> sortedThoughtList = new ArrayList<>();

    public RightPanel rightPanel;

    public LeftPanel leftPanel;


    public boolean ready;
    public final SettingsHandler settings = new SettingsHandler();
    public final FirebaseHandler db = new FirebaseHandler(this);


    public Thoughts() {
        if (settings.isLightMode()) {
            ThoughtsThemeLight.setup();
        } else {
            ThoughtsThemeDark.setup();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        createGUI();
        this.window.getContentPane().add(this.container);

        this.window.pack();
        this.window.setVisible(true);

        onStartUp();

    }


    private void onStartUp() {
        if (!TC.Paths.UNSORTED_DIRECTORY_PATH.isDirectory()) {
            TC.Paths.UNSORTED_DIRECTORY_PATH.mkdirs();
        }
        if (!TC.Paths.SORTED_DIRECTORY_PATH.isDirectory()) {
            TC.Paths.SORTED_DIRECTORY_PATH.mkdir();
        }


        refreshThoughtList();

        if (this.unsortedThoughtList.isEmpty() || leftPanel.unsortedListLabel == null) {
            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, null);

        } else {
            leftPanel.unsortedListLabel.click();

        }

        ThoughtsPCS.getInstance().addPropertyChangeListener(this);


        this.ready = true;
    }

    private void createGUI() {

        this.window = new ThoughtsFrame("Thoughts", settings.getWindowWidth(), settings.getWindowHeight());
        this.window.setLocation(new Point(settings.getWindowX(), settings.getWindowY()));

        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                ready = false;
                settings.check();

                if (settings.isPushOnClose()) {
                    ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.PUSH);
                }
                settings.changeWindowDimension(window.getSize());
                settings.changeIsMaximized(
                        window.getExtendedState() == JFrame.MAXIMIZED_BOTH);
                settings.changeWindowPosition(window.getLocation());

            }
        });

        this.window.setExtendedState(settings.isMaximized() ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);


        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyBinds());


        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!ready) {
                return;
            }

            /*
             * 501 is mouse pressed
             * 502 is mouse released
             * 500 is mouse clicked
             *
             * 504 is mouse entered
             * 505 is mouse exit
             */

            if (event.getID() == 501
                    && !(event.getSource() instanceof JTextArea)
                    && !(event.getSource() instanceof JTabbedPane)
                    && !(event.getSource() instanceof JButton)) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();
                refreshThoughtList();
            }

        }, AWTEvent.MOUSE_EVENT_MASK);
        this.container = new JPanel(new MigLayout());
        this.container.setPreferredSize(new Dimension(settings.getWindowWidth(), settings.getWindowHeight()));


        createCenterPanel();

    }

    private void createCenterPanel() {
        leftPanel = new LeftPanel(this);
        rightPanel = new RightPanel(this);

        this.container.add(leftPanel, "west, w 35%, h 100%");
        this.container.add(rightPanel, "east, w 65%, h 100%");


        db.refreshPushPullLabels();
    }


    public ThoughtObject readFileContents(final File filePath, final boolean isSorted) {
        try {
            final String jsonString = new String(Files.readAllBytes(filePath.toPath()));
            final JSONObject data = (JSONObject) JSONValue.parse(jsonString);

            return new ThoughtObject(isSorted,
                    data.get("title").toString().trim(),
                    data.get("date").toString().trim(),
                    data.get("tag").toString().trim(),
                    data.get("body").toString().trim(),
                    filePath);

        } catch (Exception e) {
            System.err.println("Found invalid file " + filePath.toPath());
        }
        return null;
    }

    /**
     * Refreshes through all files in the sorted/unsorted directory.
     * <p>
     * IMPORTANT: THIS SHOULD BE CALLED AS FEW TIMES IN A ROW AS POSSIBLE.
     * <p>
     * Currently called by:
     * - createGUI();
     * <p>
     * - TextAreaFocusListener.focusLost();
     * <p>
     * - ListItemPressed.setContentFields();
     * - - FileActionButtonPressed.actionPerformed();
     * - - ListTabbedPressed.mousePressed();
     */
    public void refreshThoughtList() {
        final long startTime = System.currentTimeMillis();
        final File[] unsortedFileDirectory = TC.Paths.UNSORTED_DIRECTORY_PATH.listFiles();
        final File[] sortedFileDirectory = TC.Paths.SORTED_DIRECTORY_PATH.listFiles();

        final ThoughtObject selected = selectedFile;

        // Resets all models and lists
        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        leftPanel.thoughtListByTag.clear();



        /* UNSORTED FILES */
        for (final File file : Objects.requireNonNull(unsortedFileDirectory)) {
            final ThoughtObject content = readFileContents(file, false);
            if (content != null && SearchBar.searchFor(content, leftPanel.searchBar.getText())) {
                unsortedThoughtList.add(content);
            }
        }

        /* SORTED FILES */
        for (final File file : Objects.requireNonNull(sortedFileDirectory)) {
            final ThoughtObject content = readFileContents(file, true);
            if (content != null && SearchBar.searchFor(content, leftPanel.searchBar.getText())) {
                sortedThoughtList.add(content);
            }
        }

        unsortedThoughtList.sort(ThoughtObject::compareTo);
        sortedThoughtList.sort(ThoughtObject::compareTo);

        leftPanel.populateTagList();

        if (leftPanel.selectedTag != null) leftPanel.selectedTag.click();
        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, selected);





        if (this.db.isOnline && this.db.getList() != null) {
            this.db.refreshPushPullLabels();
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("Total refresh time: " + (endTime - startTime) + "ms");
    }


    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TC.Properties.OPEN_SETTINGS_WINDOW -> SettingsWindow.getInstance(this);
            case TC.Properties.EXIT -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            case TC.Properties.REFRESH -> refreshThoughtList();
            case TC.Properties.CLOUD_SETTINGS -> CloudSettingsWindow.getInstance(db);
            case TC.Properties.EXPORT -> {
                if (selectedFile == null) {
                    return;
                }

                final FileDialog fd = new FileDialog(window, "Export", FileDialog.SAVE);
                fd.setFile(selectedFile.getTitle() + ".txt");
                fd.setVisible(true);
                selectedFile.exportAsText(fd.getDirectory());
            }
            case TC.Properties.CREDITS -> CreditsWindow.getInstance();

            case TC.Properties.TEST -> {
                System.out.println(leftPanel.getWidth());
            }
            default -> {
            }
        }
    }


    public static class KeyBinds implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(final KeyEvent event) {
            // 401 is key-down
            if (event.getID() != 401) {
                return false;
            }
            final int key = event.getKeyCode();
            final boolean c = event.isControlDown();

            switch (key) {
                case KeyEvent.VK_Z -> { // Undo
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.UNDO);
                    }
                }
                case KeyEvent.VK_Y -> { // Redo
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.REDO);
                    }
                }
                case KeyEvent.VK_N -> { // New File
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.NEW_FILE);
                    }
                }
                case KeyEvent.VK_D -> { // Delete File
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.DELETE);
                    }
                }
                case KeyEvent.VK_Q -> { // Sort File
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.SORT);
                    }
                }
                case KeyEvent.VK_P -> { // Push/Pull
                    if (c) {
                        if (event.isShiftDown()) { // Pull
                            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.PULL);
                        } else { // Push
                            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.PUSH);
                        }
                    }
                }

                case KeyEvent.VK_F5 -> ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.REFRESH);

                default -> {
                }
            }

            return false;
        }

    }


}
