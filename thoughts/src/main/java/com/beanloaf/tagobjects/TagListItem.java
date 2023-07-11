package com.beanloaf.tagobjects;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.LeftPanel;

public class TagListItem extends JLabel {
//    private static final Dimension TAB_DIM = new Dimension(150, 50);


    private List<ThoughtObject> taggedObjects = new ArrayList<>();

    private final DefaultListModel<String> model = new DefaultListModel<>();

    private final String tag;

    private final TagListItem thisList = this;

    public TagListItem(final LeftPanel left, final String tag) {
        super(tag, SwingConstants.CENTER);
        this.tag = tag;

        setFont(TC.Fonts.h4);
        setBorder(BorderFactory.createLineBorder(Color.gray));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                System.out.println("Label : " + tag + " pressed");


                left.itemList.removeAll();

                final GBC constraints = new GBC()
                        .setAnchor(GBC.Anchor.NORTH)
                        .setFill(GBC.Fill.HORIZONTAL)
                        .setWeightXY(Double.MIN_VALUE, Double.MIN_VALUE);

                for (final ThoughtObject obj : taggedObjects) {
                    final ListItem listItem = new ListItem(obj);
                    left.itemList.add(listItem, constraints.increaseGridY());

                }

                left.itemList.revalidate();
                left.itemList.repaint();

                left.itemList.add(new JPanel(), constraints.increaseGridX().setWeightXY(1, 1));


                left.selectedTag = thisList;

                ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, get(0));

            }
        });

    }

    public String getTag() {
        return this.tag;
    }


    public TagListItem add(final ThoughtObject obj) {
        if (!this.taggedObjects.contains(obj)) {
            this.taggedObjects.add(obj);
        }

        return this;
    }

    public ThoughtObject get(final int index) {
        return taggedObjects.size() == 0 ? null : taggedObjects.get(index);
    }

    public void setList(final List<ThoughtObject> list) {
        for (final ThoughtObject obj : list) {
            add(obj);
        }
    }





}
