package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        //Set Toolbar (see menu file)
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    // Menu icons in toolbar are inflated just as with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goback, menu);
        return true;
    }

    //Handle Toolbar Icon click events, hand over Bundle containing difficulty to main acitivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.goback:
                //Go back to main activity and hand over difficulty info
                Bundle letterBundle = selectLetter();
                Intent intent = new Intent(DifficultyActivity.this, MainActivity.class);
                intent.putExtras(letterBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //todo keep selection when switching activities
    //get the difficulty group selected, random select letter, and hand over to Toolbar Click-function above
    private Bundle selectLetter() {
        //get selected difficulty
        int difficulty = 1;
        //check, which difficulty is selected (radio button) and set difficulty accordingly
        RadioGroup selectDifficultyRadioGroup = (RadioGroup) findViewById(R.id.radio_group_select_difficulty);
        int checkedId = selectDifficultyRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radiobutton_easy) {
            difficulty = 1;
        } else if (checkedId == R.id.radiobutton_middle) {
            difficulty = 2;
        } else {
            difficulty = 3;
        }
        //Put Letter and Difficulty into Bundle for hand over to main activity
        Bundle letterBundle = new Bundle();
        letterBundle.putInt("Difficulty", difficulty);
        return letterBundle;
    }




}


