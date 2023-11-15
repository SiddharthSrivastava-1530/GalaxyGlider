package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.newapp.R;

public class SelectSlotsActivity extends AppCompatActivity {

    TextView confirm_time_slots;

    private TextView[] slots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_slots);

        getSupportActionBar().hide();

        // Initialize your TextView array
        slots = new TextView[]{
                findViewById(R.id.slot03),
                findViewById(R.id.slot36),
                findViewById(R.id.slot69),
                findViewById(R.id.slot9_12),
                findViewById(R.id.slot12_15),
                findViewById(R.id.slot15_18),
                findViewById(R.id.slot18_21),
                findViewById(R.id.slot21_24)
        };

        confirm_time_slots = findViewById(R.id.confirm_time_slots);

        // Set up click listeners for all TextViews
        for (TextView slot : slots) {
            slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleBackground(slot);
                }
            });
        }

        confirm_time_slots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void toggleBackground(TextView textView) {
        int currentBackgroundColor = ((ColorDrawable) textView.getBackground()).getColor();
        int targetColor = Color.parseColor("#FF1744");
        int newBackgroundColor = (currentBackgroundColor == targetColor) ? Color.GREEN : targetColor;

        // Set the new background color
        textView.setBackgroundColor(newBackgroundColor);
    }
}