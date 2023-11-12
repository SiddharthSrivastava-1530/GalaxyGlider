package com.example.newapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SeatConfigurationActivity extends AppCompatActivity {

    private TextView seat1;
    private TextView seat2;
    private TextView seat3;
    private TextView seat4;
    private TextView seat5;
    private TextView seat6;
    private TextView seat7;
    private TextView seat8;
    private TextView seat9;
    private TextView seat10;
    private TextView seat11;
    private TextView seat12;

    private TextView music;

    private TextView music_not;
    private TextView sleep;
    private TextView sleep_not;
    private TextView fitness;

    private TextView fitness_not;
    private TextView food;
    private TextView food_not;
    private ImageView addSeatbtn;

    private ImageView removeSeatbtn;

    private int total_seats;

    private TextView confirmSeatConfiguration;

    private Boolean booleanUpdate;
    private SpaceShip spaceShip;
    private String spaceShipKey;
    private String name;
    private String rating;
    private String description;
    private String seats;
    private String price;
    private float speed;
    private String busyTime;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;

    ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_configuration);

        seat1 = findViewById(R.id.seat1);
        seat2 = findViewById(R.id.seat2);
        seat3 = findViewById(R.id.seat3);
        seat4 = findViewById(R.id.seat4);
        seat5 = findViewById(R.id.seat5);
        seat6 = findViewById(R.id.seat6);
        seat7 = findViewById(R.id.seat7);
        seat8 = findViewById(R.id.seat8);
        seat9 = findViewById(R.id.seat9);
        seat10 = findViewById(R.id.seat10);
        seat11 = findViewById(R.id.seat11);
        seat12 = findViewById(R.id.seat12);

        music = findViewById(R.id.music_tv);
        music_not = findViewById(R.id.music_not_tv);

        sleep = findViewById(R.id.sleep_tv);
        sleep_not = findViewById(R.id.sleep_not_tv);

        food = findViewById(R.id.food_tv);
        food_not = findViewById(R.id.food_not_tv);

        fitness = findViewById(R.id.fitness_tv);
        fitness_not = findViewById(R.id.fitness_not_tv);





        addSeatbtn = findViewById(R.id.add_btn_seats);
        removeSeatbtn = findViewById(R.id.remove_seat);

        confirmSeatConfiguration = findViewById(R.id.confirm_seat_config);


        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        rating = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss", 0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        booleanUpdate = intent.getBooleanExtra("update_spaceship", false);
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");


        total_seats = 0;


        addSeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_seats==0){
                    seat1.setVisibility(View.VISIBLE);
                    seat2.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else if(total_seats==2){
                    seat3.setVisibility(View.VISIBLE);
                    seat4.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else if(total_seats==4){
                    seat5.setVisibility(View.VISIBLE);
                    seat6.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else if(total_seats==6){
                    seat7.setVisibility(View.VISIBLE);
                    seat8.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else if(total_seats==8){
                    seat9.setVisibility(View.VISIBLE);
                    seat10.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else if(total_seats==10){
                    seat11.setVisibility(View.VISIBLE);
                    seat12.setVisibility(View.VISIBLE);
                    total_seats+=2;
                }
                else{
                    Toast.makeText(SeatConfigurationActivity.this,"You cannot add more seats to your spaceship",Toast.LENGTH_SHORT).show();
                }
            }
        });

        removeSeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_seats==2){
                    seat1.setVisibility(View.INVISIBLE);
                    seat2.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else if(total_seats==4){
                    seat3.setVisibility(View.INVISIBLE);
                    seat4.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else if(total_seats==6){
                    seat5.setVisibility(View.INVISIBLE);
                    seat6.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else if(total_seats==8){
                    seat7.setVisibility(View.INVISIBLE);
                    seat8.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else if(total_seats==10){
                    seat9.setVisibility(View.INVISIBLE);
                    seat10.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else if(total_seats==12){
                    seat11.setVisibility(View.INVISIBLE);
                    seat12.setVisibility(View.INVISIBLE);
                    total_seats-=2;
                }
                else{

                }
            }
        });

        food_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food.setVisibility(View.VISIBLE);
                food_not.setVisibility(View.GONE);
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food.setVisibility(View.GONE);
                food_not.setVisibility(View.VISIBLE);
            }
        });

        music_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.setVisibility(View.VISIBLE);
                music_not.setVisibility(View.GONE);
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music.setVisibility(View.GONE);
                music_not.setVisibility(View.VISIBLE);
            }
        });

        sleep_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sleep_not.setVisibility(View.GONE);
                sleep.setVisibility(View.VISIBLE);
            }
        });

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sleep.setVisibility(View.GONE);
                sleep_not.setVisibility(View.VISIBLE);
            }
        });

        fitness_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fitness_not.setVisibility(View.GONE);
                fitness.setVisibility(View.VISIBLE);
            }
        });

        fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fitness.setVisibility(View.GONE);
                fitness_not.setVisibility(View.VISIBLE);
            }
        });




        confirmSeatConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!booleanUpdate){
                    saveSpaceShipsData();
                }
            }
        });

    }




    // save the new spaceship data in database
    private void saveSpaceShipsData() {

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company").child(companyId);
        ArrayList<Review> reviews = new ArrayList<>();
        spaceShip = new SpaceShip(name, description, "", rating, String.valueOf(total_seats),
                haveSharedRide, Long.parseLong("0"), Float.parseFloat(price), 0, reviews);

        companyRef.child("spaceShips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SpaceShip> spaceShips = new ArrayList<>();

                // Check if the spaceShips field exists
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<ArrayList<SpaceShip>> t = new GenericTypeIndicator<ArrayList<SpaceShip>>() {
                    };
                    spaceShips = dataSnapshot.getValue(t);
                }

                // Add the new spaceShip to the ArrayList
                spaceShips.add(spaceShip);

                // Set the updated spaceShips ArrayList back to the company reference
                companyRef.child("spaceShips").setValue(spaceShips);
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

}