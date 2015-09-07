package com.teinproductions.tein.pitrainer.records;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
        // This method uses the bubble sort algorithm
        for (int i = data.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (data.get(j).getDigitsPerMinute() < data.get(j + 1).getDigitsPerMinute()) {
                    swapRecords(j, j + 1);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByNumberOfDigits() {
        // This method uses the bubble sort algorithm
        for (int i = data.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (data.get(j).getDigits() < data.get(j + 1).getDigits()) {
                    swapRecords(j, j + 1);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByDate() {
        // This method uses the bubble sort algorithm
        for (int i = data.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (data.get(j).getDateMillis() < data.get(j + 1).getDateMillis()){
                    swapRecords(j, j + 1);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void swapRecords(int index1, int index2) {
        Record temp = data.get(index1);
        data.set(index1, data.get(index2));
        data.set(index2, temp);
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
            titleTextView.setText(record.getRecordHolder());
            dateTextView.setText(record.getDateString());

            double digitsPerMinute = (double) record.getDigits() / record.getMilliseconds() * 60000;
            descriptionTextView.setText(context.getString(
                    R.string.record_card_description, record.getDigits(), digitsPerMinute));
        }
    }
}
