package com.beanloaf.tagobjects;


import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.beanloaf.input.ListItemPressed;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class ListItems extends JList<String> {

    private final ListItemPressed l;

    public ListItems(final Thoughts main,
                     final List<ThoughtObject> thoughtList,
                     final DefaultListModel<String> model) {
        super(model);

        l = new ListItemPressed(main, this, thoughtList);
        this.setFont(TC.Fonts.h4);
        this.setCellRenderer(new TC.CellRenderer());
        this.setVisibleRowCount(0);
        this.addMouseListener(l);

    }

    public ListItemPressed getMouseEvent() {
        return l;
    }


}