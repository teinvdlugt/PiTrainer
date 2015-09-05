package com.teinproductions.tein.pitrainer;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Game {

    private @StringRes int name;
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
