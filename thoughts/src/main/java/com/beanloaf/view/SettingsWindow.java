package com.beanloaf.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

public class SettingsWindow {

    private final Thoughts main;

    private final JFrame window;
    private JTabbedPane tabs;

    public SettingsWindow(final Thoughts main) {
        this.main = main;
        this.window = new JFrame("Settings");
        createGUI();
        this.window.setVisible(true);
    }

    private void createGUI() {
        this.window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.window.setFocusable(true);
        this.window.setSize(500, 600);
        this.window.setLocationRelativeTo(null);

        final JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(32, 32, 32));
        this.window.add(container);

        this.tabs = new JTabbedPane(JTabbedPane.LEFT);
        this.tabs.setFont(TC.Fonts.h4);
        container.add(this.tabs, new GBC().setFill(GridBagConstraints.BOTH));
        generalSettings();

    }

    private void generalSettings() {
        final JPanel generalSettingsPanel = new JPanel(new GridBagLayout());
        this.tabs.add(generalSettingsPanel, "General");

        final GBC panelConstraints = new GBC().setAnchor(GridBagConstraints.NORTHWEST)
                .setFill(GridBagConstraints.HORIZONTAL);

        final JLabel generalSettingsLabel = new JLabel("General Settings");
        generalSettingsLabel.setBackground(new Color(32, 32, 32));
        generalSettingsLabel.setHorizontalAlignment(JLabel.CENTER);
        generalSettingsLabel.setFont(TC.Fonts.h3);
        generalSettingsPanel.add(generalSettingsLabel, panelConstraints);

        generalSettingsPanel.add(createCheckboxPanel("Push on close:", "push"),
                panelConstraints.setGridY(1).setWeightY(0.01));

        generalSettingsPanel.add(createCheckboxPanel("Pull on startup:", "pull"),
                panelConstraints.setGridY(2).setWeightY(0.01));


        generalSettingsPanel.add(createCheckboxPanel("Light mode:", "lightMode"),
                panelConstraints.setGridY(3).setWeightY(0.9));

    }

    private JPanel createCheckboxPanel(final String label, final String actionName) {
        final JPanel panelContainer = new JPanel(new GridBagLayout());

        final JLabel textLabel = new JLabel(label);
        textLabel.setFont(TC.Fonts.h4);

        panelContainer.add(textLabel, new GBC().setAnchor(GridBagConstraints.LINE_START)
                .setInsets(0, 30, 0, 0));

        final JCheckBox checkBox = new JCheckBox();
        switch (actionName) {
            case "push" -> checkBox.setSelected(this.main.settings.isPushOnClose());
            case "pull" -> checkBox.setSelected(this.main.settings.isPullOnStartup());
            case "lightMode" -> checkBox.setSelected(this.main.settings.isLightMode());
            default -> throw new IllegalArgumentException();
        }

        checkBox.addItemListener(event -> {
            switch (actionName) {
                case "push" -> main.settings.changePushOnClose(checkBox.isSelected());
                case "pull" -> main.settings.changePullOnStartup(checkBox.isSelected());
                case "lightMode" -> main.settings.changeIsLightMode(checkBox.isSelected());
                default -> throw new IllegalArgumentException();
            }

        });

        panelContainer.add(checkBox, new GBC(0.9, 0.1)
                .setAnchor(GridBagConstraints.LINE_END)
                .setInsets(0, 0, 0, 30));

        return panelContainer;
    }

}
