package com.teinproductions.tein.pitrainer.records;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private RecordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        View theView = inflater.inflate(R.layout.fragment_records, container, false);
        RecyclerView recyclerView = (RecyclerView) theView.findViewById(R.id.records_recyclerView);
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
        switch (sortBySpinner.getSelectedItemPosition()) {
            case 0:
                adapter.sortByDigitsPerMinute();
                break;
            case 1:
                adapter.sortByNumberOfDigits();
                break;
            case 2:
                adapter.sortByDate();
                break;
        }
    }

    private void setSpinnerAdapter() {
        String[] sortMethods = getActivity().getResources().getStringArray(R.array.sort_methods);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sortMethods);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(spinnerAdapter);

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
                        break;
                    case 2:
                        adapter.sortByDate();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {/*ignored*/}
        });

        int savedSelection = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(SPINNER_SELECTION, 0);
        try {
            sortBySpinner.setSelection(savedSelection);
        } catch (Exception e) {
            sortBySpinner.setSelection(0);
        }
    }

    @Override
    public void notifyDigitsChanged() {
        reloadRecords();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {/* ignored */}

    @Override
    public void refreshKeyboard() {/* ignored */}

    @Override
    public Class getPreviousFragment() {
        return TimeFragment.class;
    }
}
