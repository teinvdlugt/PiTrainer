package com.teinproductions.tein.pitrainer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements ActivityInterface {

    private static final String VIBRATE = "VIBRATE";
    public static final String ON_SCREEN_KEYBOARD = "ON_SCREEN_KEYBOARD";
    public static final String CURRENT_DIGITS_ORDINAL = "CURRENT_DIGITS_ORDINAL";
    public static final String CURRENT_GAME = "CURRENT_GAME";

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

    private MainActivity.Digits current_digits;
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
        if (current_digits != null) {
            setTitle(getResources().getStringArray(R.array.trainers)[current_digits.ordinal()]);
        }
    }

    private void swapFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
                new AlertDialog.Builder(this)
                        .setTitle(R.string.number_option_dialog_title)
                        .setSingleChoiceItems(R.array.numbers, current_digits.ordinal(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (current_digits.ordinal() != i) {
                                    current_digits = Digits.values()[i];
                                    getPreferences(0).edit().putInt(CURRENT_DIGITS_ORDINAL, i).apply();
                                    fragmentInterface.setCurrentDigits(current_digits);
                                    setTitle();
                                }
                                dialogInterface.dismiss();
                            }
                        }).show();

                return true;
            default:
                return false;
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
                .putInt(CURRENT_DIGITS_ORDINAL, current_digits.ordinal())
                .putInt(CURRENT_GAME, currentGame)
                .apply();
        super.onPause();
    }

    private void restoreValues() {
        vibrate = getPreferences(0).getBoolean(VIBRATE, true);
        current_digits = Digits.values()[getPreferences(0).getInt(CURRENT_DIGITS_ORDINAL, 0)];
        onScreenKeyboard = getPreferences(0).getBoolean(ON_SCREEN_KEYBOARD, false);
        currentGame = getPreferences(0).getInt(CURRENT_GAME, 0);
    }
}