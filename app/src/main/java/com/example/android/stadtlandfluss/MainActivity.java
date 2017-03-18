package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.android.stadtlandfluss.R.id.difficulty;

public class MainActivity extends AppCompatActivity {

    public String selectedLetter;
    private Chronometer chronometer;
    private KeyListener editTextCityKeyListener;
    private KeyListener editTextCountryKeyListener;
    private KeyListener editTextRiverKeyListener;
    private KeyListener editTextMountainKeyListener;
    private Toolbar myToolbar;

    //todo: keep status when switching activities and when switching to landscape
    //todo: add game icon and background icon b/W
    //todo: data base!!!! getText from editText
    //todo: colors.xml
    //todo: adjust difficulty according to database entries
    //todo: educational - link to Wiki
    //todo: arrange table in blocks above each other / center fields!?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set Toolbar (see menu file) - https://guides.codepath.com/android/Using-the-App-Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //hide stop button
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.GONE);
        //Setup table to play with selected settings from fields activity - if user made a choice
        setupTableToPlay();
        //Fetch keyListener from EditText fields in table
        EditText editTextCity = (EditText) findViewById(R.id.edit_text_city);
        editTextCityKeyListener = editTextCity.getKeyListener();
        EditText editTextCountry = (EditText) findViewById(R.id.edit_text_country);
        editTextCountryKeyListener = editTextCountry.getKeyListener();
        EditText editTextRiver = (EditText) findViewById(R.id.edit_text_river);
        editTextRiverKeyListener = editTextRiver.getKeyListener();
        EditText editTextMountain = (EditText) findViewById(R.id.edit_text_mountain);
        editTextMountainKeyListener = editTextMountain.getKeyListener();
        //hide bar that shows score
        TextView scoreBar = (TextView) findViewById(R.id.your_score_is);
        scoreBar.setVisibility(View.GONE);
    }

    // Menu icons in toolbar are inflated just as with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handle Toolbar Icon click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.table:
                //Start second activity: table
                Intent intentTable = new Intent(MainActivity.this, TableActivity.class);
                startActivity(intentTable);
                return true;
            case difficulty:
                //Start third activity: difficulty
                Intent intentDifficulty = new Intent(MainActivity.this, DifficultyActivity.class);
                startActivity(intentDifficulty);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Start button: selected letter and stop button are displayed, stop watch is reset, text entry in table is enabled
    public void startGame(View view) {
        //display Letter selected in Difficulty Activity
        showSelectLetter();
        //reset & start stop watch
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        //Hide start button and show stop button
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.GONE);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.VISIBLE);
        //clear EditText with Start and enable text entry
        EditText editTextCity = (EditText) findViewById(R.id.edit_text_city);
        editTextCity.setText("");
        editTextCity.setKeyListener(editTextCityKeyListener);
        EditText editTextCountry = (EditText) findViewById(R.id.edit_text_country);
        editTextCountry.setText("");
        editTextCountry.setKeyListener(editTextCountryKeyListener);
        EditText editTextRiver = (EditText) findViewById(R.id.edit_text_river);
        editTextRiver.setText("");
        editTextRiver.setKeyListener(editTextRiverKeyListener);
        EditText editTextMountain = (EditText) findViewById(R.id.edit_text_mountain);
        editTextMountain.setText("");
        editTextMountain.setKeyListener(editTextMountainKeyListener);
        //todo: hide keyboard after reset
    }

    //Stop button: start button is displayed, watch stops,text entry in table is disabled
    public void stopGame(View view) {
        //replace stop with start button
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.VISIBLE);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.GONE);
        //stop watch
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.stop();
        //Disable text entry
        EditText editTextCity = (EditText) findViewById(R.id.edit_text_city);
        editTextCity.setKeyListener(null);
        EditText editTextCountry = (EditText) findViewById(R.id.edit_text_country);
        editTextCountry.setKeyListener(null);
        EditText editTextRiver = (EditText) findViewById(R.id.edit_text_river);
        editTextRiver.setKeyListener(null);
        EditText editTextMountain = (EditText) findViewById(R.id.edit_text_mountain);
        editTextMountain.setKeyListener(null);
        //pass letter to DB and read lists for city, country, river, mountain
        readGeoNamesFromDB();
        //calculate and display score, unhide score bar
        TextView scoreBar = (TextView) findViewById(R.id.your_score_is);
        scoreBar.setVisibility(View.VISIBLE);
        calculateScore();
    }

    //calculate score from time elapsed: each minute reduces score of 10 by one point, max 10 points possible.
    private int timeScore() {
        int scoreFromTime = 0;
        //get Time elapsed and convert into score (each minute used reduces score of 10)
        long saveTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        int timeElapsed = (int)(saveTime/1000);
        if(timeElapsed / 60 < 10) {
            scoreFromTime = 10 - timeElapsed / 60;
        }
        return scoreFromTime;
    }


    //todo: calculate score from correct fields * difficulty * score from Time
    private void calculateScore() {
        int score = 0;
        int difficulty = 1;
        difficulty = difficultyFromBundle();         //get difficulty info from activity

        //todo: check for correct answers, each correct answer adds 1 point

        //calculate score
        score = timeScore() * difficulty;
        //display score
        String showScore = String.valueOf(score);
        TextView selectedLetterText = (TextView) findViewById(R.id.your_score_is);
        selectedLetterText.setText(getString(R.string.your_score_is) + " " + showScore + " points");
    }

    //inititate firebase DB for selected letter with press of stop button
    private void readGeoNamesFromDB() {
        //get DB reference for selected letter
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //todo: remove "A" after DB setup is complete !!!!!!
        selectedLetter = "A";
        DatabaseReference dbRef = database.getReference(selectedLetter);

        //Read all geografic names from the database that start with the selected letter
        //todo: read only DB entries selected as table fields
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                List<Object> values = new ArrayList<>(td.values());
                //todo: delete
                Log.i("Value is: ", String.valueOf(values));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });
    }


    //Get selectedLetter from Bundle from Difficulty Activity
    private int difficultyFromBundle() {
        Bundle letterBundle  = getIntent().getExtras();
        int difficulty = 1;
        if(letterBundle != null) {
            difficulty = letterBundle.containsKey("Difficulty") ? letterBundle.getInt("Difficulty") : 1;
        }
        return difficulty;
    }

    //get difficulty from Bundle, make random Letter selection, display, and return letter
    private void showSelectLetter() {
        selectedLetter = "A";
        int difficulty = 1;
        difficulty = difficultyFromBundle();         //get difficulty info from activity
        //random select letter
        Random randomLetter = new Random();
        String easyLetters = getString(R.string.easy_letters);
        String middleLetters = getString(R.string.middle_letters);
        String hardLetters = getString(R.string.hard_letters);
        if (difficulty == 1) {
            selectedLetter = String.valueOf(easyLetters.charAt(randomLetter.nextInt(easyLetters.length())));
        } else if (difficulty == 2) {
            selectedLetter = String.valueOf(middleLetters.charAt(randomLetter.nextInt(middleLetters.length())));
        } else {
            selectedLetter = String.valueOf(hardLetters.charAt(randomLetter.nextInt(hardLetters.length())));
        }
        //display letter
        TextView selectedLetterText = (TextView) findViewById(R.id.letter_to_play);
        selectedLetterText.setText(getString(R.string.letter_to_play) + " " + selectedLetter);
        //return letter for further use in DB read;
    }

    //Provide fields to play in table based on boxes checked
    private void setupTableToPlay() {
        //get selections from fields activity (true/false via Extras(Bundle))
        boolean citySelected = true;
        boolean countrySelected = true;
        boolean riverSelected = true;
        boolean mountainSelected = true;
        Bundle fields  = getIntent().getExtras();
        if(fields != null) {
            citySelected = fields.containsKey("City") ? fields.getBoolean("City") : true;
            countrySelected = fields.containsKey("Country") ? fields.getBoolean("Country") : true;
            riverSelected = fields.containsKey("River") ? fields.getBoolean("River") : true;
            mountainSelected = fields.containsKey("Mountain") ? fields.getBoolean("Mountain") : true;
        }
        //Each field to play (column) is made visible/gone depending on checkbox, here: "city"
        TextView cityTableHead = (TextView) findViewById(R.id.table_head_city);
        TextView cityTableField = (TextView) findViewById(R.id.edit_text_city);
        if (citySelected) {
            cityTableHead.setText(R.string.checkbox_city);
            cityTableHead.setVisibility(View.VISIBLE);
            cityTableField.setVisibility(View.VISIBLE);
        } else {
            cityTableHead.setVisibility(View.GONE);
            cityTableField.setVisibility(View.GONE);
        }
        //here: "country"
        TextView countryTableHead = (TextView) findViewById(R.id.table_head_country);
        TextView countryTableField = (TextView) findViewById(R.id.edit_text_country);
        if (countrySelected) {
            countryTableHead.setText(R.string.checkbox_country);
            countryTableHead.setVisibility(View.VISIBLE);
            countryTableField.setVisibility(View.VISIBLE);
        } else {
            countryTableHead.setVisibility(View.GONE);
            countryTableField.setVisibility(View.GONE);
        }
        //here: "river"
        TextView riverTableHead = (TextView) findViewById(R.id.table_head_river);
        TextView riverTableField = (TextView) findViewById(R.id.edit_text_river);
        if (riverSelected) {
            riverTableHead.setText(R.string.checkbox_river);
            riverTableHead.setVisibility(View.VISIBLE);
            riverTableField.setVisibility(View.VISIBLE);
        } else {
            riverTableHead.setVisibility(View.GONE);
            riverTableField.setVisibility(View.GONE);
        }
        //here: "mountain"
        TextView mountainTableHead = (TextView) findViewById(R.id.table_head_mountain);
        TextView mountainTableField = (TextView) findViewById(R.id.edit_text_mountain);
        if (mountainSelected) {
            mountainTableHead.setText(R.string.checkbox_mountain);
            mountainTableHead.setVisibility(View.VISIBLE);
            mountainTableField.setVisibility(View.VISIBLE);
        } else {
            mountainTableHead.setVisibility(View.GONE);
            mountainTableField.setVisibility(View.GONE);
        }
    }

}