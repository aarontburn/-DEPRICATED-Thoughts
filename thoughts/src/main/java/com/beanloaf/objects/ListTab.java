package com.beanloaf.objects;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.beanloaf.common.TC;
import com.beanloaf.events.ListItemPressed;
import com.beanloaf.main.ThoughtsMain;

public class ListTab extends JList<String> {

    private ListItemPressed l;

    public ListTab(ThoughtsMain main,
            ArrayList<ThoughtObject> thoughtList,
            DefaultListModel<String> model) {
        super(model);

        l = new ListItemPressed(main, this, thoughtList);
        this.addMouseListener(l);
        this.setFont(TC.h4);
        this.setCellRenderer(main.new CellRenderer());
        this.setVisibleRowCount(0);
    }

    public ListItemPressed getMouseEvent() {
        return l;
    }
}