package com.beanloaf.events;


import com.beanloaf.view.ThoughtsMain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ThoughtsPCS extends PropertyChangeSupport {

    public ThoughtsPCS(final ThoughtsMain main) {
        super(main);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }

    public void firePropertyChange(final String propertyName) {
        super.firePropertyChange(propertyName, null, null);
    }



}
