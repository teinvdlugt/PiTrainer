package com.teinproductions.tein.pitrainer;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
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
    private static final String STARTING_DIGIT = "STARTING_DIGIT_"; // Append the name of the digit

    private ActivityInterface activityInterface;

    private EditText inputET;
    private TextView integerPartTV, digitsTV, errorsTV, percentageTV;
    private Keyboard keyboard;
    private ImageButton restartButton;
    private ViewGroup root;
    private EditText startDigitET;
    private ImageButton openSettingsButton;
    private ViewGroup settingsLayout;

    private int startDigit = 1; // Starts counting at 1

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;
    private int errors = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        View view = inflater.inflate(R.layout.fragment_practise, container, false);

        inputET = view.findViewById(R.id.input_editText);
        digitsTV = view.findViewById(R.id.digits_textView);
        keyboard = view.findViewById(R.id.keyboard);
        restartButton = view.findViewById(R.id.refresh_button);
        errorsTV = view.findViewById(R.id.errors_textView);
        percentageTV = view.findViewById(R.id.percentage_textView);
        integerPartTV = view.findViewById(R.id.integerPart_textView);
        root = view.findViewById(R.id.root);
        startDigitET = view.findViewById(R.id.startDigit_editText);
        openSettingsButton = view.findViewById(R.id.openSettings_button);
        settingsLayout = view.findViewById(R.id.settings_layout);

        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log a firebase event.
                activityInterface.logEventSelectContent("openSettingsButton", "openSettingsButton", MainActivity.CONTENT_TYPE_BUTTON);
                // Animate expansion of the settings menu.
                TransitionManager.beginDelayedTransition(root, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.VISIBLE);
                openSettingsButton.setVisibility(View.GONE);
                // Hide the keyboard when the settings menu is open, because maybe awkward.
                // But only in portrait mode.
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    keyboard.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.closeSettings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate collapse of the settings menu.
                TransitionManager.beginDelayedTransition(root, new AutoTransition()
                        .setDuration(200));
                settingsLayout.setVisibility(View.GONE);
                openSettingsButton.setVisibility(View.VISIBLE);
                // Show the keyboard again, if preferences say so
                showOnScreenKeyboard(getActivity().getPreferences(0)
                        .getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
            }
        });

        keyboard.setEditText(inputET);
        setTextWatcher(); // Has to be called before restoreValues() because restoreValues depends on this.
        restoreValues();
        setSettingsListeners();
        fillTextViews();
        setRestartImageResource();

        inputET.requestFocus();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void restoreValues() {
        // Restore startDigit preference:
        startDigit = getActivity().getPreferences(0).getInt(STARTING_DIGIT + Digits.currentDigit.getName(), 1);
        startDigitET.setText(startDigit + "");
        setStartDigit(startDigit);

        // Restore error amount:
        errors = getActivity().getPreferences(0).getInt(ERRORS, 0);

        // Restore inputET contents (this should be done AFTER restoring startDigitET, because
        // startDigitET's TextWatcher calls onClickRestart()):
        String input = getActivity().getPreferences(0).getString(INPUT, "");
        inputET.setText(input); // The coloring of the text will happen in the TextWatcher
        inputET.setSelection(inputET.length());

        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
    }

    private boolean indirectTextChangeStartDigitET = false;

    private void setSettingsListeners() {
        startDigitET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (indirectTextChangeStartDigitET) return;
                // Try to parse the input.
                int input;
                try {
                    input = Integer.parseInt(s.toString().trim());
                    if (input < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    startDigitET.setError(getString(R.string.error_message_invalid_integer));
                    return;
                }

                // Check if the input isn't too large
                if (input > Digits.currentDigit.getFractionalPart().length() - 2) {
                    startDigitET.setError(getString(R.string.error_message_too_large));
                    return;
                }
                // Input of 0 is equivalent to an input of 1:
                if (input == 0) input = 1;

                // Everything seems to be alright. Reset the input field and apply changes.
                onClickRestart(false);
                setStartDigit(input);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Sets this class's field this.startDigit to startDigit, saves the startDigit preference,
     * checks if startDigit has a legal value, and loads text into integerPartTV.
     * It DOESN'T change the text in startDigitET.
     */
    @SuppressLint("SetTextI18n")
    private void setStartDigit(int startDigit) {
        this.startDigit = startDigit;
        getActivity().getPreferences(0).edit()
                .putInt(STARTING_DIGIT + Digits.currentDigit.getName(), startDigit).apply();
        // Determine what should be in the integerPartTV.
        if (startDigit >= Digits.currentDigit.getFractionalPart().length() - 2) {
            // The startDigit value is too high. Reset it to 1.
            startDigit = 1;
        }
        // Show the six digits in front of the starting digit. If there are no six digits, show the
        // integer part plus all digits in front of the starting digit.
        if (startDigit > 6) {
            integerPartTV.setText("…" + Digits.currentDigit.getFractionalPart()
                    .substring(startDigit - 7, startDigit - 1) + "…");
        } else {
            integerPartTV.setText(Digits.currentDigit.getIntegerPart() + Digits.decimalSeparator +
                    Digits.currentDigit.getFractionalPart().substring(0, startDigit - 1));
        }
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        activityInterface.preventSoftKeyboardFromShowingUp(inputET, show);
        if (keyboard.getVisibility() == View.GONE && show) {
            TransitionManager.beginDelayedTransition(root);
            keyboard.setVisibility(View.VISIBLE);
        } else if (keyboard.getVisibility() == View.VISIBLE && !show) {
            TransitionManager.beginDelayedTransition(root);
            keyboard.setVisibility(View.GONE);
        }
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
                // Log a firebase event.
                activityInterface.logEventSelectContent("restartButton", "restartButton", MainActivity.CONTENT_TYPE_BUTTON);

                onClickRestart(true);
            }
        });
    }

    private void onClickRestart(boolean animate) {
        if (animate) {
            // Animate rotation of the restart button.
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable drawable = restartButton.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat)
                    ((AnimatedVectorDrawableCompat) drawable).start();
                else if (drawable instanceof AnimatedVectorDrawable)
                    ((AnimatedVectorDrawable) drawable).start();
            }
        }

        inputET.setText(""); // This will trigger the TextWatcher which will set the Toolbar color.
        errors = 0;
        fillTextViews();
    }

    private void fillTextViews() {
        String input = inputET.getText().toString();

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == Digits.currentDigit.getFractionalPart().charAt(i + startDigit - 1)) {
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
                // If maximum number of digits is reached:
                if (inputET.length() >= Digits.currentDigit.getFractionalPart().length() - startDigit + 1) {
                    // Show dialog message
                    tooMuchInput();
                    // Delete characters that come after the last digit, if any
                    indirectTextChange = true;
                    inputET.getText().delete(
                            Digits.currentDigit.getFractionalPart().length() - startDigit + 1, inputET.length());
                    inputET.setSelection(inputET.length());
                    indirectTextChange = false;
                }
                // We can now assume that inputET.length() <= fractionalPart.length() - startDigit + 1

                selection = inputET.getSelectionStart();

                // Check if the input is wrong:
                if (Digits.isIncorrect(inputET.getText().toString(), startDigit)) {
                    activityInterface.animateToolbarColor(false);

                    // Check if the last typed character was wrong:
                    if (lastTextLength < inputET.length() // backspace is not pressed
                            && selection != 0 // For catching index o.o.b. exception in next line
                            && inputET.getText().toString().charAt(selection - 1)
                            != Digits.currentDigit.getFractionalPart().charAt(selection - 1 + startDigit - 1)) { // The typed character is wrong

                        errors++;
                        activityInterface.vibrate();
                    }
                } else {
                    // All the input is correct, so make the toolbar blue again.
                    activityInterface.animateToolbarColor(true);
                }

                // Color the text
                indirectTextChange = true;
                inputET.setText(toColoredSpannable(inputET.getText().toString()));
                indirectTextChange = false;
                // Reset the selection to where it was before we called setText().
                inputET.setSelection(Math.min(selection, inputET.length()));

                lastTextLength = inputET.length();

                fillTextViews();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Colors the characters in the Editable by checking them against currentDigit.fractionalPart. Make
     * sure that  input.length() <= fractionalPart.length() - startDigit + 1  before calling this method.
     */
    private SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != Digits.currentDigit.getFractionalPart().charAt(i + startDigit - 1)) {
                // The character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }

    /**
     * Shows a dialog containing the message that the user has reached the maximum amount of digits.
     */
    private void tooMuchInput() {
        if (!inputET.hasWindowFocus()) /*There probably is a dialog already*/ return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.too_much_input_dialog_message)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

    @Override
    public void notifyDigitsChanged() {
        startDigit = getActivity().getPreferences(0).getInt(STARTING_DIGIT + Digits.currentDigit.getName(), 1);
        indirectTextChangeStartDigitET = true;
        startDigitET.setText(startDigit + "");
        indirectTextChangeStartDigitET = false;
        setStartDigit(startDigit);
        onClickRestart(true);
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
