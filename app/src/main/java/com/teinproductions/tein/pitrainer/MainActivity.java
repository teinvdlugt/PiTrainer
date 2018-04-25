package com.teinproductions.tein.pitrainer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.teinproductions.tein.pitrainer.keyboard.ChooseKeyboardActivity;
import com.teinproductions.tein.pitrainer.keyboard.KeyboardSizeActivity;
import com.teinproductions.tein.pitrainer.records.RecordDialog;
import com.teinproductions.tein.pitrainer.records.TimeFragment;


public class MainActivity extends AppCompatActivity
        implements ActivityInterface, NavigationView.OnNavigationItemSelectedListener,
        RecordDialog.OnAppliedListener {

    public static final String THEME_MODE = "theme_mode"; // Int preference
    private static final String VIBRATE = "VIBRATE"; // Boolean preference
    public static final String ON_SCREEN_KEYBOARD = "ON_SCREEN_KEYBOARD"; // Boolean preference
    public static final String KEYBOARD_FEEDBACK = "keyboard_feedback"; // Boolean preference
    private static final String CURRENT_DIGITS_NAME = "CURRENT_DIGITS_NAME";
    private static final String CURRENT_GAME = "CURRENT_GAME";
    private static final int NUMBERS_ACTIVITY_REQUEST_CODE = 1;
    private static final int CHOOSE_KEYBOARD_ACTIVITY_REQUEST_CODE = 2;
    private static final int KEYBOARD_SIZE_ACTIVITY_REQUEST_CODE = 3;

    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean onScreenKeyboard;
    private FragmentInterface fragmentInterface;

    public static final Game[] GAMES = {
            new Game(R.string.practise, PractiseFragment.class),
            new Game(R.string.reference, ReferenceFragment.class),
            new Game(R.string.complete_the_statement, CompleteFragment.class),
            new Game(R.string.timed_mode_game, TimeFragment.class)};
    private int currentGame;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private boolean vibrate;
    private boolean toolbarCurrentlyRed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        applyNightMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        initDrawerToggle();

        Digits.initDigits(this);
        restoreValues();
        setTitle();
        swapFragment(GAMES[currentGame].getFragment());
    }

    private void applyNightMode() {
        if (Build.VERSION.SDK_INT >= 14) {
            int mode = PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt(THEME_MODE, 0);
            //noinspection WrongConstant
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    private void initDrawerToggle() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
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

    @Override
    public void swapFragment(Class fragmentClass) {
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

            case R.id.keyboard_feedback:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putBoolean(KEYBOARD_FEEDBACK, item.isChecked()).apply();
                fragmentInterface.refreshKeyboard();
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
                                    fragmentInterface.notifyDigitsChanged();
                                    setTitle();
                                }

                                dialog.dismiss();
                            }
                        }).show();
                return true;

            case R.id.choose_keyboard_layout:
                startActivityForResult(new Intent(this, ChooseKeyboardActivity.class),
                        CHOOSE_KEYBOARD_ACTIVITY_REQUEST_CODE);
                return true;

            case R.id.keyboard_size:
                startActivityForResult(new Intent(this, KeyboardSizeActivity.class),
                        KEYBOARD_SIZE_ACTIVITY_REQUEST_CODE);
                return true;

            case R.id.menu_action_theme:
                if (Build.VERSION.SDK_INT < 14) {
                    Toast.makeText(this, R.string.night_mode_not_available_message, Toast.LENGTH_SHORT).show();
                    return true;
                }

                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                final int currentNightMode = pref.getInt(THEME_MODE, 0);

                new AlertDialog.Builder(this)
                        .setTitle(R.string.theme)
                        .setSingleChoiceItems(R.array.themes, currentNightMode, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which != currentNightMode) {
                                    pref.edit().putInt(THEME_MODE, which).apply();
                                    // UiModeManager.MODE_NIGHT_AUTO = 0, NO = 1, YES = 2
                                    AppCompatDelegate.setDefaultNightMode(which);
                                    recreate();
                                }
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NUMBERS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            getPreferences(0).edit().putString(CURRENT_DIGITS_NAME, Digits.currentDigit.getName()).apply();
            fragmentInterface.notifyDigitsChanged();
            setTitle();
        } else if (requestCode == CHOOSE_KEYBOARD_ACTIVITY_REQUEST_CODE ||
                requestCode == KEYBOARD_SIZE_ACTIVITY_REQUEST_CODE) {
            fragmentInterface.refreshKeyboard();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentInterface.getPreviousFragment() != null) {
            swapFragment(fragmentInterface.getPreviousFragment());
        } else super.onBackPressed();
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
                        return true; // consume touch event
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
    public void vibrate() {
        if (vibrate) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        }
    }

    /**
     * Change the color of the ToolBar.
     *
     * @param correct Whether the user did the assignment right. If true, changes
     *                the color to the primary app color. If false, changes the color to red.
     */
    @Override
    public void animateToolbarColor(boolean correct) {
        if (!correct && !toolbarCurrentlyRed) {
            // Paint the toolBar red.
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

            // Update this value:
            toolbarCurrentlyRed = true;
        } else if (correct && toolbarCurrentlyRed) {
            // Paint the toolbar not-red.
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

            // Update this value:
            toolbarCurrentlyRed = false;
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
        // For the NavigationView
        @IdRes int checkedItem;
        switch (currentGame) {
            case 0:
                checkedItem = R.id.practise_navigation_item;
                break;
            case 1:
                checkedItem = R.id.reference_navigation_item;
                break;
            case 2:
                checkedItem = R.id.complete_the_statement_navigation_item;
                break;
            case 3:
                checkedItem = R.id.timed_mode_navigation_item;
                break;
            default:
                checkedItem = R.id.practise_navigation_item;
        }
        navigationView.setCheckedItem(checkedItem);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.practise_navigation_item:
                currentGame = 0;
                break;
            case R.id.reference_navigation_item:
                currentGame = 1;
                break;
            case R.id.complete_the_statement_navigation_item:
                currentGame = 2;
                break;
            case R.id.timed_mode_navigation_item:
                currentGame = 3;
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        swapFragment(GAMES[currentGame].getFragment());
        return true;
    }

    @Override
    public void reloadRecords() {
        fragmentInterface.notifyDigitsChanged();
    }
}