package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SpaceShipDetailsActivity extends AppCompatActivity {

    private TextView nameTextview;
    private TextView priceTextview;
    private TextView ratingTextview;
    private TextView seatAvailableTextview;
    private TextView speedTextview;
    private TextView busyTimeTextview;
    private TextView sharedRideTextview;
    private TextView descriptionTextview;
    private ImageView shipImageView;
    private String imageUrl;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_details);

        nameTextview = findViewById(R.id.spaceShipName_details_activity);
        priceTextview = findViewById(R.id.spaceShip_price_details_activity);
        speedTextview = findViewById(R.id.spaceShip_speed_details_activity);
        sharedRideTextview = findViewById(R.id.spaceShip_rideSharing_details_activity);
        descriptionTextview = findViewById(R.id.desc_details_activity);
        shipImageView = findViewById(R.id.img_SpaceShip_details_activity);
        seatAvailableTextview = findViewById(R.id.seats_spaceShip_details_activity);
        ratingTextview = findViewById(R.id.spaceShip_rating_details_activity);
        busyTimeTextview = findViewById(R.id.spaceShip_busyTime_details_activity);

        fab = findViewById(R.id.fab_details_activity);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        ratings = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        imageUrl = intent.getStringExtra("picUrl_ss");
        speed = intent.getFloatExtra("speed_ss",0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss",false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");

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
                    intent1.putExtra("picUrl_ss", imageUrl);
                    intent1.putExtra("speed_ss",speed);
                    intent1.putExtra("busyTime_ss",busyTime);
                    intent1.putExtra("seats_ss",seats);
                    intent1.putExtra("shared_ride_ss",haveSharedRide);
                    intent1.putExtra("loginMode",loginMode);
                    intent1.putExtra("companyID",companyId);
                    intent1.putExtra("update_spaceship",true);
                    startActivity(intent1);
                }
            }
        });



    }

    private void setViewData() {

        nameTextview.setText(name);
        priceTextview.setText(price);
        speedTextview.setText(String.valueOf(speed));
        sharedRideTextview.setText(String.valueOf(haveSharedRide));
        descriptionTextview.setText(description);
        seatAvailableTextview.setText(seats);
        ratingTextview.setText(ratings);
        busyTimeTextview.setText(busyTime);

        Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(shipImageView);

    }
}