package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        bookRideButton = findViewById(R.id.bookRideButton);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        distance = findViewById(R.id.distance_journey_et);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
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
        if(checkData()) {
            updateSeats();
            Intent intent1 = new Intent(CheckoutActivity.this, UserReviewsActivity.class);
            intent1.putExtra("name_ss", name);
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
            intent1.putExtra("reviews_ss", reviews);
            intent1.putExtra("refId", s);
            intent1.putExtra("source", fromLocation.getText().toString());
            intent1.putExtra("destination", toLocation.getText().toString());
            intent1.putExtra("distance", distance.getText().toString());
            startActivity(intent1);
        }
        Log.d("onSUCCESS", "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("onERROR", "onPaymentError: " + s);
    }

    private void updateSeats() {

        attachSeatsListener();

        SpaceShip currentSpaceShip = new SpaceShip(name, description, "", spaceShipRating, seats, services,
                haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                .child(companyId).child("spaceShips");

        // Fetch the existing spaceShips
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
                            if (areEqualSpaceShips(spaceShip, currentSpaceShip)) {
                                index = counter;
                            }
                            counter++;
                        }
                    }
                }


                if(updateSeatsConfiguration()) {
                    currentSpaceShip.setSeatsAvailable(updatedSeatsConfiguration);
                } else {
                    Toast.makeText(CheckoutActivity.this, "Chosen configuration unavailable at moment. Please retry", Toast.LENGTH_SHORT).show();
                }


                // Update the spaceShip
                try {
                    if (index != -1) {
                        spaceShipArrayList.set(index, currentSpaceShip);
                        Toast.makeText(CheckoutActivity.this, "Your seats are booked. Enjoy the journey",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(CheckoutActivity.this, "Your Seats have not been booked. Please retry",
                            Toast.LENGTH_SHORT).show();
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CheckoutActivity.this, "Seats confirmed", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    private Boolean areEqualSpaceShips(SpaceShip spaceShip1, SpaceShip spaceShip2) {
        if (!(spaceShip1.getSpaceShipName().equals(spaceShip2.getSpaceShipName()))) {
            return false;
        }
        if (!(spaceShip1.getSpaceShipId().equals(spaceShip2.getSpaceShipId()))) {
            return false;
        }
        if (!(spaceShip1.getBusyTime() == spaceShip2.getBusyTime())) {
            return false;
        }
        if (!(spaceShip1.getPrice() == spaceShip2.getPrice())) {
            return false;
        }
        if (!(spaceShip1.getDescription().equals(spaceShip2.getDescription()))) {
            return false;
        }
        if (!(spaceShip1.getSpeed() == spaceShip2.getSpeed())) {
            return false;
        }
        return true;
    }

    private boolean updateSeatsConfiguration() {
        String copy = updatedSeatsConfiguration;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1')
            {
                if (seats.charAt(position) == '1') {
                    updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '0');
                } else {
                    updatedSeatsConfiguration = copy;
                    return false;
                }
            } else
            {
                char character = seats.charAt(position);
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, character);
            }
        }
        return true;
    }

    private String setCharAt(String services, int position, char ch) {
        char[] charArray = services.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    private void attachSeatsListener() {

        try {
            SpaceShip originalSpaceShip = new SpaceShip(name, description, "", spaceShipRating, seats,
                    services, haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(companyId).child("spaceShips");

            // Fetch the existing spaceShips
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
                                if (areEqualSpaceShips(spaceShip, originalSpaceShip)) {
                                    index = counter;
                                }
                                counter++;
                            }
                        }
                    }

                    // Update the spaceShip you want to delete
                    try {
                        if (index != -1) {
                            seats = spaceShipArrayList.get(index).getSeatsAvailable();
                            updatedSeatsConfiguration = seats;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(CheckoutActivity.this, "Data not updated. Please retry", Toast.LENGTH_SHORT).show();
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

    private boolean checkData(){
        if(TextUtils.isEmpty(fromLocation.getText().toString())){
            Toast.makeText(this, "Please enter source", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(toLocation.getText().toString())){
            Toast.makeText(this, "Please enter destination", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(distance.getText().toString())){
            Toast.makeText(this, "Please enter approximated distance", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}