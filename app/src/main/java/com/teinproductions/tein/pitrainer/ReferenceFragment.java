package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ReferenceFragment extends Fragment
        implements FragmentInterface {

    public static final String TEXT_SIZE = "TEXT_SIZE_INT"; // Text size was once a float so to prevent
    // an old saved float value of getting in the way, let's name it TEXT_SIZE_INT
    public static final String SPACINGS = "SPACINGS";
    private static final int MIN_TEXT_SIZE_VALUE = 10;
    private static final int MAX_TEXT_SIZE_VALUE = 40;

    private int textSize;
    private int spacings;

    private TextView integerPart, fractionalPart;
    private CardView settingsLayout;
    private ImageButton openSettingsButton;
    private SeekBar textSizeSB;
    private TextView textSizeTV; // TextView above textSizeSB
    private EditText spacingsET;
    private ViewGroup animationContainer; // Contains Views that should be animated (when the settings panel is expanded/collapsed)

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reference, container, false);

        integerPart = root.findViewById(R.id.integerPart_textView);
        fractionalPart = root.findViewById(R.id.fractionalPart_textView);
        settingsLayout = root.findViewById(R.id.settings_layout);
        openSettingsButton = root.findViewById(R.id.openSettings_button);
        textSizeSB = root.findViewById(R.id.textSize_seekBar);
        spacingsET = root.findViewById(R.id.spacings_editText);
        textSizeTV = root.findViewById(R.id.textSize_textView);
        animationContainer = root.findViewById(R.id.animation_container);

        restoreValues();
        reload();

        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Animate expansion of the settings menu.
                TransitionManager.beginDelayedTransition(animationContainer, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.VISIBLE);
                openSettingsButton.setVisibility(View.GONE);
            }
        });
        root.findViewById(R.id.closeSettings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate collapse of the settings menu.
                TransitionManager.beginDelayedTransition(animationContainer, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.GONE);
                openSettingsButton.setVisibility(View.VISIBLE);
            }
        });

        setTextWatchers();

        return root;
    }

    private void setTextWatchers() {
        // Set listeners on the settings menu so that the layout is updated instantly.
        spacingsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Parse the text
                try {
                    spacings = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    spacings = 0;
                }
                // Load the new value
                reload();
                // Save the new preference
                getActivity().getPreferences(0).edit().putInt(SPACINGS, spacings).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        textSizeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                // The range of text size should be 10 to 40, but SeekBar only has a max setting.
                // Max is set to 30, so add 10 to progress each time.
                progress += 10;
                // Check if progress is within allowed bounds:
                if (progress < MIN_TEXT_SIZE_VALUE) {
                    textSize = MIN_TEXT_SIZE_VALUE;
                } else if (progress > MAX_TEXT_SIZE_VALUE) {
                    textSize = MAX_TEXT_SIZE_VALUE;
                } else {
                    textSize = progress;
                }
                // Set the text size. Let's not bother calling reload(), just do it manually:
                fractionalPart.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                // Update text above the SeekBar:
                textSizeTV.setText(getContext().getString(R.string.text_size_colon, textSize));
                // Save the new preference
                getActivity().getPreferences(0).edit().putInt(TEXT_SIZE, textSize).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void restoreValues() {
        // Load preferences from permanent storage
        textSize = getActivity().getPreferences(0).getInt(TEXT_SIZE, 18);
        spacings = getActivity().getPreferences(0).getInt(SPACINGS, 10);

        // Setup views in the settings menu
        textSizeSB.setProgress(textSize - 10); // See comments in fragment_reference.xml
        textSizeTV.setText(getContext().getString(R.string.text_size_colon, textSize));
        spacingsET.setText(Integer.toString(spacings));
    }

    private void reload() {
        // Set texts to current Digits (thereby also setting the desired spacings in fractionalPart)
        integerPart.setText(Digits.currentDigit.getIntegerPart() + "."); // TODO Use point or comma depending on locale
        setFractionalPartText();

        // Set the desired text size
        fractionalPart.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    @Override
    public void notifyDigitsChanged() {
        reload();
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

    @Override
    public void showOnScreenKeyboard(boolean show) {
        // N/A
    }

    @Override
    public void refreshKeyboard() {
        // N/A
    }

    @Override
    public Class getPreviousFragment() {
        return null;
    }
}
