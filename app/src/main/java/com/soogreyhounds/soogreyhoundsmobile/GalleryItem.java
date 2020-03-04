package com.soogreyhounds.soogreyhoundsmobile;

public class GalleryItem {
    private String mCaption;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
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

    private String mId;
    private String mUrl;
    @Override
    public String toString() {
        return mCaption;
    }
}
