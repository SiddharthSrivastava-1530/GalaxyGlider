package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {
    EditText fromLocation,toLocation,date,time;
    Button bookRideButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        bookRideButton = findViewById(R.id.bookRideButton);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        date = findViewById(R.id.dateOfRide);
        time = findViewById(R.id.timeOfRide);

        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this,PaymentSuccessfulActivity.class);
                intent.putExtra("from",fromLocation.getText().toString());
                intent.putExtra("to",toLocation.getText().toString());
                intent.putExtra("date",date.getText().toString());
                intent.putExtra("time",time.getText().toString());
                startActivity(intent);
            }
        });

    }
}