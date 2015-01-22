package com.jams.music.player.TechPodcastClient;

import java.util.HashMap;

/**
 * Created by jessica on 1/21/2015.
 */
public class Directory {
    private int appVer = 0;
    private int libVer = 0;
    private HashMap<String, Category> categories = new HashMap<String, Category>(20);

    public int getAppVer() {
        return appVer;
    }

    public void setAppVer(int appVer) {
        this.appVer = appVer;
    }

    public int getLibVer() {
        return libVer;
    }

    public void setLibVer(int libVer) {
        this.libVer = libVer;
    }

    public HashMap<String, Category> getCategories() {
        return categories;
    }

    public void setCategories(HashMap<String, Category> categories) {
        this.categories = categories;
    }
}
