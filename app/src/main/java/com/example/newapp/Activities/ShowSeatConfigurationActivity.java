package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import java.util.UUID;

public class ShowSeatConfigurationActivity extends AppCompatActivity {
    private EditText fromLocation, toLocation, distance;
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
    private TextView noRideSharingMessage;
    private TextView selectRecurringTextView;
    private SpaceShip currentSpaceShip;
    private String companyId;
    private String selectedSlotNumber;
    private String currentSeatConfiguration;
    private String chosenSeatConfiguration;
    private boolean isRideRecurring;

    private TextView prev_ride_detail;

    private TextView next_ride_detail;

    private int count_next_click;

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat_configuration);

        getSupportActionBar().hide();

        count_next_click=0;

        viewFlipper = findViewById(R.id.ride_detail_viewFlipper);

        fromLocation = findViewById(R.id.dept_et);
        toLocation = findViewById(R.id.dest_et);
        distance = findViewById(R.id.distance_journey_et);

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
        noRideSharingMessage = findViewById(R.id.no_ride_sharing_message_tv);
        selectRecurringTextView = findViewById(R.id.recurring_ride_selection_tv);

        prev_ride_detail = findViewById(R.id.prev_ride_detail);
        next_ride_detail = findViewById(R.id.next_ride_detail);

        Intent intent = getIntent();
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship_ss");
        selectedSlotNumber = intent.getStringExtra("slot_number");
        companyId = intent.getStringExtra("companyID");

