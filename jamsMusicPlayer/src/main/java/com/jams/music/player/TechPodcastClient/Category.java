package com.jams.music.player.TechPodcastClient;

/**
 * Created by jessica on 1/21/2015.
 */
public class Category {
    private String name = "";
    private String queryUrl = "";
    private int ver = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }
}
