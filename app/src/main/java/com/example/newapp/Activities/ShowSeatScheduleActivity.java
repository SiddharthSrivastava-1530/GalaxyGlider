package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowSeatScheduleActivity extends AppCompatActivity {

    private TextView[] slots;
    private SpaceShip currentSpaceShip;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat_schedule);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship_ss");
        companyId = intent.getStringExtra("companyID");

        // Initialize your TextView array
        slots = new TextView[]{
                findViewById(R.id.slot03_show),
                findViewById(R.id.slot36_show),
                findViewById(R.id.slot69_show),
                findViewById(R.id.slot9_12_show),
                findViewById(R.id.slot12_15_show),
                findViewById(R.id.slot15_18_show),
                findViewById(R.id.slot18_21_show),
                findViewById(R.id.slot21_24_show)
        };


        // Set up click listeners for all TextViews
        for (int position=0; position < 8; position++) {
            final int pos = position;
            slots[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked(slots[pos],pos);
                }
            });
        }

        attachSpaceShipListener();


    }


    private void clicked(TextView textView,int position) {
        if(Integer.parseInt(textView.getText().toString()) > 0) {
            Intent intent1 = new Intent(ShowSeatScheduleActivity.this, ShowSeatConfigurationActivity.class);
            intent1.putExtra("spaceship_ss", currentSpaceShip);
            intent1.putExtra("companyID", companyId);
            intent1.putExtra("slot_number", String.valueOf(position));
            startActivity(intent1);
        } else {
            Toast.makeText(this, "No seats available for this time slot...", Toast.LENGTH_SHORT).show();
        }
    }


    // set character at given index of string.
    private String setCharAt(String services, int i, char ch) {
        char[] charArray = services.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }



    private void attachSpaceShipListener(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"
                + companyId + "/spaceShips");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                    if(spaceShip != null && spaceShip.getSpaceShipId().equals(currentSpaceShip.getSpaceShipId())){
                        currentSpaceShip = spaceShip;
                    }
                }
                setSlotSeatCountView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void setSlotSeatCountView(){

        slots[0].setText(getAvailableSeatCount(currentSpaceShip.getSlot1()));
        slots[1].setText(getAvailableSeatCount(currentSpaceShip.getSlot2()));
        slots[2].setText(getAvailableSeatCount(currentSpaceShip.getSlot3()));
        slots[3].setText(getAvailableSeatCount(currentSpaceShip.getSlot4()));
        slots[4].setText(getAvailableSeatCount(currentSpaceShip.getSlot5()));
        slots[5].setText(getAvailableSeatCount(currentSpaceShip.getSlot6()));
        slots[6].setText(getAvailableSeatCount(currentSpaceShip.getSlot7()));
        slots[7].setText(getAvailableSeatCount(currentSpaceShip.getSlot8()));

    }

    private String getAvailableSeatCount(String seatsAvailable){
        int count = 0;
        for(int position=0; position<12; position++){
            if(seatsAvailable.charAt(position)=='1') count++;
        }
        return String.valueOf(count);
    }

}