package com.beanloaf.tagobjects;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.LeftPanel;

public class TagListItem extends JPanel implements IdentifiableObject {
//    private static final Dimension TAB_DIM = new Dimension(150, 50);


    private List<ThoughtObject> taggedObjects = new ArrayList<>();

    private final String tag;

    private final TagListItem thisList = this;

    private final LeftPanel left;

    public final JTextArea label;

    private String identifier;

    public TagListItem(final LeftPanel left, final String tag) {
        super(new GridBagLayout());
        this.tag = tag;
        this.left = left;
        setIdentifier(tag);


        label = new JTextArea(tag);
        label.setFont(TC.Fonts.h4);
        label.setLineWrap(true);
        label.setEditable(false);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                click();

            }
        });
        this.add(label, new GBC().setFill(GBC.Fill.BOTH));

        this.setBorder(BorderFactory.createLineBorder(Color.gray));

        resize();


    }

    public void resize() {
        this.setMaximumSize(new Dimension(left.getMaximumSize().width / 2, 50));
        this.setMinimumSize(new Dimension(left.getMaximumSize().width / 2, 50));

    }

    public void click() {
        left.itemList.removeAll();

        taggedObjects.sort(ThoughtObject::compareTo);

        for (final ThoughtObject obj : taggedObjects) {
            final ListItem listItem = new ListItem(left, obj);
            left.itemList.add(listItem, "north, wrap, w 100%");

        }
        left.itemList.revalidate();
        left.itemList.repaint();

        left.selectedTag = thisList;

        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, getItem(0));
    }


    public String getTag() {
        return this.tag;
    }


    public TagListItem addItem(final ThoughtObject obj) {
        if (!this.taggedObjects.contains(obj)) {
            this.taggedObjects.add(obj);
        }

        return this;
    }


    public ThoughtObject getItem(final int index) {
        return taggedObjects.size() == 0 ? null : taggedObjects.get(index);
    }

    public void removeItem(final ThoughtObject obj) {
        this.taggedObjects.remove(obj);
    }

    public void setList(final List<ThoughtObject> list) {
        for (final ThoughtObject obj : list) {
            addItem(obj);
        }
    }

    public List<ThoughtObject> getList() {
        return this.taggedObjects;
    }

    public boolean isEmpty() {
        return this.taggedObjects.size() == 0;
    }

    public int numItems() {
        return this.taggedObjects.size();
    }

    public void clear() {
        this.taggedObjects.clear();
    }

    public void sort(Comparator<ThoughtObject> comparator) {
        this.taggedObjects.sort(comparator);
    }


    @Override
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "[TagListItem] ID: " + identifier;
    }
}
