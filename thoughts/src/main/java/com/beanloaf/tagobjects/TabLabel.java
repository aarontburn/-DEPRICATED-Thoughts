package com.beanloaf.tagobjects;

import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;

public class TabLabel extends JLabel {

    private static final Dimension TAG_DIM = new Dimension(150, 25);

    public TabLabel(final String tagName) {
        super(tagName, SwingConstants.CENTER);
        this.setFont(TC.Fonts.h4);
        this.setPreferredSize(TAG_DIM);

    }

}
