package com.teinproductions.tein.pitrainer;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;

public class Keyboard extends TableLayout {

    private OnTypeListener onTypeListener;

    public interface OnTypeListener {
        public void onTypeDigit(int digit);

        public void onTypeBackspace();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_keyboard, this);

        setDigitListeners();

        ImageButton backspace = (ImageButton) findViewById(R.id.buttonBackspace);
        backspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeBackspace();
            }
        });
    }

    public void setOnTypeListener(OnTypeListener listener) {
        onTypeListener = listener;
    }

    private void setDigitListeners() {
        Button button0 = (Button) findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);
        Button button9 = (Button) findViewById(R.id.button9);

        button0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(0);
            }
        });
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(1);
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(2);
            }
        });
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(3);
            }
        });
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(4);
            }
        });
        button5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(5);
            }
        });
        button6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(6);
            }
        });
        button7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(7);
            }
        });
        button8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(8);
            }
        });
        button9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTypeListener != null) onTypeListener.onTypeDigit(9);
            }
        });
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
