package com.beanloaf.view;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.res.theme.ThoughtsThemeDark;
import com.beanloaf.res.theme.ThoughtsThemeLight;
import com.beanloaf.textfields.SearchBar;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.beanloaf.database.FirebaseHandler;
import com.beanloaf.events.SettingsHandler;
import com.beanloaf.input.ListItemPressed;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

/**
 * @author beanloaf
 */
public class Thoughts implements PropertyChangeListener {

    public final ThoughtsPCS thoughtsPCS = ThoughtsPCS.getInstance(this);
    public ThoughtObject selectedFile;
    public JFrame window;
    public JPanel container;

    public final List<File> unsortedFiles = new ArrayList<>();
    public final List<File> sortedFiles = new ArrayList<>();
    public final List<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public final List<ThoughtObject> sortedThoughtList = new ArrayList<>();

    public RightPanel rightPanel;

    public LeftPanel leftPanel;

    public JSplitPane splitPane;

    public final DefaultListModel<String> unsortedListModel = new DefaultListModel<>();
    public final DefaultListModel<String> sortedListModel = new DefaultListModel<>();
    public final List<String> tagList = new ArrayList<>();

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

        if (!this.unsortedThoughtList.isEmpty()) {
            new ListItemPressed(this,
                    this.leftPanel.unsortedListLabel,
                    this.unsortedThoughtList).setContentFields(0);
        } else {
            new ListItemPressed(this).setContentFields(0);
        }

        ThoughtsPCS.getInstance().addPropertyChangeListener(this);


        this.ready = true;
    }

    private void createGUI() {

        this.window = new JFrame("Thoughts");
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setFocusable(true);
        this.window.setSize(settings.getWindowWidth(), settings.getWindowHeight());
        this.window.setLocation(new Point(settings.getWindowX(), settings.getWindowY()));
        this.window.setJMenuBar(new MenuBar());

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

        try {
            this.window.setIconImage(ImageIO.read(new File(TC.Paths.ICON_DIRECTORY + "icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyBinds());

        this.container = new JPanel(new GridBagLayout());
        this.window.add(this.container);

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

        createCenterPanel();

    }

    private void createCenterPanel() {
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.resetToPreferredSizes();
        this.splitPane.setDividerSize(10);
        this.splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(final Border border) {
                    }

                    @Override
                    public void paint(final Graphics graphics) {
                        graphics.setColor(new Color(48, 48, 48));
                        graphics.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(graphics);
                    }
                };
            }

        });

        this.container.add(splitPane, new GBC(0, 1, 0.1, 1)
                .setFill(GBC.Fill.BOTH));
        leftPanel = new LeftPanel(this);
        rightPanel = new RightPanel(this);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        db.refreshPushPullLabels();
    }

    public ThoughtObject readFileContents(final File filePath) {
        try {
            final String jsonString = new String(Files.readAllBytes(filePath.toPath()));
            final JSONObject data = (JSONObject) JSONValue.parse(jsonString);

            return new ThoughtObject(
                    data.get("title").toString().trim(),
                    data.get("date").toString().trim(),
                    data.get("tag").toString().trim(),
                    data.get("body").toString().trim(),
                    filePath,
                    data.get("styles").toString());

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
        // Resets number of tags to 2
        leftPanel.numTags = 2;

        // Stores currently selected tab
        final int selectedTab = leftPanel.leftTabs.getSelectedIndex();

        leftPanel.leftTabs.removeAll();

        // Resets all models and lists
        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        unsortedFiles.clear();
        sortedFiles.clear();
        unsortedListModel.clear();
        sortedListModel.clear();
        tagList.clear();

        /* UNSORTED FILES */
        for (final File file : Objects.requireNonNull(unsortedFileDirectory)) {
            final ThoughtObject content = readFileContents(file);
            if (content != null && SearchBar.searchFor(content, leftPanel.searchBar.getText())) {

                unsortedThoughtList.add(content);
                unsortedListModel.addElement(content.getTitle());
                unsortedFiles.add(file);
            }
        }

        /* SORTED FILES */
        for (final File file : Objects.requireNonNull(sortedFileDirectory)) {
            final ThoughtObject content = readFileContents(file);
            if (content != null && SearchBar.searchFor(content, leftPanel.searchBar.getText())) {
                sortedThoughtList.add(content);
                sortedListModel.addElement(content.getTitle());
                sortedFiles.add(file);
            }
        }
        leftPanel.createTabs();
        leftPanel.setTagModel();

        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.SET_TAB_INDEX, selectedTab);

        if (this.db.isOnline && this.db.getList() != null) {
            this.db.refreshPushPullLabels();
        }

        final long endTime = System.currentTimeMillis();
//        System.out.println("Total refresh time: " + (endTime - startTime) + "ms");
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
                case KeyEvent.VK_U -> { // Underline
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TOGGLE_UNDERLINE);
                    }
                }

                case KeyEvent.VK_B -> { // Bold
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TOGGLE_BOLD);
                    }
                }

                case KeyEvent.VK_I -> { // Italics
                    if (c) {
                        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TOGGLE_ITALIC);
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
