package com.teinproductions.tein.pitrainer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final String VIBRATE = "VIBRATE";
    private static final String ON_SCREEN_KEYBOARD = "ON_SCREEN_KEYBOARD";
    private static final String ERRORS = "ERRORS";
    private static final String INPUT = "INPUT";
    private static final String CURRENT_DIGITS_ORDINAL = "CURRENT_DIGITS_ORDINAL";

    public static enum Digits {
        PI("3.", "14159265358979323846264338327950288419716939937510582097494459" +
                "230781640628620899862803482534211706798214808651328230664709384" +
                "460955058223172535940812848111745028410270193852110555964462294" +
                "895493038196442881097566593344612847564823378678316527120190914" +
                "564856692346034861045432664821339360726024914127372458700660631" +
                "558817488152092096282925409171536436789259036001133053054882046" +
                "652138414695194151160943305727036575959195309218611738193261179" +
                "310511854807446237996274956735188575272489122793818301194912983" +
                "367336244065664308602139494639522473719070217986094370277053921" +
                "717629317675238467481846766940513200056812714526356082778577134" +
                "275778960917363717872146844090122495343014654958537105079227968" +
                "925892354201995611212902196086403441815981362977477130996051870" +
                "721134999999837297804995105973173281609631859502445945534690830" +
                "264252230825334468503526193118817101000313783875288658753320838" +
                "142061717766914730359825349042875546873115956286388235378759375" +
                "195778185778053217122680661300192787661119590921642019893809525" +
                "720106548586327886593615338182796823030195203530185296899577362" +
                "259941389124972177528347913151557485724245415069595082953311686" +
                "172785588907509838175463746493931925506040092770167113900984882" +
                "401285836160356370766010471018194295559619894676783744944825537"),
        TAU("6.", "28318530717958647692528676655900576839" +
                "4338798750211641949889184615632812572417997256069650684234135964296173" +
                "0265646132941876892191011644634507188162569622349005682054038770422111" +
                "1928924589790986076392885762195133186689225695129646757356633054240381" +
                "8291297133846920697220908653296426787214520498282547449174013212631176" +
                "3497630418419256585081834307287357851807200226610610976409330427682939" +
                "0388302321886611454073151918390618437223476386522358621023709614892475" +
                "9925499134703771505449782455876366023898259667346724881313286172042789" +
                "8927904494743814043597218874055410784343525863535047693496369353388102" +
                "6400113625429052712165557154268551557921834727435744293688180244990686" +
                "0293099170742101584559378517847084039912224258043921728068836319627259" +
                "5495426199210374144226999999967459560999021194634656321926371900489189" +
                "1069381660528504461650668937007052386237634202000627567750577317506641" +
                "6762841234355338294607196506980857510937462319125727764707575187503915" +
                "5637155610643424536132260038557532223918184328403978761905144021309717"),
        EULER("2.", "7182818284590452353602874713526624977572470936999595749669676277240766303535" +
                "47594571382178525166427427466391932003059921817413596629043572900334295260595630" +
                "73813232862794349076323382988075319525101901157383418793070215408914993488416750" +
                "92447614606680822648001684774118537423454424371075390777449920695517027618386062" +
                "61331384583000752044933826560297606737113200709328709127443747047230696977209310" +
                "14169283681902551510865746377211125238978442505695369677078544996996794686445490" +
                "59879316368892300987931277361782154249992295763514822082698951936680331825288693" +
                "98496465105820939239829488793320362509443117301238197068416140397019837679320683" +
                "28237646480429531180232878250981945581530175671736133206981125099618188159304169" +
                "03515988885193458072738667385894228792284998920868058257492796104841984443634632" +
                "44968487560233624827041978623209002160990235304369941849146314093431738143640546" +
                "25315209618369088870701676839642437814059271456354906130310720851038375051011574" +
                "77041718986106873969655212671546889570350354021234078498193343210681701210056278" +
                "80235193033224745015853904730419957777093503660416997329725088687696640355570716" +
                "22684471625607988265178713419512466520103059212366771943252786753985589448969709" +
                "64097545918569563802363701621120477427228364896134225164450781824423529486363721" +
                "41740238893441247963574370263755294448337998016125492278509257782562092622648326" +
                "27793338656648162772516401910590049164499828931505660472580277863186415519565324" +
                "42586982946959308019152987211725563475463964479101459040905862984967912874068705" +
                "04895858671747985466775757320568128845920541334053922000113786300945560688166740");

        final String integerPart, fractionalPart;

        Digits(String integerPart, String fractionalPart) {
            this.integerPart = integerPart;
            this.fractionalPart = fractionalPart;
        }
    }

    private Digits current_digits;

    private EditText inputET;
    private Toolbar toolbar;
    private TextView integerPartTV, digitsTV, errorsTV, percentageTV;
    private Keyboard keyboard;
    private ImageButton restartButton;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerListView;

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;
    private boolean toolbarCurrentlyRed = false;
    private int errors = 0;

    private boolean vibrate;
    private boolean onScreenKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.drawer_listView);
        initDrawerToggle();

        inputET = (EditText) findViewById(R.id.input_editText);
        digitsTV = (TextView) findViewById(R.id.digits_textView);
        keyboard = (Keyboard) findViewById(R.id.keyboard);
        restartButton = (ImageButton) findViewById(R.id.refresh_button);
        errorsTV = (TextView) findViewById(R.id.errors_textView);
        percentageTV = (TextView) findViewById(R.id.percentage_textView);
        integerPartTV = (TextView) findViewById(R.id.integerPart_textView);

        restoreValues();
        setTypeListener();
        setRestartImageResource();
        setTitle();
        fillTextViews();

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (indirectTextChange) return;

                selection = inputET.getSelectionStart();

                if (isIncorrect(inputET.getText().toString()) && inputET.length() != 0) {
                    animateToolbarColor(false);

                    // If the last typed character is wrong:
                    if (lastTextLength < inputET.length() // backspace is not pressed
                            && inputET.getText().toString().charAt(selection - 1)
                            != current_digits.fractionalPart.charAt(selection - 1)) { // The typed character is wrong

                        errors++;

                        if (vibrate) {
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                        }
                    }

                } else {
                    animateToolbarColor(true);
                }

                indirectTextChange = true;
                inputET.setText(toColoredSpannable(inputET.getText().toString()));
                indirectTextChange = false;
                if (selection < inputET.length()) {
                    inputET.setSelection(selection);
                } else {
                    inputET.setSelection(inputET.length());
                }

                lastTextLength = inputET.length();

                fillTextViews();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    private void setTypeListener() {
        keyboard.setOnTypeListener(new Keyboard.OnTypeListener() {
            @Override
            public void onTypeDigit(int digit) {
                final int selection = inputET.getSelectionStart();
                inputET.getText().insert(selection, Integer.toString(digit));
            }

            @Override
            public void onTypeBackspace() {
                final int selection = inputET.getSelectionStart();
                if (selection > 0) {
                    inputET.getText().replace(selection - 1, selection, "");
                }
            }
        });
    }

    private void setRestartImageResource() {
        if (Build.VERSION.SDK_INT >= 21) {
            restartButton.setImageResource(R.drawable.anim_ic_restart);
        } else {
            restartButton.setImageResource(R.mipmap.ic_refresh_white_36dp);
        }
    }

    private void animateToolbarColor(boolean correct) {
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

    public boolean isIncorrect(String stringToCheck) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            if (stringToCheck.charAt(i) != current_digits.fractionalPart.charAt(i)) {
                return true;
            }
        }

        return false;
    }

    private void fillTextViews() {
        String input = inputET.getText().toString();

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == current_digits.fractionalPart.charAt(i)) {
                count++;
            }
        }

        digitsTV.setText(" " + count);
        errorsTV.setText(" " + errors);

        if (inputET.length() == 0 || errors > inputET.length()) {
            percentageTV.setText(" 0%");
        } else {
            percentageTV.setText(" " + (int) Math.floor(100 - ((double) errors / inputET.length() * 100)) + "%");
        }
    }

    private SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != current_digits.fractionalPart.charAt(i)) {
                // If the character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
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

                showOnScreenKeyboard(item.isChecked());
                return true;

            case R.id.number:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setSingleChoiceItems(R.array.numbers, current_digits.ordinal(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (current_digits.ordinal() != i) {
                            current_digits = Digits.values()[i];
                            integerPartTV.setText(current_digits.integerPart);
                            setTitle();
                            onClickRestart(null);
                        }
                        dialogInterface.dismiss();
                    }
                }).show();

                return true;
            default:
                return false;
        }
    }

    private void showOnScreenKeyboard(boolean show) {
        onScreenKeyboard = show;
        getPreferences(0).edit().putBoolean(ON_SCREEN_KEYBOARD, show).apply();
        preventSoftKeyboardFromShowingUp(show);

        keyboard.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void preventSoftKeyboardFromShowingUp(boolean prevent) {
        if (prevent) {
            if (Build.VERSION.SDK_INT >= 21) {
                inputET.setShowSoftInputOnFocus(false);
            } else {
                inputET.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int inputType = inputET.getInputType(); // backup the input type
                        inputET.setInputType(InputType.TYPE_NULL); // disable soft input
                        inputET.onTouchEvent(motionEvent); // call native handler
                        inputET.setInputType(inputType); // restore input type
                        inputET.setFocusable(true);
                        return true; // consume touch even
                    }
                });
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                inputET.setShowSoftInputOnFocus(true);
            } else {
                inputET.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return inputET.onTouchEvent(motionEvent);
                    }
                });
            }
        }
    }

    public void onClickRestart(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            ((AnimatedVectorDrawable) restartButton.getDrawable()).start();
        }
        inputET.setText("");
        errors = 0;
        fillTextViews();
    }

    private void setTitle() {
        if (current_digits != null) {
            setTitle(getResources().getStringArray(R.array.trainers)[current_digits.ordinal()]);
        }
    }

    @Override
    protected void onDestroy() {
        getPreferences(0).edit()
                .putInt(ERRORS, errors)
                .putString(INPUT, inputET.getText().toString())
                .putInt(CURRENT_DIGITS_ORDINAL, current_digits.ordinal())
                .apply();
        super.onDestroy();
    }

    private void restoreValues() {
        errors = getPreferences(0).getInt(ERRORS, 0);

        inputET.setText(getPreferences(0).getString(INPUT, ""));
        inputET.setSelection(inputET.length());

        vibrate = getPreferences(0).getBoolean(VIBRATE, true);

        current_digits = Digits.values()[getPreferences(0).getInt(CURRENT_DIGITS_ORDINAL, 0)];
        integerPartTV.setText(current_digits.integerPart);

        showOnScreenKeyboard(getPreferences(0).getBoolean(ON_SCREEN_KEYBOARD, false));
    }
}