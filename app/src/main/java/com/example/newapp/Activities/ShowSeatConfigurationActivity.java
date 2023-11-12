package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.newapp.R;

public class ShowSeatConfigurationActivity extends AppCompatActivity {

    private TextView seat1;
    private TextView seat2;
    private TextView seat3;
    private TextView seat4;
    private TextView seat5;
    private TextView seat6;
    private TextView seat7;
    private TextView seat8;
    private TextView seat9;
    private TextView seat10;
    private TextView seat11;
    private TextView seat12;

    private TextView seat1_trans;
    private TextView seat2_trans;
    private TextView seat3_trans;
    private TextView seat4_trans;
    private TextView seat5_trans;
    private TextView seat6_trans;
    private TextView seat7_trans;
    private TextView seat8_trans;
    private TextView seat9_trans;
    private TextView seat10_trans;
    private TextView seat11_trans;
    private TextView seat12_trans;

    private TextView music;

    private TextView music_not;
    private TextView sleep;
    private TextView sleep_not;
    private TextView fitness;

    private TextView fitness_not;
    private TextView food;
    private TextView food_not;

    private TextView confirm_seats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat_configuration);

        seat1 = findViewById(R.id.seat1_show);
        seat2 = findViewById(R.id.seat2_show);
        seat3 = findViewById(R.id.seat3_show);
        seat4 = findViewById(R.id.seat4_show);
        seat5 = findViewById(R.id.seat5_show);
        seat6 = findViewById(R.id.seat6_show);
        seat7 = findViewById(R.id.seat7_show);
        seat8 = findViewById(R.id.seat8_show);
        seat9 = findViewById(R.id.seat9_show);
        seat10 = findViewById(R.id.seat10_show);
        seat11 = findViewById(R.id.seat11_show);
        seat12 = findViewById(R.id.seat12_show);

        seat1_trans = findViewById(R.id.seat1_show_trans);
        seat2_trans = findViewById(R.id.seat2_show_trans);
        seat3_trans = findViewById(R.id.seat3_show_trans);
        seat4_trans = findViewById(R.id.seat4_show_trans);
        seat5_trans = findViewById(R.id.seat5_show_trans);
        seat6_trans = findViewById(R.id.seat6_show_trans);
        seat7_trans = findViewById(R.id.seat7_show_trans);
        seat8_trans = findViewById(R.id.seat8_show_trans);
        seat9_trans = findViewById(R.id.seat9_show_trans);
        seat10_trans = findViewById(R.id.seat10_show_trans);
        seat11_trans = findViewById(R.id.seat11_show_trans);
        seat12_trans = findViewById(R.id.seat12_show_trans);

        music = findViewById(R.id.music_tv_show);
        music_not = findViewById(R.id.music_not_tv_show);

        sleep = findViewById(R.id.sleep_tv_show);
        sleep_not = findViewById(R.id.sleep_not_tv_show);

        food = findViewById(R.id.food_tv_show);
        food_not = findViewById(R.id.food_not_tv_show);

        fitness = findViewById(R.id.fitness_tv_show);
        fitness_not = findViewById(R.id.fitness_not_tv_show);

        confirm_seats = findViewById(R.id.confirm_seats);

        confirm_seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowSeatConfigurationActivity.this, PaymentSuccessfulActivity.class);
                startActivity(intent);
            }
        });

    }
}