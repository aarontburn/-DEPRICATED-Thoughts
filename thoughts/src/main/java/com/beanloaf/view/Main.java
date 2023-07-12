package com.beanloaf.view;


import javax.swing.SwingUtilities;

public final class Main {

    private Main() {
        throw new RuntimeException("What are we doing here");
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(Thoughts::new);
    }

}
