package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;


public class TableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
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

    //Handle Toolbar Icon click events, hand over Bundle containing table information to main acitivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.goback:
                //Go back to main activity and hand over checked boxes info
                Bundle checkedBoxes = findBoxesChecked();
                Intent intent = new Intent(TableActivity.this, MainActivity.class);
                intent.putExtras(checkedBoxes);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //todo keep selection when switching activities
    //Identify checked boxes and hand over to Toolbar click-function above
    private Bundle findBoxesChecked() {
        Bundle checkedBoxes = new Bundle();
        CheckBox cityCheckBox = (CheckBox) findViewById(R.id.play_checkbox_city);
        boolean citySelected = cityCheckBox.isChecked();
        checkedBoxes.putBoolean("City", citySelected);
        CheckBox countryCheckBox = (CheckBox) findViewById(R.id.play_checkbox_country);
        boolean countrySelected = countryCheckBox.isChecked();
        checkedBoxes.putBoolean("Country", countrySelected);
        CheckBox riverCheckBox = (CheckBox) findViewById(R.id.play_checkbox_river);
        boolean riverSelected = riverCheckBox.isChecked();
        checkedBoxes.putBoolean("River", riverSelected);
        CheckBox mountainCheckBox = (CheckBox) findViewById(R.id.play_checkbox_mountain);
        boolean mountainSelected = mountainCheckBox.isChecked();
        checkedBoxes.putBoolean("Mountain", mountainSelected);
        return checkedBoxes;
    }
}
