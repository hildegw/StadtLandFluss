package com.example.android.stadtlandfluss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import static android.os.SystemClock.elapsedRealtime;
import static com.example.android.stadtlandfluss.R.id.difficulty;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    public String selectedLetter;
    public String geoNamesString;
    private Chronometer chronometer;
    private KeyListener editTextCityKeyListener;
    private KeyListener editTextCountryKeyListener;
    private KeyListener editTextRiverKeyListener;
    private KeyListener editTextMountainKeyListener;

    //todo: make sure that complete word is compared (e.g. not just "Q") in compareTableFieldsWithGeoNames
    //todo: feed DB
    //todo: keep status when switching activities and when switching to landscape
    //todo: add game icon and background icon b/W
    //todo: present score more explicitly in calculateScore()
    //todo: limit compare to each category, compare each word of a name in compareTableFieldsWithGeoNames
    //todo: catch error if no DB entry exists for selected letter in readGeoNamesFromDB
    //todo: login to DB
    //todo: remove Logs
    /* nice to haves
    //get rid of public variables
    //adjust difficulty according to database entries in strings.xml
    //read only DB entries selected as table fields
    //educational - link to Wiki, Scattergories
    //arrange table in blocks above each other / center fields!?
    */

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

    //todo store state in State Bundle
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        // Store UI state to the savedInstanceState. This bundle will be passed to onCreate on next call.
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        savedInstanceState.putLong("Chronometer", chronometer.getBase());

        savedInstanceState.putString("SelectedLetter", selectedLetter);

        Button stopButton = (Button) findViewById(R.id.stop_button);
        savedInstanceState.putInt("StopButton", stopButton.getVisibility());

        TextView scoreBar = (TextView) findViewById(R.id.your_score_is);
        savedInstanceState.putInt("ScoreBar", scoreBar.getVisibility());
        savedInstanceState.putCharSequence("ScoreText", scoreBar.getText());

    }


    //todo click on stop crashes app after flipping: restore DB and score, table???
    //todo restore saved instance from savedInstanceState, e.g. when phone is flipped
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        //restore Start/Stop Button visibility and view according to game status
        if((savedInstanceState !=null) && savedInstanceState.containsKey("StopButton")) {
            Button stopButton = (Button) findViewById(R.id.stop_button);
            Button startButton = (Button) findViewById(R.id.start_button);
            chronometer = (Chronometer) findViewById(R.id.chronometer);
            TextView scoreBar = (TextView) findViewById(R.id.your_score_is);
            TextView selectedLetterText = (TextView) findViewById(R.id.letter_to_play);
            int stopButtonVisibility = savedInstanceState.getInt("StopButton");
            Log.i("Stop ", valueOf(stopButtonVisibility)); //todo remove
            //restore status: game is started
            if (stopButtonVisibility == 0) {
                stopButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                //chronometer continues counting
                if(savedInstanceState.containsKey("Chronometer")) {
                    long chronoTime;
                    chronoTime = savedInstanceState.getLong("Chronometer");
                    chronometer.setBase(chronoTime - elapsedRealtime()); //todo: set correct base
                    chronometer.start();
                }
                //restore selected letter and display
                if(savedInstanceState.containsKey("SelectedLetter")) {
                    selectedLetter = savedInstanceState.getString("SelectedLetter");
                    selectedLetterText.setText(getString(R.string.letter_to_play) + " " + selectedLetter);
                }
            //status: game is stopped
            } else {
                stopButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                //chronometer is stopped and shows stop time
                if(savedInstanceState.containsKey("Chronometer")) {
                    long chronoTime;
                    chronoTime = savedInstanceState.getLong("Chronometer");
                    chronometer.setBase(chronoTime); //todo: set correct base
                }
                //restore score text
                if(savedInstanceState.containsKey("ScoreBar")) {
                    int scoreBarVisibility = savedInstanceState.getInt("ScoreBar");
                    if (scoreBarVisibility == 0) {
                        scoreBar.setVisibility(View.VISIBLE);
                        scoreBar.setText(savedInstanceState.getCharSequence("ScoreText"));
                    } else {
                        scoreBar.setVisibility(View.GONE);
                    }
                }
            }

        } else {
            onCreate(savedInstanceState);
        }





        if((savedInstanceState !=null) && savedInstanceState.containsKey("SelectedLetter")) {
            selectedLetter = savedInstanceState.getString("SelectedLetter");
        }






    }








    //Start button: selected letter and stop button are displayed, stop watch is reset, text entry in table is enabled
    public void startGame(View view) {
        //display Letter selected in Difficulty Activity
        showSelectLetter();
        //read lists for city, country, river, mountain for selected letter
        //needs to be called early, otherwise global variable geoNamesString will be null
        readGeoNamesFromDB();
        //reset & start stop watch
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.stop();
        chronometer.setBase(elapsedRealtime());
        chronometer.start();
        //Hide start button and show stop button
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.GONE);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.VISIBLE);
        //Hide score bar if visible
        TextView scoreText = (TextView) findViewById(R.id.your_score_is);
        scoreText.setVisibility(View.GONE);
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
    }

    //Stop button: start button is displayed, watch stops,text entry in table is disabled
    public void stopGame(View view) {
        //close keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        //calculate and display score, unhide score bar
        TextView scoreBar = (TextView) findViewById(R.id.your_score_is);
        scoreBar.setVisibility(View.VISIBLE);
        calculateScore();
    }


    //inititate firebase DB for selected letter - called by compareTableFieldsWithGeoNames()
    private void readGeoNamesFromDB() {
        //get DB reference for selected letter
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        geoNamesString = "XXXX"; //setting up default geo names string just in case read from firebase takes longer
        DatabaseReference dbRef = database.getReference(selectedLetter);
        //Read all geografic names from the database that start with the selected letter
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                List<Object> geoNames = new ArrayList<>(td.values());
                geoNamesString = String.valueOf(geoNames);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });
    }

    //copmare user table entries with geo names for every field
    private int compareTableFieldsWithGeoNames() {
        int correctFieldsCount = 0;
        //get user's table input - if it exists
        TextView cityTableField = (TextView) findViewById(R.id.edit_text_city);
        String cityTableEntry = cityTableField.getText().toString();
        //check, if table entry is found in geo names string
        if (!cityTableEntry.matches("") && geoNamesString.contains(cityTableEntry)) {
            Log.i("City entered is: ", cityTableEntry); //todo remove
            correctFieldsCount++;
        }
        //repeat for all fields
        TextView countryTableField = (TextView) findViewById(R.id.edit_text_country);
        String countryTableEntry = countryTableField.getText().toString();
        if (!countryTableEntry.matches("") && geoNamesString.contains(countryTableEntry)) {
            Log.i("country entered is: ", countryTableEntry); //todo remove
            correctFieldsCount++;
        }
        TextView riverTableField = (TextView) findViewById(R.id.edit_text_river);
        String riverTableEntry = riverTableField.getText().toString();
        if (!riverTableEntry.matches("") && geoNamesString.contains(riverTableEntry)) {
            Log.i("river entered is: ", riverTableEntry); //todo remove
            correctFieldsCount++;
        }
        TextView mountainTableField = (TextView) findViewById(R.id.edit_text_mountain);
        String mountainTableEntry = mountainTableField.getText().toString();
        if (!mountainTableEntry.matches("") && geoNamesString.contains(mountainTableEntry)) {
            Log.i("mountain entered is: ", mountainTableEntry); //todo remove
            correctFieldsCount++;
        }
        return correctFieldsCount;
    }

    //Get difficulty from Bundle handed over from Difficulty Activity
    private int difficultyFromBundle() {
        Bundle letterBundle  = getIntent().getExtras();
        int difficulty = 1;
        if(letterBundle != null) {
            difficulty = letterBundle.containsKey("Difficulty") ? letterBundle.getInt("Difficulty") : 1;
        }
        return difficulty;
    }

    //make random Letter selection, display, and store letter in variable
    private void showSelectLetter() {
        selectedLetter = "A";                        //set default
        int difficulty;
        difficulty = difficultyFromBundle();         //get difficulty info from activity
        //random select letter
        Random randomLetter = new Random();
        String easyLetters = getString(R.string.easy_letters);
        String middleLetters = getString(R.string.middle_letters);
        String hardLetters = getString(R.string.hard_letters);
        if (difficulty == 1) {
            selectedLetter = valueOf(easyLetters.charAt(randomLetter.nextInt(easyLetters.length())));
        } else if (difficulty == 2) {
            selectedLetter = valueOf(middleLetters.charAt(randomLetter.nextInt(middleLetters.length())));
        } else {
            selectedLetter = valueOf(hardLetters.charAt(randomLetter.nextInt(hardLetters.length())));
        }
        //display letter
        TextView selectedLetterText = (TextView) findViewById(R.id.letter_to_play);
        selectedLetterText.setText(getString(R.string.letter_to_play) + " " + selectedLetter);
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

    //calculate score from time elapsed: each minute reduces score of 10 by one point, max 10 points possible.
    private int timeScore() {
        int scoreFromTime = 0;
        //get Time elapsed and convert into score (each minute used reduces score of 10)
        long saveTime = elapsedRealtime() - chronometer.getBase();
        int timeElapsed = (int)(saveTime/1000);
        if(timeElapsed / 60 < 10) {
            scoreFromTime = 10 - timeElapsed / 60;
        }
        return scoreFromTime;
    }

    //calculate score from correct fields * difficulty * score from Time
    private void calculateScore() {
        int difficulty = difficultyFromBundle();                    //get difficulty info from activity
        int correctFieldsCount = compareTableFieldsWithGeoNames();  //get correct fields count
        int score = timeScore() * difficulty * correctFieldsCount;  //calculate score
        //display score
        String showScore = valueOf(score);
        TextView scoreText = (TextView) findViewById(R.id.your_score_is);
        if(score <= 30){
            scoreText.setText(getString(R.string.your_score_is) + " " + showScore + " points.\n"
                + "Try again!");
        } else if(30 < score && score <= 80) {
            scoreText.setText(getString(R.string.your_score_is) + " " + showScore + " points.\n"
                    + "Not bad - try again!");
        } else if (score > 80) {
            scoreText.setText(getString(R.string.your_score_is) + " " + showScore + " points.\n"
                    + "You are a true expert!");
        } else
            scoreText.setText("Try again!");

        Log.i("Value is: ", valueOf(correctFieldsCount)); //todo remove
        //Log.i("geo ", geoNamesString); //todo remove
    }
}