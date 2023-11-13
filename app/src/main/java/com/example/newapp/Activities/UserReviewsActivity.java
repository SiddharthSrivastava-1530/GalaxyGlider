package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.InvoiceActivity;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserReviewsActivity extends AppCompatActivity {

    private TextView submitReview_tv;
    private RatingBar ratingBar;
    private float rating;
    private EditText reviews_et;
    private String userName;
    private String userEmail;
    private String name;
    private String spaceShipRating;
    private String description;
    private String seats;
    private String chosenSeatConfig;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

        submitReview_tv = findViewById(R.id.submit_review_tv);
        reviews_et = findViewById(R.id.user_review_et);
        ratingBar = findViewById(R.id.ratingBar);

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
        refId = intent.getStringExtra("refId");
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");
        distance = intent.getStringExtra("distance");

        if(reviews == null) {
            reviews = new ArrayList<>();
        }

        getUserData();

        submitReview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReviews();
                Intent intent1 = new Intent(UserReviewsActivity.this, InvoiceActivity.class);
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
                intent1.putExtra("refId", refId);
                intent1.putExtra("source",source);
                intent1.putExtra("destination", destination);
                intent1.putExtra("distance", distance);
                startActivity(intent1);
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float chosenRating, boolean fromUser) {
                rating = chosenRating;
            }
        });

    }



    private void updateReviews() {

        SpaceShip currentSpaceShip = new SpaceShip(name, description, "", spaceShipRating, seats,services,
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

                String reviewText = "";
                if(reviews_et != null){
                    reviewText = reviews_et.getText().toString();
                }

                currentSpaceShip.setSpaceShipRating(updateCompanyRating(currentSpaceShip));
                // update reviews in current spaceShip
                reviews.add(new Review(reviewText,String.valueOf(rating),userName,userEmail,System.currentTimeMillis()));
                currentSpaceShip.setReviews(reviews);

                // Update the spaceShip you want to delete
                try {
                    spaceShipArrayList.set(index, currentSpaceShip);
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(UserReviewsActivity.this, "Data not updated. Please retry",
                            Toast.LENGTH_SHORT).show();
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserReviewsActivity.this, "Your review have been added. " +
                                "Thanks for reviewing", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private String updateCompanyRating(SpaceShip currentSpaceShip) {
        float reviewCount = currentSpaceShip.getReviews().size();
        float currentRating = Float.parseFloat(currentSpaceShip.getSpaceShipRating());
        return String.valueOf(((currentRating * reviewCount) + rating)/(reviewCount + 1));
    }


    private Boolean areEqualSpaceShips(SpaceShip spaceShip1, SpaceShip spaceShip2) {
        if (!(spaceShip1.getSpaceShipName().equals(spaceShip2.getSpaceShipName()))) {
            return false;
        }
        if (!(spaceShip1.getSpaceShipId().equals(spaceShip2.getSpaceShipId()))) {
            return false;
        }
        if (!(spaceShip1.getBusyTime()==spaceShip2.getBusyTime())) {
            return false;
        }
        if (!(spaceShip1.getPrice()==spaceShip2.getPrice())) {
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


    // Getting data about user from database.
    private void getUserData() {
        try {
            FirebaseDatabase.getInstance().getReference("users/" +
                            FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userName = snapshot.getValue(Customer.class).getName();
                            userEmail = snapshot.getValue(Customer.class).getEmail();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(UserReviewsActivity.this, "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }
    }


}