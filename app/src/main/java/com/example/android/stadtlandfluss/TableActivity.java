package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;

import static com.example.android.stadtlandfluss.MainActivity.PREFS_NAME;


public class TableActivity extends AppCompatActivity {

    private boolean citySelected;
    private boolean countrySelected;
    private boolean riverSelected;
    private boolean mountainSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        //Set Toolbar (see menu file)
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Restore preference settings
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        citySelected = settings.getBoolean("citySelected", true);
        countrySelected = settings.getBoolean("countrySelected", true);
        riverSelected = settings.getBoolean("riverSelected", true);
        mountainSelected = settings.getBoolean("mountainSelected", true);
        CheckBox cityCheckBox = (CheckBox) findViewById(R.id.play_checkbox_city);
        cityCheckBox.setChecked(citySelected);
        CheckBox countryCheckBox = (CheckBox) findViewById(R.id.play_checkbox_country);
        countryCheckBox.setChecked(countrySelected);
        CheckBox riverCheckBox = (CheckBox) findViewById(R.id.play_checkbox_river);
        riverCheckBox.setChecked(riverSelected);
        CheckBox mountainCheckBox = (CheckBox) findViewById(R.id.play_checkbox_mountain);
        mountainCheckBox.setChecked(mountainSelected);
    }

    //Handle Toolbar Icon click events, hand over Bundle containing table information to main acitivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        findBoxesChecked();
        //Save preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("citySelected", citySelected);
        editor.putBoolean("countrySelected", countrySelected);
        editor.putBoolean("riverSelected", riverSelected);
        editor.putBoolean("mountainSelected", mountainSelected);
        editor.commit();
        //go back to main activity
        Intent intent = new Intent(TableActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    //Identify checked boxes and hand over to Toolbar click-function above
    private void findBoxesChecked() {
        CheckBox cityCheckBox = (CheckBox) findViewById(R.id.play_checkbox_city);
        citySelected = cityCheckBox.isChecked();
        CheckBox countryCheckBox = (CheckBox) findViewById(R.id.play_checkbox_country);
        countrySelected = countryCheckBox.isChecked();
        CheckBox riverCheckBox = (CheckBox) findViewById(R.id.play_checkbox_river);
        riverSelected = riverCheckBox.isChecked();
        CheckBox mountainCheckBox = (CheckBox) findViewById(R.id.play_checkbox_mountain);
        mountainSelected = mountainCheckBox.isChecked();
    }
}
