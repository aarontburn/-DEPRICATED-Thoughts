package com.beanloaf.events;

import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.textfields.SearchBar;

import javax.swing.*;


public class Search {

    private final JTextPane searchBar;


    public Search(final SearchBar searchBar) {
        this.searchBar = searchBar;

    }


    public boolean searchFor(final ThoughtObject object) {
        final String searchText = searchBar.getText();

        final String[] splitText = searchText.split(" ");

        if (splitText.length == 1) {
            return true;
        }

        switch (splitText[0]) {
            case "!title" -> {
                return searchText.toLowerCase().contains(object.getTitle().toLowerCase());
            }
            case "!tag" -> {
                return searchText.toLowerCase().contains(object.getTag().toLowerCase());
            }

            case "!date" -> {
                return searchText.toLowerCase().contains(object.getDate().toLowerCase());

            }
            case "!body" -> {
                return searchText.toLowerCase().contains(object.getBody().toLowerCase());
            }

            default -> {
                return true;
            }


        }


    }


}
