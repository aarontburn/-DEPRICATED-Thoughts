package com.beanloaf.main;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.beanloaf.res.TC;
import com.beanloaf.res.theme.ThoughtsTheme;

public class SettingsWindow {

    JFrame window;
    JPanel container;

    JTabbedPane tabs;

    public SettingsWindow() {
        createGUI();
        this.window.setVisible(true);
    }

    private void createGUI() {
        ThoughtsTheme.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.window = new JFrame("Settings");
        this.window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.window.setFocusable(true);
        this.window.setSize(800, 700);
        this.window.setLocationRelativeTo(null);

        this.container = new JPanel(new GridBagLayout());
        this.window.add(this.container);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 0.1;
        this.tabs = new JTabbedPane(JTabbedPane.LEFT);
        this.tabs.setFont(TC.h4);
        this.container.add(this.tabs, c);
        generalSettings();

    }

    private void generalSettings() {
        JPanel generalSettingsPanel = new JPanel(new GridBagLayout());
        generalSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        this.tabs.add(generalSettingsPanel, "General");

        
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.anchor = GridBagConstraints.NORTHWEST;
        panelConstraints.weightx = 0.1;
        panelConstraints.fill = GridBagConstraints.HORIZONTAL;

        panelConstraints.gridy = 0;
        panelConstraints.weighty = 0.01;
        generalSettingsPanel.add(createCheckboxPanel("Push on close"), panelConstraints);

        panelConstraints.gridy = 1;
        panelConstraints.weighty = 0.01;
        generalSettingsPanel.add(createCheckboxPanel("Pull on startup"), panelConstraints);

        panelConstraints.gridy = 2;
        panelConstraints.weighty = 0.9;
        generalSettingsPanel.add(createCheckboxPanel("Dark mode"), panelConstraints);



    }

    private JPanel createCheckboxPanel(String label) {
        JPanel panelContainer = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        c.gridy = 0;

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(TC.h4);
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 50, 0, 0);
        panelContainer.add(textLabel, c);

        JCheckBox checkBox = new JCheckBox();
        c.weightx = 0.9;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 50);
        panelContainer.add(checkBox, c);

        return panelContainer;
    }

}
