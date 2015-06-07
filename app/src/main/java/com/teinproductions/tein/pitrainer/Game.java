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

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public Class getFragment() {
        return fragment;
    }


    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private Game[] data;
        private Context context;
        private OnClickListener clickListener;

        public interface OnClickListener {
            void onClick(int i);
        }

        public RecyclerAdapter(Context context, Game[] data, OnClickListener clickListener) {
            super();
            this.data = data;
            this.context = context;
            this.clickListener = clickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_game, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.textView.setText(data[i].getName());
            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            LinearLayout root;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textView);
                root = (LinearLayout) itemView.findViewById(R.id.root);
            }
        }
    }
}
