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
import android.widget.TextView;
import android.widget.Toast;

import com.teinproductions.tein.pitrainer.keyboard.Keyboard;

public class CompleteFragment extends Fragment
        implements FragmentInterface, CompleteFragmentSettingsDialog.Listener {

    public static final String RANGE = "RANGE";
    public static final String NUM_OF_DIGITS_GIVEN = "NUM_OF_DIGITS_GIVEN";
    public static final String LENGTH_OF_ANSWER = "LENGTH_OF_ANSWER";

    private ActivityInterface listener;

    private int range, numOfDigits, answerLength;
    private String answer;

    private int selection = 0, lastTextLength = 0;
    private boolean indirectTextChange = false;

    private TextView statement; // I call this "statement" because this game is called "complete the statement"
    private EditText editText;
    private Keyboard keyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listener = (ActivityInterface) getActivity();

        View theView = inflater.inflate(R.layout.fragment_complete, container, false);

        statement = (TextView) theView.findViewById(R.id.statement_textView);
        editText = (EditText) theView.findViewById(R.id.editText);
        keyboard = (Keyboard) theView.findViewById(R.id.keyboard);

        theView.findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSettings();
            }
        });

        theView.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        setTextWatcher();
        keyboard.setEditText(editText);
        reload();

        return theView;
    }

    private boolean restoreValues() {
        String name = Digits.currentDigit.getName();

        range = getActivity().getPreferences(0).getInt(RANGE + name, -1);
        numOfDigits = getActivity().getPreferences(0).getInt(NUM_OF_DIGITS_GIVEN + name, -1);
        answerLength = getActivity().getPreferences(0).getInt(LENGTH_OF_ANSWER + name, -1);

        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));

        // If the values existed, return true and if not, return false
        return !(range == -1 || numOfDigits == -1 || answerLength == -1);
    }

    private void next() {
        editText.setText("");
        String digits = Digits.currentDigit.getFractionalPart().substring(0, range);

        final int rangeOfIndex = range - 1 - answerLength - numOfDigits;
        if (rangeOfIndex < 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.check_your_settings),
                    Toast.LENGTH_SHORT).show();
            onClickSettings();
            return;
        }

        final int index = (int) Math.floor(Math.random() * rangeOfIndex);

        statement.setText(digits.substring(index, index + numOfDigits) + "...");
        answer = digits.substring(index + numOfDigits, index + numOfDigits + answerLength);
    }

    private void setTextWatcher() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
                    // TODO show the correct answer for a short time before continuing to the next challenge
                    return;
                } else {
                    listener.animateToolbarColor(true);
                }

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
            public void afterTextChanged(Editable editable) {
            }
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

    private void onClickSettings() {
        CompleteFragmentSettingsDialog.show(this, numOfDigits, range, answerLength);
    }

    @Override
    public void notifyDigitsChanged() {
        reload();
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
    }

    @Override
    public Class getPreviousFragment() {
        return null;
    }

    @Override
    public void reload() {
        if (!restoreValues()) {
            // Default settings:
            if (Digits.currentDigit.getFractionalPart().length() >= 50) {
                numOfDigits = 12;
                range = 50;
                answerLength = 6;
            } else {
                range = Digits.currentDigit.getFractionalPart().length();
                numOfDigits = range / 4;
                if (numOfDigits == 0) numOfDigits = 1;
                answerLength = numOfDigits;
                if (answerLength + numOfDigits >= range) {
                    answerLength = range - numOfDigits;
                }
            }
            onClickSettings();
        }

        next();
    }
}
