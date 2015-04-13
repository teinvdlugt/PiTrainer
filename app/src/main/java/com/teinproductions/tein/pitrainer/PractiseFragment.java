package com.teinproductions.tein.pitrainer;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

public class PractiseFragment extends Fragment implements FragmentInterface {

    private static final String ERRORS = "ERRORS";
    private static final String INPUT = "PRACTISE_FRAGMENT_INPUT";

    private ActivityInterface listener;

    private EditText inputET;
    private TextView integerPartTV, digitsTV, errorsTV, percentageTV;
    private Keyboard keyboard;
    private ImageButton restartButton;

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;
    private int errors = 0;

    private MainActivity.Digits current_digits;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listener = (ActivityInterface) getActivity();

        View root = inflater.inflate(R.layout.fragment_practise, container, false);

        inputET = (EditText) root.findViewById(R.id.input_editText);
        digitsTV = (TextView) root.findViewById(R.id.digits_textView);
        keyboard = (Keyboard) root.findViewById(R.id.keyboard);
        restartButton = (ImageButton) root.findViewById(R.id.refresh_button);
        errorsTV = (TextView) root.findViewById(R.id.errors_textView);
        percentageTV = (TextView) root.findViewById(R.id.percentage_textView);
        integerPartTV = (TextView) root.findViewById(R.id.integerPart_textView);

        keyboard.setEditText(inputET);
        restoreValues();
        fillTextViews();
        setRestartImageResource();
        setTextWatcher();

        return root;
    }

    private void restoreValues() {
        errors = getActivity().getPreferences(0).getInt(ERRORS, 0);

        current_digits = MainActivity.Digits.values()[
                getActivity().getPreferences(0).getInt(MainActivity.CURRENT_DIGITS_ORDINAL, 0)];
        integerPartTV.setText(current_digits.integerPart);

        String input = getActivity().getPreferences(0).getString(INPUT, "");
        inputET.setText(toColoredSpannable(input));
        inputET.setSelection(inputET.length());
        listener.animateToolbarColor(!isIncorrect(inputET.getText().toString()));

        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        listener.preventSoftKeyboardFromShowingUp(inputET, show);
        keyboard.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.mipmap.ic_refresh_white_36dp);
        }

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRestart();
            }
        });
    }

    private void onClickRestart() {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AnimatedVectorDrawable) restartButton.getDrawable()).start();
        }
        inputET.setText("");
        errors = 0;
        fillTextViews();
    }

    private void fillTextViews() {
        String input = inputET.getText().toString();

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == current_digits.fractionalPart.charAt(i)) {
                count++;
            }
        }

        digitsTV.setText(" " + count);
        errorsTV.setText(" " + errors);

        if (inputET.length() == 0 || errors > inputET.length()) {
            percentageTV.setText(" 0%");
        } else {
            percentageTV.setText(" " + (int) Math.floor(100 - ((double) errors / inputET.length() * 100)) + "%");
        }
    }

    private void setTextWatcher() {
        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (indirectTextChange) return;

                selection = inputET.getSelectionStart();

                if (isIncorrect(inputET.getText().toString()) && inputET.length() != 0) {
                    listener.animateToolbarColor(false);

                    // If the last typed character is wrong:
                    if (lastTextLength < inputET.length() // backspace is not pressed
                            && selection != 0 // For catching index o.o.b. exception in next line
                            && inputET.getText().toString().charAt(selection - 1)
                            != current_digits.fractionalPart.charAt(selection - 1)) { // The typed character is wrong

                        errors++;
                        listener.vibrate(100);
                    }
                } else {
                    listener.animateToolbarColor(true);
                }

                indirectTextChange = true;
                inputET.setText(toColoredSpannable(inputET.getText().toString()));
                indirectTextChange = false;
                if (selection < inputET.length()) {
                    inputET.setSelection(selection);
                } else {
                    inputET.setSelection(inputET.length());
                }

                lastTextLength = inputET.length();

                fillTextViews();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean isIncorrect(String stringToCheck) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            if (stringToCheck.charAt(i) != current_digits.fractionalPart.charAt(i)) {
                return true;
            }
        }

        return false;
    }

    private SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != current_digits.fractionalPart.charAt(i)) {
                // If the character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }


    @Override
    public void setCurrentDigits(MainActivity.Digits digits) {
        current_digits = digits;
        integerPartTV.setText(current_digits.integerPart);
        onClickRestart();
    }

    @Override
    public void onPause() {
        getActivity().getPreferences(0).edit()
                .putInt(ERRORS, errors)
                .putString(INPUT, inputET.getText().toString())
                .apply();
        super.onPause();
    }
}
