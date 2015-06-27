package com.teinproductions.tein.pitrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddNumberActivity extends AppCompatActivity {

    public static final String DIGITS = "DIGITS";

    private EditText nameET, integerET, fractionalET;

    private Digits digits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_number);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        nameET = (EditText) findViewById(R.id.name_editText);
        integerET = (EditText) findViewById(R.id.integerPart_editText);
        fractionalET = (EditText) findViewById(R.id.fractionalPart_editText);

        restoreValues();
    }

    private void restoreValues() {
        digits = (Digits) getIntent().getSerializableExtra(DIGITS);
        if (digits == null) {
            // Most probably a new Digits
            digits = new Digits();
        } else {
            nameET.setText(digits.getName());
            integerET.setText(digits.getIntegerPart());
            fractionalET.setText(digits.getFractionalPart());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dismiss:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.save:
                return save();
            default:
                return false;
        }
    }

    private boolean save() {
        String[] names = Digits.digitsNames();
        String oldName = digits.getName();
        String newName = nameET.getText().toString();
        boolean newDigits = oldName == null;

        if (nameET.length() == 0) {
            Toast.makeText(this, "Please provide a name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (integerET.length() == 0) {
            Toast.makeText(this, "Please provide an integer part", Toast.LENGTH_SHORT).show();
            return false;
        } else if (fractionalET.length() == 0) {
            Toast.makeText(this, "Please provide a fractional part", Toast.LENGTH_SHORT).show();
            return false;
        } else if (contains(names, newName)
                && (newDigits || !newName.equals(oldName))) {
            Toast.makeText(this, "There is already a number with that name", Toast.LENGTH_SHORT).show();
            return false;
        }

        String integerPart = integerET.getText().toString();
        String fractionalPart = fractionalET.getText().toString();
        digits = new Digits(newName, integerPart, fractionalPart);

        Intent intent = new Intent();
        intent.putExtra(DIGITS, digits);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    public static boolean contains(String[] strings, String string) {
        for (String string2 : strings) {
            if (string2.equals(string)) return true;
        }
        return false;
    }
}
