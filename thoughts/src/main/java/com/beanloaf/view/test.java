package com.beanloaf.view;

import com.beanloaf.res.theme.ThoughtsThemeDark;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class test {


    public static JFrame window;
    public static void main(String[] args) {

        window = new JFrame("test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusable(true);

        final JPanel container = new JPanel(new MigLayout());
        window.add(container);



        container.add(new JLabel("hi"));
        container.add(new JLabel("hi 2"), "span 2");
        container.add(new JLabel("hi 3"), "wrap");
        container.add(new JLabel("hi 4"));

        window.pack();
        window.setVisible(true);



    }



}
