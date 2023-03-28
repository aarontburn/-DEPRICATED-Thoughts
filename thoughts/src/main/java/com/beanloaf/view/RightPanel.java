package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.input.FileActionButtonPressed;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.textfields.BodyTextArea;
import com.beanloaf.textfields.TagTextArea;
import com.beanloaf.textfields.AbstractTextArea;
import com.beanloaf.textfields.TitleTextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RightPanel extends JPanel implements PropertyChangeListener {

    private static final String NOT_CONNECTED_MESSAGE = "Not connected";

    private final Thoughts main;
    public JLabel dateLabel, pushLabel, pullLabel;

    public AbstractTextArea titleTextArea, tagTextArea;
    public BodyTextArea bodyTextArea;
    public final UndoManager undoManager = new UndoManager();
    public JButton sortButton, deleteButton, newFileButton, pullButton, pushButton;
    private JLabel userNameLabel;


    public RightPanel(final Thoughts main) {
        super();

        this.main = main;
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(750, 0));
        this.setMinimumSize(new Dimension(0, 0));
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);
        createUI();
    }

    private void createUI() {
        createSettingsBar();

        /* Top */
        final JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        this.add(topPanel, new GBC(0, 1)
                .setAnchor(GBC.Anchor.WEST)
                .setInsets(-20, 10, -40, 0));

        final GBC topPanelConstraints = new GBC().setAnchor(GBC.Anchor.LINE_START);

        final GBC titlePanelConstraints = new GBC().setAnchor(GBC.Anchor.LINE_START);
        final JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(createCheckBox("lockTitle"), titlePanelConstraints);
        titleTextArea = new TitleTextArea(main, undoManager);
        titlePanel.add(titleTextArea, titlePanelConstraints.increaseGridX().setWeightY(0.9));
        topPanel.add(titlePanel, topPanelConstraints);

        final GBC tagPanelConstraints = new GBC().setAnchor(GBC.Anchor.LINE_START);
        final JPanel tagPanel = new JPanel(new GridBagLayout());
        tagPanel.add(createCheckBox("lockTag"), tagPanelConstraints);
        tagTextArea = new TagTextArea(main, undoManager);
        tagPanel.add(tagTextArea, tagPanelConstraints.increaseGridX().setWeightY(0.9));
        topPanel.add(tagPanel, topPanelConstraints.increaseGridY());


        dateLabel = new JLabel("Created on: " + TC.DEFAULT_DATE);
        dateLabel.setFont(TC.Fonts.h4);
        topPanel.add(dateLabel, topPanelConstraints.increaseGridY());

        /* Bottom */
        this.add(createCheckBox("lockBody"), new GBC(0, 2, 0.1, 0.001)
                .setAnchor(GBC.Anchor.NORTHEAST));

        bodyTextArea = new BodyTextArea(main, undoManager);
        final JScrollPane bodyScroll = new JScrollPane(
                bodyTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        bodyScroll.setBorder(null);
        bodyScroll.setPreferredSize(new Dimension(0, 0));
        bodyScroll.setMaximumSize(new Dimension(0, 0));
        bodyScroll.getVerticalScrollBar().setUI(new TC.ScrollBar());

        this.add(bodyScroll, new GBC(0, 3, 0.1, 0.9)
                .setAnchor(GBC.Anchor.CENTER)
                .setInsets(5, 5, 5, 5)
                .setFill(GBC.Fill.BOTH));

        createFileButtons();

    }

    private void createFileButtons() {
        // Buttons
        final JPanel buttonPanel = new JPanel(new GridBagLayout());
        this.add(buttonPanel, new GBC(0, 4, 0.3, 0.05)
                .setAnchor(GBC.Anchor.CENTER)
                .setInsets(5, 5, 5, 5)
                .setFill(GBC.Fill.BOTH));


        final GBC buttonConstraints = new GBC().setWeightXY(0.3, 0.1).setFill(GBC.Fill.BOTH);
        sortButton = new JButton("Sort/Unsort");
        sortButton.setName("sort");
        sortButton.setFont(TC.Fonts.h4);
        sortButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(sortButton, buttonConstraints);

        newFileButton = new JButton("New File");
        newFileButton.setName("newFile");
        newFileButton.setFont(TC.Fonts.h4);
        newFileButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(newFileButton, buttonConstraints.increaseGridX());

        deleteButton = new JButton("Delete");
        deleteButton.setName("delete");
        deleteButton.setFont(TC.Fonts.h4);
        deleteButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(deleteButton, buttonConstraints.increaseGridX());

    }

    private void createSettingsBar() {
        final JPanel settingsBar = new JPanel(new GridBagLayout());
        this.add(settingsBar, new GBC().setWeightY(0.005)
                .setAnchor(GBC.Anchor.WEST)
                .setFill(GBC.Fill.BOTH));

        final GBC settingConstraints = new GBC()
                .setFill(GBC.Fill.HORIZONTAL)
                .setAnchor(GBC.Anchor.LINE_START)
                .setInsets(10, 15, 0, 0);


        userNameLabel = new JLabel(main.db.user == null ? NOT_CONNECTED_MESSAGE : "Logged in as: " + main.db.user.displayName());
        userNameLabel.setFont(TC.Fonts.h4);
        settingsBar.add(userNameLabel, settingConstraints.setGridWidth(GBC.Grid.REMAINDER));

        final Dimension buttonDim = new Dimension(90, 35);
        pushButton = new JButton("Push");
        pushButton.setPreferredSize(buttonDim);
        pushButton.setFont(TC.Fonts.h4);
        settingsBar.add(pushButton, settingConstraints.setGridWidth(1).increaseGridY());
        pushButton.addActionListener(event -> main.db.push());

        pushLabel = new JLabel("");
        pushLabel.setOpaque(false);
        pushLabel.setFont(TC.Fonts.h5);
        settingsBar.add(pushLabel, settingConstraints.increaseGridX());

        pullButton = new JButton("Pull");
        pullButton.setPreferredSize(buttonDim);
        pullButton.setFont(TC.Fonts.h4);
        settingsBar.add(pullButton, settingConstraints.increaseGridX());
        pullButton.addActionListener(event -> main.db.pull());

        pullLabel = new JLabel("");
        pullLabel.setOpaque(false);
        pullLabel.setFont(TC.Fonts.h5);
        settingsBar.add(pullLabel, settingConstraints.increaseGridX());

    }

    private JCheckBox createCheckBox(final String actionName) {
        final JCheckBox checkBox = new JCheckBox();
        checkBox.setIcon(new ImageIcon(TC.Paths.ICON_DIRECTORY + "open_lock.png"));
        checkBox.setSelectedIcon(new ImageIcon(TC.Paths.ICON_DIRECTORY + "closed_lock.png"));
        checkBox.setName(actionName);
        checkBox.setOpaque(false);
        checkBox.setRolloverEnabled(false);

        switch (actionName) {
            case "lockTitle" -> checkBox.setSelected(main.settings.isTitleLocked());
            case "lockTag" -> checkBox.setSelected(main.settings.isTagLocked());
            case "lockBody" -> checkBox.setSelected(main.settings.isBodyLocked());
            default -> throw new IllegalArgumentException();
        }

        checkBox.addItemListener(event -> {
            final JCheckBox box = (JCheckBox) event.getSource();
            switch (box.getName()) {
                case "lockTitle" -> main.settings.changeLockTitle(box.isSelected());
                case "lockTag" -> main.settings.changeLockTag(box.isSelected());
                case "lockBody" -> main.settings.changeLockBody(box.isSelected());
                default -> throw new IllegalArgumentException();
            }
        });
        return checkBox;
    }

    public void checkEmpty() {
        titleTextArea.changedUpdate(null);
        tagTextArea.changedUpdate(null);
        bodyTextArea.changedUpdate(null);
    }

    public void selectTextField(final JTextArea textArea) {
        textArea.requestFocusInWindow();
        textArea.selectAll();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {

        switch (event.getPropertyName()) {
            case TC.Properties.PUSH -> this.pushButton.doClick();
            case TC.Properties.UNDO -> {
                try {
                    this.undoManager.undo();
                } catch (Exception e) {

                }
            }
            case TC.Properties.REDO -> {
                try {
                    this.undoManager.redo();
                } catch (Exception e) {

                }
            }
            case TC.Properties.NEW_FILE -> this.newFileButton.doClick();
            case TC.Properties.DELETE -> this.deleteButton.doClick();
            case TC.Properties.SORT -> this.sortButton.doClick();
            case TC.Properties.DISCONNECTED -> {
                userNameLabel.setText(NOT_CONNECTED_MESSAGE);
                pullLabel.setText("Not connected.");
                pushLabel.setText("Not connected.");
                pullButton.setEnabled(false);
                pushButton.setEnabled(false);
            }
            case TC.Properties.CONNECTED -> {
                userNameLabel.setText(main.db.user == null ? NOT_CONNECTED_MESSAGE : "Logged in as: " + main.db.user.displayName());
                pullButton.setEnabled(true);
                pushButton.setEnabled(true);
            }
            case TC.Properties.UNPULLED_FILES -> pullLabel.setText(event.getNewValue() + " files can be pulled.");
            case TC.Properties.UNPUSHED_FILES -> pushLabel.setText(event.getNewValue() + " files not pushed.");

            case TC.Properties.TEXT -> {
                final StyledDocument doc = bodyTextArea.getStyledDocument();
                bodyTextArea.getInputAttributes().removeAttributes(bodyTextArea.getInputAttributes());

                final ThoughtObject textObject = (ThoughtObject) event.getNewValue();
                final boolean enabledFields = textObject.getPath() != null;

                titleTextArea.setEnabled(enabledFields);
                tagTextArea.setEnabled(enabledFields);
                bodyTextArea.setEnabled(enabledFields);

                titleTextArea.setText(textObject.getTitle());
                tagTextArea.setText(textObject.getTag());
                dateLabel.setText(enabledFields ? "Created on: " + textObject.getDate() : " ");
                bodyTextArea.setText(textObject.getBody());
                undoManager.discardAllEdits();


                try {
                    final JSONArray stylesArray = (JSONArray) new JSONParser().parse(textObject.getStylesList());
                    for (int i = 0; i < stylesArray.size(); i++) {
                        final JSONObject obj = (JSONObject) stylesArray.get(i);
                        final SimpleAttributeSet attrs = new SimpleAttributeSet();
                        for (final Object key : obj.keySet()) {
                            switch ((String) key) {
                                case "bold" -> StyleConstants.setBold(attrs, Boolean.parseBoolean((String) obj.get(key)));
                                case "italic" -> StyleConstants.setItalic(attrs, Boolean.parseBoolean((String) obj.get(key)));
                                case "underline" -> StyleConstants.setUnderline(attrs, Boolean.parseBoolean((String) obj.get(key)));
                                default -> {
                                }
                            }
                        }

                        doc.setCharacterAttributes(i, 1, attrs, true);
                    }
                } catch (ParseException e) {
//                    e.printStackTrace();
                }


                bodyTextArea.setCaretPosition(0);

            }
            case TC.Properties.LIST_ITEM_PRESSED -> checkEmpty();
            case TC.Properties.FOCUS_TITLE_FIELD -> selectTextField(titleTextArea);
            case TC.Properties.TOGGLE_UNDERLINE -> bodyTextArea.toggleUnderline();
            case TC.Properties.TOGGLE_ITALIC -> bodyTextArea.toggleItalic();
            case TC.Properties.TOGGLE_BOLD -> bodyTextArea.toggleBold();

            default -> {
            }
        }

    }
}
