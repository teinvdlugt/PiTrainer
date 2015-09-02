package com.teinproductions.tein.pitrainer.records;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.teinproductions.tein.pitrainer.ActivityInterface;
import com.teinproductions.tein.pitrainer.FragmentInterface;

public class RecordsFragment extends Fragment implements FragmentInterface {

    private ActivityInterface activityInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        Button button = new Button(getActivity());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityInterface.swapFragment(TimeFragment.class);
            }
        });
        button.setText("Click here");

        return button;
    }

    @Override
    public void notifyDigitsChanged() {

    }


    @Override
    public void showOnScreenKeyboard(boolean show) {/* ignored */}
}
