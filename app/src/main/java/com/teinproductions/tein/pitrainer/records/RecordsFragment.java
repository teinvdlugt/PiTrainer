package com.teinproductions.tein.pitrainer.records;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.teinproductions.tein.pitrainer.ActivityInterface;
import com.teinproductions.tein.pitrainer.FragmentInterface;
import com.teinproductions.tein.pitrainer.R;

public class RecordsFragment extends Fragment implements FragmentInterface {

    private ActivityInterface activityInterface;
    private Spinner sortBySpinner;
    private RecyclerView recyclerView;
    private RecordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        View theView = inflater.inflate(R.layout.fragment_records, container, false);
        recyclerView = (RecyclerView) theView.findViewById(R.id.records_recyclerView);
        sortBySpinner = (Spinner) theView.findViewById(R.id.sortBy_spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecordsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        theView.findViewById(R.id.tryAgain_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityInterface.swapFragment(TimeFragment.class);
            }
        });

        reloadRecords();
        return theView;
    }

    private void reloadRecords() {
        adapter.setData(RecordsHandler.loadRecords(getActivity()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDigitsChanged() {
        reloadRecords();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {/* ignored */}
}
