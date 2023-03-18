package com.beanloaf.view;

import com.beanloaf.input.FileActionButtonPressed;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.TextPropertyObject;
import com.beanloaf.res.TC;
import com.beanloaf.textfields.BodyTextArea;
import com.beanloaf.textfields.TagTextArea;
import com.beanloaf.textfields.AbstractTextArea;
import com.beanloaf.textfields.TitleTextArea;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RightPanel extends JPanel implements PropertyChangeListener {

    private final Thoughts main;

    public JLabel dateLabel, pushLabel, pullLabel;
    public AbstractTextArea titleLabel, tagLabel, bodyLabel;
    public final UndoManager undoManager = new UndoManager();
    public JButton sortButton, deleteButton, newFileButton, pullButton, pushButton;


    public RightPanel(final Thoughts main) {
        super();
        this.main = main;
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(750, 0));
        this.setMinimumSize(new Dimension(0, 0));
        main.thoughtsPCS.addPropertyChangeListener(this);
        createUI();
    }

    private void createUI() {
        createSettingsBar();

        /* Top */
        final JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        this.add(topPanel, new GBC(0, 1).setAnchor(GridBagConstraints.WEST)
                .setInsets(-20, 10, -40, 0));

        final GBC topPanelConstraints = new GBC().setAnchor(GridBagConstraints.LINE_START);

        final GBC titlePanelConstraints = new GBC().setAnchor(GridBagConstraints.LINE_START);
        final JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(createCheckBox("lockTitle"), titlePanelConstraints.setGridX(0).setWeightY(0.1));
        titleLabel = new TitleTextArea(main, undoManager);
        titlePanel.add(titleLabel, titlePanelConstraints.setGridX(1).setWeightY(0.9));
        topPanel.add(titlePanel, topPanelConstraints.setGridY(0));

        final GBC tagPanelConstraints = new GBC().setAnchor(GridBagConstraints.LINE_START);
        final JPanel tagPanel = new JPanel(new GridBagLayout());
        tagPanel.add(createCheckBox("lockTag"), tagPanelConstraints);
        tagLabel = new TagTextArea(main, undoManager);
        tagPanel.add(tagLabel, tagPanelConstraints.setGridX(1).setWeightY(0.9));
        topPanel.add(tagPanel, topPanelConstraints.setGridY(1));


        dateLabel = new JLabel("Created on: " + TC.DEFAULT_DATE);
        dateLabel.setFont(TC.Fonts.h4);
        topPanel.add(dateLabel, topPanelConstraints.setGridY(2));

        /* Bottom */
        this.add(createCheckBox("lockBody"), new GBC(0, 2, 0.1, 0.001)
                .setAnchor(GridBagConstraints.NORTHEAST));

        bodyLabel = new BodyTextArea(main, undoManager);
        final JScrollPane bodyScroll = new JScrollPane(bodyLabel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bodyScroll.setBorder(null);
        bodyScroll.setPreferredSize(new Dimension(0, 0));
        bodyScroll.getVerticalScrollBar().setUI(new TC.ScrollBar());
        this.add(bodyScroll, new GBC(0, 3, 0.1, 0.9)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(5, 5, 5, 5)
                .setFill(GridBagConstraints.BOTH));

        createFileButtons();

    }

    private void createFileButtons() {
        // Buttons
        final JPanel buttonPanel = new JPanel(new GridBagLayout());
        this.add(buttonPanel, new GBC(0, 4, 0.3, 0.05)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(5, 5, 5, 5)
                .setFill(GridBagConstraints.BOTH));


        final GBC buttonConstraints = new GBC().setWeightXY(0.3, 0.1).setFill(GridBagConstraints.BOTH);
        sortButton = new JButton("Sort/Unsort");
        sortButton.setName("sort");
        sortButton.setFont(TC.Fonts.h4);
        sortButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(sortButton, buttonConstraints.setGridX(0));

        deleteButton = new JButton("Delete");
        deleteButton.setName("delete");
        deleteButton.setFont(TC.Fonts.h4);
        deleteButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(deleteButton, buttonConstraints.setGridX(3));

        newFileButton = new JButton("New File");
        newFileButton.setName("newFile");
        newFileButton.setFont(TC.Fonts.h4);
        newFileButton.addActionListener(new FileActionButtonPressed(this.main));
        buttonPanel.add(newFileButton, buttonConstraints.setGridX(2));
    }

    private void createSettingsBar() {
        /* Settings */
        final JPanel settingsBar = new JPanel(new GridBagLayout());
        this.add(settingsBar, new GBC().setWeightY(0.005)
                .setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.BOTH));

        final GBC settingConstraints = new GBC().setWeightX(0.01)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setAnchor(GridBagConstraints.LINE_START)
                .setInsets(10, 15, 0, 0);

        final Dimension buttonDim = new Dimension(90, 35);
        pushButton = new JButton("Push");
        pushButton.setPreferredSize(buttonDim);
        pushButton.setFont(TC.Fonts.h4);
        settingsBar.add(pushButton, settingConstraints);
        pushButton.addActionListener(event -> {
            if (main.db != null) {
                main.db.push();
            } else {
                System.err.println("not pushing");
            }
        });

        pushLabel = new JLabel("");
        pushLabel.setOpaque(false);
        pushLabel.setFont(TC.Fonts.h5);
        settingsBar.add(pushLabel, settingConstraints.setGridX(1));

        pullButton = new JButton("Pull");
        pullButton.setPreferredSize(buttonDim);
        pullButton.setFont(TC.Fonts.h4);
        settingsBar.add(pullButton, settingConstraints.setGridX(2));
        pullButton.addActionListener(event -> {
            if (main.db != null) {
                main.db.pull();
            }
        });

        pullLabel = new JLabel("");
        pullLabel.setOpaque(false);
        pullLabel.setFont(TC.Fonts.h5);
        settingsBar.add(pullLabel, settingConstraints.setGridX(3));

        final JButton settingsButton = new JButton();
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setOpaque(false);
        settingsButton.setIcon(new ImageIcon(TC.Paths.ICON_DIRECTORY + "/gear.png"));
        settingsButton.addActionListener(event -> new SettingsWindow(main));
        settingsBar.add(settingsButton, settingConstraints.setGridX(4));
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
            JCheckBox box = (JCheckBox) event.getSource();
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
        titleLabel.changedUpdate(null);
        tagLabel.changedUpdate(null);
        bodyLabel.changedUpdate(null);
    }

    public void selectTextField(JTextArea textArea) {
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
                pullLabel.setText("Not connected.");
                pushLabel.setText("Not connected.");
                pullButton.setEnabled(false);
                pushButton.setEnabled(false);
            }
            case TC.Properties.UNPULLED_FILES -> pullLabel.setText(event.getNewValue() + " files can be pulled.");
            case TC.Properties.UNPUSHED_FILES -> pushLabel.setText(event.getNewValue() + " files not pushed.");
            case TC.Properties.TEXT -> {
                final TextPropertyObject textObject = (TextPropertyObject) event.getNewValue();
                titleLabel.setText(textObject.title);
                tagLabel.setText(textObject.tag);
                dateLabel.setText("Created on: " + textObject.date);
                bodyLabel.setText(textObject.body);
            }
            case TC.Properties.LIST_ITEM_PRESSED, TC.Properties.LIST_TAB_PRESSED -> checkEmpty();
            case TC.Properties.FOCUS_TITLE_FIELD -> selectTextField(titleLabel);

            default -> {
            }
        }

    }
}
