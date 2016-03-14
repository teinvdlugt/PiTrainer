package com.teinproductions.tein.pitrainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class InfiniteSeriesActivity extends AppCompatActivity {
    public static final String MESSAGE_SHOWN_PREF = "message_shown";

    private FloatingActionButton fab;
    private TextView resultTV, nTextView;
    private PiTask piTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinite_series);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultTV = (TextView) findViewById(R.id.result_textView);
        nTextView = (TextView) findViewById(R.id.n_textView);
        fab = (FloatingActionButton) findViewById(R.id.start_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFab();
            }
        });

        if (!getPreferences(0).getBoolean(MESSAGE_SHOWN_PREF, false))
            showMessage();
    }

    private void showMessage() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.infinite_series_disclaimer_title)
                .setMessage(R.string.infinite_series_disclaimer_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPreferences(0).edit().putBoolean(MESSAGE_SHOWN_PREF, true).apply();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void onClickFab() {
        if (piTask != null && piTask.getStatus() == AsyncTask.Status.RUNNING) {
            piTask.cancel(true);
            fab.setImageDrawable(getDrawableCompat(R.mipmap.ic_play_arrow_white_24dp));
        } else {
            piTask = new PiTask();
            piTask.execute();
            fab.setImageDrawable(getDrawableCompat(R.mipmap.ic_pause_white_24dp));
        }
    }

    private class PiTask extends AsyncTask<Void, Double, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int n = 1;
            double pi = 0;

            while (!isCancelled()) {
                if ((n + 1) % 4 == 0) {
                    pi -= 4. / n;
                } else {
                    pi += 4. / n;
                }

                // Update delay = 20000
                if ((n + 1) % 20000 == 0) {
                    publishProgress(pi, (double) n);
                }

                n += 2;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            if (!isCancelled()) {
                resultTV.setText(Double.toString(values[0]));
                nTextView.setText(getString(R.string.n_textView_format, values[1].intValue()));
            }
        }
    }

    @Override
    protected void onPause() {
        if (piTask != null) piTask.cancel(true);
        fab.setImageDrawable(getDrawableCompat(R.mipmap.ic_play_arrow_white_24dp));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_infinite_series, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_learnMore) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://en.wikipedia.org/wiki/Leibniz_formula_for_%CF%80"));
            startActivity(intent);
            return true;
        }
        return false;
    }

    private Drawable getDrawableCompat(@DrawableRes int drawableId) {
        if (Build.VERSION.SDK_INT >= 21) {
            return getDrawable(drawableId);
        } else {
            return getResources().getDrawable(drawableId);
        }
    }
}
