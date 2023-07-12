package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.tagobjects.ListItem;
import com.beanloaf.tagobjects.TagListItem;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.textfields.SearchBar;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class LeftPanel extends JPanel implements PropertyChangeListener {


    private final Thoughts main;

    public TagListItem unsortedListLabel, sortedListLabel;

    public final SearchBar searchBar;

    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();

    public final JPanel tagList, itemList;
    public TagListItem selectedTag;

    public final CustomScrollPane tagScrollPane, itemScrollPane;


    public LeftPanel(final Thoughts main) {
        super(new MigLayout());
        this.main = main;
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);


        searchBar = new SearchBar();
        this.add(searchBar, "north, cell 0 0 2 1");


        tagList = new JPanel();
        tagList.setLayout(new MigLayout());
        tagScrollPane = new CustomScrollPane(tagList);


        this.add(tagScrollPane, "west, cell 1 0, w 45%, h 100%");


        itemList = new JPanel();
        itemList.setLayout(new MigLayout());
        itemScrollPane = new CustomScrollPane(itemList);
        this.add(itemScrollPane, "east, cell 1 1, w 55%, h 100%");


    }

    public void populateTagList() {
//        final int tagScrollDistance = tagScrollPane.getVerticalScrollBar().getValue();
//        final int itemScrollDistance = itemScrollPane.getVerticalScrollBar().getValue();
//        System.out.println(tagScrollDistance);

        tagList.removeAll();
        unsortedListLabel = new TagListItem(this, "Unsorted");
        sortedListLabel = new TagListItem(this, "Sorted");

        tagList.add(unsortedListLabel, "north, w 100%");
        tagList.add(sortedListLabel, "north, wrap, w 100%, h 3%");

        unsortedListLabel.setList(main.unsortedThoughtList);
        sortedListLabel.setList(main.sortedThoughtList);


        for (final ThoughtObject obj : main.sortedThoughtList) {
            final String tag = obj.getTag();
            TagListItem list = thoughtListByTag.get(tag);

            if (list == null) {
                list = new TagListItem(this, tag);
                thoughtListByTag.put(tag, list);
            }
            list.add(obj);
            obj.setParent(list);

        }


        final List<String> set = new ArrayList<>(thoughtListByTag.keySet());
        set.sort(String.CASE_INSENSITIVE_ORDER);

        for (final String tag : set) {
            tagList.add(thoughtListByTag.get(tag), "north, wrap, w 100%, h 3%");

        }

//        tagScrollPane.getVerticalScrollBar().setValue(tagScrollDistance);
//        itemScrollPane.getVerticalScrollBar().setValue(itemScrollDistance);

    }

    /*
     *   Unused since we are refreshing the list every change. To increase performance, implement dynamic
     *       tag list by removing and adding appropriate tags, and use this to validate the item list.
     * */
    public void validateItemList() {
        for (final Component obj : itemList.getComponents()) {
            if (obj.getClass() != ListItem.class) {
                continue;
            }

            final ListItem listItem = (ListItem) obj;

            if (!listItem.getText().equals(listItem.getThoughtObject().getTitle())) {
                listItem.setText(listItem.getThoughtObject().getTitle());
            }

        }

    }




    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {

            default -> {
            }
        }
    }


    public static class CustomScrollPane extends JScrollPane {


        public CustomScrollPane(final JPanel panel) {
            super(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.setBorder(null);
            this.getVerticalScrollBar().setUI(new TC.ScrollBar());
            this.getVerticalScrollBar().setUnitIncrement(12);
        }
    }
}
