package com.teinproductions.tein.pitrainer.records;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.R;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private ArrayList<Record> data;
    private Context context;

    public RecordsAdapter(Context context) {
        this.context = context;
    }

    public void sortByDigitsPerMinute() {
        final int size = data.size();
        ArrayList<Record> sorted = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Record highest = data.get(0);
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getDigitsPerMinute() > highest.getDigitsPerMinute()) {
                    highest = data.get(j);
                }
            }
            sorted.add(highest);
            data.remove(highest);
        }

        data = sorted;
        notifyDataSetChanged();
    }

    public void sortByNumberOfDigits() {
        final int size = data.size();
        ArrayList<Record> sorted = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Record highest = data.get(0);
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j).getDigits() > highest.getDigits()) {
                    highest = data.get(j);
                }
            }
            sorted.add(highest);
            data.remove(highest);
        }

        data = sorted;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.displayRecordData(context, data.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Record> data) {
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, titleTextView, dateTextView, descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            rankTextView = (TextView) itemView.findViewById(R.id.rank_textView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.numbers_textView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_textView);
        }

        public void displayRecordData(Context context, Record record, int rank) {
            rankTextView.setText("" + rank);
            titleTextView.setText("Tein");
            dateTextView.setText("5-9-2015");

            double digitsPerMinute = (double) record.getDigits() / record.getMilliseconds() * 60000;
            descriptionTextView.setText(context.getString(
                    R.string.record_card_description, record.getDigits(), digitsPerMinute));
        }
    }
}
