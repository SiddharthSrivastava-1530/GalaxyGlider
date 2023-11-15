package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private String spaceShipId;
    private SpaceShip currentSpaceShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);
        getSupportActionBar().hide();

        submitReview_tv = findViewById(R.id.submit_review_tv);
        reviews_et = findViewById(R.id.user_review_et);
        ratingBar = findViewById(R.id.ratingBar);

        reviews = new ArrayList<>();

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        spaceShipId = intent.getStringExtra("id_ss");
        spaceShipRating = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss", 0);
        busyTime = intent.getStringExtra("busyTime_ss");
        services = intent.getStringExtra("services_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
//        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");
        refId = intent.getStringExtra("refId");
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");
        distance = intent.getStringExtra("distance");


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


    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }



    // showing confirmation dialog to user onBackPress.
    private void showExitConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Exit the process!!");
        builder.setMessage("Are you sure you want to go back to spaceship Lists");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(UserReviewsActivity.this, AllSpaceShipsListActivity.class);
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



    private String updateCompanyRating(SpaceShip currentSpaceShip) {
        float reviewCount = 0;
        if(currentSpaceShip.getReviews() != null) {
            reviewCount  = currentSpaceShip.getReviews().size();
        }
        float currentRating = Float.parseFloat(currentSpaceShip.getSpaceShipRating());
        return String.valueOf(((currentRating * reviewCount) + rating)/(reviewCount + 1));
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



    private void updateReviews() {

        try {
            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(companyId).child("spaceShips");

            companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int index = 0, position = -1;
                    ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShipId.equals(spaceShip.getSpaceShipId())) {
                                reviews = spaceShip.getReviews();
                                currentSpaceShip = spaceShip;
                                position = index;
                            }
                            spaceShipArrayList.add(spaceShip);
                            index++;
                        }
                    }
                    String review = "";
                    if(reviews_et != null){
                        review = reviews_et.getText().toString();
                    }
                    if(reviews == null) reviews = new ArrayList<>();
                    reviews.add(new Review(review,updateCompanyRating(currentSpaceShip),userName,userEmail,
                            System.currentTimeMillis()));
                    currentSpaceShip.setReviews(reviews);

                    if(position != -1){
                        spaceShipArrayList.set(position, currentSpaceShip);
                    }

                    companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent1 = new Intent(UserReviewsActivity.this, InvoiceActivity.class);
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
                            intent1.putExtra("source",source);
                            intent1.putExtra("destination", destination);
                            intent1.putExtra("distance", distance);
                            intent1.putExtra("user_name",userName);
                            intent1.putExtra("user_email", userEmail);
                            startActivity(intent1);
                        }
                    });

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


}