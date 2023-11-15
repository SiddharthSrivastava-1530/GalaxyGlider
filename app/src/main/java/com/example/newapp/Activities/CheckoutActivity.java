package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {
    private TextView bookRideButton;
    private String userEmail;
    private String userName;
    private String name;
    private String spaceShipRating;
    private String description;
    private String seats;
    private String price;
    private float speed;
    private String busyTime;
    private String distance;

    private String departure;

    private String destination;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;
    private String services;
    private String chosenSeatConfig;
    private ArrayList<Review> reviews;
    private String updatedSeatsConfiguration;
    private String spaceShipId;
    private String companyName;
    private SpaceShip currentSpaceShip;
    private ArrayList<Transaction> transactionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        getSupportActionBar().hide();

        bookRideButton = findViewById(R.id.bookRideButton);

        // getting data passed by intent
        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        spaceShipId = intent.getStringExtra("id_ss");
        spaceShipRating = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss", 0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        services = intent.getStringExtra("services_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");
        chosenSeatConfig = intent.getStringExtra("chosen_seat_config");
        distance = intent.getStringExtra("dist");
        departure = intent.getStringExtra("dept");
        destination = intent.getStringExtra("dest");

        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CheckoutActivity.this, UserReviewsActivity.class);
//                intent.putExtra("from", fromLocation.getText().toString());
//                intent.putExtra("to", toLocation.getText().toString());
//                intent.putExtra("date", date.getText().toString());
//                intent.putExtra("time", time.getText().toString());
//                startPayment();
                String refId = UUID.randomUUID().toString();
                onPaymentSuccess(refId);
            }
        });

        getUserData();
        getCompanyName();

    }



    private float calculateFair(int countOfGliders) {
        float journeyDistance = Float.parseFloat(distance);
        float basePay = 1000;
        float pricePerLY = Float.parseFloat(price);
        float serviceCharges = 20;
        float totalCost = (countOfGliders/100.0f)*basePay+pricePerLY*journeyDistance+serviceCharges;
        return totalCost;
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_YEx4Fc8oJfPIUu");
        checkout.setImage(R.drawable.checkout_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            String amount = "6";
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
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }




    @Override
    public void onPaymentSuccess(String s) {
//        Checkout.clearUserData(this);
        updateSeats(s);
        Log.d("onSUCCESS", "onPaymentSuccess: " + s);
    }




    // if payment fails redirect user to current activity.
    @Override
    public void onPaymentError(int i, String s) {
        Log.d("onERROR", "onPaymentError: " + s);
    }





    /* update the realtime seat availability and match it with chosenSeatConfiguration of user
     for checking seat availability */
    private void updateSeats(String refId) {

        attachSeatsListener();

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                .child(companyId).child("spaceShips");

        // Fetch the existing SpaceShips
        companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                int index = -1, counter = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                        SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                        if (spaceShip != null) {
                            spaceShipArrayList.add(spaceShip);
                            if (spaceShip.getSpaceShipId().equals(spaceShipId)) {
                                index = counter;
                                currentSpaceShip = spaceShip;
                            }
                            counter++;
                        }
                    }
                }


                // if seats are available update the seats in currentSpaceShip object.
                if (updateSeatsConfiguration()) {
                    currentSpaceShip.setSeatsAvailable(updatedSeatsConfiguration);
                } else {
                    Toast.makeText(CheckoutActivity.this, "Chosen configuration unavailable at moment. Please retry", Toast.LENGTH_SHORT).show();
                }


                // Update the spaceShipArrayList by adding update currentSpaceShip object.
                if (index != -1) {
                    spaceShipArrayList.set(index, currentSpaceShip);
                    Toast.makeText(CheckoutActivity.this, "Your seats are booked. Enjoy the journey",
                            Toast.LENGTH_SHORT).show();
                }

                // Set the updated spaceShips back to the company reference and sending intent to
                // journey activity on completion of task.
                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CheckoutActivity.this, "Seats confirmed", Toast.LENGTH_SHORT).show();


                        // Set this ride as an ongoing transaction object in database in users node.
                        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if(transactionArrayList == null){
                            transactionArrayList = new ArrayList<>();
                        }

                        transactionArrayList.add(new Transaction(userUID,userName,userEmail,refId,companyId,companyName,
                                spaceShipId, name, chosenSeatConfig, departure,destination,
                                distance, System.currentTimeMillis(),
                                235667,false));

                        FirebaseDatabase.getInstance().getReference("users/" + userUID + "/transactions")
                                .setValue(transactionArrayList);


                        // Start the journey and move further to review activity.
                        Intent intent1 = new Intent(CheckoutActivity.this, UserReviewsActivity.class);
                        intent1.putExtra("name_ss", name);
                        intent1.putExtra("id_ss", spaceShipId);
                        intent1.putExtra("description_ss", description);
                        intent1.putExtra("price_ss", price);
                        intent1.putExtra("rating_ss", spaceShipRating);
                        intent1.putExtra("speed_ss", speed);
                        intent1.putExtra("busyTime_ss", busyTime);
                        intent1.putExtra("seats_ss", seats);
                        intent1.putExtra("shared_ride_ss", haveSharedRide);
                        intent1.putExtra("loginMode", loginMode);
                        intent1.putExtra("companyID", companyId);
                        intent1.putExtra("update_spaceship", false);
                        intent1.putExtra("chosen_seat_config", chosenSeatConfig);
                        intent1.putExtra("reviews_ss", reviews);
                        intent1.putExtra("refId", refId);
                        intent1.putExtra("source", departure);
                        intent1.putExtra("destination", destination);
                        intent1.putExtra("distance", distance);
                        startActivity(intent1);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    // update the new seat configuration if its available
    // if not available return false and retain the original seat configuration.
    private boolean updateSeatsConfiguration() {
        String copy = updatedSeatsConfiguration;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                if (seats.charAt(position) == '1') {
                    updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '0');
                } else {
                    updatedSeatsConfiguration = copy;
                    return false;
                }
            } else {
                char character = seats.charAt(position);
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, character);
            }
        }
        return true;
    }


    // set given character at specific position of string.
    private String setCharAt(String services, int position, char ch) {
        char[] charArray = services.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }



    // updates the seat dynamically on any update in seats configuration data (in realtime).
    private void attachSeatsListener() {

        try {
            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(companyId).child("spaceShips");

            // Fetching the existing spaceShips from specific database reference.
            companyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.getSpaceShipId().equals(spaceShipId)) {
                                seats = spaceShip.getSeatsAvailable();
                                updatedSeatsConfiguration = seats;
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void getCompanyName(){

        FirebaseDatabase.getInstance().getReference("company/" + companyId )
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        companyName = snapshot.getValue(Company.class).getName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getUserData(){
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.getValue(Company.class).getName();
                        userEmail = snapshot.getValue(Company.class).getEmail();
                        transactionArrayList = snapshot.getValue(Customer.class).getTransactions();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}