package com.beanloaf.view;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Thoughts::new);
    }

}
