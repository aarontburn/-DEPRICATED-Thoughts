package com.beanloaf.events;


import com.beanloaf.view.Thoughts;

import java.beans.PropertyChangeSupport;

public final class ThoughtsPCS extends PropertyChangeSupport {

    public static ThoughtsPCS instance;

    public static ThoughtsPCS getInstance() {
        if (instance == null) {
            throw new RuntimeException();
        }
        return instance;

    }


    public static ThoughtsPCS getInstance(final Thoughts main) {
        if (instance == null) {
            instance = new ThoughtsPCS(main);
        }
        return instance;
    }



    private ThoughtsPCS(final Thoughts main) {
        super(main);
    }

    public void firePropertyChange(final String propertyName) {
        super.firePropertyChange(propertyName, null, null);
    }

    public void firePropertyChange(final String propertyName, final Object newValue) {
        super.firePropertyChange(propertyName, null, newValue);
    }

}
