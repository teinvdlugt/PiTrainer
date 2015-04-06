package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ReferenceFragment extends Fragment implements FragmentInterface {

    TextView integerPart, fractionalPart;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getActivity());

        LinearLayout content = new LinearLayout(getActivity());
        content.setOrientation(LinearLayout.VERTICAL);

        integerPart = new TextView(getActivity());
        fractionalPart = new TextView(getActivity());

        integerPart.setTextSize(56);
        fractionalPart.setTextSize(18);

        setCurrentDigits(MainActivity.Digits.values()[
                getActivity().getPreferences(0).getInt(MainActivity.CURRENT_DIGITS_ORDINAL, 0)]);

        Log.i("ONLY_FOR_GEEKS", "onCreateView");

        content.addView(integerPart,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(fractionalPart,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.addView(content,
                new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return scrollView;
    }

    @Override
    public void setCurrentDigits(MainActivity.Digits digits) {
        integerPart.setText(digits.integerPart);
        setFractionalPartText(digits.fractionalPart);
        Log.i("ONLY_FOR_GEEKS", "setCurrentDigits " + digits.integerPart);
    }

    private void setFractionalPartText(String string) {
        StringBuilder sb = new StringBuilder(string);
        for (int i = 10; i < sb.length(); i += 11) {
            sb.insert(i, " ");
        }
        fractionalPart.setText(sb);
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        // N/A
    }
}
