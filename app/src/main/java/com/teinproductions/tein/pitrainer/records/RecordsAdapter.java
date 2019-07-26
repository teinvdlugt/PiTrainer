package com.teinproductions.tein.pitrainer.records;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.displayRecordData(data.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Record> data) {
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, digitsTV, timeTV, dpmTV;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            digitsTV = itemView.findViewById(R.id.digits_textView);
            timeTV = itemView.findViewById(R.id.time_textView);
            dpmTV = itemView.findViewById(R.id.dpm_textView);
        }

        public void displayRecordData(Record record, int rank) {
            titleTextView.setText(String.format("%d. %s", rank, record.getRecordHolder()));
            dateTextView.setText(record.getDateString());

            digitsTV.setText(itemView.getContext().getString(R.string.record_digits_format, record.getDigits()));
            timeTV.setText(itemView.getContext().getString(R.string.record_time_format, record.getMilliseconds() / 1000.));
            double dpm = record.getDigitsPerMinute();
            if (Double.isNaN(dpm) || Double.isInfinite(dpm))
                dpmTV.setText("");
            else dpmTV.setText(itemView.getContext().getString(R.string.record_dpm_format, dpm));
        }
    }
}
