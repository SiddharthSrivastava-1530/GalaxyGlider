package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.newapp.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private Button payButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        payButton = findViewById(R.id.pay_button);
        Checkout.preload(getApplicationContext());

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });
    }

    private void startPayment(){

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_q6UwqZNsqzlog3");
        checkout.setImage(R.drawable.checkout_logo);

        // Reference to current activity
        final Activity activity = PaymentActivity.this;


        //Pass your payment options to the Razorpay Checkout as a JSONObject

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Galaxy Glider");
            options.put("description", "description");
            // put order id here
            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("theme.color", "#000000");
            options.put("currency", "INR");
            options.put("amount", "6");//pass amount in currency subunits
            options.put("prefill.email", "as.nishu18@gmail.com");
            options.put("prefill.contact","8707279750");
            JSONObject retryObj = new JSONObject();

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s){
//        Checkout.clearUserData(this);
        Intent intent = new Intent(PaymentActivity.this,PaymentSuccessfulActivity.class);
        intent.putExtra("refId",s);
        startActivity(intent);
        Log.d("ONSUCCESS", "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.d("ONERROR", "onPaymentError: "+s);
    }
}