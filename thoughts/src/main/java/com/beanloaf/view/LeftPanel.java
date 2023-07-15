package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.ListDisplayView;
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

    public final ListDisplayView tagList, itemList;
    public TagListItem selectedTag;



    public LeftPanel(final Thoughts main) {
        super(new MigLayout());
        this.main = main;
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);


        searchBar = new SearchBar();
        this.add(searchBar, "north, cell 0 0 2 1");


        tagList = new ListDisplayView(new MigLayout());
        this.add(new CustomScrollPane(tagList), "west, cell 1 0, w 45%, h 100%");


        itemList = new ListDisplayView(new MigLayout());
        this.add(new CustomScrollPane(itemList), "east, cell 1 1, w 55%, h 100%");


    }

    public void populateTagList() {
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
            list.addItem(obj);
            obj.setParent(list);

        }


        final List<String> set = new ArrayList<>(thoughtListByTag.keySet());
        set.sort(String.CASE_INSENSITIVE_ORDER);

        for (final String tag : set) {
            tagList.add(thoughtListByTag.get(tag), "north, wrap, w 100%, h 3%");

        }


    }


    /*
     *   Unused since we are refreshing the list every change. To increase performance, implement dynamic
     *       tag list by removing and adding appropriate tags, and use this to validate the item list.
     * */
    public void validateItemList() {
        for (final Component obj : itemList.getComponents()) {
            if (obj.getClass() != ListItem.class) continue;


            final ListItem listItem = (ListItem) obj;

            if (!listItem.getText().equals(listItem.getThoughtObject().getTitle())) {
                listItem.setText(listItem.getThoughtObject().getTitle());
            }

        }

    }



    public void delete() {

    }

    public void newFile() {

    }

    public void sort(final ThoughtObject obj) {
        if (obj == null) {
            return;
        }


        System.out.println((obj.isSorted() ? "Unsorting " : "Sorting ") + obj.getTitle());

        obj.sort();

        if (obj.isSorted()) { // adding to sorted list
            System.out.println("adding to sorted list");

            main.unsortedThoughtList.remove(obj);
            main.sortedThoughtList.add(obj);

            unsortedListLabel.removeItem(obj);
            sortedListLabel.addItem(obj);

            obj.setParent(addTagToTagList(obj));


        } else { // adding to unsorted list
            System.out.println("adding to unsorted list");

            main.sortedThoughtList.remove(obj);
            main.unsortedThoughtList.add(obj);

            sortedListLabel.removeItem(obj);
            unsortedListLabel.addItem(obj);

            removeTagFromTagList(obj);
        }


        itemList.remove(itemList.findComponentWithTag(obj.getFile()));


        EventQueue.invokeLater(() -> {
            itemList.revalidate();
            itemList.repaint();

            tagList.revalidate();
            tagList.repaint();

            this.revalidate();
            this.repaint();
        });


//        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.REVAL_REPAINT);

    }

    private void removeTagFromTagList(final ThoughtObject obj) {
        final TagListItem list = obj.getParent();
        if (list == null) return;

        list.removeItem(obj);
        if (list.isEmpty()) {
            thoughtListByTag.remove(list.getTag());
            // remove tag from view

            tagList.remove(tagList.findComponentWithTag(list.getTag()));
            this.revalidate();
            this.repaint();
        }
    }

    private TagListItem addTagToTagList(final ThoughtObject obj) {
        final String tag = obj.getTag();

        TagListItem list = thoughtListByTag.get(tag);

        if (list == null) { // tag doesn't exist in list yet
            list = new TagListItem(this, tag);
            thoughtListByTag.put(tag, list);

            final int index = getAlphaIndex(list);

            System.out.println(index);
            tagList.add(list, index);


        }
        list.addItem(obj);
        return list;
    }

    private int getAlphaIndex(final TagListItem tagListItem) {
        return binaryGetIndex(tagListItem.getTag(), 2, tagList.getComponentCount() - 1);

    }

    private int binaryGetIndex(final String tag, final int left, final int right) {
        final int mid = left + (right - left) / 2;

        if (left > right) {
            return right + 1;
        }

        Component m;
        Component n;
        try {
            m = tagList.getComponent(mid);
        } catch (Exception e) {
            return 3;
        }

        try {
            n = tagList.getComponent(mid + 1);
        } catch (Exception e) {
            return mid;
        }



        TagListItem midItem = null;
        TagListItem nextItem = null;

        if (m != null && m.getClass() == TagListItem.class) {
            midItem = (TagListItem) m;
        }
        if (n != null && n.getClass() == TagListItem.class) {
            nextItem = (TagListItem) n;
        }

        if (midItem == null || nextItem == null) {
            return mid + 1;
        }

        final int midToTag = tag.compareToIgnoreCase(midItem.getTag());
        final int nextToTag = tag.compareToIgnoreCase(nextItem.getTag());


        if (midToTag < 0) {
            return binaryGetIndex(tag, left, mid - 1);

        } else {
            if (nextToTag < 0) {
                return mid + 1;
            } else {
                return binaryGetIndex(tag, mid + 1, right);
            }
        }


    }






    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TC.Properties.TEST -> {

            }
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