//        distance.setText("123");
//        fromLocation.setText("dis");
//        toLocation.setText("dis");


        chosenSeatConfiguration = "000000000000";

        attachSpaceShipListener();

        if (currentSpaceShip.isHaveRideSharing()) {
            noRideSharingMessage.setVisibility(View.GONE);
        }

        selectRecurringTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectRecurringTextView.getText().toString().equals("YES")){
                    isRideRecurring = true;
                    selectRecurringTextView.setText("NO");
                } else {
                    isRideRecurring = false;
                    selectRecurringTextView.setText("YES");
                }
            }
        });

        prev_ride_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_next_click--;
                if(count_next_click<2){
                    next_ride_detail.setVisibility(View.VISIBLE);
                }
                if(count_next_click==0){
                    prev_ride_detail.setVisibility(View.GONE);
                }
                viewFlipper.showPrevious();
            }
        });

        next_ride_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_next_click++;
                if(count_next_click==1){
                    prev_ride_detail.setVisibility(View.VISIBLE);
                }
                if(count_next_click==2){
                    next_ride_detail.setVisibility(View.GONE);
                }
                viewFlipper.showNext();
            }
        });

        // Move to checkout activity if user confirms seat configuration.
        confirm_seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkData()) {
                    Intent intent1 = new Intent(ShowSeatConfigurationActivity.this, CheckoutActivity.class);
                    intent1.putExtra("companyID", companyId);
                    intent1.putExtra("chosen_seat_config", chosenSeatConfiguration);
                    intent1.putExtra("slot_number", selectedSlotNumber);
                    intent1.putExtra("dept", fromLocation.getText().toString());
                    intent1.putExtra("dest", toLocation.getText().toString());
                    intent1.putExtra("dist", distance.getText().toString());
//                    startActivity(intent1);
                    if (!currentSpaceShip.isHaveRideSharing()) {
                        if (isPossibleToBookSeats()) {
                            if(isRideRecurring){
                                intent1.putExtra("isRecurring", isRideRecurring);
                            }
                            intent1.putExtra("spaceship", currentSpaceShip);
                            intent1.putExtra("chosen_seat_config", chosenSeatConfiguration);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(ShowSeatConfigurationActivity.this, "No " +
                                    "seats available...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(isRideRecurring){
                            intent1.putExtra("isRecurring", isRideRecurring);
                        }
                        intent1.putExtra("spaceship", currentSpaceShip);
                        intent1.putExtra("chosen_seat_config", chosenSeatConfiguration);
                        startActivity(intent1);
                    }
                } else {
                    Toast.makeText(ShowSeatConfigurationActivity.this, "Please choose at least 1 seat...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (currentSpaceShip.isHaveRideSharing()) {
            seat1_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(0) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 0, '1');
                        seat1_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 0, '0');
                        seat1_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat2_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(1) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 1, '1');
                        seat2_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 1, '0');
                        seat2_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat3_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(2) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 2, '1');
                        seat3_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 2, '0');
                        seat3_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat4_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(3) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 3, '1');
                        seat4_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 3, '0');
                        seat4_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat5_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(4) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 4, '1');
                        seat5_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 4, '0');
                        seat5_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat6_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(5) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 5, '1');
                        seat6_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 5, '0');
                        seat6_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat7_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(6) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 6, '1');
                        seat7_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 6, '0');
                        seat7_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat8_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(7) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 7, '1');
                        seat8_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 7, '0');
                        seat8_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat9_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(8) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 8, '1');
                        seat9_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 8, '0');
                        seat9_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat10_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(9) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 9, '1');
                        seat10_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 9, '0');
                        seat10_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat11_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(10) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 10, '1');
                        seat11_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 10, '0');
                        seat11_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

            seat12_trans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenSeatConfiguration.charAt(11) == '0') {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 11, '1');
                        seat12_trans.setBackgroundResource(R.drawable.colored_seats);
                    } else {
                        chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, 11, '0');
                        seat12_trans.setBackgroundResource(R.drawable.transparent_seat);
                    }
                }
            });

        }


    }

    // return true if at least one seat is chosen.
    private boolean checkData() {

        if (TextUtils.isEmpty(fromLocation.getText().toString())) {
            Toast.makeText(this, "Please enter source", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(toLocation.getText().toString())) {
            Toast.makeText(this, "Please enter destination", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(distance.getText().toString())) {
            Toast.makeText(this, "Please enter approximated distance", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!currentSpaceShip.isHaveRideSharing()) return true;

        int chosenSeats = 0;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfiguration.charAt(position) == '1') {
                chosenSeats++;
            }
        }
        return chosenSeats > 0;

    }


    @Override
    protected void onResume() {
        super.onResume();
        attachSpaceShipListener();
    }


    // set the seat configuration to show the user changes in seat configuration in realtime.
    private void setSeatConfigurationViews() {
        for (int i = 0; i < 12; i++) {
            if (i == 0) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat1_trans.setVisibility(View.VISIBLE);
                    seat1.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat1_trans.setVisibility(View.GONE);
                    seat1.setVisibility(View.GONE);
                } else {
                    seat1_trans.setVisibility(View.GONE);
                    seat1.setVisibility(View.VISIBLE);
                }
            } else if (i == 1) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat2_trans.setVisibility(View.VISIBLE);
                    seat2.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat2_trans.setVisibility(View.GONE);
                    seat2.setVisibility(View.GONE);
                } else {
                    seat2_trans.setVisibility(View.GONE);
                    seat2.setVisibility(View.VISIBLE);
                }
            } else if (i == 2) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat3_trans.setVisibility(View.VISIBLE);
                    seat3.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat3_trans.setVisibility(View.GONE);
                    seat3.setVisibility(View.GONE);
                } else {
                    seat3_trans.setVisibility(View.GONE);
                    seat3.setVisibility(View.VISIBLE);
                }
            } else if (i == 3) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat4_trans.setVisibility(View.VISIBLE);
                    seat4.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat4_trans.setVisibility(View.GONE);
                    seat4.setVisibility(View.GONE);
                } else {
                    seat4_trans.setVisibility(View.GONE);
                    seat4.setVisibility(View.VISIBLE);
                }
            } else if (i == 4) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat5_trans.setVisibility(View.VISIBLE);
                    seat5.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat5_trans.setVisibility(View.GONE);
                    seat5.setVisibility(View.GONE);
                } else {
                    seat5_trans.setVisibility(View.GONE);
                    seat5.setVisibility(View.VISIBLE);
                }
            } else if (i == 5) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat6_trans.setVisibility(View.VISIBLE);
                    seat6.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat6_trans.setVisibility(View.GONE);
                    seat6.setVisibility(View.GONE);
                } else {
                    seat6_trans.setVisibility(View.GONE);
                    seat6.setVisibility(View.VISIBLE);
                }
            } else if (i == 6) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat7_trans.setVisibility(View.VISIBLE);
                    seat7.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat7_trans.setVisibility(View.GONE);
                    seat7.setVisibility(View.GONE);
                } else {
                    seat7_trans.setVisibility(View.GONE);
                    seat7.setVisibility(View.VISIBLE);
                }
            } else if (i == 7) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat8_trans.setVisibility(View.VISIBLE);
                    seat8.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat8_trans.setVisibility(View.GONE);
                    seat8.setVisibility(View.GONE);
                } else {
                    seat8_trans.setVisibility(View.GONE);
                    seat8.setVisibility(View.VISIBLE);
                }
            } else if (i == 8) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat9_trans.setVisibility(View.VISIBLE);
                    seat9.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat9_trans.setVisibility(View.GONE);
                    seat9.setVisibility(View.GONE);
                } else {
                    seat9_trans.setVisibility(View.GONE);
                    seat9.setVisibility(View.VISIBLE);
                }
            } else if (i == 9) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat10_trans.setVisibility(View.VISIBLE);
                    seat10.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat10_trans.setVisibility(View.GONE);
                    seat10.setVisibility(View.GONE);
                } else {
                    seat10_trans.setVisibility(View.GONE);
                    seat10.setVisibility(View.VISIBLE);
                }
            } else if (i == 10) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat11_trans.setVisibility(View.VISIBLE);
                    seat11.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat11_trans.setVisibility(View.GONE);
                    seat11.setVisibility(View.GONE);
                } else {
                    seat11_trans.setVisibility(View.GONE);
                    seat11.setVisibility(View.VISIBLE);
                }
            } else if (i == 11) {
                if (currentSeatConfiguration.charAt(i) == '1') {
                    seat12_trans.setVisibility(View.VISIBLE);
                    seat12.setVisibility(View.GONE);
                } else if (currentSeatConfiguration.charAt(i) == '2') {
                    seat12_trans.setVisibility(View.GONE);
                    seat12.setVisibility(View.GONE);
                } else {
                    seat12_trans.setVisibility(View.GONE);
                    seat12.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // set Services Views by traversing over services string to show services offered by spaceship.
    private void setServicesViews() {
        if (currentSpaceShip.getServicesAvailable() != null) {
            for (int i = 0; i < currentSpaceShip.getServicesAvailable().length(); i++) {
                if (currentSpaceShip.getServicesAvailable().charAt(i) == '1') {
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


    // set character at given index of string.
    private String setCharAt(String services, int i, char ch) {
        char[] charArray = services.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }


    private void attachSpaceShipListener() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"
                + companyId + "/spaceShips");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                    if (spaceShip != null && spaceShip.getSpaceShipId().equals(currentSpaceShip.getSpaceShipId())) {
                        currentSpaceShip = spaceShip;
                    }
                }
                getSlotConfiguration();
                setServicesViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getSlotConfiguration() {

        if (selectedSlotNumber.equals("0")) {
            currentSeatConfiguration = currentSpaceShip.getSlot1();
        } else if (selectedSlotNumber.equals("1")) {
            currentSeatConfiguration = currentSpaceShip.getSlot2();
        } else if (selectedSlotNumber.equals("2")) {
            currentSeatConfiguration = currentSpaceShip.getSlot3();
        } else if (selectedSlotNumber.equals("3")) {
            currentSeatConfiguration = currentSpaceShip.getSlot4();
        } else if (selectedSlotNumber.equals("4")) {
            currentSeatConfiguration = currentSpaceShip.getSlot5();
        } else if (selectedSlotNumber.equals("5")) {
            currentSeatConfiguration = currentSpaceShip.getSlot6();
        } else if (selectedSlotNumber.equals("6")) {
            currentSeatConfiguration = currentSpaceShip.getSlot7();
        } else if (selectedSlotNumber.equals("7")) {
            currentSeatConfiguration = currentSpaceShip.getSlot8();
        }
        setSeatConfigurationViews();
    }


    private boolean isPossibleToBookSeats() {
        String copy = currentSeatConfiguration;
        for (int position = 0; position < 12; position++) {
            if (currentSeatConfiguration.charAt(position) == '1') {
                chosenSeatConfiguration = setCharAt(chosenSeatConfiguration, position, '1');
                currentSeatConfiguration = setCharAt(currentSeatConfiguration, position, '0');
            } else if (currentSeatConfiguration.charAt(position) == '0') {
                chosenSeatConfiguration = "000000000000";
                currentSeatConfiguration = copy;
                return false;
            }
        }
        return true;
    }


}