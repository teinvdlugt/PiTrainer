package com.teinproductions.tein.pitrainer.records;


import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.ActivityInterface;
import com.teinproductions.tein.pitrainer.Digits;
import com.teinproductions.tein.pitrainer.FragmentInterface;
import com.teinproductions.tein.pitrainer.MainActivity;
import com.teinproductions.tein.pitrainer.R;
import com.teinproductions.tein.pitrainer.keyboard.Keyboard;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class TimeFragment extends Fragment implements FragmentInterface {
    private ActivityInterface activityInterface;

    private EditText inputET;
    private TextView digitsTV, integerPartTV;
    private Keyboard keyboard;
    private ImageButton restartButton, highScoresButton;
    private StopWatch stopWatch;
    private ViewGroup root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        View theView = inflater.inflate(R.layout.fragment_time, container, false);

        integerPartTV = theView.findViewById(R.id.integerPart_textView);
        inputET = theView.findViewById(R.id.input_editText);
        stopWatch = theView.findViewById(R.id.timer);
        digitsTV = theView.findViewById(R.id.digits_textView);
        keyboard = theView.findViewById(R.id.keyboard);
        restartButton = theView.findViewById(R.id.restart_button);
        highScoresButton = theView.findViewById(R.id.highScores_button);
        root = theView.findViewById(R.id.root);

        keyboard.setEditText(inputET);
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + Digits.decimalSeparator);
        updateDigitsText();
        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, MainActivity.ON_SCREEN_KEYBOARD_DEFAULT));
        setRestartImageResource();
        setButtonOnClickListeners();
        setTextWatcher();
        onClickRestart();

        inputET.setSingleLine(false);
        inputET.requestFocus();
        return theView;
    }

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.drawable.ic_refresh_36dp);
        }
    }

    private void setButtonOnClickListeners() {
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log a firebase event.
                activityInterface.logEventSelectContent("restartButton", "restartButton", MainActivity.CONTENT_TYPE_BUTTON);

                onClickRestart();
            }
        });
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputET.length() > 0) {
                    // Log a firebase event.
                    activityInterface.logEventSelectContent("doneButton", "doneButton", MainActivity.CONTENT_TYPE_BUTTON);

                    end(inputET.length(), false);
                } else {
                    stopWatch.stop();
                    // Release screen orientation lock
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    activityInterface.swapFragment(RecordsFragment.class);
                }
            }
        });
    }

    private void setTextWatcher() {
        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if maximum number of digits is reached:
                if (inputET.length() >= Digits.currentDigit.getFractionalPart().length()) {
                    // End the game
                    end(inputET.length() - 1, true);
                    return;
                }
                // We can now assume that inputET.length() < fractionalPart.length(), so we can safely
                // call Digits.isIncorrect.
                if (Digits.isIncorrect(inputET.getText().toString(), 1)) {
                    end(inputET.length() - 1, true);
                } else {
                    if (inputET.getText().length() == 1 && before == 0) {
                        stopWatch.start();
                        highScoresButton.setImageResource(R.drawable.ic_stop_36dp);
                        // Lock screen orientation
                        int orientation = getContext().getResources().getConfiguration().orientation;
                        getActivity().setRequestedOrientation(orientation == Configuration.ORIENTATION_LANDSCAPE ?
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }
                    updateDigitsText();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {/*ignored*/}
        });
    }

    private void end(int numOfDigits, boolean vibrate) {
        long result = stopWatch.stop();
        if (vibrate) activityInterface.vibrate();
        inputET.setEnabled(false);
        keyboard.setEnabled(false);
        // Release screen orientation lock
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        RecordDialog.show(TimeFragment.this, numOfDigits, (int) result);
        activityInterface.swapFragment(RecordsFragment.class);
    }

    private void updateDigitsText() {
        digitsTV.setText(String.format(getString(R.string.digits_colon_format), inputET.getText().length()));
    }

    private void onClickRestart() {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = restartButton.getDrawable();
            if (drawable instanceof AnimatedVectorDrawable)
                ((AnimatedVectorDrawable) drawable).start();
            else if (drawable instanceof AnimatedVectorDrawableCompat)
                ((AnimatedVectorDrawableCompat) drawable).start();
        }

        inputET.setText("");
        updateDigitsText();

        stopWatch.reset();
        highScoresButton.setImageResource(R.drawable.ic_crown_36dp);
        // Release screen orientation lock
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void notifyDigitsChanged() {
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + Digits.decimalSeparator);
        onClickRestart();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        activityInterface.preventSoftKeyboardFromShowingUp(inputET, show);
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

    @Override
    public void onPause() {
        super.onPause();
        stopWatch.stop();
        // Release screen orientation lock
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
