package com.beanloaf.view;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.events.FirebaseHandler;
import com.beanloaf.events.SettingsHandler;
import com.beanloaf.input.ListItemPressed;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.res.theme.ThoughtsTheme;

/**
 *
 * @author beanloaf
 */
public class Thoughts {

    public final ThoughtsPCS thoughtsPCS;
    public ThoughtObject selectedFile;
    public JFrame window;
    public JPanel container;

    public ArrayList<File> unsortedFiles = new ArrayList<>();
    public ArrayList<File> sortedFiles = new ArrayList<>();
    public ArrayList<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public ArrayList<ThoughtObject> sortedThoughtList = new ArrayList<>();

    public RightPanel rightPanel;

    public LeftPanel leftPanel;

    /* Left Panel */
    public JSplitPane splitPane;

    public DefaultListModel<String> unsortedListModel = new DefaultListModel<>();
    public DefaultListModel<String> sortedListModel = new DefaultListModel<>();
    public final ArrayList<String> tagList = new ArrayList<>();

    public boolean ready;
    public final SettingsHandler settings = new SettingsHandler();
    public final FirebaseHandler db = new FirebaseHandler(this);


    public Thoughts() {
        this.thoughtsPCS = new ThoughtsPCS(this);
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

        if (this.unsortedThoughtList.isEmpty()) {
            new ListItemPressed(this,
                    this.leftPanel.unsortedListLabel,
                    this.unsortedThoughtList).setContentFields(0);
        }

        this.ready = true;
    }

    private void createGUI() {
        ThoughtsTheme.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.window = new JFrame("Thoughts");
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setFocusable(true);
        this.window.setSize(settings.getWindowWidth(), settings.getWindowHeight());
        this.window.setLocation(new Point(settings.getWindowX(), settings.getWindowY()));
        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                settings.check();

                if (settings.isPushOnClose()) {
                    thoughtsPCS.firePropertyChange(TC.Properties.PUSH, null, null);
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

            final String eventName = event.getSource().getClass().getSimpleName();
            // System.out.println(eventName);
            if (event.getID() == 501
                    && !eventName.equals("JTextArea")
                    && !eventName.equals("JTabbedPane")
                    && !eventName.equals("JButton")) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();
                refreshThoughtList();
            }

        }, AWTEvent.MOUSE_EVENT_MASK);

        createCenterPanel();

    }

    private void createTopPanel() {
        final JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        final JButton testButton = new JButton("test");
        testButton.addActionListener(e -> {
            // TODO Auto-generated method stub

        });
        topPanel.add(testButton);

        this.container.add(topPanel, new GBC(0.1, 0.01).setFill(GridBagConstraints.BOTH).setGridWidth(2));
    }



    private void createCenterPanel() {
        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.resetToPreferredSizes();
        this.splitPane.setDividerSize(10);
        this.splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {
                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(48, 48, 48));
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(g);
                    }
                };
            }

        });

        this.container.add(splitPane, new GBC(0, 1, 0.1, 1).setFill(GridBagConstraints.BOTH));
        // .setGridWidth(GridBagConstraints.REMAINDER)

        leftPanel = new LeftPanel(this);
        this.splitPane.setLeftComponent(leftPanel);

        rightPanel = new RightPanel(this);
        splitPane.setRightComponent(rightPanel);
    }

    public ThoughtObject readFileContents(final File filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            final StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            final JSONObject json = (JSONObject) new JSONParser().parse(sb.toString());
            return new ThoughtObject(
                    json.get("title").toString().trim(),
                    json.get("date").toString().trim(),
                    json.get("tag").toString().trim(),
                    json.get("body").toString().trim(),
                    filePath);
        } catch (Exception e) {
            System.err.printf("Found invalid file '%s'.", filePath.toPath());
        }
        return null;
    }

    /**
     * Refreshes through all files in the sorted/unsorted directory.
     *
     * IMPORTANT: THIS SHOULD BE CALLED AS FEW TIMES IN A ROW AS POSSIBLE.
     *
     * Currently called by:
     * - createGUI();
     *
     * - TextAreaFocusListener.focusLost();
     *
     * - ListItemPressed.setContentFields();
     * - - FileActionButtonPressed.actionPerformed();
     * - - ListTabbedPressed.mousePressed();
     *
     */
    public void refreshThoughtList() {
        long startTime = System.currentTimeMillis();
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
        for (final File file : unsortedFileDirectory) {
            final ThoughtObject content = readFileContents(file);
            if (content != null) {
                unsortedThoughtList.add(content);
                unsortedListModel.addElement(content.getTitle());
                unsortedFiles.add(file);
            }
        }

        /* SORTED FILES */
        for (final File file : sortedFileDirectory) {
            final ThoughtObject content = readFileContents(file);
            if (content != null) {
                sortedThoughtList.add(content);
                sortedListModel.addElement(content.getTitle());
                sortedFiles.add(file);
            }
        }
        leftPanel.createTabs();
        leftPanel.setTagModel();

        thoughtsPCS.firePropertyChange(TC.Properties.SET_TAB_INDEX, selectedTab);

        if (this.db.isOnline && this.db.getList() != null) {
            this.db.refreshPushPullLabels();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total refresh time: " + (endTime - startTime) + "ms");
    }



    public void selectTextField(JTextArea textArea) {
        textArea.requestFocusInWindow();
        textArea.selectAll();
    }



    public static class ScrollBar extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = Color.gray;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        protected JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0, 0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }
    }

    public class KeyBinds implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(final KeyEvent event) {
            // 401 is key-down
            if (event.getID() != 401) {
                return false;
            }
            int key = event.getKeyCode();
            boolean c = event.isControlDown();

            switch (key) {
                case KeyEvent.VK_Z -> { // Undo
                    if (c) {
                        thoughtsPCS.firePropertyChange(TC.Properties.UNDO);
                    }
                }
                case KeyEvent.VK_Y -> { // Redo
                    if (c) {
                        thoughtsPCS.firePropertyChange(TC.Properties.REDO);
                    }
                }
                case KeyEvent.VK_N -> { // New File
                    if (c) {
                        thoughtsPCS.firePropertyChange(TC.Properties.NEW_FILE);
                    }
                }
                case KeyEvent.VK_D -> { // Delete File
                    if (c) {
                        thoughtsPCS.firePropertyChange(TC.Properties.DELETE);
                    }
                }
                case KeyEvent.VK_Q -> { // Sort File
                    if (c) {
                        thoughtsPCS.firePropertyChange(TC.Properties.SORT);

                    }
                }
                case KeyEvent.VK_P -> { // Push/Pull
                    if (c) {
                        if (event.isShiftDown()) { // Pull
                            thoughtsPCS.firePropertyChange(TC.Properties.PULL);

                        } else { // Push
                            thoughtsPCS.firePropertyChange(TC.Properties.PUSH);
                        }
                    }
                }
                default -> {
                }
            }

            return false;
        }

    }

    public class CellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(final JList<?> list,
                                                      final Object value,
                                                      final int index,
                                                      final boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setHorizontalAlignment(JLabel.CENTER);
            c.setPreferredSize(new Dimension(25, 25));
            c.setOpaque(true);
            return c;
        }
    }

}
