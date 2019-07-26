package com.teinproductions.tein.pitrainer.records;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.widget.AppCompatTextView;

// Inspired by android.widget.Chronometer

public class StopWatch extends AppCompatTextView {
    public StopWatch(Context context) {
        super(context);
        reset(); // to display the time 0
    }

    public StopWatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        reset();
    }

    public StopWatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        reset();
    }

    private boolean running = false;
    private long startTime;
    private final long delay = 100;

    /**
     * Stops the timer and resets the time to 0.
     */
    public void reset() {
        stop();
        startTime = SystemClock.elapsedRealtime();
        updateText();
    }

    /**
     * Stops the timer, resets the time to 0 and starts counting again.
     */
    public void start() {
        stop();
        startTime = SystemClock.elapsedRealtime();
        running = true;
        updateText();
        postDelayed(runnable, delay);
    }

    public long stop() {
        long result = running ? SystemClock.elapsedRealtime() - startTime : 0;
        running = false;
        removeCallbacks(runnable);
        return result;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                updateText();
                postDelayed(runnable, delay);
            }
        }
    };

    private SimpleDateFormat format = new SimpleDateFormat("mm:ss.S", Locale.getDefault());
    private Date time = new Date();

    public void updateText() {
        time.setTime(SystemClock.elapsedRealtime() - startTime);
        setText(format.format(time));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}
