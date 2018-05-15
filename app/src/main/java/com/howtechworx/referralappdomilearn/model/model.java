package com.howtechworx.referralappdomilearn.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class model {
    @Exclude
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public <T extends model> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;

    }
}
