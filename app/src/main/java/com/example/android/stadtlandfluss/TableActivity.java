package com.example.android.stadtlandfluss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

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

    //Handle Toolbar Icon click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.table:
                //Go back to main activity
                Intent intent = new Intent(TableActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    //todo handover result to main acitivity
    //Provide fields to play in table based on boxes checked
    private void setupTableToPlay() {
        //Each field to play (column) is made visible/gone depending on checkbox, here: "city"
        CheckBox cityCheckBox = (CheckBox) findViewById(R.id.play_checkbox_city);
        boolean citySelected = cityCheckBox.isChecked();
        TextView cityTableHead = (TextView) findViewById(R.id.table_head_city);
        TextView cityTableField = (TextView) findViewById(R.id.edit_text_city);
        if (citySelected == true) {
            cityTableHead.setText(R.string.checkbox_city);
            cityTableHead.setVisibility(View.VISIBLE);
            cityTableField.setVisibility(View.VISIBLE);
        } else {
            cityTableHead.setVisibility(View.GONE);
            cityTableField.setVisibility(View.GONE);
        }

        //here: "country"
        CheckBox countryCheckBox = (CheckBox) findViewById(R.id.play_checkbox_country);
        boolean countrySelected = countryCheckBox.isChecked();
        TextView countryTableHead = (TextView) findViewById(R.id.table_head_country);
        TextView countryTableField = (TextView) findViewById(R.id.edit_text_country);
        if (countrySelected == true) {
            countryTableHead.setText(R.string.checkbox_country);
            countryTableHead.setVisibility(View.VISIBLE);
            countryTableField.setVisibility(View.VISIBLE);
        } else {
            countryTableHead.setVisibility(View.GONE);
            countryTableField.setVisibility(View.GONE);
        }

        //here: "river"
        CheckBox riverCheckBox = (CheckBox) findViewById(R.id.play_checkbox_river);
        boolean riverSelected = riverCheckBox.isChecked();
        TextView riverTableHead = (TextView) findViewById(R.id.table_head_river);
        TextView riverTableField = (TextView) findViewById(R.id.edit_text_river);
        if (riverSelected == true) {
            riverTableHead.setText(R.string.checkbox_river);
            riverTableHead.setVisibility(View.VISIBLE);
            riverTableField.setVisibility(View.VISIBLE);
        } else {
            riverTableHead.setVisibility(View.GONE);
            riverTableField.setVisibility(View.GONE);
        }

        //here: "mountain"
        CheckBox mountainCheckBox = (CheckBox) findViewById(R.id.play_checkbox_mountain);
        boolean mountainSelected = mountainCheckBox.isChecked();
        TextView mountainTableHead = (TextView) findViewById(R.id.table_head_mountain);
        TextView mountainTableField = (TextView) findViewById(R.id.edit_text_mountain);
        if (mountainSelected == true) {
            mountainTableHead.setText(R.string.checkbox_mountain);
            mountainTableHead.setVisibility(View.VISIBLE);
            mountainTableField.setVisibility(View.VISIBLE);
        } else {
            mountainTableHead.setVisibility(View.GONE);
            mountainTableField.setVisibility(View.GONE);
        }
    }


}
