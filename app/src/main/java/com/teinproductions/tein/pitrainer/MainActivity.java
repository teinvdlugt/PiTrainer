package com.teinproductions.tein.pitrainer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ActivityInterface {

    private static final String VIBRATE = "VIBRATE";
    public static final String ON_SCREEN_KEYBOARD = "ON_SCREEN_KEYBOARD";
    public static final String CURRENT_DIGITS_NAME = "CURRENT_DIGITS_NAME";
    public static final String CURRENT_GAME = "CURRENT_GAME";
    public static final int NUMBERS_ACTIVITY_REQUEST_CODE = 1;

    private boolean onScreenKeyboard;
    private FragmentInterface fragmentInterface;

    public static final Game[] GAMES = {
            new Game(R.string.practise, PractiseFragment.class),
            new Game(R.string.reference, ReferenceFragment.class),
            new Game(R.string.complete_the_statement, CompleteFragment.class)};
    private int currentGame;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView drawerRecyclerView;
    private Toolbar toolbar;

    private boolean vibrate;
    private boolean toolbarCurrentlyRed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_recyclerView);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerRecyclerView.setAdapter(new Game.RecyclerAdapter(this, GAMES, new Game.RecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int i) {
                currentGame = i;
                drawerLayout.closeDrawer(drawerRecyclerView);
                swapFragment(GAMES[i].getFragment());
            }
        }));
        initDrawerToggle();

        Digits.initDigits(this);
        restoreValues();
        setTitle();
        swapFragment(GAMES[currentGame].getFragment());
    }

    private void initDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setTitle() {
        if (Digits.currentDigit != null) {
            setTitle(Digits.currentDigit.getName());
        }
    }

    private void swapFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
        fragmentInterface = (FragmentInterface) fragment;
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

                getPreferences(0).edit().putBoolean(ON_SCREEN_KEYBOARD, item.isChecked()).apply();
                fragmentInterface.showOnScreenKeyboard(item.isChecked());
                return true;

            case R.id.number:
                String[] digitsNames = Digits.digitsNames();
                final String[] singleChoiceItems = new String[digitsNames.length + 1];
                System.arraycopy(digitsNames, 0, singleChoiceItems, 0, digitsNames.length);
                singleChoiceItems[singleChoiceItems.length - 1] = getString(R.string.custom_numbers);

                final int currentDigitsIndex = Digits.currentDigitsIndex();

                new AlertDialog.Builder(this)
                        .setTitle(R.string.number_option_dialog_title)
                        .setSingleChoiceItems(singleChoiceItems, currentDigitsIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == singleChoiceItems.length - 1) {
                                    Intent intent = new Intent(MainActivity.this, NumbersActivity.class);
                                    startActivityForResult(intent, NUMBERS_ACTIVITY_REQUEST_CODE);
                                } else if (which != currentDigitsIndex) {
                                    Digits newDigits = Digits.digits[which];
                                    Digits.currentDigit = newDigits;
                                    getPreferences(0).edit().putString(CURRENT_DIGITS_NAME, newDigits.getName()).apply();
                                    fragmentInterface.resetCurrentDigits();
                                    setTitle();
                                }

                                dialog.dismiss();
                            }
                        }).show();

                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NUMBERS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            getPreferences(0).edit().putString(CURRENT_DIGITS_NAME, Digits.currentDigit.getName()).apply();
            fragmentInterface.resetCurrentDigits();
            setTitle();
        }
    }

    @Override
    public void preventSoftKeyboardFromShowingUp(final EditText et, boolean prevent) {
        if (prevent) {
            if (Build.VERSION.SDK_INT >= 21) {
                et.setShowSoftInputOnFocus(false);
            } else {
                et.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int inputType = et.getInputType(); // backup the input type
                        et.setInputType(InputType.TYPE_NULL); // disable soft input
                        et.onTouchEvent(motionEvent); // call native handler
                        et.setInputType(inputType); // restore input type
                        et.setFocusable(true);
                        return true; // consume touch even
                    }
                });
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                et.setShowSoftInputOnFocus(true);
            } else {
                et.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return et.onTouchEvent(motionEvent);
                    }
                });
            }
        }
    }

    @Override
    public void vibrate(int millis) {
        if (vibrate) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        }
    }

    @Override
    public void animateToolbarColor(boolean correct) {
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

    @Override
    protected void onPause() {
        getPreferences(0).edit()
                .putString(CURRENT_DIGITS_NAME, Digits.currentDigit.getName())
                .putInt(CURRENT_GAME, currentGame)
                .apply();
        super.onPause();
    }

    private void restoreValues() {
        vibrate = getPreferences(0).getBoolean(VIBRATE, true);
        Digits.currentDigit = Digits.findDigits(
                getPreferences(0).getString(CURRENT_DIGITS_NAME, Digits.digits[0].getName()));
        onScreenKeyboard = getPreferences(0).getBoolean(ON_SCREEN_KEYBOARD, false);
        currentGame = getPreferences(0).getInt(CURRENT_GAME, 0);
    }
}