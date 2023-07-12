package com.beanloaf.objects;

import com.beanloaf.res.TC;
import com.beanloaf.view.MenuBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ThoughtsFrame extends JFrame {


    private final Dimension frameSize;

    public ThoughtsFrame(final String windowTitle, final int windowWidth, final int windowHeight) {
        super(windowTitle);
        this.frameSize = new Dimension(windowWidth, windowHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(windowWidth, windowHeight); // DON'T REMOVE FOR SOME REASON?????
        this.setFocusable(true);
        this.setJMenuBar(new MenuBar());

        try {
            this.setIconImage(ImageIO.read(new File(TC.Paths.ICON_DIRECTORY + "icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return this.frameSize;
    }

}
