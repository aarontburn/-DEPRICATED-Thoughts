package com.beanloaf.main;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.events.FirebaseHandler;
import com.beanloaf.events.SettingsHandler;
import com.beanloaf.events.TextAreaFocusListener;
import com.beanloaf.input.FileActionButtonPressed;
import com.beanloaf.input.KeyChange;
import com.beanloaf.input.ListItemPressed;
import com.beanloaf.input.ListTabPressed;
import com.beanloaf.input.TabKeyPressed;
import com.beanloaf.objects.ListTab;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.res.theme.ThoughtsTheme;

/**
 * 
 * @author beanloaf
 */
public class ThoughtsMain {

    public ThoughtObject selectedFile;
    public JFrame window;
    public JPanel container;

    public ArrayList<File> unsortedFiles = new ArrayList<>();
    public ArrayList<File> sortedFiles = new ArrayList<>();
    public ArrayList<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public ArrayList<ThoughtObject> sortedThoughtList = new ArrayList<>();

    /* Input Fields */
    public JLabel dateLabel;

    /* Right Panel Input Fields */
    public JTextArea titleLabel, tagLabel, bodyLabel;
    public UndoManager undo = new UndoManager();
    public JButton sortButton, deleteButton, newFileButton;
    /* Right Panel Ghost Text */
    public JLabel emptyTitle, emptyTag, emptyBody;

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
    public JLabel pushLabel, pullLabel;
    public JButton pullButton, pushButton;

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
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ThoughtsMain();
            }
        });
    }

    public ThoughtsMain() {
        createGUI();
        this.window.setVisible(true);
        onStartUp();

    }

    private void onStartUp() {
        if (!TC.UNSORTED_DIRECTORY_PATH.isDirectory()) {
            TC.UNSORTED_DIRECTORY_PATH.mkdirs();
        }
        if (!TC.SORTED_DIRECTORY_PATH.isDirectory()) {
            TC.SORTED_DIRECTORY_PATH.mkdir();
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
                settings.changeWindowDimension(window.getSize());
                settings.changeIsMaximized(
                        window.getExtendedState() == JFrame.MAXIMIZED_BOTH);
                settings.changeWindowPosition(window.getLocation());
            }
        });

        int extendedState = JFrame.NORMAL;
        if (settings.getIsMaximized() == true) {
            extendedState = JFrame.MAXIMIZED_BOTH;
        }
        this.window.setExtendedState(extendedState);

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyBinds());

        this.container = new JPanel(new GridBagLayout());
        this.window.add(this.container);

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
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

            }
        }, AWTEvent.MOUSE_EVENT_MASK);

        // createTopPanel();
        createCenterPanel();
        createLeftPanel();
        createRightPanel();
        // createBottomPanel();

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

        this.container.add(topPanel, c);
    }

    private void createBottomPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.weighty = 0.12;
        c.fill = GridBagConstraints.BOTH;

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        this.container.add(bottomPanel, c);
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
        this.leftTabs.setFont(TC.h4);
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
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(0, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel unsortedLabel = new JLabel("Unsorted");
        unsortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unsortedLabel.setFont(TC.h3);
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
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(1, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel sortedLabel = new JLabel("Sorted");
        sortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sortedLabel.setFont(TC.h3);
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
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(numTags, tabLabel);
        numTags++;

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.gridx = 0;

        JLabel tagNameLabel = new JLabel(tagName);
        tagNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tagNameLabel.setFont(TC.h3);

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
        // TODO refractor this ENTIRE method

        /* Houses the entire right panel */
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(750, 0));
        rightPanel.setMinimumSize(new Dimension(0, 0));
        splitPane.setRightComponent(rightPanel);

        /* Top */
        GridBagConstraints topc = new GridBagConstraints();
        topc.gridx = 0;
        topc.gridy = 0;
        topc.weightx = 0.1;
        topc.weighty = 0.005;
        topc.anchor = GridBagConstraints.WEST;

        /* Settings */
        JPanel settingsBar = new JPanel(new GridBagLayout());
        topc.fill = GridBagConstraints.BOTH;
        // settingsBar.setBorder(BorderFactory.createLineBorder(Color.white));
        rightPanel.add(settingsBar, topc);

        GridBagConstraints settingsConstraints = new GridBagConstraints();
        settingsConstraints.weightx = 0.01;
        settingsConstraints.weighty = 0.1;
        settingsConstraints.fill = GridBagConstraints.VERTICAL;
        settingsConstraints.insets = new Insets(10, 15, 0, 0);
        settingsConstraints.anchor = GridBagConstraints.LINE_START;

        Dimension buttonDim = new Dimension(90, 35);
        pushButton = new JButton("Push");
        pushButton.setPreferredSize(buttonDim);
        pushButton.setFont(TC.h4);
        settingsConstraints.gridx = 0;
        settingsBar.add(pushButton, settingsConstraints);
        pushButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (db != null) {
                    db.push();
                }
            }
        });

        pushLabel = new JLabel("");
        pushLabel.setFont(TC.h5);
        settingsConstraints.gridx = 1;
        settingsConstraints.weightx = 0.1;
        settingsBar.add(pushLabel, settingsConstraints);

        pullButton = new JButton("Pull");
        pullButton.setPreferredSize(buttonDim);
        pullButton.setFont(TC.h4);
        settingsConstraints.gridx = 2;
        settingsConstraints.weightx = 0.001;
        settingsBar.add(pullButton, settingsConstraints);
        pullButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (db != null) {
                    db.pull();
                }
            }
        });

        pullLabel = new JLabel("");
        pullLabel.setFont(TC.h5);
        settingsConstraints.gridx = 3;
        settingsConstraints.weightx = 0.3;
        settingsBar.add(pullLabel, settingsConstraints);

        JButton settingsButton = new JButton();
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setOpaque(false);
        settingsButton.setIcon(new ImageIcon(TC.ICON_DIRECTORY + "/gear.png"));
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("here");

            }
        });
        settingsConstraints.gridx = 4;
        settingsConstraints.weightx = 0.9;
        settingsConstraints.anchor = GridBagConstraints.LINE_END;
        settingsBar.add(settingsButton, settingsConstraints);

        /* End of Settings */
        topc.weightx = 0.1;
        topc.weighty = 0.1;

        topc.fill = GridBagConstraints.NONE;
        topc.gridy = 1;
        topc.insets = new Insets(-30, 10, -50, 0);

        JPanel topLabel = new JPanel(new GridBagLayout());
        // topLabel.setBorder(BorderFactory.createLineBorder(Color.blue));
        topLabel.setOpaque(false);
        rightPanel.add(topLabel, topc);
        topc.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints cc = new GridBagConstraints();
        cc.anchor = GridBagConstraints.LINE_START;

        GridBagConstraints titlePanelConstraints = new GridBagConstraints();
        titlePanelConstraints.gridy = 0;
        titlePanelConstraints.weighty = 0.1;
        titlePanelConstraints.anchor = GridBagConstraints.LINE_START;

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanelConstraints.gridx = 0;
        titlePanelConstraints.weightx = 0.1;
        titlePanel.add(createCheckBox("lockTitle"), titlePanelConstraints);

        titleLabel = new JTextArea(TC.DEFAULT_TITLE);
        titleLabel.setColumns(6);
        titleLabel.setOpaque(false);
        titleLabel.setFont(TC.h1);
        titleLabel.setName("titleLabel");
        titleLabel.getDocument().putProperty("filterNewlines", true);
        titleLabel.getDocument().addUndoableEditListener(undo);
        titleLabel.getDocument().addDocumentListener(new KeyChange(this));
        titleLabel.getDocument().putProperty("labelType", titleLabel);
        titleLabel.addKeyListener(new TabKeyPressed(this, titleLabel));
        titleLabel.addFocusListener(new TextAreaFocusListener(this));
        titleLabel.setLayout(new GridBagLayout());

        GridBagConstraints et = new GridBagConstraints();
        et.weightx = 0.1;
        et.weighty = 0.1;
        et.anchor = GridBagConstraints.NORTHWEST;
        emptyTitle = new JLabel("");
        emptyTitle.setFont(TC.h1);
        emptyTitle.setOpaque(false);
        emptyTitle.setEnabled(false);
        emptyTitle.setVerticalAlignment(JLabel.CENTER);

        titlePanel.add(emptyTitle, et);
        titlePanelConstraints.gridx = 1;
        titlePanelConstraints.weightx = 0.9;
        titlePanel.add(titleLabel, titlePanelConstraints);

        cc.gridx = 0;
        topLabel.add(titlePanel, cc);

        GridBagConstraints tagPanelConstraints = new GridBagConstraints();
        tagPanelConstraints.gridy = 0;
        tagPanelConstraints.weighty = 0.1;
        tagPanelConstraints.anchor = GridBagConstraints.LINE_START;

        JPanel tagPanel = new JPanel(new GridBagLayout());
        tagPanelConstraints.gridx = 0;
        tagPanelConstraints.weightx = 0.1;

        tagPanel.add(createCheckBox("lockTag"), tagPanelConstraints);

        tagLabel = new JTextArea(TC.DEFAULT_TAG);
        tagLabel.setColumns(9);
        tagLabel.setOpaque(false);
        tagLabel.setFont(TC.h3);
        tagLabel.getDocument().addDocumentListener(new KeyChange(this));
        tagLabel.getDocument().putProperty("labelType", tagLabel);
        tagLabel.setName("tagLabel");
        tagLabel.addFocusListener(new TextAreaFocusListener(this));
        tagLabel.getDocument().putProperty("filterNewlines", true);
        tagLabel.getDocument().addUndoableEditListener(undo);
        tagLabel.addKeyListener(new TabKeyPressed(this, tagLabel));
        tagLabel.setLayout(new GridBagLayout());
        cc.gridy = 1;

        // Ghost text for the tag
        emptyTag = new JLabel("");
        emptyTag.setFont(TC.h3);
        emptyTag.setOpaque(false);
        emptyTag.setEnabled(false);
        tagLabel.add(emptyTag, et);

        tagPanelConstraints.gridx = 1;
        tagPanelConstraints.weightx = 0.9;
        tagPanel.add(tagLabel, titlePanelConstraints);
        topLabel.add(tagPanel, cc);

        dateLabel = new JLabel("Created on: " + TC.DEFAULT_DATE);
        dateLabel.setFont(TC.h4);
        cc.gridx = 0;
        cc.gridy = 2;
        topLabel.add(dateLabel, cc);

        /* Bottom */
        GridBagConstraints botc = new GridBagConstraints();
        botc.weightx = 0.1;
        botc.weighty = 0.001;
        botc.gridx = 0;
        botc.gridy = 2;
        botc.anchor = GridBagConstraints.NORTHEAST;
        rightPanel.add(createCheckBox("lockBody"), botc);

        bodyLabel = new JTextArea(TC.DEFAULT_BODY);
        bodyLabel.setBackground(new Color(32, 32, 32));
        bodyLabel.setFont(TC.p);
        bodyLabel.setLineWrap(true);
        bodyLabel.setWrapStyleWord(true);
        bodyLabel.getDocument().addDocumentListener(new KeyChange(this));
        bodyLabel.getDocument().putProperty("labelType", bodyLabel);
        bodyLabel.setName("bodyLabel");
        bodyLabel.addFocusListener(new TextAreaFocusListener(this));
        bodyLabel.getDocument().addUndoableEditListener(undo);
        bodyLabel.setLayout(new GridBagLayout());
        botc.weightx = 0.1;
        botc.weighty = 0.9;
        botc.gridx = 0;
        botc.gridy = 3;
        botc.insets = new Insets(5, 5, 5, 5);
        botc.anchor = GridBagConstraints.CENTER;
        botc.fill = GridBagConstraints.BOTH;

        JScrollPane bodyScroll = new JScrollPane(bodyLabel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bodyScroll.setBorder(null);
        bodyScroll.setPreferredSize(new Dimension(0, 0));
        bodyScroll.getVerticalScrollBar().setUI(new ScrollBar());
        rightPanel.add(bodyScroll, botc);

        // Ghost text for body
        emptyBody = new JLabel("");
        emptyBody.setFont(TC.p);
        emptyBody.setOpaque(false);
        emptyBody.setEnabled(false);
        bodyLabel.add(emptyBody, et);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        botc.weightx = 0.3;
        botc.weighty = 0.05;
        botc.gridx = 0;
        botc.gridy = 4;
        rightPanel.add(buttonPanel, botc);

        GridBagConstraints bc = new GridBagConstraints();
        bc.weightx = 0.3;
        bc.weighty = 0.1;
        bc.fill = GridBagConstraints.BOTH;

        sortButton = new JButton("Sort/Unsort");
        sortButton.setName("sort");
        sortButton.setFont(TC.h4);
        sortButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(sortButton, bc);

        deleteButton = new JButton("Delete");
        deleteButton.setName("delete");
        deleteButton.setFont(TC.h4);
        deleteButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(deleteButton, bc);

        newFileButton = new JButton("New File");
        newFileButton.setName("newFile");
        newFileButton.setFont(TC.h4);
        newFileButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(newFileButton, bc);

    }

    private JCheckBox createCheckBox(String actionName) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setIcon(new ImageIcon(TC.ICON_DIRECTORY + "closed_lock.png"));
        checkBox.setSelectedIcon(new ImageIcon(TC.ICON_DIRECTORY + "open_lock_1.png"));
        checkBox.setName(actionName);
        checkBox.setOpaque(false);
        checkBox.setRolloverEnabled(false);

        switch (actionName) {
            case "lockTitle":
                checkBox.setSelected(settings.isTitleLocked());
                break;
            case "lockTag":
                checkBox.setSelected(settings.isTagLocked());
                break;
            case "lockBody":
                checkBox.setSelected(settings.isBodyLocked());
                break;
            default:
                throw new IllegalArgumentException();
        }

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                switch (box.getName()) {
                    case "lockTitle":
                        settings.changeLockTitle(box.isSelected());
                        break;
                    case "lockTag":
                        settings.changeLockTag(box.isSelected());
                        break;
                    case "lockBody":
                        settings.changeLockBody(box.isSelected());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        });
        return checkBox;
    }

    private Map<String, ThoughtObject> thoughtMap = new HashMap<>();

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
            System.err.println(
                    String.format(
                            "Found invalid file '%s'.", filePath.toPath()));
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
        File[] unsortedFileDirectory = TC.UNSORTED_DIRECTORY_PATH.listFiles();
        File[] sortedFileDirectory = TC.SORTED_DIRECTORY_PATH.listFiles();
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
        for (int i = 0; i < sortedFiles.size(); i++) {
            ThoughtObject content = readFileContents(sortedFiles.get(i));

            if (content != null) {
                if (!tagNames.contains(content.getTag())) {
                    tagNames.add(content.getTag());
                }
            }

        }
        tagNames.sort(String::compareToIgnoreCase);
        for (int i = 0; i < tagNames.size(); i++) {
            DefaultListModel<String> tempTagModel = new DefaultListModel<>();
            ArrayList<ThoughtObject> thoughtObjectList = new ArrayList<>();
            for (int j = 0; j < sortedFiles.size(); j++) {
                ThoughtObject content = readFileContents(sortedFiles.get(j));
                if (content != null && content.getTag().equals(tagNames.get(i))) {
                    tempTagModel.addElement(content.getTitle());
                    thoughtObjectList.add(content);
                }
            }
            createTagTabs(tempTagModel, thoughtObjectList, tagNames.get(i));
        }
    }

    public void selectTextField(JTextArea textArea) {
        textArea.requestFocusInWindow();
        textArea.selectAll();
    }

    public class ScrollBar extends BasicScrollBarUI {
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
                case KeyEvent.VK_Z: // Undo
                    if (c) {
                        try {
                            undo.undo();
                        } catch (Exception err) {
                        }
                    }
                    break;

                case KeyEvent.VK_Y: // Redo
                    if (c) {
                        try {
                            undo.redo();
                        } catch (Exception err) {
                        }
                    }
                    break;

                case KeyEvent.VK_N: // New File
                    if (c) {
                        newFileButton.doClick();
                    }
                    break;

                case KeyEvent.VK_D: // Delete File
                    if (c) {
                        deleteButton.doClick();
                    }
                    break;

                case KeyEvent.VK_Q: // Sort File
                    if (c) {
                        sortButton.doClick();
                    }
                    break;
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
