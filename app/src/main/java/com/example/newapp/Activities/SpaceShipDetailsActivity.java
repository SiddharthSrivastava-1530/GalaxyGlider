package com.example.newapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newapp.DataModel.Review;
import com.example.newapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private String name;
    private String description;
    private String ratings;
    private String seats;
    private String price;
    private float speed;
    private String busyTime;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;
    private FloatingActionButton fab;
    private ArrayList<Review> reviews;
    private String services;

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

        reviews = new ArrayList<>();

        nameTextview = findViewById(R.id.spaceShipName_details_activity);
        priceTextview = findViewById(R.id.spaceShip_price_details_activity);
        speedTextview = findViewById(R.id.spaceShip_speed_details_activity);
//        sharedRideTextview = findViewById(R.id.spaceShip_rideSharing_details_activity);
        descriptionTextview = findViewById(R.id.desc_details_activity);
        seatAvailableTextview = findViewById(R.id.seats_spaceShip_details_activity);
//        ratingTextview = findViewById(R.id.spaceShip_rating_details_activity);
//        busyTimeTextview = findViewById(R.id.spaceShip_busyTime_details_activity);
        bookSpaceShipTextView = findViewById(R.id.book_ss_tv);
        seeAllReviews = findViewById(R.id.see_Reviews_tv);

        fab = findViewById(R.id.fab_details_activity);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        ratings = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss",0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        services = intent.getStringExtra("services_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss",false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");

        if(!loginMode.equals("user")){
            bookSpaceShipTextView.setVisibility(View.GONE);
        }

        setViewData();

        if(!loginMode.equals("owner")){
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMode.equals("owner")){
                    Intent intent1 = new Intent(SpaceShipDetailsActivity.this,SpaceShipEditorActivity.class);
                    intent1.putExtra("name_ss", name);
                    intent1.putExtra("rating_ss", ratings);
                    intent1.putExtra("description_ss", description);
                    intent1.putExtra("price_ss", price);
                    intent1.putExtra("speed_ss",speed);
                    intent1.putExtra("busyTime_ss",busyTime);
                    intent1.putExtra("seats_ss",seats);
                    intent1.putExtra("shared_ride_ss",haveSharedRide);
                    intent1.putExtra("loginMode",loginMode);
                    intent1.putExtra("companyID",companyId);
                    intent1.putExtra("update_spaceship",true);
                    intent1.putExtra("reviews_ss", reviews);
                    intent1.putExtra("services_ss", services);
                    startActivity(intent1);
                }
            }
        });

        bookSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SpaceShipDetailsActivity.this, ShowSeatConfigurationActivity.class);
                intent1.putExtra("name_ss", name);
                intent1.putExtra("rating_ss", ratings);
                intent1.putExtra("description_ss", description);
                intent1.putExtra("price_ss", price);
                intent1.putExtra("speed_ss",speed);
                intent1.putExtra("busyTime_ss",busyTime);
                intent1.putExtra("seats_ss",seats);
                intent1.putExtra("shared_ride_ss",haveSharedRide);
                intent1.putExtra("companyID",companyId);
                intent1.putExtra("update_spaceship",true);
                intent1.putExtra("reviews_ss", reviews);
                intent1.putExtra("services_ss", services);
                startActivity(intent1);
            }
        });

        seeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SpaceShipDetailsActivity.this, SpaceShipReviews.class);
                intent1.putExtra("reviews_ss", reviews);
                startActivity(intent1);
            }
        });

    }

    private void setViewData() {

        nameTextview.setText(name);
        priceTextview.setText(price);
        speedTextview.setText(String.valueOf(speed));
//        sharedRideTextview.setText(String.valueOf(haveSharedRide));
        descriptionTextview.setText(description);
        seatAvailableTextview.setText(String.valueOf(getSeatCount()));
//        ratingTextview.setText(ratings);
//        busyTimeTextview.setText(busyTime);

    }

    private int getSeatCount(){
        int seatCount = 0;
        if(seats != null)
        {
            for (int i = 0; i < seats.length(); i++)
            {
                if (seats.charAt(i)=='1')
                {
                    seatCount++;
                }
            }
        }
        return seatCount;
    }
}