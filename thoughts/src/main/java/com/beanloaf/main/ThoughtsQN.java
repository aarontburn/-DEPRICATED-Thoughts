package com.beanloaf.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.beanloaf.common.TC;
import com.beanloaf.shared.CheckForFolders;
import com.beanloaf.shared.SaveNewFile;
import com.beanloaf.shared.TabPressed;
import com.beanloaf.shared.TextAreaFocusListener;

/**
 * The note-taking portion of Thoughts; supposed to run as fast as possible.
 * 
 * @author beanloaf
 */
public class ThoughtsQN {
    JFrame window;

    JPanel container;

    JPanel titlePanel, bodyPanel, tagPanel;

    public JTextArea titleTextArea, bodyTextArea, tagTextArea;

    /* Event Handlers */
    TextAreaFocusListener mListener = new TextAreaFocusListener();

    public static void main(String[] args) {
        new ThoughtsQN();
    }

    public ThoughtsQN() {
        new CheckForFolders().createDataFolder();
        // new CheckForSettings().check();

        createGUI();

        // window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        titleTextArea.requestFocusInWindow();
        // For some reason, this only works if this is here twice.
        titleTextArea.selectAll();

    }

    private void createGUI() {
        window = new JFrame();
        window = new JFrame("Thoughts Quick Note");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusable(true);
        window.setSize(700, 600);

        container = new JPanel();
        container.setLayout(new GridBagLayout());
        window.add(container);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;

        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        c.gridy = 0;
        c.weighty = 0.2;
        container.add(titlePanel, c);

        tagPanel = new JPanel();
        tagPanel.setLayout(new BorderLayout());
        c.gridy = 1;
        c.weighty = 0.1;
        container.add(tagPanel, c);

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BorderLayout());
        c.gridy = 2;
        c.weighty = 0.8;
        container.add(bodyPanel, c);

        JButton submitButton = new JButton("submit");
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new CheckForFolders().createDataFolder();
                new SaveNewFile(
                        titleTextArea.getText(),
                        bodyTextArea.getText(),
                        tagTextArea.getText()).save();

                // Resets input fields
                titleTextArea.setText(TC.DEFAULT_TITLE);
                bodyTextArea.setText(TC.DEFAULT_BODY);
                tagTextArea.setText(TC.DEFAULT_TAG);
            }
        });
        submitButton.setBackground(Color.LIGHT_GRAY);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        c.gridy = 3;
        c.weighty = 0.05;
        container.add(submitButton, c);

        createElements();

    }

    private void createElements() {
        titleTextArea = new JTextArea(TC.DEFAULT_TITLE);
        titleTextArea.setBackground(Color.BLACK);
        titleTextArea.setForeground(Color.white);
        titleTextArea.setFont(TC.h1);
        titleTextArea.setLineWrap(true);
        titleTextArea.setPreferredSize(new Dimension(0, 10));
        titleTextArea.addKeyListener(new TabPressed(titleTextArea));
        titleTextArea.addFocusListener(mListener);
        titleTextArea.setCaretColor(Color.white);
        titleTextArea.getDocument().putProperty("filterNewlines", true);
        titlePanel.add(titleTextArea, BorderLayout.CENTER);

        tagTextArea = new JTextArea(TC.DEFAULT_TAG);
        tagTextArea.setBackground(new Color(32, 32, 32));
        tagTextArea.setForeground(Color.WHITE);
        tagTextArea.setLineWrap(true);
        tagTextArea.setFont(TC.p);
        tagTextArea.setPreferredSize(new Dimension(0, 10));
        tagTextArea.setWrapStyleWord(true);
        tagTextArea.addKeyListener(new TabPressed(tagTextArea));
        tagTextArea.addFocusListener(mListener);
        tagTextArea.setCaretColor(Color.white);
        tagTextArea.getDocument().putProperty("filterNewlines", true);
        tagPanel.add(tagTextArea, BorderLayout.CENTER);

        bodyTextArea = new JTextArea(TC.DEFAULT_BODY);
        bodyTextArea.setBackground(Color.DARK_GRAY);
        bodyTextArea.setForeground(Color.WHITE);
        bodyTextArea.setLineWrap(true);
        bodyTextArea.setFont(TC.p);
        bodyTextArea.setPreferredSize(new Dimension(0, 10));
        bodyTextArea.setWrapStyleWord(true);
        bodyTextArea.addKeyListener(new TabPressed(bodyTextArea));
        bodyTextArea.addFocusListener(mListener);
        bodyTextArea.setCaretColor(Color.white);
        bodyPanel.add(bodyTextArea, BorderLayout.CENTER);
    }

}