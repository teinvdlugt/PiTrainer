package com.teinproductions.tein.pitrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

        String errorMessage = null;
        if (nameET.length() == 0) errorMessage = "Please provide a name";
        else if (integerET.length() == 0) errorMessage = "Please provide an integer part";
        else if (fractionalET.length() == 0) errorMessage = "Please provide a fractional part";
        else if (contains(names, newName) && (newDigits || !newName.equals(oldName)))
            errorMessage = "There is already a number with that name";
        if (errorMessage != null) {
            Snackbar.make(findViewById(R.id.root), errorMessage, Snackbar.LENGTH_LONG).show();
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

    public void onClickDelete(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.sure_delete_digits)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(NumbersActivity.DELETE_DIGITS, true);
                        resultIntent.putExtra(DIGITS, digits);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }).setNegativeButton(R.string.no, null)
                .create().show();
    }

    private static boolean contains(String[] strings, String string) {
        for (String string2 : strings) {
            if (string2.equals(string)) return true;
        }
        return false;
    }
}
