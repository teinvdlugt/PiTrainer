package com.teinproductions.tein.pitrainer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static final String VIBRATE = "VIBRATE";
    public static final String ON_SCREEN_KEYBOARD = "ON_SCREEN_KEYBOARD";
    public static final String ERRORS = "ERRORS";
    public static final String INPUT = "INPUT";

    public static final String PI_DIGITS = "1415926535897932384626433832795028841971693993" +
            "7510582097494459230781640628620899862803482534211706798214808651328230" +
            "6647093844609550582231725359408128481117450284102701938521105559644622" +
            "9489549303819644288109756659334461284756482337867831652712019091456485" +
            "6692346034861045432664821339360726024914127372458700660631558817488152" +
            "0920962829254091715364367892590360011330530548820466521384146951941511" +
            "6094330572703657595919530921861173819326117931051185480744623799627495" +
            "6735188575272489122793818301194912983367336244065664308602139494639522" +
            "4737190702179860943702770539217176293176752384674818467669405132000568" +
            "1271452635608277857713427577896091736371787214684409012249534301465495" +
            "8537105079227968925892354201995611212902196086403441815981362977477130" +
            "9960518707211349999998372978049951059731732816096318595024459455346908" +
            "3026425223082533446850352619311881710100031378387528865875332083814206";

    private EditText inputET;
    private Toolbar toolbar;
    private TextView digitsTV, errorsTV, percentageTV;
    private Keyboard keyboard;
    private ImageButton restartButton;

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;
    private boolean toolbarCurrentlyRed = false;
    private int errors = 0;

    private boolean vibrate;
    private boolean onScreenKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        inputET = (EditText) findViewById(R.id.input_editText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        digitsTV = (TextView) findViewById(R.id.digits_textView);
        keyboard = (Keyboard) findViewById(R.id.keyboard);
        restartButton = (ImageButton) findViewById(R.id.refresh_button);
        errorsTV = (TextView) findViewById(R.id.errors_textView);
        percentageTV = (TextView) findViewById(R.id.percentage_textView);

        setTypeListener();
        setRestartImageResource();
        fillTextViews();

        vibrate = getPreferences(0).getBoolean(VIBRATE, true);
        showOnScreenKeyboard(getPreferences(0).getBoolean(ON_SCREEN_KEYBOARD, false));

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (indirectTextChange) return;

                selection = inputET.getSelectionStart();

                if (!isCorrect(inputET.getText().toString()) && inputET.length() != 0) {
                    animateToolbarColor(false);

                    // If the last typed character is wrong:
                    if (lastTextLength < inputET.length() // backspace is not pressed
                            && inputET.getText().toString().charAt(selection - 1)
                            != PI_DIGITS.charAt(selection - 1)) { // The typed character is wrong

                        errors++;

                        if (vibrate) {
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                        }
                    }

                } else {
                    animateToolbarColor(true);
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

        restoreValues();
    }

    private void setTypeListener() {
        keyboard.setOnTypeListener(new Keyboard.OnTypeListener() {
            @Override
            public void onTypeDigit(int digit) {
                final int selection = inputET.getSelectionStart();
                inputET.getText().insert(selection, Integer.toString(digit));
            }

            @Override
            public void onTypeBackspace() {
                final int selection = inputET.getSelectionStart();
                if (selection > 0) {
                    inputET.getText().replace(selection - 1, selection, "");
                }
            }
        });
    }

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.mipmap.ic_refresh_white_36dp);
        }
    }

    private void animateToolbarColor(boolean correct) {
        if (!correct && !toolbarCurrentlyRed) {

            toolbarCurrentlyRed = true;

            if (Build.VERSION.SDK_INT >= 11) {
                ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(),
                        getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.red));
                animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        toolbar.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();

            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
            }

        } else if (correct && toolbarCurrentlyRed) {

            toolbarCurrentlyRed = false;

            if (Build.VERSION.SDK_INT >= 11) {
                ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(),
                        getResources().getColor(R.color.red),
                        getResources().getColor(R.color.colorPrimary));
                animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        toolbar.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();

            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

        }
    }

    public static boolean isCorrect(String stringToCheck) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            if (stringToCheck.charAt(i) != PI_DIGITS.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private void fillTextViews() {
        String input = inputET.getText().toString();

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == PI_DIGITS.charAt(i)) {
                count++;
            }
        }

        digitsTV.setText(" " + count);
        errorsTV.setText(" " + errors);

        if (errors == 0 || inputET.length() == 0) {
            percentageTV.setText(" 0%");
        } else {
            percentageTV.setText(" " + (int) Math.floor(100 - ((double) errors / inputET.length() * 100)) + "%");
        }
    }

    public SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != PI_DIGITS.charAt(i)) {
                // If the character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.vibrate).setChecked(vibrate);
        menu.findItem(R.id.keyboard).setChecked(onScreenKeyboard);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vibrate:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                vibrate = item.isChecked();

                getPreferences(0).edit().putBoolean(VIBRATE, vibrate).apply();

                return true;
            case R.id.keyboard:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                showOnScreenKeyboard(item.isChecked());
            default:
                return false;
        }
    }

    private void showOnScreenKeyboard(boolean show) {
        onScreenKeyboard = show;
        getPreferences(0).edit().putBoolean(ON_SCREEN_KEYBOARD, show).apply();
        preventSoftKeyboardFromShowingUp(show);

        keyboard.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void preventSoftKeyboardFromShowingUp(boolean prevent) {
        if (prevent) {
            if (Build.VERSION.SDK_INT >= 21) {
                inputET.setShowSoftInputOnFocus(false);
            } else {
                inputET.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int inputType = inputET.getInputType(); // backup the input type
                        inputET.setInputType(InputType.TYPE_NULL); // disable soft input
                        inputET.onTouchEvent(motionEvent); // call native handler
                        inputET.setInputType(inputType); // restore input type
                        inputET.setFocusable(true);
                        return true; // consume touch even
                    }
                });
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                inputET.setShowSoftInputOnFocus(true);
            } else {
                inputET.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return inputET.onTouchEvent(motionEvent);
                    }
                });
            }
        }
    }

    public void onClickRestart(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AnimatedVectorDrawable) restartButton.getDrawable()).start();
        }
        inputET.setText("");
        errors = 0;
        fillTextViews();
    }

    @Override
    protected void onDestroy() {
        getPreferences(0).edit().putInt(ERRORS, errors).putString(INPUT, inputET.getText().toString()).apply();
        super.onDestroy();
    }

    private void restoreValues() {
        errors = getPreferences(0).getInt(ERRORS, 0);
        inputET.setText(getPreferences(0).getString(INPUT, ""));
        fillTextViews();
        inputET.setSelection(inputET.length());
    }
}