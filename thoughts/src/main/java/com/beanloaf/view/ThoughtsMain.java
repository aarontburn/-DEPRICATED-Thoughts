package com.beanloaf.view;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.beanloaf.events.ThoughtsPCS;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.events.FirebaseHandler;
import com.beanloaf.events.SettingsHandler;
import com.beanloaf.input.ListItemPressed;
import com.beanloaf.input.ListTabPressed;
import com.beanloaf.objects.ListTab;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.res.theme.ThoughtsTheme;

/**
 * 
 * @author beanloaf
 */
public class ThoughtsMain {

    public final ThoughtsPCS thoughtsPCS;
    public ThoughtObject selectedFile;
    public JFrame window;
    public JPanel container;

    public ArrayList<File> unsortedFiles = new ArrayList<>();
    public ArrayList<File> sortedFiles = new ArrayList<>();
    public ArrayList<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public ArrayList<ThoughtObject> sortedThoughtList = new ArrayList<>();

    public RightPanel rightPanel;

    /* Left Panel */
    public JSplitPane splitPane;

    public JPanel leftPanel;
    public JTabbedPane leftTabs;
    public DefaultListModel<String> unsortedListModel = new DefaultListModel<>();
    public DefaultListModel<String> sortedListModel = new DefaultListModel<>();
    public ListTab unsortedListLabel, sortedListLabel;
    public ArrayList<String> tagList = new ArrayList<>();

    public boolean ready = false;

    public SettingsHandler settings = new SettingsHandler();


    public FirebaseHandler db;

    /**
     * Number of tags on the displayed
     * 
     * Default to 2 for sorted/unsorted
     */
    public int numTags = 2;

