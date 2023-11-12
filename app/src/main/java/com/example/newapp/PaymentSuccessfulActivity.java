package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

import java.util.Objects;

public class PaymentSuccessfulActivity extends AppCompatActivity {
    private TextView referenceID,review ;
    private RatingBar ratingbar;
    private Button Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ratingbar = (RatingBar) findViewById(R.id.ratingBar);
        Submit = findViewById(R.id.SubmitButton);
        review = findViewById(R.id.ReviewText);
        referenceID = findViewById(R.id.ReferenceId);

        // store all these to DataBase
        String reviewText = review.getText().toString();
        String rating = String.valueOf(ratingbar.getRating());
        String id = getIntent().getStringExtra("refId");


        referenceID.setText("Payment Successful :"+ id) ;


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PaymentSuccessfulActivity.this,SpaceShipList.class);
                review.setText("");
                ratingbar.setRating(0.0f);
                startActivity(intent);
            }
        });

    }
}