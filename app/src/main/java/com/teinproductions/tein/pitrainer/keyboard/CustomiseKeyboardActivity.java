package com.teinproductions.tein.pitrainer.keyboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.teinproductions.tein.pitrainer.R;

public class CustomiseKeyboardActivity extends AppCompatActivity {
    private CustomiseKeyboardView keyboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_keyboard);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        keyboardView = (CustomiseKeyboardView) findViewById(R.id.customiseKeyboardView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO: 19-11-2016 Save or discard
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