    /**
     * Default size of each tag label
     */
    public Dimension tagDim = new Dimension(150, 25);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(ThoughtsMain::new);
    }

    public ThoughtsMain() {
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

        db = new FirebaseHandler(this);

        refreshThoughtList();

        if (this.unsortedThoughtList.size() > 0) {
            new ListItemPressed(this,
                    this.unsortedListLabel,
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
            public void windowClosing(WindowEvent e) {
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

        int extendedState = JFrame.NORMAL;
        if (settings.getIsMaximized()) {
            extendedState = JFrame.MAXIMIZED_BOTH;
        }
        this.window.setExtendedState(extendedState);

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

            String eventName = event.getSource().getClass().getSimpleName();
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
        createLeftPanel();
        createRightPanel();

    }

    private void createTopPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.weighty = 0.01;
        c.fill = GridBagConstraints.BOTH;

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton testButton = new JButton("test");
        testButton.addActionListener(e -> {
            // TODO Auto-generated method stub

        });
        topPanel.add(testButton);

        this.container.add(topPanel, c);
    }



    private void createCenterPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

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

        this.container.add(splitPane, c);
    }

    private void createLeftPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        this.leftPanel = new JPanel(new GridBagLayout());
        this.leftPanel.setPreferredSize(new Dimension(450, 0));
        this.leftPanel.setMinimumSize(new Dimension(0, 0));
        this.splitPane.setLeftComponent(leftPanel);

        this.leftTabs = new JTabbedPane(JTabbedPane.LEFT);
        this.leftTabs.setFont(TC.Fonts.h4);
        this.leftTabs.addMouseListener(new ListTabPressed(this));
        this.leftTabs.setPreferredSize(new Dimension(200, 200));
        this.leftTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.leftPanel.add(this.leftTabs, c);
        createUnsortedTab();
        createSortedTab();

    }

    private void createUnsortedTab() {
        GridBagConstraints cc = new GridBagConstraints();

        JPanel unsortedPanel = new JPanel(new GridBagLayout());

        this.leftTabs.add(createScrollView(unsortedPanel),
                "Unsorted");

        JLabel tabLabel = new JLabel("Unsorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(0, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel unsortedLabel = new JLabel("Unsorted");
        unsortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unsortedLabel.setFont(TC.Fonts.h3);
        cc.gridy = 0;
        cc.weighty = 0.01;
        cc.anchor = GridBagConstraints.NORTH;
        unsortedPanel.add(unsortedLabel, cc);

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        cc.gridy = 1;
        cc.weighty = 1;
        unsortedPanel.add(listContainer, cc);

        unsortedListLabel = new ListTab(this, unsortedThoughtList, unsortedListModel);
        listContainer.add(unsortedListLabel, BorderLayout.CENTER);
    }

    private void createSortedTab() {
        GridBagConstraints cc = new GridBagConstraints();

        JPanel sortedPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(sortedPanel), "Sorted");

        JLabel tabLabel = new JLabel("Sorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(1, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel sortedLabel = new JLabel("Sorted");
        sortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sortedLabel.setFont(TC.Fonts.h3);
        cc.gridy = 0;
        cc.weighty = 0.01;
        cc.anchor = GridBagConstraints.NORTH;
        sortedPanel.add(sortedLabel, cc);

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        cc.gridy = 1;
        cc.weighty = 1;
        sortedPanel.add(listContainer, cc);

        sortedListLabel = new ListTab(this, sortedThoughtList, sortedListModel);
        listContainer.add(sortedListLabel, BorderLayout.CENTER);

    }

    private void createTagTabs(DefaultListModel<String> model,
            ArrayList<ThoughtObject> arrayList, String tagName) {

        GridBagConstraints cc = new GridBagConstraints();

        JPanel tagPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(tagPanel), tagName);

        JLabel tabLabel = new JLabel(tagName, SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(numTags, tabLabel);
        numTags++;

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel tagNameLabel = new JLabel(tagName);
        tagNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tagNameLabel.setFont(TC.Fonts.h3);

        cc.gridy = 0;
        cc.weighty = 0.01;
        cc.anchor = GridBagConstraints.NORTH;
        tagPanel.add(tagNameLabel, cc);

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        cc.gridy = 1;
        cc.weighty = 1;
        tagPanel.add(listContainer, cc);

        ListTab tagListLabel = new ListTab(this, arrayList, model);
        listContainer.add(tagListLabel, BorderLayout.CENTER);

    }

    private JScrollPane createScrollView(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new ScrollBar());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private void createRightPanel() {
        /* Houses the entire right panel */
        rightPanel = new RightPanel(this);
        splitPane.setRightComponent(rightPanel);

    }



    private final Map<String, ThoughtObject> thoughtMap = new HashMap<>();

    public ThoughtObject readFileContents(File filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            JSONObject json = (JSONObject) new JSONParser().parse(sb.toString());
            ThoughtObject thought = new ThoughtObject(
                    json.get("title").toString().trim(),
                    json.get("date").toString().trim(),
                    json.get("tag").toString().trim(),
                    json.get("body").toString().trim(),
                    filePath);
            thoughtMap.put(filePath.getName(), thought);
            return thought;
        } catch (Exception e) {
            System.err.printf("Found invalid file '%s'.%n", filePath.toPath());
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
        File[] unsortedFileDirectory = TC.Paths.UNSORTED_DIRECTORY_PATH.listFiles();
        File[] sortedFileDirectory = TC.Paths.SORTED_DIRECTORY_PATH.listFiles();
        // Resets number of tags to 2
        this.numTags = 2;

        // Stores currently selected tab
        final int selectedTab = leftTabs.getSelectedIndex();

        leftTabs.removeAll();

        // Resets all models and lists
        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        unsortedFiles.clear();
        sortedFiles.clear();
        unsortedListModel.clear();
        sortedListModel.clear();
        tagList.clear();

        /* UNSORTED FILES */
        for (File file : unsortedFileDirectory) {
            ThoughtObject content = readFileContents(file);
            if (content != null) {
                unsortedThoughtList.add(content);
                unsortedListModel.addElement(content.getTitle());
                unsortedFiles.add(file);
            }
        }

        /* SORTED FILES */
        for (File file : sortedFileDirectory) {
            ThoughtObject content = readFileContents(file);
            if (content != null) {
                sortedThoughtList.add(content);
                sortedListModel.addElement(content.getTitle());
                sortedFiles.add(file);
            }
        }
        createUnsortedTab();
        createSortedTab();
        setTagModel();

        try {
            leftTabs.setSelectedIndex(selectedTab);
        } catch (Exception e) {
            leftTabs.setSelectedIndex(selectedTab - 1);
        }

        if (this.db.isOnline && this.db.getList() != null) {
            this.db.refreshPushPullLabels();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total refresh time: " + (endTime - startTime) + "ms");
    }

    public void setTagModel() {
        ArrayList<String> tagNames = new ArrayList<>();
        for (File sortedFile : sortedFiles) {
            ThoughtObject content = readFileContents(sortedFile);

            if (content != null) {
                if (!tagNames.contains(content.getTag())) {
                    tagNames.add(content.getTag());
                }
            }

        }
        tagNames.sort(String::compareToIgnoreCase);
        for (String tagName : tagNames) {
            DefaultListModel<String> tempTagModel = new DefaultListModel<>();
            ArrayList<ThoughtObject> thoughtObjectList = new ArrayList<>();
            for (File sortedFile : sortedFiles) {
                ThoughtObject content = readFileContents(sortedFile);
                if (content != null && content.getTag().equals(tagName)) {
                    tempTagModel.addElement(content.getTitle());
                    thoughtObjectList.add(content);
                }
            }
            createTagTabs(tempTagModel, thoughtObjectList, tagName);
        }
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
        public boolean dispatchKeyEvent(KeyEvent e) {
            // 401 is key-down
            if (e.getID() != 401) {
                return false;
            }
            int key = e.getKeyCode();
            boolean c = e.isControlDown();

            switch (key) {
                case KeyEvent.VK_Z -> { // Undo
                    if (c) {
                        try {
                            thoughtsPCS.firePropertyChange(TC.Properties.UNDO);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
                case KeyEvent.VK_Y -> { // Redo
                    if (c) {
                        try {
                            thoughtsPCS.firePropertyChange(TC.Properties.REDO);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
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
                        if (e.isShiftDown()) { // Pull
                            thoughtsPCS.firePropertyChange(TC.Properties.PULL);

                        } else { // Push
                            thoughtsPCS.firePropertyChange(TC.Properties.PUSH);
                        }
                    }
                }
            }

            return false;
        }

    }

    public class CellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setHorizontalAlignment(JLabel.CENTER);
            c.setPreferredSize(new Dimension(25, 25));
            c.setOpaque(true);
            return c;
        }
    }

}
