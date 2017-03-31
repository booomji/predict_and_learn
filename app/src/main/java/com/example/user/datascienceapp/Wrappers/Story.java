package com.example.user.datascienceapp.Wrappers;

import java.io.Serializable;

/**
 * @serial Objects to be used in comments
 */
public class Story implements Serializable {

    private String text;
    private boolean image;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void imagePresent(boolean image) {
        this.image = image;
    }

    public boolean isImage() {
        return image;
    }


}
