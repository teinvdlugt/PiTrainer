package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.R;

public class KeyboardSizeActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    public static final String KEYBOARD_WIDTH = "keyboard_width"; // In pixels; 0 if wrap_content
    public static final String KEYBOARD_HEIGHT = "keyboard_height"; // In pixels; 0 if wrap_content
    public static final int KEYBOARD_MAX_WIDTH = 512; // In DIPs
    public static final int KEYBOARD_MAX_HEIGHT = 512; // In DIPs

    private TextView widthTV, heightTV;
    private SeekBar widthSeekBar, heightSeekBar;
    private Button widthSmallBtn, widthLargeBtn, heightSmallBtn, heightLargeBtn;
    private Keyboard keyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_size);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        widthTV = (TextView) findViewById(R.id.keyboard_width_textView);
        heightTV = (TextView) findViewById(R.id.keyboard_height_textView);
        widthSeekBar = (SeekBar) findViewById(R.id.keyboardWidthSeekBar);
        heightSeekBar = (SeekBar) findViewById(R.id.keyboardHeightSeekBar);
        keyboard = (Keyboard) findViewById(R.id.keyboard);
        widthSmallBtn = (Button) findViewById(R.id.keyboard_width_button_smallest);
        widthLargeBtn = (Button) findViewById(R.id.keyboard_width_button_largest);
        heightSmallBtn = (Button) findViewById(R.id.keyboard_height_button_smallest);
        heightLargeBtn = (Button) findViewById(R.id.keyboard_height_button_largest);

        // Define the max size of the keyboard
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, KEYBOARD_MAX_WIDTH, metrics);
        int maxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, KEYBOARD_MAX_HEIGHT, metrics);
        widthSeekBar.setMax(maxWidth);
        heightSeekBar.setMax(maxHeight);

        // Setup the SeekBars
        widthSeekBar.setOnSeekBarChangeListener(this);
        heightSeekBar.setOnSeekBarChangeListener(this);

        int width = getKeyboardWidth(this);
        if (width < 0) { // The width is WRAP_CONTENT (-2) or MATCH_PARENT (-1)
            widthSeekBar.setEnabled(false);
            widthSeekBar.setProgress(widthSeekBar.getMax());
        } else {
            widthSeekBar.setEnabled(true);
            widthSeekBar.setProgress(Math.min(width, widthSeekBar.getMax()));
        }

        int height = getKeyboardHeight(this);
        if (height < 0) {// The width is WRAP_CONTENT (-2) or MATCH_PARENT (-1)
            heightSeekBar.setEnabled(false);
            heightSeekBar.setProgress(widthSeekBar.getMax());
        } else {
            heightSeekBar.setEnabled(true);
            heightSeekBar.setProgress(Math.min(height, heightSeekBar.getMax()));
        }

        // Setup buttons
        setupButtons(width, height);
    }

    /**
     * @param width  Current width of the keyboard. For example, if width == -2, the WRAP_CONTENT button is highlighted
     * @param height Current height of the keyboard
     */
    private void setupButtons(int width, int height) {
        setupWidthButtons(width);
        setupHeightButtons(height);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String px = progress + " px";

        if (seekBar.equals(widthSeekBar))
            widthTV.setText(getString(R.string.keyboard_width_format, px));
        else if (seekBar.equals(heightSeekBar))
            heightTV.setText(getString(R.string.keyboard_height_format, px));

        if (fromUser) {
            if (seekBar.equals(widthSeekBar))
                keyboard.setKeyboardWidth(progress);
            else if (seekBar.equals(heightSeekBar))
                keyboard.setKeyboardHeight(progress);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keyboard_size_save:
                save();
                finish();
                return true;
            case R.id.keyboard_size_discard:
                finish();
                return true;
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.keyboard_size_save_or_discard)
                        .setPositiveButton(R.string.keyboard_size_save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.keyboard_size_discard, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(KEYBOARD_WIDTH, keyboard.getKeyboardWidth())
                .putInt(KEYBOARD_HEIGHT, keyboard.getKeyboardHeight())
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_keyboard_size, menu);
        return true;
    }

    public static int getKeyboardWidth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEYBOARD_WIDTH, -2);
    }

    public static int getKeyboardHeight(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEYBOARD_HEIGHT, -2);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void setupWidthButtons(int width) {
        if (width == -2) { // WRAP_CONTENT
            widthTV.setText(getString(R.string.keyboard_width_format, getString(R.string.keyboard_size_smallest)));
            widthSmallBtn.setTextColor(getColorCompat(R.color.colorAccent));
            widthLargeBtn.setTextColor(getColorCompat(R.color.primary_text));
            widthSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(true);
                    keyboard.setKeyboardWidth(widthSeekBar.getProgress());
                    setupWidthButtons(widthSeekBar.getProgress());
                }
            });
            widthLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(false);
                    keyboard.setKeyboardWidth(-1); // MATCH_PARENT
                    setupWidthButtons(-1);
                }
            });
        } else if (width == -1) { // MATCH_PARENT
            widthTV.setText(getString(R.string.keyboard_width_format, getString(R.string.keyboard_size_largest)));
            widthSmallBtn.setTextColor(getColorCompat(R.color.primary_text));
            widthLargeBtn.setTextColor(getColorCompat(R.color.colorAccent));
            widthSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(false);
                    keyboard.setKeyboardWidth(-2); // WRAP_CONTENT
                    setupWidthButtons(-2);
                }
            });
            widthLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(true);
                    keyboard.setKeyboardWidth(widthSeekBar.getProgress());
                    setupWidthButtons(widthSeekBar.getProgress());
                }
            });
        } else {
            widthTV.setText(getString(R.string.keyboard_width_format, widthSeekBar.getProgress() + " px"));
            widthSmallBtn.setTextColor(getColorCompat(R.color.primary_text));
            widthLargeBtn.setTextColor(getColorCompat(R.color.primary_text));
            widthSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(false);
                    keyboard.setKeyboardWidth(-2); // WRAP_CONTENT
                    setupWidthButtons(-2);
                    widthTV.setText(getString(R.string.keyboard_width_format, getString(R.string.keyboard_size_smallest)));
                }
            });
            widthLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widthSeekBar.setEnabled(false);
                    keyboard.setKeyboardWidth(-1); // MATCH_PARENT
                    setupWidthButtons(-1);
                    widthTV.setText(getString(R.string.keyboard_width_format, getString(R.string.keyboard_size_largest)));
                }
            });
        }
    }

    private void setupHeightButtons(final int height) {
        if (height == -2) { // WRAP_CONTENT
            heightTV.setText(getString(R.string.keyboard_height_format, getString(R.string.keyboard_size_smallest)));
            heightSmallBtn.setTextColor(getColorCompat(R.color.colorAccent));
            heightLargeBtn.setTextColor(getColorCompat(R.color.primary_text));
            heightSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(true);
                    keyboard.setKeyboardHeight(heightSeekBar.getProgress());
                    setupHeightButtons(heightSeekBar.getProgress());
                }
            });
            heightLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(false);
                    keyboard.setKeyboardHeight(-1); // MATCH_PARENT
                    setupHeightButtons(-1);
                }
            });
        } else if (height == -1) { // MATCH_PARENT
            heightTV.setText(getString(R.string.keyboard_height_format, getString(R.string.keyboard_size_largest)));
            heightSmallBtn.setTextColor(getColorCompat(R.color.primary_text));
            heightLargeBtn.setTextColor(getColorCompat(R.color.colorAccent));
            heightSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(false);
                    keyboard.setKeyboardHeight(-2); // WRAP_CONTENT
                    setupHeightButtons(-2);
                }
            });
            heightLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(true);
                    keyboard.setKeyboardHeight(heightSeekBar.getProgress());
                    setupHeightButtons(heightSeekBar.getProgress());
                }
            });
        } else {
            heightTV.setText(getString(R.string.keyboard_height_format, heightSeekBar.getProgress() + " px"));
            heightSmallBtn.setTextColor(getColorCompat(R.color.primary_text));
            heightLargeBtn.setTextColor(getColorCompat(R.color.primary_text));
            heightSmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(false);
                    keyboard.setKeyboardHeight(-2); // WRAP_CONTENT
                    setupHeightButtons(-2);
                    heightTV.setText(getString(R.string.keyboard_height_format, getString(R.string.keyboard_size_smallest)));
                }
            });
            heightLargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heightSeekBar.setEnabled(false);
                    keyboard.setKeyboardHeight(-1); // MATCH_PARENT
                    setupHeightButtons(-1);
                    heightTV.setText(getString(R.string.keyboard_height_format, getString(R.string.keyboard_size_largest)));
                }
            });
        }
    }

    public int getColorCompat(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT > 23) {
            return getColor(resId);
        } else {
            return getResources().getColor(resId);
        }
    }
}
