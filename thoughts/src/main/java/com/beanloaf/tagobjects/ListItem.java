package com.beanloaf.tagobjects;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.LeftPanel;

public class ListItem extends JPanel {

    private final ThoughtObject thoughtObject;

    public final JTextArea label;

    private final LeftPanel left;
    public ListItem(final LeftPanel left, final ThoughtObject obj) {
        super(new GridBagLayout());

        this.thoughtObject = obj;
        this.left = left;

        label = new JTextArea(obj.getTitle());
        label.setFont(TC.Fonts.h4);
        label.setLineWrap(true);
        label.setEditable(false);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, thoughtObject);


            }
        });
        this.add(label, new GBC().setFill(GBC.Fill.BOTH));
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        resize();


    }

    public void resize() {
        this.setMaximumSize(new Dimension(left.getMaximumSize().width / 2, 50));
        this.setMinimumSize(new Dimension(left.getMaximumSize().width / 2, 50));
    }

    public ThoughtObject getThoughtObject() {
        return this.thoughtObject;
    }

    public String getText() {
        return label.getText();
    }

    public void setText(final String text) {
        label.setText(text);
    }


}
