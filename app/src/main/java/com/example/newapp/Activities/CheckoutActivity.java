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

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {
    private EditText fromLocation, toLocation, distance;
    private TextView bookRideButton;
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
    private String services;
    private String chosenSeatConfig;
    private ArrayList<Review> reviews;
    private String updatedSeatsConfiguration;
    private String spaceShipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        getSupportActionBar().hide();

        bookRideButton = findViewById(R.id.bookRideButton);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        distance = findViewById(R.id.distance_journey_et);

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

        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CheckoutActivity.this, UserReviewsActivity.class);
//                intent.putExtra("from", fromLocation.getText().toString());
//                intent.putExtra("to", toLocation.getText().toString());
//                intent.putExtra("date", date.getText().toString());
//                intent.putExtra("time", time.getText().toString());
//                startPayment();
                onPaymentSuccess("h2njdfk456iowGFbjsvd");
            }
        });
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
        if (checkData()) {
            updateSeats(s);
        }
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

        // creating current SpaceShip object using available data
        SpaceShip currentSpaceShip = new SpaceShip(name, description, spaceShipId, spaceShipRating, seats, services,
                haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

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
                            if (currentSpaceShip.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                index = counter;
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
                        Intent intent1 = new Intent(CheckoutActivity.this, JourneyActivity.class);
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
                        intent1.putExtra("source", fromLocation.getText().toString());
                        intent1.putExtra("destination", toLocation.getText().toString());
                        intent1.putExtra("distance", distance.getText().toString());
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
            // create the original spaceShip object using the original data.
            SpaceShip originalSpaceShip = new SpaceShip(name, description, spaceShipId, spaceShipRating, seats,
                    services, haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(companyId).child("spaceShips");

            // Fetching the existing spaceShips from specific database reference.
            companyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                    int index = -1, counter = 0;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null) {
                                spaceShipArrayList.add(spaceShip);
                                if (spaceShip.getSpaceShipId().equals(originalSpaceShip.getSpaceShipId())) {
                                    index = counter;
                                }
                                counter++;
                            }
                        }
                    }

                    // getting the updated seat configuration
                    try {
                        if (index != -1) {
                            seats = spaceShipArrayList.get(index).getSeatsAvailable();
                            updatedSeatsConfiguration = seats;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(CheckoutActivity.this, "Data not updated. Please retry",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // checking if data not filled or not, if not return false.
    private boolean checkData() {
        if (TextUtils.isEmpty(fromLocation.getText().toString())) {
            Toast.makeText(this, "Please enter source", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(toLocation.getText().toString())) {
            Toast.makeText(this, "Please enter destination", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(distance.getText().toString())) {
            Toast.makeText(this, "Please enter approximated distance", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}