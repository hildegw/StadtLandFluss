package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static com.example.android.stadtlandfluss.MainActivity.PREFS_NAME;
import static java.lang.String.valueOf;

public class DifficultyActivity extends AppCompatActivity {

    private int difficultySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        //Set Toolbar (see menu file)
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        difficultySelected = settings.getInt("difficultySelected", 1);  //1 is default
        Log.i("diff-act", valueOf(difficultySelected));
        RadioButton easyRadio = (RadioButton) findViewById(R.id.radiobutton_easy);
        RadioButton middleRadio = (RadioButton) findViewById(R.id.radiobutton_middle);
        RadioButton hardRadio = (RadioButton) findViewById(R.id.radiobutton_hard);
        switch (difficultySelected) {
            case 1:
                easyRadio.setChecked(true);
                return;
            case 2:
                middleRadio.setChecked(true);
                return;
            case 3:
                hardRadio.setChecked(true);
                return;
        }
    }

    //Handle Toolbar Icon click events, hand over Bundle containing difficulty to main acitivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setDifficulty();
        //Save preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("difficultySelected", difficultySelected);
        editor.commit();
        //go back to main activity
        Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    //get the difficulty group selected by the user
    private void setDifficulty() {
        //get selected difficulty
        //check, which difficulty is selected (radio button) and set difficulty accordingly
        RadioGroup selectDifficultyRadioGroup = (RadioGroup) findViewById(R.id.radio_group_select_difficulty);
        int checkedId = selectDifficultyRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radiobutton_easy) {
            difficultySelected = 1;
        } else if (checkedId == R.id.radiobutton_middle) {
            difficultySelected = 2;
        } else {
            difficultySelected = 3;
        }
    }
}


