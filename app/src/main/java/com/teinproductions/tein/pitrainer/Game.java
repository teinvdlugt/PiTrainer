package com.teinproductions.tein.pitrainer;

import androidx.annotation.StringRes;

public class Game {

    private
    @StringRes
    int name;
    private Class fragment;

    public Game(int name, Class fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public void setName(int name) {
        this.name = name;
    }

    public Class getFragment() {
        return fragment;
    }
}
