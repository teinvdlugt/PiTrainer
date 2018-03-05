package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.keyboard.Keyboard;

public class CompleteFragment extends Fragment implements FragmentInterface {

    // Keys for Preferences
    public static final String RANGE_START = "RANGE_START";
    public static final String RANGE_STOP = "RANGE";
    public static final String NUM_OF_DIGITS_GIVEN = "NUM_OF_DIGITS_GIVEN";
    public static final String LENGTH_OF_ANSWER = "LENGTH_OF_ANSWER";
    // Preference stating whether this fragment was already once opened with this number.
    // For the real preference key, append the Digits' name to this value.
    public static final String FRAGMENT_OPENED = "FRAGMENT_OPENED_";

    // Defaults for preferences
    public static final int RANGE_START_DEFAULT = 1;
    public static final int RANGE_STOP_DEFAULT_MAX = 50; // When currentDigits.length < 50 we set range stop to currentDigits.length
    public static final int NUM_DIGITS_GIVEN_DEFAULT = 12;
    public static final int LENGTH_OF_ANSWER_DEFAULT = 6;

    private ActivityInterface listener;

    private int rangeStart; // Starts counting at 1, inclusive
    private int rangeStop;  // Starts counting at 1, inclusive
    private int numDigits, answerLength;
    private String answer; // The correct answer that should be filled in

    private int selection = 0, lastTextLength = 0;
    private boolean indirectTextChange = false;

    private TextView statement; // I call this "statement" because this game is called "complete the statement"
    private EditText editText;  // EditText for the answer
    private Keyboard keyboard;
    private EditText rangeStartET, rangeStopET; // EditTexts in the settings menu
    private SeekBar numDigitsSB, lengthAnsSB;
    private TextView numDigitsTV, lengthAnsTV;
    private ImageButton openSettingsButton;
    private ViewGroup settingsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listener = (ActivityInterface) getActivity();

        View root = inflater.inflate(R.layout.fragment_complete, container, false);

