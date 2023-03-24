package com.beanloaf.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

public final class SettingsWindow extends JFrame{

    private static SettingsWindow instance;


    private final Thoughts main;

    private JTabbedPane tabs;


    public static SettingsWindow getInstance(final Thoughts main) {
        if (instance == null) {
            instance = new SettingsWindow(main);
            instance.setVisible(true);
        } else {
            instance.toFront();
            instance.setExtendedState(JFrame.NORMAL);
        }
        return instance;
    }

    private SettingsWindow(final Thoughts main) {
        super("Settings");
        this.main = main;
        createGUI();
        this.setVisible(true);
    }

    private void createGUI() {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                super.windowClosing(event);
                instance = null;
            }
        });

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setFocusable(true);
        this.setSize(500, 600);
        this.setLocationRelativeTo(null);

        this.setLayout(new GridBagLayout());

        this.tabs = new JTabbedPane(JTabbedPane.LEFT);
        this.tabs.setFont(TC.Fonts.h4);
        this.add(this.tabs, new GBC().setFill(GBC.Fill.BOTH));
        generalSettings();
        cloudSettings();

    }

    private void generalSettings() {
        final JPanel generalSettingsPanel = new JPanel(new GridBagLayout());
        this.tabs.add(generalSettingsPanel, "General");

        final GBC panelConstraints = new GBC().setAnchor(GBC.Anchor.NORTHWEST)
                .setFill(GBC.Fill.HORIZONTAL);

        final JLabel generalSettingsLabel = new JLabel("General Settings");
        generalSettingsLabel.setHorizontalAlignment(JLabel.CENTER);
        generalSettingsLabel.setFont(TC.Fonts.h3);
        generalSettingsPanel.add(generalSettingsLabel, panelConstraints);

        generalSettingsPanel.add(createCheckboxPanel("Push on close:", "push"),
                panelConstraints.increaseGridY().setWeightY(0.01));

        generalSettingsPanel.add(createCheckboxPanel("Pull on startup:", "pull"),
                panelConstraints.increaseGridY().setWeightY(0.01));


        generalSettingsPanel.add(createCheckboxPanel("Light mode:", "lightMode"),
                panelConstraints.increaseGridY().setWeightY(0.9));

    }

    private void cloudSettings() {
        final JPanel cloudPanel = new JPanel(new GridBagLayout());
        this.tabs.add(cloudPanel, "Cloud");

        final GBC panelConstraints = new GBC().setAnchor(GBC.Anchor.NORTH);

        final JLabel cloudSettingsTitle = new JLabel("Cloud Settings");
        cloudSettingsTitle.setHorizontalAlignment(JLabel.CENTER);
        cloudSettingsTitle.setFont(TC.Fonts.h3);
        cloudPanel.add(cloudSettingsTitle, panelConstraints.increaseGridY());

        final JLabel openCloudText = new JLabel(
                "<html>To change cloud settings, press the button below to open an external window.</html>", SwingConstants.CENTER);
        openCloudText.setFont(TC.Fonts.h5);
        cloudPanel.add(openCloudText, panelConstraints.increaseGridY().setFill(GBC.Fill.HORIZONTAL).setWeightY(0.1).setInsets(15));


        final JButton openCloudButton = new JButton("Open Cloud settings");
        openCloudButton.setFont(TC.Fonts.h4);
        openCloudButton.addActionListener(event -> main.thoughtsPCS.firePropertyChange(TC.Properties.CLOUD_SETTINGS));
        cloudPanel.add(openCloudButton, panelConstraints.increaseGridY().setFill(GBC.Fill.NONE).setWeightY(0.6).setInsets(0));



    }

    private JPanel createCheckboxPanel(final String label, final String actionName) {
        final JPanel panelContainer = new JPanel(new GridBagLayout());

        final JLabel textLabel = new JLabel(label);
        textLabel.setFont(TC.Fonts.h4);

        panelContainer.add(textLabel, new GBC().setAnchor(GBC.Anchor.LINE_START)
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
                .setAnchor(GBC.Anchor.LINE_END)
                .setInsets(0, 0, 0, 30));

        return panelContainer;
    }

}
