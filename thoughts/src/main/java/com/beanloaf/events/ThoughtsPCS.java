package com.beanloaf.events;


import com.beanloaf.view.Thoughts;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ThoughtsPCS extends PropertyChangeSupport {

    public ThoughtsPCS(final Thoughts main) {
        super(main);
    }

    public void firePropertyChange(final String propertyName) {
        super.firePropertyChange(propertyName, null, null);
    }

    public void firePropertyChange(final String propertyName, final Object newValue) {
        super.firePropertyChange(propertyName, null, newValue);
    }

}
