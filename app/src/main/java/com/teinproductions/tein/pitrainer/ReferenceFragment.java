package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class ReferenceFragment extends Fragment
        implements FragmentInterface, ReferenceFragmentSettingsDialog.Listener {

    public static final String TEXT_SIZE = "TEXT_SIZE";
    public static final String SPACINGS = "SPACINGS";

    private float textSize;
    private int spacings;

    private TextView integerPart, fractionalPart;
    private ImageButton settingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_reference, container, false);

        integerPart = (TextView) theView.findViewById(R.id.integerPart_textView);
        fractionalPart = (TextView) theView.findViewById(R.id.fractionalPart_textView);
        settingsButton = (ImageButton) theView.findViewById(R.id.settings_button);

        restoreValues();
        notifyDigitsChanged();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSettings();
            }
        });

        return theView;
    }

    public void restoreValues() {
        textSize = getActivity().getPreferences(0).getFloat(TEXT_SIZE, 18);
        spacings = getActivity().getPreferences(0).getInt(SPACINGS, 10);

        fractionalPart.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    @Override
    public void notifyDigitsChanged() {
        integerPart.setText(Digits.currentDigit.getIntegerPart() + ".");
        setFractionalPartText();
    }

    private void setFractionalPartText() {
        StringBuilder sb = new StringBuilder(Digits.currentDigit.getFractionalPart());
        if (spacings > 0) { // If 0, then no spacings
            for (int i = spacings; i < sb.length(); i += spacings + 1) {
                sb.insert(i, " ");
            }
        }
        fractionalPart.setText(sb);
    }

    private void onClickSettings() {
        ReferenceFragmentSettingsDialog.show(this, textSize, spacings);
        // When on this dialog OK is clicked, reload() is called
    }

    @Override
    public void reload() {
        restoreValues();
        setFractionalPartText();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        // N/A
    }
}
