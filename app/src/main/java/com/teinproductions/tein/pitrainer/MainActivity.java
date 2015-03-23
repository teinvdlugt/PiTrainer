package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static final String VIBRATE = "VIBRATE";

    public static final String PI_DIGITS = "1415926535897932384626433832795028841971693993" +
            "7510582097494459230781640628620899862803482534211706798214808651328230" +
            "6647093844609550582231725359408128481117450284102701938521105559644622" +
            "9489549303819644288109756659334461284756482337867831652712019091456485" +
            "6692346034861045432664821339360726024914127372458700660631558817488152" +
            "0920962829254091715364367892590360011330530548820466521384146951941511" +
            "6094330572703657595919530921861173819326117931051185480744623799627495" +
            "6735188575272489122793818301194912983367336244065664308602139494639522" +
            "4737190702179860943702770539217176293176752384674818467669405132000568" +
            "1271452635608277857713427577896091736371787214684409012249534301465495" +
            "8537105079227968925892354201995611212902196086403441815981362977477130" +
            "9960518707211349999998372978049951059731732816096318595024459455346908" +
            "3026425223082533446850352619311881710100031378387528865875332083814206";

    private EditText inputET;
    private Toolbar toolbar;
    private TextView digits;

    private boolean indirectTextChange = false;
    private int selection = 0;
    private int lastTextLength = 0;

    private boolean vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputET = (EditText) findViewById(R.id.input_editText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        digits = (TextView) findViewById(R.id.digits_textView);

        vibrate = getPreferences(0).getBoolean(VIBRATE, true);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                digits.setText(getString(R.string.digits_colon) + inputET.length());

                if (indirectTextChange) return;

                selection = inputET.getSelectionStart();

                if (!isCorrect(inputET.getText().toString()) && inputET.length() != 0) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.red));

                    if (lastTextLength < inputET.length() && vibrate) {
                        // That means backspace is not pressed
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    }
                } else {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.primary_material_dark));
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.vibrate).setChecked(vibrate);
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
            default:
                return false;
        }
    }

    public static boolean isCorrect(String stringToCheck) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            if (stringToCheck.charAt(i) != PI_DIGITS.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    public SpannableStringBuilder toColoredSpannable(String string) {
        SpannableStringBuilder sb = new SpannableStringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != PI_DIGITS.charAt(i)) {
                // If the character is incorrect
                ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
                sb.setSpan(redSpan, i, i + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        return sb;
    }
}
