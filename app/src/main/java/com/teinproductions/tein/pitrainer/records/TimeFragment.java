package com.teinproductions.tein.pitrainer.records;


import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFragment extends Fragment implements FragmentInterface {
    private ActivityInterface activityInterface;

    private EditText inputET;
    private TextView timer, digitsTV, integerPartTV;
    private Keyboard keyboard;
    private ImageButton restartButton, doneButton, highScoresButton;
    private TimerTask timerTask;
    private ViewGroup root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityInterface = (ActivityInterface) getActivity();

        View theView = inflater.inflate(R.layout.fragment_time, container, false);

        integerPartTV = (TextView) theView.findViewById(R.id.integerPart_textView);
        inputET = (EditText) theView.findViewById(R.id.input_editText);
        timer = (TextView) theView.findViewById(R.id.timer);
        digitsTV = (TextView) theView.findViewById(R.id.digits_textView);
        TextView integerPartTV = (TextView) theView.findViewById(R.id.integerPart_textView);
        keyboard = (Keyboard) theView.findViewById(R.id.keyboard);
        restartButton = (ImageButton) theView.findViewById(R.id.restart_button);
        doneButton = (ImageButton) theView.findViewById(R.id.done_button);
        highScoresButton = (ImageButton) theView.findViewById(R.id.high_scores_button);
        root = (ViewGroup) theView.findViewById(R.id.root);

        keyboard.setEditText(inputET);
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + ".");
        updateDigitsText();
        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
        setRestartImageResource();
        setButtonOnClickListeners();
        setTextWatcher();
        onClickRestart();

        return theView;
    }

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.mipmap.ic_refresh_white_36dp);
        }
    }

    private void setButtonOnClickListeners() {
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRestart();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputET.length() > 0) {
                    end(inputET.length(), false);
                } else {
                    Snackbar snack = Snackbar.make(root, R.string.please_type_more_digits_snackbar, Snackbar.LENGTH_LONG);
                    View snackView = snack.getView();
                    ((TextView) snackView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                    snack.show();
                }
            }
        });
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerTask != null) timerTask.cancel(true);
                activityInterface.swapFragment(RecordsFragment.class);
            }
        });
    }

    private void setTextWatcher() {
        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Digits.isIncorrect(inputET.getText().toString())) {
                    end(inputET.length() - 1, true);
                } else {
                    if (inputET.getText().length() == 1 && before == 0) {
                        timerTask = new TimerTask();
                        timerTask.execute();
                    }
                    updateDigitsText();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {/*ignored*/}
        });
    }

    private void end(int numOfDigits, boolean vibrate) {
        if (timerTask != null) timerTask.cancel(true);
        if (vibrate) activityInterface.vibrate();
        inputET.setEnabled(false);
        keyboard.setEnabled(false);

        RecordDialog.show(TimeFragment.this, numOfDigits, timerTask.getMilliseconds());
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

        if (timerTask != null) {
            timerTask.cancel(true);
        }
        timerTask = new TimerTask();
        timerTask.onProgressUpdate(0);
    }

    @Override
    public void notifyDigitsChanged() {
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + ".");
        onClickRestart();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        activityInterface.preventSoftKeyboardFromShowingUp(inputET, show);
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


    class TimerTask extends AsyncTask<Void, Integer, Void> {

        private final int sleepTime = 100;

        private int milliseconds = 0;
        private SimpleDateFormat format;

        public TimerTask() {
            format = new SimpleDateFormat("mm:ss.S", Locale.getDefault());
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                publishProgress(milliseconds);

                try {
                    Thread.sleep(sleepTime);
                    milliseconds += sleepTime;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        private Date time = new Date(0);

        @Override
        protected void onProgressUpdate(Integer... values) {
            time.setTime(values[0]);
            timer.setText(format.format(time));
            /*StringBuilder text = new StringBuilder(Integer.toString(centiseconds / 600)).append(":");
            if (text.length() == 2) text.insert(0, "0");
            text.append(centiseconds / 10 % 60);
            if (text.length() == 4) text.insert(3, "0");
            text.append(".").append(centiseconds % 10);
            timer.setText(text);*/
        }

        public int getMilliseconds() {
            return milliseconds;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerTask != null) timerTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        timerTask = new TimerTask();
    }
}
