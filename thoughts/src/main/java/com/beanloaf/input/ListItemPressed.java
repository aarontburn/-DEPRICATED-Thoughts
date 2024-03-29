package com.beanloaf.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.tagobjects.ListItems;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class ListItemPressed extends MouseAdapter {
    private final Thoughts main;
    private final List<ThoughtObject> arrayList;
    private final JList<?> list;

    public ListItemPressed(final Thoughts main) {
        this(main, null, null);
    }

    public ListItemPressed(final Thoughts main,
                           final ListItems list,
                           final List<ThoughtObject> arrayList) {
        super();
        this.main = main;
        this.arrayList = arrayList;
        this.list = list;
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        final int selectedIndex = this.list.getSelectedIndex();
        setContentFields(selectedIndex);
        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.LIST_ITEM_PRESSED);

    }




    public void setContentFields(final int index) {
        this.main.ready = false;

        main.refreshThoughtList();

        ThoughtObject obj = new ThoughtObject(
                "Thoughts",
                "",
                "by @beanloaf",
                "Get started by creating or selecting a file.",
                null);

        if (this.arrayList != null && this.arrayList.size() > index) {
            obj = this.arrayList.get(index);

        }

        this.main.selectedFile = obj.getPath() != null ? obj : null;

        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, obj);

        this.main.ready = true;

    }

    public void setContentFields(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }
        this.main.selectedFile = obj;
        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, obj);

    }

    @Override
    public String toString() {
        return arrayList.toString();
    }

}
