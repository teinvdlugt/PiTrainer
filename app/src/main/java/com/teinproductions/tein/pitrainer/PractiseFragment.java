package com.teinproductions.tein.pitrainer;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.teinproductions.tein.pitrainer.keyboard.Keyboard;

public class PractiseFragment extends Fragment implements FragmentInterface {

    private static final String ERRORS = "ERRORS";
    private static final String INPUT = "PRACTISE_FRAGMENT_INPUT";

    private ActivityInterface listener;

    private EditText inputET;
    private TextView integerPartTV, digitsTV, errorsTV, percentageTV;
    private Keyboard keyboard;
    private ImageButton restartButton;
    private ViewGroup root;

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;
    private int errors = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listener = (ActivityInterface) getActivity();

        View view = inflater.inflate(R.layout.fragment_practise, container, false);

        inputET = (EditText) view.findViewById(R.id.input_editText);
        digitsTV = (TextView) view.findViewById(R.id.digits_textView);
        keyboard = (Keyboard) view.findViewById(R.id.keyboard);
        restartButton = (ImageButton) view.findViewById(R.id.refresh_button);
        errorsTV = (TextView) view.findViewById(R.id.errors_textView);
        percentageTV = (TextView) view.findViewById(R.id.percentage_textView);
        integerPartTV = (TextView) view.findViewById(R.id.integerPart_textView);
        root = view.findViewById(R.id.root);

        keyboard.setEditText(inputET);
        restoreValues();
        fillTextViews();
        setRestartImageResource();
        setTextWatcher();

        return view;
    }

    private void restoreValues() {
        errors = getActivity().getPreferences(0).getInt(ERRORS, 0);

        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + ".");

        String input = getActivity().getPreferences(0).getString(INPUT, "");
        inputET.setText(toColoredSpannable(input));
        inputET.setSelection(inputET.length());
        listener.animateToolbarColor(!Digits.isIncorrect(inputET.getText().toString()));

        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        listener.preventSoftKeyboardFromShowingUp(inputET, show);
        TransitionManager.beginDelayedTransition(root);
        keyboard.setVisibility(show ? View.VISIBLE : View.GONE);
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

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.drawable.ic_refresh_36dp);
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
            Drawable drawable = restartButton.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat)
                ((AnimatedVectorDrawableCompat) drawable).start();
            else if (drawable instanceof AnimatedVectorDrawable)
                ((AnimatedVectorDrawable) drawable).start();
        }

        inputET.setText("");
        errors = 0;
        fillTextViews();
    }

    private void fillTextViews() {
        String input = inputET.getText().toString();

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == Digits.currentDigit.getFractionalPart().charAt(i)) {
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
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (indirectTextChange) return;
                if (inputET.length() >= Digits.currentDigit.getFractionalPart().length()) {
                    tooMuchInput();
                    indirectTextChange = true;
                    inputET.setText(inputET.getText().delete(
                            Digits.currentDigit.getFractionalPart().length() - 1, inputET.length() - 1));
                    inputET.setSelection(inputET.length());
                    indirectTextChange = false;
                }

                selection = inputET.getSelectionStart();

                if (Digits.isIncorrect(inputET.getText().toString()) && inputET.length() != 0) {
                    listener.animateToolbarColor(false);

                    // If the last typed character is wrong:
                    if (lastTextLength < inputET.length() // backspace is not pressed
                            && selection != 0 // For catching index o.o.b. exception in next line
                            && inputET.getText().toString().charAt(selection - 1)
                            != Digits.currentDigit.getFractionalPart().charAt(selection - 1)) { // The typed character is wrong

                        errors++;
                        listener.vibrate();
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

    private SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != Digits.currentDigit.getFractionalPart().charAt(i)) {
                // If the character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }

    private void tooMuchInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.too_much_input_dialog_message)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }


    @Override
    public void notifyDigitsChanged() {
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + ".");
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
