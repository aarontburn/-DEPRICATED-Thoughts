package com.beanloaf.objects;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.beanloaf.input.ListItemPressed;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class ListTab extends JList<String> {

    private final ListItemPressed l;

    public ListTab(final Thoughts main,
                   final ArrayList<ThoughtObject> thoughtList,
                   final DefaultListModel<String> model) {
        super(model);

        l = new ListItemPressed(main, this, thoughtList);
        this.addMouseListener(l);
        this.setFont(TC.Fonts.h4);
        this.setCellRenderer(new TC.CellRenderer());
        this.setVisibleRowCount(0);
    }

    public ListItemPressed getMouseEvent() {
        return l;
    }
}