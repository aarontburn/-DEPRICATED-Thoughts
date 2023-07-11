package com.beanloaf.tagobjects;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

public class ListItem extends JLabel {

    private final ThoughtObject thoughtObject;
    public ListItem(final ThoughtObject obj) {
        super(obj.getTitle(), SwingConstants.CENTER);

        this.thoughtObject = obj;

        this.setBorder(BorderFactory.createLineBorder(Color.white));
        this.setFont(TC.Fonts.h4);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, thoughtObject);


            }
        });

    }



}
