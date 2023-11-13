package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowSeatConfigurationActivity extends AppCompatActivity {

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
    private TextView seat1_trans;
    private TextView seat2_trans;
    private TextView seat3_trans;
    private TextView seat4_trans;
    private TextView seat5_trans;
    private TextView seat6_trans;
    private TextView seat7_trans;
    private TextView seat8_trans;
    private TextView seat9_trans;
    private TextView seat10_trans;
    private TextView seat11_trans;
    private TextView seat12_trans;
    private TextView music;
    private TextView music_not;
    private TextView sleep;
    private TextView sleep_not;
    private TextView fitness;
    private TextView fitness_not;
    private TextView food;
    private TextView food_not;
    private TextView confirm_seats;
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
    private ArrayList<Review> reviews;
    private String services;
    private String currentSeatConfiguration;
    private String chosenSeatConfiguration;
    private int seatsChosen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat_configuration);

        seat1 = findViewById(R.id.seat1_show);
        seat2 = findViewById(R.id.seat2_show);
        seat3 = findViewById(R.id.seat3_show);
        seat4 = findViewById(R.id.seat4_show);
        seat5 = findViewById(R.id.seat5_show);
        seat6 = findViewById(R.id.seat6_show);
        seat7 = findViewById(R.id.seat7_show);
        seat8 = findViewById(R.id.seat8_show);
        seat9 = findViewById(R.id.seat9_show);
        seat10 = findViewById(R.id.seat10_show);
        seat11 = findViewById(R.id.seat11_show);
        seat12 = findViewById(R.id.seat12_show);

        seat1_trans = findViewById(R.id.seat1_show_trans);
        seat2_trans = findViewById(R.id.seat2_show_trans);
        seat3_trans = findViewById(R.id.seat3_show_trans);
        seat4_trans = findViewById(R.id.seat4_show_trans);
        seat5_trans = findViewById(R.id.seat5_show_trans);
        seat6_trans = findViewById(R.id.seat6_show_trans);
        seat7_trans = findViewById(R.id.seat7_show_trans);
        seat8_trans = findViewById(R.id.seat8_show_trans);
        seat9_trans = findViewById(R.id.seat9_show_trans);
        seat10_trans = findViewById(R.id.seat10_show_trans);
        seat11_trans = findViewById(R.id.seat11_show_trans);
        seat12_trans = findViewById(R.id.seat12_show_trans);

        music = findViewById(R.id.music_tv_show);
        music_not = findViewById(R.id.music_not_tv_show);

        sleep = findViewById(R.id.sleep_tv_show);
        sleep_not = findViewById(R.id.sleep_not_tv_show);

        food = findViewById(R.id.food_tv_show);
        food_not = findViewById(R.id.food_not_tv_show);

        fitness = findViewById(R.id.fitness_tv_show);
        fitness_not = findViewById(R.id.fitness_not_tv_show);

        confirm_seats = findViewById(R.id.confirm_seats);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        ratings = intent.getStringExtra("rating_ss");
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

        currentSeatConfiguration = seats;
        chosenSeatConfiguration = "000000000000";

        attachSeatsListener();
        setServicesViews();

        confirm_seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ShowSeatConfigurationActivity.this, CheckoutActivity.class);
                intent1.putExtra("name_ss", name);
                intent1.putExtra("description_ss", description);
                intent1.putExtra("price_ss", price);
                intent1.putExtra("rating_ss", ratings);
                intent1.putExtra("speed_ss", speed);
                intent1.putExtra("busyTime_ss", busyTime);
                intent1.putExtra("seats_ss", seats);
                intent1.putExtra("shared_ride_ss", haveSharedRide);
                intent1.putExtra("loginMode", loginMode);
                intent1.putExtra("companyID", companyId);
                intent1.putExtra("update_spaceship", false);
                intent1.putExtra("reviews_ss", reviews);
                intent1.putExtra("chosen_seat_config", chosenSeatConfiguration);
                startActivity(intent1);
            }
        });


        seat1_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(0) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 0, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 0, '1');
                    seat1_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 0, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 0, '0');
                    seat1_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat2_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(1) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 1, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 1, '1');
                    seat2_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 1, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 1, '0');
                    seat2_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat3_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(2) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 2, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 2, '1');
                    seat3_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 2, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 2, '0');
                    seat3_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat4_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(3) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 3, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 3, '1');
                    seat4_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 3, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 3, '0');
                    seat4_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat5_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(4) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 4, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 4, '1');
                    seat5_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 4, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 4, '0');
                    seat5_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat6_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(5) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 5, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 5, '1');
                    seat6_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 5, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 5, '0');
                    seat6_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat7_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(6) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 6, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 6, '1');
                    seat7_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 6, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 6, '0');
                    seat7_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat8_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(7) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 7, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 7, '1');
                    seat8_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 7, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 7, '0');
                    seat8_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat9_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(8) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 8, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 8, '1');
                    seat9_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 8, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 8, '0');
                    seat9_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat10_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(9) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 9, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 9, '1');
                    seat10_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 9, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 9, '0');
                    seat10_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat11_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(10) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 10, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 10, '1');
                    seat11_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 10, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 10, '0');
                    seat11_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });

        seat12_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeatConfiguration.charAt(11) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 11, '0');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 11, '1');
                    seat12_trans.setBackgroundResource(R.drawable.colored_seats);
                } else {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, 11, '1');
                    chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 11, '0');
                    seat12_trans.setBackgroundResource(R.drawable.transparent_seat);
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        attachSeatsListener();
    }

    private void setSeatConfigurationViews() {
        for (int i = 0; i < 12; i++) {
            if (i == 0) {
                if (seats.charAt(i) == '1') {
                    seat1_trans.setVisibility(View.VISIBLE);
                    seat1.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat1_trans.setVisibility(View.GONE);
                    seat1.setVisibility(View.GONE);
                } else {
                    seat1_trans.setVisibility(View.GONE);
                    seat1.setVisibility(View.VISIBLE);
                }
            } else if (i == 1) {
                if (seats.charAt(i) == '1') {
                    seat2_trans.setVisibility(View.VISIBLE);
                    seat2.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat2_trans.setVisibility(View.GONE);
                    seat2.setVisibility(View.GONE);
                } else {
                    seat2_trans.setVisibility(View.GONE);
                    seat2.setVisibility(View.VISIBLE);
                }
            } else if (i == 2) {
                if (seats.charAt(i) == '1') {
                    seat3_trans.setVisibility(View.VISIBLE);
                    seat3.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat3_trans.setVisibility(View.GONE);
                    seat3.setVisibility(View.GONE);
                } else {
                    seat3_trans.setVisibility(View.GONE);
                    seat3.setVisibility(View.VISIBLE);
                }
            } else if (i == 3) {
                if (seats.charAt(i) == '1') {
                    seat4_trans.setVisibility(View.VISIBLE);
                    seat4.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat4_trans.setVisibility(View.GONE);
                    seat4.setVisibility(View.GONE);
                } else {
                    seat4_trans.setVisibility(View.GONE);
                    seat4.setVisibility(View.VISIBLE);
                }
            } else if (i == 4) {
                if (seats.charAt(i) == '1') {
                    seat5_trans.setVisibility(View.VISIBLE);
                    seat5.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat5_trans.setVisibility(View.GONE);
                    seat5.setVisibility(View.GONE);
                } else {
                    seat5_trans.setVisibility(View.GONE);
                    seat5.setVisibility(View.VISIBLE);
                }
            } else if (i == 5) {
                if (seats.charAt(i) == '1') {
                    seat6_trans.setVisibility(View.VISIBLE);
                    seat6.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat6_trans.setVisibility(View.GONE);
                    seat6.setVisibility(View.GONE);
                } else {
                    seat6_trans.setVisibility(View.GONE);
                    seat6.setVisibility(View.VISIBLE);
                }
            } else if (i == 6) {
                if (seats.charAt(i) == '1') {
                    seat7_trans.setVisibility(View.VISIBLE);
                    seat7.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat7_trans.setVisibility(View.GONE);
                    seat7.setVisibility(View.GONE);
                } else {
                    seat7_trans.setVisibility(View.GONE);
                    seat7.setVisibility(View.VISIBLE);
                }
            } else if (i == 7) {
                if (seats.charAt(i) == '1') {
                    seat8_trans.setVisibility(View.VISIBLE);
                    seat8.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat8_trans.setVisibility(View.GONE);
                    seat8.setVisibility(View.GONE);
                } else {
                    seat8_trans.setVisibility(View.GONE);
                    seat8.setVisibility(View.VISIBLE);
                }
            } else if (i == 8) {
                if (seats.charAt(i) == '1') {
                    seat9_trans.setVisibility(View.VISIBLE);
                    seat9.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat9_trans.setVisibility(View.GONE);
                    seat9.setVisibility(View.GONE);
                } else {
                    seat9_trans.setVisibility(View.GONE);
                    seat9.setVisibility(View.VISIBLE);
                }
            } else if (i == 9) {
                if (seats.charAt(i) == '1') {
                    seat10_trans.setVisibility(View.VISIBLE);
                    seat10.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat10_trans.setVisibility(View.GONE);
                    seat10.setVisibility(View.GONE);
                } else {
                    seat10_trans.setVisibility(View.GONE);
                    seat10.setVisibility(View.VISIBLE);
                }
            } else if (i == 10) {
                if (seats.charAt(i) == '1') {
                    seat11_trans.setVisibility(View.VISIBLE);
                    seat11.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat11_trans.setVisibility(View.GONE);
                    seat11.setVisibility(View.GONE);
                } else {
                    seat11_trans.setVisibility(View.GONE);
                    seat11.setVisibility(View.VISIBLE);
                }
            } else if (i == 11) {
                if (seats.charAt(i) == '1') {
                    seat12_trans.setVisibility(View.VISIBLE);
                    seat12.setVisibility(View.GONE);
                } else if (seats.charAt(i) == '2') {
                    seat12_trans.setVisibility(View.GONE);
                    seat12.setVisibility(View.GONE);
                } else {
                    seat12_trans.setVisibility(View.GONE);
                    seat12.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setServicesViews() {
        if (services != null) {
            for (int i = 0; i < services.length(); i++) {
                if (services.charAt(i) == '1') {
                    if (i == 0) {
                        food_not.setVisibility(View.GONE);
                        food.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        music_not.setVisibility(View.GONE);
                        music.setVisibility(View.VISIBLE);
                    } else if (i == 2) {
                        sleep_not.setVisibility(View.GONE);
                        sleep.setVisibility(View.VISIBLE);
                    } else if (i == 3) {
                        fitness_not.setVisibility(View.GONE);
                        fitness.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private String setCharAt(String services, int i, char ch) {
        char[] charArray = services.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }

    private void attachSeatsListener() {
//        Toast.makeText(this, seats, Toast.LENGTH_SHORT).show();////////////////////////////////
        try {
            SpaceShip originalSpaceShip = new SpaceShip(name, description, "", ratings, seats, services,
                    haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);
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

                    // get upadated the spaceShip seats
                    try {
                        if (index != -1) seats = spaceShipArrayList.get(index).getSeatsAvailable();
                        setSeatConfigurationViews();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ShowSeatConfigurationActivity.this, "Data not updated. Please retry", Toast.LENGTH_SHORT).show();
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

    private Boolean areEqualSpaceShips(SpaceShip spaceShip1, SpaceShip spaceShip2) {
        if (!(spaceShip1.getSpaceShipName().equals(spaceShip2.getSpaceShipName()))) {
            return false;
        }
        if (!(spaceShip1.getBusyTime() == spaceShip2.getBusyTime())) {
            return false;
        }
        if (!(spaceShip1.getPrice() == spaceShip2.getPrice())) {
            return false;
        }
        if (!(spaceShip1.getSpaceShipRating().equals(spaceShip2.getSpaceShipRating()))) {
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