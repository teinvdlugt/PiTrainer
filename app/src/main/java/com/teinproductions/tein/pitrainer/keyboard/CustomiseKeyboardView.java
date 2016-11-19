package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.R;

public class CustomiseKeyboardView extends FrameLayout implements View.OnTouchListener {
    public static final String key0 = "0";
    public static final String key1 = "1";
    public static final String key2 = "2";
    public static final String key3 = "3";
    public static final String key4 = "4";
    public static final String key5 = "5";
    public static final String key6 = "6";
    public static final String key7 = "7";
    public static final String key8 = "8";
    public static final String key9 = "9";
    public static final String keyBack = "b";
    public static final String keyEmpty = " ";

    private String[] currentLayout = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", " ", "b"};
    private TableLayout tableLayout;
    private TableRow[] tableRows;
    private Space placeholder;

    public void setKeyboardLayout(String[] layout) {
        this.currentLayout = layout;
        for (int i = 0; i < layout.length; i++) {
            ((TextView) tableRows[i / 3].getChildAt(i % 3)).setText(layout[i]);
        }
    }

    private void init() {
        tableLayout = new TableLayout(getContext());
        tableRows = new TableRow[4];
        tableRows[0] = new TableRow(getContext());
        tableRows[1] = new TableRow(getContext());
        tableRows[2] = new TableRow(getContext());
        tableRows[3] = new TableRow(getContext());
        for (TableRow tableRow : tableRows) {
            for (int i = 0; i < 3; i++) {
                View key = inflate(getContext(), R.layout.layout_key, null);
                tableRow.addView(key);
                key.setOnTouchListener(this);
            }
            tableLayout.addView(tableRow);
        }
        addView(tableLayout);

        placeholder = new Space(getContext()); // TODO: 19-11-2016 Set layout_weight = 1?

        setKeyboardLayout(currentLayout);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            view.setSelected(true);
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            view.setSelected(false);
            return true;
        }
        return false;
    }

    public CustomiseKeyboardView(Context context) {
        super(context);
        init();
    }

    public CustomiseKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomiseKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
}
