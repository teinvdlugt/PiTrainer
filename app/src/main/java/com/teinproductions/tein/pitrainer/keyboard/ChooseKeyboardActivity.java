package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.teinproductions.tein.pitrainer.R;

public class ChooseKeyboardActivity extends AppCompatActivity implements ChooseKeyboardAdapter.OnItemClickListener {
    public static final String CURRENT_KEYBOARD_PREFERENCE = "current_keyboard";
    public static final int DEFAULT_KEYBOARD = 0;
    public static final int[][] LAYOUTS = {
            {7, 8, 9, 4, 5, 6, 1, 2, 3, 0, -2, -1},
            {7, 8, 9, 4, 5, 6, 1, 2, 3, -2, 0, -1},
            {7, 8, 9, 4, 5, 6, 1, 2, 3, -1, -2, 0},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, -2, -1},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, -2, 0, -1},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -2, 0}}; // -1 = backspace, -2 = empty space

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_keyboard);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChooseKeyboardAdapter adapter = new ChooseKeyboardAdapter(this, getCurrentKeyboardIndex(this), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickKeyboard(int index) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(CURRENT_KEYBOARD_PREFERENCE, index).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getCurrentKeyboardIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(CURRENT_KEYBOARD_PREFERENCE, DEFAULT_KEYBOARD);
    }
}
