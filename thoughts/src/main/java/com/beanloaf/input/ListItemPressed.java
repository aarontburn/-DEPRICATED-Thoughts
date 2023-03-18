package com.beanloaf.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JList;

import com.beanloaf.objects.ListTab;
import com.beanloaf.objects.TextPropertyObject;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class ListItemPressed extends MouseAdapter {
    private final Thoughts main;
    private final ArrayList<ThoughtObject> arrayList;
    private final JList<?> list;

    public ListItemPressed(final Thoughts main) {
        this(main, null, null);
    }

    public ListItemPressed(final Thoughts main,
                           final ListTab list,
                           final ArrayList<ThoughtObject> arrayList) {
        super();
        this.main = main;
        this.arrayList = arrayList;
        this.list = list;
    }

    public void mousePressed(final MouseEvent event) {
        int selectedIndex = this.list.getSelectedIndex();
        setContentFields(selectedIndex);
        this.main.rightPanel.undoManager.discardAllEdits();
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.LIST_ITEM_PRESSED);

    }


    public void setContentFields(final int index) {
        main.refreshThoughtList();
        this.main.ready = false;
        ThoughtObject obj = new ThoughtObject(
                "",
                "",
                "",
                "",
                null);

        if (index < 0) {
            this.main.selectedFile = null;
            this.main.leftPanel.leftTabs.setSelectedIndex(0);
        }

        if (this.arrayList != null && this.arrayList.size() > index) {
            obj = this.arrayList.get(index);
            this.main.selectedFile = obj;

        }

        this.main.thoughtsPCS.firePropertyChange(TC.Properties.TEXT, null, new TextPropertyObject(
                obj.getTitle(),
                obj.getTag(),
                obj.getDate(),
                obj.getBody()));

        this.main.ready = true;

    }

    public void setContentFields(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }
        this.main.selectedFile = obj;
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.TEXT, null, new TextPropertyObject(
                obj.getTitle(),
                obj.getTag(),
                obj.getDate(),
                obj.getBody()));

    }

}