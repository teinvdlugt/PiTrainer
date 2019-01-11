package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.legacy.widget.Space;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.teinproductions.tein.pitrainer.MainActivity;
import com.teinproductions.tein.pitrainer.R;

public class Keyboard extends LinearLayout implements View.OnLongClickListener {

    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0;
    private ImageButton backspace;
    private Space space;
    private LinearLayout lastRow;
    private OnTypeListener onTypeListener;
    private int keyboardWidth, keyboardHeight; // Custom, defined by the user in KeyboardSizeActivity. In pixels, 0 means wrap_content
    private boolean backspaceLongClicking = false;

    public interface OnTypeListener {
        void onTypeDigit(int digit);
        void onTypeBackspace();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_keyboard, this);
        initViews();
        resetBackgrounds();
        refreshKeyboardLayout();
        post(new Runnable() {
            @Override
            public void run() {
                refreshKeyboardSize();
            }
        });

        // Backspace long-click (See also this.onLongClick)
        backspace.setOnLongClickListener(this);
        backspace.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    backspaceLongClicking = false;
                return false;
            }
        });

        // Set button clickListeners
        for (final Button key : new Button[]{button1, button2, button3, button4,
                button5, button6, button7, button8, button9, button0}) {
            key.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onTypeListener != null) onTypeListener.onTypeDigit(
                            Integer.parseInt(key.getText().toString()));
                }
            });
        }

        // Set backspace clickListener
        backspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeBackspace();
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        // Called when backspace is long clicked
        backspaceLongClicking = true;
        if (onTypeListener != null) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (onTypeListener != null) onTypeListener.onTypeBackspace();
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (backspaceLongClicking) {
                        post(runnable);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }).start();
        }

        return false;
    }

    private void initViews() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        lastRow = (LinearLayout) findViewById(R.id.last_keyboard_row);
        space = (Space) findViewById(R.id.keyboard_space);
        button0 = (Button) findViewById(R.id.button0);
        backspace = (ImageButton) findViewById(R.id.buttonBackspace);
    }

    public void resetBackgrounds() {
        boolean keyFeedback = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(MainActivity.KEYBOARD_FEEDBACK, true);
        View[] buttons = new View[]{button1, button2, button3, button4, button5,
                button6, button7, button8, button9, button0, backspace};
        if (keyFeedback) {
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            for (View button : buttons)
                button.setBackgroundResource(outValue.resourceId);
        } else {
            for (View button : buttons) {
                if (Build.VERSION.SDK_INT >= 16)
                    button.setBackground(null);
                else button.setBackgroundDrawable(null);
            }
        }
    }

    public void refreshKeyboardLayout() {
        int[] layout = ChooseKeyboardActivity.LAYOUTS[
                ChooseKeyboardActivity.getCurrentKeyboardIndex(getContext())];
        button1.setText(String.valueOf(layout[0]));
        button2.setText(String.valueOf(layout[1]));
        button3.setText(String.valueOf(layout[2]));
        button4.setText(String.valueOf(layout[3]));
        button5.setText(String.valueOf(layout[4]));
        button6.setText(String.valueOf(layout[5]));
        button7.setText(String.valueOf(layout[6]));
        button8.setText(String.valueOf(layout[7]));
        button9.setText(String.valueOf(layout[8]));
        // Reorder the last LinearLayout according to the order in layout
        lastRow.removeAllViews();
        for (int i = 9; i < 12; i++) {
            switch (layout[i]) {
                case 0:
                    lastRow.addView(button0);
                    break;
                case -1:
                    lastRow.addView(backspace);
                    break;
                case -2:
                    lastRow.addView(space);
            }
        }
    }

    public void refreshKeyboardSize() {
        setKeyboardWidth(KeyboardSizeActivity.getKeyboardWidth(getContext()));
        setKeyboardHeight(KeyboardSizeActivity.getKeyboardHeight(getContext()));
    }

    public void setKeyboardWidth(int keyboardWidth) {
        this.keyboardWidth = keyboardWidth;
        getLayoutParams().width = keyboardWidth;
        requestLayout();
    }

    public void setKeyboardHeight(int keyboardHeight) {
        this.keyboardHeight = keyboardHeight;
        getLayoutParams().height = keyboardHeight;
        requestLayout();
    }

    public int getKeyboardWidth() {
        return keyboardWidth;
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public void setEditText(final EditText editText) {
        onTypeListener = new OnTypeListener() {
            @Override
            public void onTypeDigit(int digit) {
                final int selection = editText.getSelectionStart();
                editText.getText().insert(selection, Integer.toString(digit));
            }

            @Override
            public void onTypeBackspace() {
                final int selection = editText.getSelectionStart();
                if (selection > 0) {
                    editText.getText().replace(selection - 1, selection, "");
                }
            }
        };
    }

    // A few overrides to make sure that there won't be problems with the
    // backspace thread. Similar to onPause() in an Activity.
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) backspaceLongClicking = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        backspaceLongClicking = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) backspaceLongClicking = false;
    }

    public Keyboard(Context context) {
        super(context);
        init();
    }

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
}
