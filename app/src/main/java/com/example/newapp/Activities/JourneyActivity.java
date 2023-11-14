package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JourneyActivity extends AppCompatActivity {

    private TextView journeyCompletedTextView;
    private String name;
    private String spaceShipRating;
    private String description;
    private String seats;
    private String services;
    private String price;
    private float speed;
    private String busyTime;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;
    private String refId;
    private ArrayList<Review> reviews;
    private String source;
    private String destination;
    private String distance;
    private String updatedSeatsConfiguration;
    private String chosenSeatConfig;
    private String spaceShipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        journeyCompletedTextView = findViewById(R.id.journey_completed_tv);

        // getting data from intent
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
        refId = intent.getStringExtra("refId");
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");
        distance = intent.getStringExtra("distance");

        updatedSeatsConfiguration = seats;

        // update changes in seatConfiguration in realtime.
        attachSeatsListener();

        // if journey completed vacate the seats and update it on database
        journeyCompletedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeats();
            }
        });

    }


    @Override
    public void onBackPressed() {

    }


    // vacate the seats and update it on database.
    private void updateSeats() {

        SpaceShip currentSpaceShip = new SpaceShip(name, description, spaceShipId, spaceShipRating, seats, services,
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
                            if (currentSpaceShip.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                index = counter;
                            }
                            counter++;
                        }
                    }
                }

                // set updatedSeatConfiguration after seats have been vacated.
                getChangedSeatConfig();

                currentSpaceShip.setSeatsAvailable(updatedSeatsConfiguration);


                // Update the spaceShip
                if (index != -1) {
                    spaceShipArrayList.set(index, currentSpaceShip);
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent1 = new Intent(JourneyActivity.this, UserReviewsActivity.class);
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
                        intent1.putExtra("reviews_ss", reviews);
                        intent1.putExtra("refId", refId);
                        intent1.putExtra("source", source);
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


    // set updatedSeatConfiguration after seats have been vacated.
    private void getChangedSeatConfig() {

        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '1');
            } else {
                char character = seats.charAt(position);
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, character);
            }
        }
    }

    // set character at given index in the string
    private String setCharAt(String services, int i, char ch) {
        char[] charArray = services.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }


    // fetch the updates in seat configuration in realtime.
    private void attachSeatsListener() {

        try {
            SpaceShip originalSpaceShip = new SpaceShip(name, description, spaceShipId, spaceShipRating, seats,
                    services, haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(companyId).child("spaceShips");


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
                                if (originalSpaceShip.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                    index = counter;
                                }
                                counter++;
                            }
                        }
                    }

                    // Update the data changes in variables
                    try {
                        if (index != -1) {
                            seats = spaceShipArrayList.get(index).getSeatsAvailable();
                            updatedSeatsConfiguration = seats;
                            reviews = spaceShipArrayList.get(index).getReviews();
                            spaceShipRating = spaceShipArrayList.get(index).getSpaceShipRating();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(JourneyActivity.this, "Data not updated. Please retry",
                                Toast.LENGTH_SHORT).show();
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


    // showing confirmation dialog to user onBackPress.
    private void showExitConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Exit the process");
        builder.setMessage("Are you sure you want to exit ");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(JourneyActivity.this, SpaceShipList.class);
                intent1.putExtra("loginMode", "user");
                intent1.putExtra("companyID", companyId);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}