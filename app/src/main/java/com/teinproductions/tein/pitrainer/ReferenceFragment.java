package com.teinproductions.tein.pitrainer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

public class ReferenceFragment extends Fragment
        implements FragmentInterface {

    // public static final String ADS_ENABLED_KEY = "ads_enabled"; // Must be the same as in Firebase console!

    private static final String TEXT_SIZE = "TEXT_SIZE_INT"; // Text size was once a float so to prevent
    // an old saved float value of getting in the way, let's name it TEXT_SIZE_INT
    private static final String SPACINGS = "SPACINGS";
    private static final String SPACINGS_ENABLED = "SPACINGS_ENABLED";
    private static final String LINE_COUNT = "LINE_COUNT";
    private static final String LINE_COUNT_ENABLED = "LINE_COUNT_ENABLED";
    private static final int MIN_TEXT_SIZE_VALUE = 5;
    private static final int MAX_TEXT_SIZE_VALUE = 60;

    private ActivityInterface activityInterface;

    private int textSize;
    private int spacings;
    private int lineCount;
    private int highlightPosition = -1;

    private TextView integerPart, fractionalPart;
    private CardView settingsLayout;
    private ImageButton openSettingsButton, closeSettingsButton;
    private SeekBar textSizeSB;
    private TextView textSizeTV; // TextView above textSizeSB
    private EditText spacingsET, lineCountET;
    private CheckBox spacingsCB, lineCountCB;
    private ViewGroup animationContainer; // Contains Views that should be animated (when the settings panel is expanded/collapsed)
    private Button scrollToButton;
    private EditText scrollToET;
    private ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();
        View root = inflater.inflate(R.layout.fragment_reference, container, false);

        integerPart = root.findViewById(R.id.integerPart_textView);
        fractionalPart = root.findViewById(R.id.fractionalPart_textView);
        settingsLayout = root.findViewById(R.id.settings_layout);
        openSettingsButton = root.findViewById(R.id.openSettings_button);
        closeSettingsButton = root.findViewById(R.id.closeSettings_button);
        textSizeSB = root.findViewById(R.id.textSize_seekBar);
        spacingsET = root.findViewById(R.id.spaces_editText);
        lineCountET = root.findViewById(R.id.lineCount_editText);
        spacingsCB = root.findViewById(R.id.spaces_checkBox);
        lineCountCB = root.findViewById(R.id.lineCount_checkBox);
        textSizeTV = root.findViewById(R.id.textSize_textView);
        animationContainer = root.findViewById(R.id.animation_container);
        // adView = root.findViewById(R.id.adView);
        // adViewContainer = root.findViewById(R.id.adView_container);
        scrollToButton = root.findViewById(R.id.scrollTo_button);
        scrollView = root.findViewById(R.id.scrollView);
        scrollToET = root.findViewById(R.id.scrollTo_editText);

        restoreValues();
        reload();
        setMenuListeners();
        setupMenuAnimations();
        // setupAdView();

        return root;
    }

    private void setupMenuAnimations() {
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log a firebase event.
                activityInterface.logEventSelectContent("openSettingsButton", "openSettingsButton", MainActivity.CONTENT_TYPE_BUTTON);
                // Animate expansion of the settings menu.
                TransitionManager.beginDelayedTransition(animationContainer, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.VISIBLE);
                openSettingsButton.setVisibility(View.GONE);
            }
        });
        closeSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate collapse of the settings menu.
                TransitionManager.beginDelayedTransition(animationContainer, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.GONE);
                openSettingsButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /*private void setupAdView() {
        // Setup adView:
        // Check if ads are enabled (Firebase Remote Config):
        if (FirebaseRemoteConfig.getInstance().getBoolean(ADS_ENABLED_KEY)) {
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    adViewContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    adViewContainer.setVisibility(View.VISIBLE);
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else adViewContainer.setVisibility(View.GONE);
    }*/

    private void setMenuListeners() {
        // Listen for changes in the text fields and update layout instantly.
        spacingsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Parse the text
                try {
                    spacings = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    spacings = 0;
                }
                // Update fractional part, if spacings is enabled
                if (spacingsCB.isChecked())
                    setFractionalPartText();
                // Save the new preference
                getActivity().getPreferences(0).edit().putInt(SPACINGS, spacings).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        spacingsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spacingsET.setEnabled(isChecked);
                setFractionalPartText();
                getActivity().getPreferences(0).edit().putBoolean(SPACINGS_ENABLED, isChecked).apply();
            }
        });

        lineCountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Parse the text
                try {
                    lineCount = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    lineCount = 0;
                }
                // Update fractional part, if lineCount is enabled
                if (lineCountCB.isChecked())
                    setFractionalPartText();
                // Save the new preference
                getActivity().getPreferences(0).edit().putInt(LINE_COUNT, lineCount).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        lineCountCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lineCountET.setEnabled(isChecked);
                setFractionalPartText();
                getActivity().getPreferences(0).edit().putBoolean(LINE_COUNT_ENABLED, isChecked).apply();
            }
        });

        textSizeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                // The range of text size should be 5 to 60, but SeekBar only has a max setting.
                // Max is set to 55, so add 5 to progress each time.
                textSize = progress + MIN_TEXT_SIZE_VALUE;
                // Just to be sure
                textSize = Math.max(textSize, MIN_TEXT_SIZE_VALUE);
                textSize = Math.min(textSize, MAX_TEXT_SIZE_VALUE);
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


        scrollToET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Integer.parseInt(s.toString());
                    scrollToButton.setEnabled(true);
                } catch (NumberFormatException e) {
                    scrollToButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Trigger this listener now to enable/disable button:
        scrollToET.setText(scrollToET.getText());


        scrollToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide keyboard
                scrollToET.clearFocus();
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(scrollToET.getWindowToken(), 0);

                // Parse input
                try {
                    int digitPosition = Integer.parseInt(scrollToET.getText().toString()) - 1;
                    digitPosition = Math.max(digitPosition, 0);
                    if (digitPosition < Digits.currentDigit.getFractionalPart().length()) {
                        // Collapse settings menu
                        settingsLayout.setVisibility(View.GONE);
                        openSettingsButton.setVisibility(View.VISIBLE);

                        // Position of corresponding character in textView
                        final int charPosition = getDigitPositionInTextView(digitPosition);

                        fractionalPart.post(new Runnable() { // Post it as the settingsLayout needs to collapse first
                            @Override
                            public void run() {
                                // Scroll to the requested digit
                                Layout layout = fractionalPart.getLayout();
                                if (layout != null) {
                                    int yCoordinate = layout.getLineTop(layout.getLineForOffset(charPosition))
                                            + fractionalPart.getTop();
                                    scrollView.scrollTo(0, yCoordinate);
                                }
                            }
                        });

                        // Highlight the requested digit
                        addHighlight(charPosition);
                        highlightPosition = digitPosition;

                        return;
                    }
                } catch (NumberFormatException ignored) {}
                // Show error message
                Snackbar.make(scrollView, R.string.invalid_input, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void restoreValues() {
        // Load preferences from permanent storage
        textSize = getActivity().getPreferences(0).getInt(TEXT_SIZE, 18);
        spacings = getActivity().getPreferences(0).getInt(SPACINGS, 10);
        boolean spacingsEnabled = getActivity().getPreferences(0).getBoolean(SPACINGS_ENABLED, true);
        lineCount = getActivity().getPreferences(0).getInt(LINE_COUNT, 20);
        boolean lineCountEnabled = getActivity().getPreferences(0).getBoolean(LINE_COUNT_ENABLED, false);

        // Setup views in the settings menu
        textSizeSB.setProgress(textSize - 10); // See comments in fragment_reference.xml
        textSizeTV.setText(getContext().getString(R.string.text_size_colon, textSize));
        spacingsET.setText(Integer.toString(spacings));
        spacingsCB.setChecked(spacingsEnabled);
        spacingsET.setEnabled(spacingsEnabled);
        lineCountET.setText(Integer.toString(lineCount));
        lineCountCB.setChecked(lineCountEnabled);
        lineCountET.setEnabled(lineCountEnabled);
    }

    /**
     * Sets text of integer and fractional parts to currentDigits and applies
     * textSize, spacings and lineCount settings. And adds highlight if applicable.
     */
    private void reload() {
        // Set texts to current Digits (thereby also setting the desired spacings and line breaks in fractionalPart)
        integerPart.setText(Digits.currentDigit.getIntegerPart() + Digits.decimalSeparator);
        setFractionalPartText();

        // Set the desired text size
        fractionalPart.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * Sets text of fractional part to currentDigits and applies spacings and lineCount
     * settings. And adds highlight if applicable.
     */
    private void setFractionalPartText() {
        StringBuilder sb = new StringBuilder(Digits.currentDigit.getFractionalPart());
        // If both spacings and lineCount are enabled and
        // spacings < lineCount (because otherwise this would yield the same result as
        // when spacings was disabled, but with more calculation time):
        if (spacingsCB.isChecked() && spacings > 0
                && lineCountCB.isChecked() && lineCount > 0
                && spacings < lineCount) {
            // Reset position of spaces for every new line
            int numOfSpacesPerLine = lineCount / spacings;
            if (lineCount % spacings == 0) numOfSpacesPerLine--; // No space at end of line
            for (int i = 0; i < sb.length(); i += lineCount + numOfSpacesPerLine + 1) { // Newline "\n" is 1 character
                if (i != 0) sb.insert(i - 1, "\n");
                for (int j = spacings; j < lineCount + numOfSpacesPerLine - 1 && i + j < sb.length(); j += spacings + 1) {
                    sb.insert(i + j, " ");
                }
                //if (i + lineCount + numOfSpacesPerLine < sb.length()) sb.insert(i + lineCount + numOfSpacesPerLine, "\n");
            }
        }
        // If only lineCount is enabled: (should be above "if only spacings is enabled" because of spacings < lineCount condition)
        else if (lineCountCB.isChecked() && lineCount > 0) {
            for (int i = lineCount; i < sb.length(); i += lineCount + 1) {
                sb.insert(i, "\n");
            }
        }
        // If only spacings are enabled:
        else if (spacingsCB.isChecked() && spacings > 0) { // If 0, then no spacings
            for (int i = spacings; i < sb.length(); i += spacings + 1) {
                sb.insert(i, " ");
            }
        }
        fractionalPart.setText(sb);

        // Add highlight
        if (highlightPosition != -1 && highlightPosition < Digits.currentDigit.getFractionalPart().length())
            addHighlight(getDigitPositionInTextView(highlightPosition));
        else highlightPosition = -1; // highlightPosition invalid
    }

    private void addHighlight(int charPosition) {
        SpannableString ss = new SpannableString(fractionalPart.getText());
        // Remove previous span
        BackgroundColorSpan[] oldSpans = ss.getSpans(0, ss.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : oldSpans)
            ss.removeSpan(span);

        // Set new span
        ss.setSpan(new BackgroundColorSpan(ContextCompat.getColor(getContext(), R.color.highlight_color)),
                charPosition, charPosition + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        fractionalPart.setText(ss);
    }

    /**
     * Returns the character index of given digit in fractionalPart textView.
     * Does not check if the digit position is larger than Digits length.
     *
     * @param digitPosition Starts counting at 0! So subtract 1 from the UI input
     * @return Character index, starts counting at 0
     */
    private int getDigitPositionInTextView(int digitPosition) {
        int numExtraChars = 0;
        // If both lines and spaces enabled:
        if (lineCountCB.isChecked() && lineCount > 0
                && spacingsCB.isChecked() && spacings > 0
                && spacings < lineCount) {
            int spacesPerLine = lineCount / spacings;
            if (lineCount % spacings == 0) spacesPerLine--; // No spaces at end of line
            int extraCharsPerLine = spacesPerLine + 1;
            int numLinesBefore = digitPosition / lineCount;
            int numExtraSpaces = (digitPosition % lineCount) / spacings;
            numExtraChars = numLinesBefore * extraCharsPerLine + numExtraSpaces;
        }
        // If only lines enabled:
        else if (lineCountCB.isChecked() && lineCount > 0) {
            numExtraChars = digitPosition / lineCount;
        }
        // If only spaces enabled:
        else if (spacingsCB.isChecked() && spacings > 0) {
            numExtraChars = digitPosition / spacings;
        }

        return digitPosition + numExtraChars;
    }

    @Override
    public void notifyDigitsChanged() {
        highlightPosition = -1; // Reset highlight
        reload();
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
