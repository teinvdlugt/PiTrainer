package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.teinproductions.tein.pitrainer.R;

public class Keyboard extends TableLayout {

    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0;
    private ImageButton backspace;
    private Space space;
    private TableRow lastRow;
    private OnTypeListener onTypeListener;

    public interface OnTypeListener {
        void onTypeDigit(int digit);
        void onTypeBackspace();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_keyboard, this);
        initViews();
        refreshKeyboardLayout();

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
        lastRow = (TableRow) findViewById(R.id.last_keyboard_row);
        space = (Space) findViewById(R.id.keyboard_space);
        button0 = (Button) findViewById(R.id.button0);
        backspace = (ImageButton) findViewById(R.id.buttonBackspace);
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
        // Reorder the last TableRow according to the order in layout
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

    public Keyboard(Context context) {
        super(context);
        init();
    }

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
}
