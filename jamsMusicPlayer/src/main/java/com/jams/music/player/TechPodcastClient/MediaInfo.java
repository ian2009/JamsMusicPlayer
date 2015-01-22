package com.jams.music.player.TechPodcastClient;

/**
 * Created by jessica on 1/18/2015.
 */
import java.util.Formatter;

/**
 * POJO class represents the media item information.
 * @author Ian Feng
 */
public class MediaInfo {

    private String src = "";
    private String title = "";
    private String url = "";
    private String description = "";
    private String digest = "";
    private String category = "";

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getDigest() {
        return digest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}