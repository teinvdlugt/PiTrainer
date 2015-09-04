package com.teinproductions.tein.pitrainer.records;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.teinproductions.tein.pitrainer.ActivityInterface;
import com.teinproductions.tein.pitrainer.FragmentInterface;
import com.teinproductions.tein.pitrainer.R;

public class RecordsFragment extends Fragment implements FragmentInterface {

    private static final String SPINNER_SELECTION = "SPINNER_SELECTION";

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

        setSpinnerAdapter();
        reloadRecords();
        return theView;
    }

    private void reloadRecords() {
        adapter.setData(RecordsHandler.loadRecords(getActivity()));
        adapter.sortByDigitsPerMinute();
    }

    private void setSpinnerAdapter() {
        String[] sortMethods = getActivity().getResources().getStringArray(R.array.sort_methods);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sortMethods);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(spinnerAdapter);

        int savedSelection = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(SPINNER_SELECTION, 0);
        try {
            sortBySpinner.setSelection(savedSelection);
        } catch (Exception e) {
            sortBySpinner.setSelection(0);
        }

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt(SPINNER_SELECTION, position).apply();
                switch (position) {
                    case 0:
                        adapter.sortByDigitsPerMinute();
                        break;
                    case 1:
                        adapter.sortByNumberOfDigits();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {/*ignored*/}
        });
    }

    @Override
    public void notifyDigitsChanged() {
        reloadRecords();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {/* ignored */}
}
