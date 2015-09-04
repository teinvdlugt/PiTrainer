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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.displayRecordData(context, data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Record> data) {
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numOfDigitsTV, digitsPerMinuteTV;

        public ViewHolder(View itemView) {
            super(itemView);
            numOfDigitsTV = (TextView) itemView.findViewById(R.id.numOfDigits_textView);
            digitsPerMinuteTV = (TextView) itemView.findViewById(R.id.digitsPerMinute_textView);
        }

        public void displayRecordData(Context context, Record record) {
            numOfDigitsTV.setText(String.format(context.getString(R.string.num_of_digits_record_format), record.getDigits()));

            double digitsPerMinute = (double) record.getDigits() / record.getMilliseconds() * 60000;
            digitsPerMinuteTV.setText(String.format(context.getString(R.string.digits_per_minute_record_format), digitsPerMinute));
        }
    }
}
