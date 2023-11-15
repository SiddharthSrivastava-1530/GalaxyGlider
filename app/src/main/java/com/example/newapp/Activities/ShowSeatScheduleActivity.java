package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.newapp.R;

public class ShowSeatScheduleActivity extends AppCompatActivity {

    private TextView[] slots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat_schedule);

        getSupportActionBar().hide();

        // Initialize your TextView array
        slots = new TextView[]{
                findViewById(R.id.slot03_show),
                findViewById(R.id.slot36_show),
                findViewById(R.id.slot69_show),
                findViewById(R.id.slot9_12_show),
                findViewById(R.id.slot12_15_show),
                findViewById(R.id.slot15_18_show),
                findViewById(R.id.slot18_21_show),
                findViewById(R.id.slot21_24_show)
        };


        // Set up click listeners for all TextViews
        for (TextView slot : slots) {
            slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(slot);
                }
            });
        }
    }
    private void clicked(TextView textView) {
//        textView.setText("12"); //Set seat available text here after fetching from firebase.
//
//        //Each textview will show this intent on clicking but will have different seat configurations
//        Intent intent = new Intent(ShowSeatScheduleActivity.this,ShowSeatConfigurationActivity.class);
//        startActivity(intent);
    }
}