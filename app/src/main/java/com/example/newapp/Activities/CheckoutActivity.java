package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.newapp.DataModel.Review;
import com.example.newapp.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {
    private EditText fromLocation,toLocation,date,time;
    private Button bookRideButton;
    private String name;
    private String spaceShipRating;
    private String description;
    private String seats;
    private String price;
    private float speed;
    private String busyTime;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;
    private ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        bookRideButton = findViewById(R.id.bookRideButton);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        date = findViewById(R.id.dateOfRide);
        time = findViewById(R.id.timeOfRide);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        spaceShipRating = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss", 0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");

        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessfulActivity.class);
                intent.putExtra("from", fromLocation.getText().toString());
                intent.putExtra("to", toLocation.getText().toString());
                intent.putExtra("date", date.getText().toString());
                intent.putExtra("time", time.getText().toString());
                startPayment();
            }
        });
    }

    private void startPayment(){
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_YEx4Fc8oJfPIUu");
        checkout.setImage(R.drawable.checkout_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            String amount = "60000";
            amount = getIntent().getStringExtra("amt");
            options.put("name", "Galaxy Glider");
            options.put("description", "description");
            options.put("theme.color", "#000000");
            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("prefill.email", "as.nishu18@gmail.com");
//            options.put("prefill.contact","8707279750");
            JSONObject retryObj = new JSONObject();
            checkout.open(activity, options);
        }
        catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s){
//        Checkout.clearUserData(this);
        Intent intent = new Intent(CheckoutActivity.this,PaymentSuccessfulActivity.class);
        intent.putExtra("refId",s);
//        intent.putExtra("amt",getIntent().getStringExtra("amt"));
        startActivity(intent);
        Log.d("onSUCCESS", "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("onERROR", "onPaymentError: "+s);
    }

}