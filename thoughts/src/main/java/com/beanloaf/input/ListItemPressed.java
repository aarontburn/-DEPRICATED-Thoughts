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

    public ListItemPressed(Thoughts main) {
        this.main = main;
        this.arrayList = null;
        this.list = null;
    }

    public ListItemPressed(Thoughts main,
                           ListTab list,
                           ArrayList<ThoughtObject> arrayList) {

        this.main = main;
        this.arrayList = arrayList;
        this.list = list;
    }

    public void mousePressed(MouseEvent e) {
        int selectedIndex = this.list.getSelectedIndex();
        setContentFields(selectedIndex);
        this.main.rightPanel.undoManager.discardAllEdits();
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.LIST_ITEM_PRESSED);

    }


    public void setContentFields(int index) {
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

    public void setContentFields(ThoughtObject obj) {
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