package com.beanloaf.shared;

import com.beanloaf.common.TC;

public class CheckForFolders {

    public void createDataFolder() {
        if (!TC.UNSORTED_DIRECTORY_PATH.isDirectory()) {
            TC.UNSORTED_DIRECTORY_PATH.mkdirs();
        }

        if (!TC.SORTED_DIRECTORY_PATH.isDirectory()) {
            TC.SORTED_DIRECTORY_PATH.mkdir();
        }
    }

}
