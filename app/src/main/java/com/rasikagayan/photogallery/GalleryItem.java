package com.rasikagayan.photogallery;

/**
 * Created by Rasika Gayan on 12/31/2015.
 */
public class GalleryItem {

    private String mCaption;
    private String mId;
    private String mUrl;

    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String Caption) {
        this.mCaption = Caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