        statement = root.findViewById(R.id.statement_textView);
        editText = root.findViewById(R.id.editText);
        keyboard = root.findViewById(R.id.keyboard);
        numDigitsSB = root.findViewById(R.id.numDigits_seekBar);
        numDigitsTV = root.findViewById(R.id.numDigits_textView);
        lengthAnsSB = root.findViewById(R.id.lengthOfAnswer_seekBar);
        lengthAnsTV = root.findViewById(R.id.lengthOfAnswer_textView);
        rangeStopET = root.findViewById(R.id.rangeStop_editText);
        rangeStartET = root.findViewById(R.id.rangeStart_editText);
        openSettingsButton = root.findViewById(R.id.openSettings_button);
        settingsLayout = root.findViewById(R.id.settings_layout);

        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtils.animateExpand(getContext(), settingsLayout, openSettingsButton);
            }
        });
        root.findViewById(R.id.closeSettings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationUtils.animateCollapse(getContext(), settingsLayout, openSettingsButton);
            }
        });

        root.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        restoreValues();
        setSettingsListeners();
        setTextWatcher();
        keyboard.setEditText(editText);
        next();

        return root;
    }

    /**
     * Load the rangeStart, rangeStop, numDigits, and answerLength values from preferences,
     * according to the currentDigits. Also load those values into the views in the settings
     * panel. Open the keyboard if the keyboard preference is set to true.
     * Open the settings panel if this is the first time this fragment is opened with this
     * Digits.
     */
    private void restoreValues() {
        String name = Digits.currentDigit.getName();

        // Load preferences into memory:
        rangeStart = getActivity().getPreferences(0).getInt(RANGE_START + name, RANGE_START_DEFAULT);
        numDigits = getActivity().getPreferences(0).getInt(NUM_OF_DIGITS_GIVEN + name, NUM_DIGITS_GIVEN_DEFAULT);
        answerLength = getActivity().getPreferences(0).getInt(LENGTH_OF_ANSWER + name, LENGTH_OF_ANSWER_DEFAULT);
        // For rangeStop, take the minimum of current digits length and saved/preloaded preference:
        int maybeRangeStop = getActivity().getPreferences(0).getInt(RANGE_STOP + name, RANGE_STOP_DEFAULT_MAX);
        rangeStop = Math.min(maybeRangeStop, Digits.currentDigit.getFractionalPart().length());

        // Setup settings Views
        numDigitsSB.setProgress(numDigits - 4);
        numDigitsTV.setText(getContext().getString(R.string.number_of_digits_given_colon, numDigits));
        lengthAnsSB.setProgress(answerLength - 4);
        lengthAnsTV.setText(getContext().getString(R.string.length_of_answer_colon, answerLength));
        rangeStartET.setText(String.valueOf(rangeStart));
        rangeStopET.setText(String.valueOf(rangeStop));

        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));

        // Open the settings menu if this is the first time this Digits is used.
        if (!getActivity().getPreferences(0).getBoolean(FRAGMENT_OPENED + name, false)) {
            openSettingsButton.performClick();
            getActivity().getPreferences(0).edit().putBoolean(
                    FRAGMENT_OPENED + Digits.currentDigit.getName(), true).apply();
        }
    }

    /**
     * Clears the answer EditText and creates a new question.
     * Assumes that rangStart, rangeStop, answerLength and numDigits are all valid numbers that
     * agree with each other and with the length of the current Digits object. TODO handle erroneous values properly elsewhere!
     */
    private void next() {
        // Clear the answer text field
        editText.setText("");

        // Get a substring of Digits that only contains the decimals within the specified range.
        // rangeStart starts from 1, so we need to subtract 1 from it to get the inclusive start index. But we also want
        // to start at 0 when rangeStart = 0, so we take max(0, rangeStart - 1). rangeStop is inclusive and
        // starts counting at 1, which is equivalent to exclusive and starts counting at zero.
        int tempRangeStart = Math.max(0, rangeStart - 1);
        String digits = Digits.currentDigit.getFractionalPart().substring(tempRangeStart, rangeStop - 1);

        final int index = (int) Math.floor(Math.random() * (rangeStop - tempRangeStart + 1 - answerLength - numDigits)) + rangeStart; // TODO nog ff over nadenken

        statement.setText(digits.substring(index, index + numDigits) + "…");
        answer = digits.substring(index + numDigits, index + numDigits + answerLength);
    }

    private void setSettingsListeners() {
        numDigitsSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                // The 'real' range of the SeekBar is 0 - 14, add 4 to make it 4 - 18.
                int newNumDigits = progress + 4;

                // Check if this value is allowed.
                if (checkEnteredSettingsAndShowErrorMessage(newNumDigits, answerLength, rangeStart, rangeStop)) {
                    // The value is allowed!
                    numDigits = newNumDigits;
                    // Save the new preference, and update the TextView.
                    getActivity().getPreferences(0).edit().putInt(
                            NUM_OF_DIGITS_GIVEN + Digits.currentDigit.getName(), numDigits).apply();
                    numDigitsTV.setText(getContext().getString(R.string.number_of_digits_given_colon, numDigits));
                    // Immediately apply the settings:
                    next();
                } // else, do nothing. An error message will be displayed in an EditText.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        lengthAnsSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                // The 'real' range of the SeekBar is 0 - 14, add 4 to make it 4 - 18.
                int newAnswerLength = progress + 4;

                // Check if this value is allowed:
                if (checkEnteredSettingsAndShowErrorMessage(numDigits, newAnswerLength, rangeStart, rangeStop)) {
                    // This value is allowed!
                    answerLength = progress + 4;
                    // Save the new preference, and update the TextView
                    getActivity().getPreferences(0).edit().putInt(
                            LENGTH_OF_ANSWER + Digits.currentDigit.getName(), answerLength).apply();
                    lengthAnsTV.setText(getContext().getString(R.string.length_of_answer_colon, answerLength));
                    // Immediately apply the settings:
                    next();
                } // else, do nothing. An error message will be displayed in an EditText.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        rangeStartET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the input is valid. Only save preference and call next() if the
                // input is valid. If not, show error message in TextInputLayout.

                int input;
                try {
                    input = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    rangeStartET.setError(getString(R.string.error_message_invalid_integer));
                    return;
                }

                // Check if entered value is invalid
                if (checkEnteredSettingsAndShowErrorMessage(numDigits, answerLength, input, rangeStop)) {
                    // The new settings are alright, so save the preferences and call next().
                    rangeStart = input;
                    getActivity().getPreferences(0).edit().putInt(
                            RANGE_START + Digits.currentDigit.getName(), rangeStart).apply();
                    next();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        rangeStopET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the input is valid. Only save preference and call next() if the
                // input is valid. If not, show error message in TextInputLayout.

                int input;
                try {
                    input = Integer.parseInt(s.toString().trim());
                } catch (NumberFormatException e) {
                    rangeStopET.setError(getString(R.string.error_message_invalid_integer));
                    return;
                }

                // Check if entered value is invalid
                if (checkEnteredSettingsAndShowErrorMessage(numDigits, answerLength, rangeStart, input)) {
                    // The new settings are alright, so save the preferences and call next().
                    rangeStop = input;
                    getActivity().getPreferences(0).edit().putInt(
                            RANGE_STOP + Digits.currentDigit.getName(), rangeStop).apply();
                    next();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Checks whether settings are in accordance with each other and with current Digits length.
     * If not, show error message in appropriate TextInputLayout and return false. Otherwise
     * clear the error messages and return true.
     */
    private boolean checkEnteredSettingsAndShowErrorMessage(int numDigits, int answerLength,
                                                            int rangeStart, int rangeStop) {
        // answerLength and numDigits are assumed to be set correctly.
        if (rangeStart > rangeStop - answerLength - numDigits + 1) {
            // rangeStop is too low.
            rangeStopET.setError(getString(R.string.error_message_too_small));
            return false;
        } else if (rangeStop > Digits.currentDigit.getFractionalPart().length()) {
            // There aren't that many digits!
            rangeStopET.setError(getString(R.string.error_message_too_large));
            return false;
        }
        return true;
    }

    private void setTextWatcher() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (indirectTextChange) return;

                selection = editText.getSelectionStart();

                if (editText.length() == 0) {
                    listener.animateToolbarColor(true);
                    lastTextLength = editText.length();
                    return;
                }

                if (!answer.startsWith(editText.getText().toString()) && editText.length() <= answerLength) {
                    // The input is wrong!
                    listener.animateToolbarColor(false);

                    // If the last typed character is wrong:
                    if (lastTextLength < editText.length() // backspace is not pressed
                            && selection != 0 // For catching index o.o.b. exception in next line
                            && editText.getText().toString().charAt(selection - 1)
                            != answer.charAt(selection - 1)) { // The typed character is wrong
                        listener.vibrate();
                    }
                } else if (!answer.startsWith(editText.getText().toString()) && editText.length() > answerLength) {
                    // Delete the typed character
                    editText.getText().delete(selection - 1, selection);
                } else if (editText.length() == answerLength) {
                    // The answer is correct, go to the next challenge
                    listener.animateToolbarColor(true);
                    next();
                    // TODO show the correct answer for a short time before continuing to the next challenge,
                    // or make the toolbar green for a moment.
                    return;
                } else {
                    listener.animateToolbarColor(true);
                }

                // Make the wrong digits red. The indirectTextChange field makes sure the new
                // setText() call is not seen as if the user changed the text. Also, restore the
                // location of the pointer (selection) in the EditText, because the setText call
                // automatically places it at the end.
                indirectTextChange = true;
                editText.setText(toColoredSpannable());
                indirectTextChange = false;
                if (selection < editText.length()) {
                    editText.setSelection(selection);
                } else {
                    editText.setSelection(editText.length());
                }

                lastTextLength = editText.length();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private Spannable toColoredSpannable() {
        String text = editText.getText().toString();
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != answer.charAt(i)) {
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }

    @Override
    public void notifyDigitsChanged() {
        // Load the preferences specific to this Digits object into memory, and
        // refresh the settings panel texts.
        restoreValues();

        // Start a new challenge.
        next();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        keyboard.setVisibility(show ? View.VISIBLE : View.GONE);
        listener.preventSoftKeyboardFromShowingUp(editText, show);
    }

    @Override
    public void refreshKeyboard() {
        keyboard.refreshKeyboardLayout();
        keyboard.refreshKeyboardSize();
        keyboard.resetBackgrounds();
    }

    @Override
    public Class getPreviousFragment() {
        return null;
    }
}
