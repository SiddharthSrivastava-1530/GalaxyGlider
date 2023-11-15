package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserTransactionDetailsActivity extends AppCompatActivity {

    private TextView reviews_et;
    private TextView reviews_tv;
    private TextView submitReview_tv;
    private RatingBar ratingBar;
    private TextView companyNameTextView;
    private TextView spaceShipNameTextView;
    private TextView transactionIdTextView;
    private TextView fromTextView;
    private TextView seatsConfig;
    private TextView toTextView;
    private TextView distanceTextView;
    private TextView totalCostTextView;
    private TextView timeTextView;
    private TextView isTransactionComplete_tv;
    private TextView completeJourneyTextView;
    private Transaction currentTransaction;
    private SpaceShip transactionSpaceShip;
    private String chosenSeatConfig;
    private String currentSeatConfiguration;
    private ArrayList<Transaction> transactionArrayList;
    private Float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_details);

        companyNameTextView = findViewById(R.id.companyName_transaction_details);
        spaceShipNameTextView = findViewById(R.id.spaceShipName_transaction_details);
        fromTextView = findViewById(R.id.from_transaction_details);
        totalCostTextView = findViewById(R.id.price_transaction_details);
        toTextView = findViewById(R.id.to_transaction_details);
        distanceTextView = findViewById(R.id.distance_transaction_details);
        transactionIdTextView = findViewById(R.id.transactionId_transaction_details);
        timeTextView = findViewById(R.id.time_transaction_details);
        isTransactionComplete_tv = findViewById(R.id.isOngoing_transaction_details);
        completeJourneyTextView = findViewById(R.id.complete_transaction_details);
        seatsConfig = findViewById(R.id.seatsChosen_transaction_details);
        reviews_et = findViewById(R.id.user_review_et);
        reviews_tv = findViewById(R.id.user_review_tv);
        ratingBar = findViewById(R.id.ratingBar);
        submitReview_tv = findViewById(R.id.submit_review_tv);

        Intent intent = getIntent();
        currentTransaction = (Transaction) intent.getSerializableExtra("transaction");
        chosenSeatConfig = currentTransaction.getChosenSeatConfiguration();
        transactionArrayList = new ArrayList<>();

        setDataViews();
        attachSeatsListener();

        if (currentTransaction.isTransactionComplete()) {
            completeJourneyTextView.setVisibility(View.GONE);
            if(currentTransaction.getReview().getTime()==0){
                reviews_tv.setVisibility(View.GONE);
            } else {
                reviews_et.setVisibility(View.GONE);
                submitReview_tv.setVisibility(View.GONE);
            }
        } else {
            reviews_et.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            submitReview_tv.setVisibility(View.GONE);
            reviews_tv.setVisibility(View.GONE);
        }

        completeJourneyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeats();
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float chosenRating, boolean fromUser) {
                rating = chosenRating;
            }
        });

        submitReview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReviews();
            }
        });


    }

    private void setDataViews() {

        spaceShipNameTextView.setText(currentTransaction.getSpaceShipName());
        companyNameTextView.setText(currentTransaction.getCompanyName());
        fromTextView.setText(currentTransaction.getDeparture());
        toTextView.setText(currentTransaction.getDestination());
        distanceTextView.setText(currentTransaction.getDistance());
        totalCostTextView.setText(String.valueOf(currentTransaction.getTotalFare()));
        transactionIdTextView.setText(currentTransaction.getTransactionId());
        timeTextView.setText(String.valueOf(currentTransaction.getTransactionTime()));
        isTransactionComplete_tv.setText(String.valueOf(currentTransaction.isTransactionComplete()));
        seatsConfig.setText(currentTransaction.getChosenSeatConfiguration());
        if(currentTransaction.getReview().getTime() > 0) {
            ratingBar.setRating(Float.parseFloat(currentTransaction.getReview().getRating()));
            reviews_tv.setText(currentTransaction.getReview().getReview());
        }

    }


    // vacate the seats and update it on database.
    private void updateSeats() {

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                .child(currentTransaction.getCompanyId()).child("spaceShips");

        // Fetch the existing spaceShips
        companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                        SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                        if (spaceShip != null) {
                            if (spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                // set updatedSeatConfiguration after seats have been vacated.
                                transactionSpaceShip = spaceShip;
                                setSeatsVacated();
                                spaceShipArrayList.add(transactionSpaceShip);
                            } else {
                                spaceShipArrayList.add(spaceShip);
                            }
                        }
                    }
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateTransactionStatus();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateTransactionStatus() {

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions")
                    .child(currentTransaction.getTransactionId());

            databaseReference.child("transactionComplete").setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            completeJourneyTextView.setVisibility(View.GONE);
                            Intent intent1 = new Intent(UserTransactionDetailsActivity.this, AllTransactionsList.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    private void updateReviews() {

        updateSpaceShipRating();

        try {
            String reviewString = "";
            if (reviews_et != null) {
                reviewString = reviews_et.getText().toString();
            }

            Review newReview = new Review(reviewString, String.valueOf(rating), currentTransaction.getUserName(), currentTransaction.getUserEmail(),
                    System.currentTimeMillis());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions")
                    .child(currentTransaction.getTransactionId());

            databaseReference.child("review").setValue(newReview)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent1 = new Intent(UserTransactionDetailsActivity.this, AllTransactionsList.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    // get the changes seat configuration by vacating seat chosen seats
    private String getChangedSeatConfig() {
        String updatedSeatsConfiguration = currentSeatConfiguration;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '1');
            } else {
                char character = currentSeatConfiguration.charAt(position);
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, character);
            }
        }
        return updatedSeatsConfiguration;
    }


    // set character at given index in the string
    private String setCharAt(String str, int i, char ch) {
        char[] charArray = str.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }


    // fetch the updates in seat configuration in realtime.
    private void attachSeatsListener() {

        try {
            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(currentTransaction.getCompanyId()).child("spaceShips");

            companyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && currentTransaction.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                transactionSpaceShip = spaceShip;
                                getSlotConfiguration();
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


    private void getSlotConfiguration() {

        if (currentTransaction.getSlotNo().equals("0")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot1();
        } else if (currentTransaction.getSlotNo().equals("1")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot2();
        } else if (currentTransaction.getSlotNo().equals("2")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot3();
        } else if (currentTransaction.getSlotNo().equals("3")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot4();
        } else if (currentTransaction.getSlotNo().equals("4")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot5();
        } else if (currentTransaction.getSlotNo().equals("5")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot6();
        } else if (currentTransaction.getSlotNo().equals("6")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot7();
        } else if (currentTransaction.getSlotNo().equals("7")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot8();
        }

    }


    private void setSeatsVacated() {

        if (currentTransaction.getSlotNo().equals("0")) {
            transactionSpaceShip.setSlot1(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("1")) {
            transactionSpaceShip.setSlot2(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("2")) {
            transactionSpaceShip.setSlot3(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("3")) {
            transactionSpaceShip.setSlot4(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("4")) {
            transactionSpaceShip.setSlot5(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("5")) {
            transactionSpaceShip.setSlot6(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("6")) {
            transactionSpaceShip.setSlot7(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("7")) {
            transactionSpaceShip.setSlot8(getChangedSeatConfig());
        }

    }


    private String updatedCompanyRating(SpaceShip currentSpaceShip) {
        float reviewCount = 0;
//        if(currentSpaceShip.getReviews() != null) {
//            reviewCount  = currentSpaceShip.getReviews().size();
//        }
        float currentRating = Float.parseFloat(currentSpaceShip.getSpaceShipRating());
        return String.valueOf(((currentRating * reviewCount) + rating) / (reviewCount + 1));
    }


    private void updateSpaceShipRating() {


        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId() + "spaceShips")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                spaceShip.setSpaceShipRating(updatedCompanyRating(spaceShip));
                            }
                            spaceShipArrayList.add(spaceShip);
                        }

                        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId()
                                + "spaceShips")
                                .setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}