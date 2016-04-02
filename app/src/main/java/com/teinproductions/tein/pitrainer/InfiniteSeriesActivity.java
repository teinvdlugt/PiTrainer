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
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class InfiniteSeriesActivity extends AppCompatActivity {
    public static final String MESSAGE_SHOWN_PREF = "message_shown";
    private static final int LEIBNIZ = 0;
    private static final int EULER = 1;

    private FloatingActionButton fab;
    private TextView resultTV, resultTextView2, nTextView;
    private PiTask piTask;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinite_series);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultTV = (TextView) findViewById(R.id.result_textView);
        resultTextView2 = (TextView) findViewById(R.id.result_textView2);
        nTextView = (TextView) findViewById(R.id.n_textView);
        fab = (FloatingActionButton) findViewById(R.id.start_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFab();
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        setupTabLayout();

        if (!getPreferences(0).getBoolean(MESSAGE_SHOWN_PREF, false))
            showDisclaimer();
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.gregory_leibniz));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.euler));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pause();
                switch (tabLayout.getSelectedTabPosition()) {
                    case LEIBNIZ:
                        arg1 = 1; // n
                        arg2 = 0; // pi / 4
                        arg3 = 0; // pi
                        break;
                    case EULER:
                        arg1 = 1; // n
                        arg2 = 0; // pi^2 / 6
                        arg3 = 0; // pi
                        break;
                }
            }

            public void onTabUnselected(TabLayout.Tab tab) {}

            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showDisclaimer() {
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
            pause();
        } else {
            piTask = new PiTask();
            piTask.execute();
            fab.setImageDrawable(getDrawableCompat(R.mipmap.ic_pause_white_24dp));
        }
    }

    private void pause() {
        if (piTask != null) piTask.cancel(true);
        fab.setImageDrawable(getDrawableCompat(R.mipmap.ic_play_arrow_white_24dp));
    }

    private long arg1 = 1;
    private double arg2 = 0;
    private double arg3 = 0;

    private class PiTask extends AsyncTask<Void, String, Void> {
        int method;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            method = tabLayout.getSelectedTabPosition();
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (method) {
                case LEIBNIZ:
                    while (!isCancelled()) {
                        if ((arg1 + 1) % 4 == 0) {
                            arg2 -= 1. / arg1;
                        } else {
                            arg2 += 1. / arg1;
                        }

                        // Update delay = 20000
                        if ((arg1 + 1) % 20000 == 0) {
                            arg3 = arg2 * 4;
                            publishProgress(
                                    Double.toString(arg3),
                                    Double.toString(arg2),
                                    getString(R.string.n_textView_format, arg1));
                        }

                        arg1 += 2;
                    }
                    break;
                case EULER:
                    while (!isCancelled()) {
                        arg2 += 1. / (arg1 * arg1);
                        if ((arg1 + 1) % 10000 == 0) {
                            arg3 = Math.sqrt(arg2 * 6);
                            publishProgress(
                                    Double.toString(arg3),
                                    Double.toString(arg2),
                                    getString(R.string.infinite_series_euler_progress_format, arg1));
                        }
                        arg1++;
                    }
                    break;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (!isCancelled()) {
                resultTV.setText(values[0]);
                resultTextView2.setText(values[1]);
                nTextView.setText(values[2]);
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
            String url;
            switch (tabLayout.getSelectedTabPosition()) {
                case LEIBNIZ:
                    url = "https://en.wikipedia.org/wiki/Leibniz_formula_for_%CF%80";
                    break;
                case EULER:
                    url = "https://en.wikipedia.org/wiki/Basel_problem";
                    break;
                default:
                    return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
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
