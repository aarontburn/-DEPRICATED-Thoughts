package com.beanloaf.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.beanloaf.res.TC;
import com.beanloaf.res.theme.ThoughtsTheme;

public class SettingsWindow {

    private final Thoughts main;

    private JFrame window;
    private JPanel container;
    private JTabbedPane tabs;

    public SettingsWindow(Thoughts main) {
        this.main = main;
        createGUI();
        this.window.setVisible(true);
    }

    private void createGUI() {
        ThoughtsTheme.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        this.window = new JFrame("Settings");
        this.window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.window.setFocusable(true);
        this.window.setSize(500, 600);
        this.window.setLocationRelativeTo(null);

        this.container = new JPanel(new GridBagLayout());
        this.container.setBackground(new Color(32, 32, 32));
        this.window.add(this.container);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 0.1;
        this.tabs = new JTabbedPane(JTabbedPane.LEFT);
        this.tabs.setFont(TC.Fonts.h4);
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

        JLabel generalSettingsLabel = new JLabel("General Settings");
        generalSettingsLabel.setBackground(new Color(32, 32, 32));
        generalSettingsLabel.setHorizontalAlignment(JLabel.CENTER);
        generalSettingsLabel.setFont(TC.Fonts.h3);
        panelConstraints.gridy = 0;
        panelConstraints.weighty = 0.1;
        generalSettingsPanel.add(generalSettingsLabel, panelConstraints);

        panelConstraints.gridy = 1;
        panelConstraints.weighty = 0.01;
        generalSettingsPanel.add(createCheckboxPanel("Push on close:", "push"), panelConstraints);

        panelConstraints.gridy = 2;
        panelConstraints.weighty = 0.01;
        generalSettingsPanel.add(createCheckboxPanel("Pull on startup:", "pull"), panelConstraints);

        panelConstraints.gridy = 3;
        panelConstraints.weighty = 0.9;
        generalSettingsPanel.add(createCheckboxPanel("Light mode:", "lightMode"), panelConstraints);

    }

    private JPanel createCheckboxPanel(String label, String actionName) {
        JPanel panelContainer = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        c.gridy = 0;

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(TC.Fonts.h4);
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 30, 0, 0);
        panelContainer.add(textLabel, c);

        JCheckBox checkBox = new JCheckBox();
        c.weightx = 0.9;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 30);

        switch (actionName) {
            case "push":
                checkBox.setSelected(this.main.settings.isPushOnClose());
                break;
            case "pull":
                checkBox.setSelected(this.main.settings.isPullOnStartup());
                break;
            case "lightMode":

                break;
            default:
                throw new IllegalArgumentException();
        }

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (actionName) {
                    case "push":
                        main.settings.changePushOnClose(checkBox.isSelected());
                        break;
                    case "pull":
                        main.settings.changePullOnStartup(checkBox.isSelected());
                        break;
                    case "lightMode":

                        break;
                    default:
                        throw new IllegalArgumentException();
                }

            }

        });
        panelContainer.add(checkBox, c);

        return panelContainer;
    }

}
