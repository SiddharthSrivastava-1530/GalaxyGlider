package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UserReviewsActivity extends AppCompatActivity {

    private TextView submitReview_tv;
    RatingBar ratingBar;
    public float rating;
    private EditText reviews_et;
    private String userName;
    private String userEmail;
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
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");

        if(reviews == null) {
            reviews = new ArrayList<>();
        }

        getUserData();

        submitReview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReviews();
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

        SpaceShip currentSpaceShip = new SpaceShip(name, description, "", spaceShipRating, seats,
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
                    Toast.makeText(UserReviewsActivity.this, "Data not updated. Please retry", Toast.LENGTH_SHORT).show();
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList);

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
        if (!(spaceShip1.getSpaceShipRating().equals(spaceShip2.getSpaceShipRating()))) {
            return false;
        }
        if (!(Objects.equals(spaceShip1.getSeatAvailability(), spaceShip2.getSeatAvailability()))) {
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