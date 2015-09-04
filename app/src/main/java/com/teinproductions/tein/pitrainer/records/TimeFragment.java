package com.teinproductions.tein.pitrainer.records;


import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.teinproductions.tein.pitrainer.Keyboard;
import com.teinproductions.tein.pitrainer.MainActivity;
import com.teinproductions.tein.pitrainer.R;

public class TimeFragment extends Fragment implements FragmentInterface {
    private ActivityInterface activityInterface;

    private EditText inputET;
    private TextView timer, digitsTV, integerPartTV;
    private Keyboard keyboard;
    private ImageButton restartButton;
    private TimerTask timerTask;

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

        keyboard.setEditText(inputET);
        integerPartTV.setText(Digits.currentDigit.getIntegerPart() + ".");
        updateDigitsText();
        showOnScreenKeyboard(getActivity().getPreferences(0).getBoolean(MainActivity.ON_SCREEN_KEYBOARD, false));
        setRestartImageResource();
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

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRestart();
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
                    timerTask.cancel(true);
                    activityInterface.vibrate(200);
                    inputET.setEnabled(false);
                    keyboard.setEnabled(false);

                    RecordsHandler.addRecord(getActivity(), before, timerTask.getCentiseconds());
                    showDialog(inputET.getText().length() - 1, timerTask.getCentiseconds());
                    activityInterface.swapFragment(RecordsFragment.class);
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

    private void showDialog(int digits, int centiseconds) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Great!")
                .setMessage("You typed " + digits + " digits in " + (centiseconds / 10d) + " seconds.")
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    private void updateDigitsText() {
        digitsTV.setText(String.format(getString(R.string.digits_colon_format), inputET.getText().length()));
    }

    private void onClickRestart() {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AnimatedVectorDrawable) restartButton.getDrawable()).start();
        }
        inputET.setText("");
        updateDigitsText();

        if (timerTask != null) {
            timerTask.cancel(true);
        }
        timer.setText("00:00.0");
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


    class TimerTask extends AsyncTask<Void, Void, Void> {

        private int centiseconds = 0;

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                publishProgress();

                try {
                    Thread.sleep(100);
                    centiseconds++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            StringBuilder text = new StringBuilder(Integer.toString(centiseconds / 600)).append(":");
            if (text.length() == 2) text.insert(0, "0");
            text.append(centiseconds / 10 % 60);
            if (text.length() == 4) text.insert(3, "0");
            text.append(".").append(centiseconds % 10);
            timer.setText(text);
        }

        public int getCentiseconds() {
            return centiseconds;
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
