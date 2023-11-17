package com.example.newapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpaceShipDetailsActivity extends AppCompatActivity {

    private TextView nameTextview;
    private TextView priceTextview;
    private TextView ratingTextview;
    private TextView seatAvailableTextview;
    private TextView speedTextview;
    private TextView sharedRideTextview;
    private TextView descriptionTextview;
    private TextView bookSpaceShipTextView;
    private TextView seeAllReviews;
    private String companyId;
    private String loginMode;
    private FloatingActionButton fab;
    private RatingBar ratingBar;
    private SpaceShip currentSpaceShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_details);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        nameTextview = findViewById(R.id.spaceShipName_details_activity);
        priceTextview = findViewById(R.id.spaceShip_price_details_activity);
//        speedTextview = findViewById(R.id.spaceShip_speed_details_activity);
//        sharedRideTextview = findViewById(R.id.spaceShip_rideSharing_details_activity);
        descriptionTextview = findViewById(R.id.desc_details_activity);
        seatAvailableTextview = findViewById(R.id.seats_spaceShip_details_activity);
//        ratingTextview = findViewById(R.id.spaceShip_rating_details_activity);
//        busyTimeTextview = findViewById(R.id.spaceShip_busyTime_details_activity);
        bookSpaceShipTextView = findViewById(R.id.book_ss_tv);
        seeAllReviews = findViewById(R.id.see_Reviews_tv);
        ratingBar = findViewById(R.id.ratingBar_spaceship_details);

        fab = findViewById(R.id.fab_details_activity);

        Intent intent = getIntent();
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship_ss");
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");

        ratingBar.setRating(Float.parseFloat(currentSpaceShip.getSpaceShipRating()));

        if (!loginMode.equals("user")) {
            bookSpaceShipTextView.setVisibility(View.GONE);
        }

        if (!loginMode.equals("owner")) {
            fab.setVisibility(View.GONE);
        }

        addSpaceShipListener();

        // Move to editor activity for editing spaceship Data.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginMode.equals("owner")) {
                    Intent intent1 = new Intent(SpaceShipDetailsActivity.this, SpaceShipEditorActivity.class);
                    intent1.putExtra("spaceship_ss", currentSpaceShip);
                    intent1.putExtra("loginMode", loginMode);
                    intent1.putExtra("companyID", companyId);
                    intent1.putExtra("update_spaceship", true);
                    startActivity(intent1);
                }
            }
        });

        // Move to seat configuration activity on booking for choosing seats (user).
        bookSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SpaceShipDetailsActivity.this, ShowSeatScheduleActivity.class);
                intent1.putExtra("spaceship_ss", currentSpaceShip);
                intent1.putExtra("companyID", companyId);
                startActivity(intent1);
            }
        });

        seeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SpaceShipDetailsActivity.this, SpaceShipReviews.class);
                intent1.putExtra("companyID", companyId);
                intent1.putExtra("id_ss", currentSpaceShip.getSpaceShipId());
                startActivity(intent1);
            }
        });

    }

    // set view data in respective views.
    private void setViewData() {


        nameTextview.setText(currentSpaceShip.getSpaceShipName());
        priceTextview.setText(String.valueOf(currentSpaceShip.getPrice()));
//        speedTextview.setText(String.valueOf(speed));
//        sharedRideTextview.setText(String.valueOf(haveSharedRide));
        descriptionTextview.setText(currentSpaceShip.getDescription());
//        seatAvailableTextview.setText(String.valueOf(getSeatCount()));
//        ratingTextview.setText(ratings);
//        busyTimeTextview.setText(busyTime);

    }


    private void addSpaceShipListener(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company/" +
                        companyId + "/spaceShips");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                    if(spaceShip != null && spaceShip.getSpaceShipId().equals(currentSpaceShip.getSpaceShipId())){
                        currentSpaceShip = spaceShip;
                    }
                }
                setViewData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}