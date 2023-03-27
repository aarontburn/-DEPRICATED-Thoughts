package com.beanloaf.view;


import javax.swing.SwingUtilities;

public final class Main {

    private Main() {

    }

    public static void main(final String[] args) {

        SwingUtilities.invokeLater(Thoughts::new);
    }

}
