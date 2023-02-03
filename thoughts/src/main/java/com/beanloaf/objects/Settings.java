package com.beanloaf.objects;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.beanloaf.common.TC;

public class Settings {

    JFrame window;
    JPanel container;

    JTabbedPane tabbedPane;
    Dimension tagDim = new Dimension(100, 25);

    JCheckBox clearTitleBox, clearTagBox, clearBodyBox;

    public Settings() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }
        createGUI();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }

    private void createGUI() {

        window = new JFrame("Thoughts - Settings");
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        window.setFocusable(true);
        window.setSize(800, 700);

        container = new JPanel();
        container.setBackground(Color.darkGray);
        container.setLayout(new GridBagLayout());
        window.add(container);

        createPanel();
    }

    private void createPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 0.1;
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        container.add(tabbedPane, c);
        generalSettings();
        keyBindSettings();

    }

    private void generalSettings() {
        JPanel settingPanel = new JPanel();
        settingPanel.setBackground(Color.DARK_GRAY);
        settingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
        tabbedPane.add(createScrollView(settingPanel), "General");

        JLabel tabLabel = new JLabel("General", SwingConstants.CENTER);
        tabLabel.setFont(TC.h5);
        tabLabel.setPreferredSize(tagDim);
        tabbedPane.setTabComponentAt(0, tabLabel);

        JLabel sectionTitle = new JLabel("General");
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionTitle.setForeground(Color.white);
        sectionTitle.setFont(TC.h3);
        settingPanel.add(sectionTitle);

        // Divider
        settingPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.white));
        p.setLayout(new GridBagLayout());
        p.setBackground(Color.darkGray);
        settingPanel.add(p);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        // Dark Mode Toggle
        JPanel darkModeGroup = new JPanel();
        darkModeGroup.setOpaque(false);
        c.gridy = 0;
        c.weighty = 0;
        p.add(darkModeGroup, c);

        darkModeGroup.add(createNewSettingLabel("Dark Mode: "));
        String[] darkModeOptions = { "Enabled", "Disabled" };
        JComboBox<String> darkModeDrop = new JComboBox<>(darkModeOptions);
        darkModeDrop.setBackground(Color.darkGray);
        darkModeDrop.setForeground(Color.white);
        // darkModeDrop.addActionListener(null);
        // darkModeDrop.setSelectedIndex(0);
        darkModeGroup.add(darkModeDrop);

        // Clear fields on save new file
        JPanel clearFieldsGroup = new JPanel();
        clearFieldsGroup.setOpaque(false);
        c.gridy = 1;
        c.weighty = 1;
        p.add(clearFieldsGroup, c);

        clearFieldsGroup.add(createNewSettingLabel("Clear fields after saving: "));
        clearFieldsGroup.add(createCheckBoxOption(clearTitleBox, "Title"));
        clearFieldsGroup.add(createCheckBoxOption(clearTagBox, "Tag"));
        clearFieldsGroup.add(createCheckBoxOption(clearBodyBox, "Body"));

    }

    private JCheckBox createCheckBoxOption(JCheckBox checkBox, String optionName) {
        checkBox = new JCheckBox(optionName);
        checkBox.setName(optionName);
        checkBox.setBackground(Color.darkGray);
        checkBox.setForeground(Color.white);
        checkBox.setFont(TC.h4);
        checkBox.addItemListener(new ItemPreseed());
        return checkBox;

    }

    private JLabel createNewSettingLabel(String labelTitle) {
        JLabel label = new JLabel(labelTitle);
        label.setFont(TC.h4);
        label.setForeground(Color.white);
        return label;
    }

    private void keyBindSettings() {
        JPanel keyBindPanel = new JPanel();
        keyBindPanel.setBackground(Color.DARK_GRAY);
        keyBindPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        keyBindPanel.setLayout(new BoxLayout(keyBindPanel, BoxLayout.Y_AXIS));
        tabbedPane.add(createScrollView(keyBindPanel), "KeyBinds");

        JLabel tabLabel = new JLabel("Keybinds", SwingConstants.CENTER);
        tabLabel.setFont(TC.h5);
        tabLabel.setPreferredSize(tagDim);
        tabbedPane.setTabComponentAt(1, tabLabel);

        JLabel keyBindLabel = new JLabel("Keybinds");
        keyBindLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyBindLabel.setForeground(Color.white);
        keyBindLabel.setFont(TC.h3);
        keyBindPanel.add(keyBindLabel);

        keyBindPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JPanel sortPanel = new JPanel();
        sortPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        sortPanel.setBackground(Color.darkGray);
        keyBindPanel.add(sortPanel);

        JLabel sortKeyBind = new JLabel("Sort:");
        sortKeyBind.setAlignmentX(Component.CENTER_ALIGNMENT);
        sortKeyBind.setForeground(Color.white);
        sortKeyBind.setFont(TC.h4);
        sortPanel.add(sortKeyBind);

        JLabel ctrlLabel = new JLabel("CTRL + ");
        ctrlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ctrlLabel.setForeground(Color.white);
        ctrlLabel.setFont(TC.h4);
        sortPanel.add(ctrlLabel);

        JTextField keyLabel = new JTextField("Q");
        keyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyLabel.setForeground(Color.white);
        keyLabel.setBackground(Color.darkGray);
        keyLabel.setPreferredSize(new Dimension(25, 25));
        keyLabel.setFont(TC.h4);
        sortPanel.add(keyLabel);

    }

    private JScrollPane createScrollView(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setBackground(Color.lightGray);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.gray;
            }
        });
        return scroll;
    }

    private class ItemPreseed implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox obj = (JCheckBox) e.getItemSelectable();
            String source = obj.getName();
            switch (source) {
                case "Title":

                    break;

                case "Tag":

                    break;

                case "Body":

                    break;
            }

        }

    }

}
