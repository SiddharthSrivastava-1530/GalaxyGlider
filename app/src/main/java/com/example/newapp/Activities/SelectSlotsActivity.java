package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class SelectSlotsActivity extends AppCompatActivity {

    TextView confirm_time_slots;
    private TextView[] slots;
    private String name;
    private String description;
    private String price;
    private String companyId;
    private SpaceShip currentSpaceShip;
    private boolean booleanUpdate;
    private String slotsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_slots);

        getSupportActionBar().hide();

        // Initialize your TextView array
        slots = new TextView[]{
                findViewById(R.id.slot03),
                findViewById(R.id.slot36),
                findViewById(R.id.slot69),
                findViewById(R.id.slot9_12),
                findViewById(R.id.slot12_15),
                findViewById(R.id.slot15_18),
                findViewById(R.id.slot18_21),
                findViewById(R.id.slot21_24)
        };

        confirm_time_slots = findViewById(R.id.confirm_time_slots);
        slotsSelected = "00000000";

        // getting data from intent.
        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        price = intent.getStringExtra("price_ss");
        description = intent.getStringExtra("description_ss");
        companyId = intent.getStringExtra("companyID");
        booleanUpdate = intent.getBooleanExtra("update_spaceship", false);
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship_ss");


        if(booleanUpdate){
            slotsSelected = currentSpaceShip.getNextSlotConfig();
            setCurrentSlotView();
        }


        // Set up click listeners for all TextViews
        int position = 0;
        for (TextView slot : slots) {
            final int index = position;
            slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleBackground(slot,index);
                }
            });
            position++;
        }

        confirm_time_slots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sending data from intent.
                // Start the journey and move further to review activity.
                if(booleanUpdate){
                    updateSpaceShipsData();
                } else {
                    Intent intent1 = new Intent(SelectSlotsActivity.this, SeatConfigurationActivity.class);
                    currentSpaceShip.setSpaceShipName(name);
                    currentSpaceShip.setDescription(description);
                    currentSpaceShip.setPrice(Float.parseFloat(price));
                    intent1.putExtra("spaceship_ss", currentSpaceShip);
                    intent1.putExtra("companyID", companyId);
                    intent1.putExtra("slot_ss", slotsSelected);
                    startActivity(intent1);
                }
            }
        });

    }

    private void toggleBackground(TextView textView, int position) {
        int currentBackgroundColor = ((ColorDrawable) textView.getBackground()).getColor();
        int targetColor = Color.parseColor("#FF1744");
        int newBackgroundColor = (currentBackgroundColor == targetColor) ? Color.GREEN : targetColor;
        // Set the new background color
        textView.setBackgroundColor(newBackgroundColor);
        if(slotsSelected.charAt(position)=='0'){
            slotsSelected = setCharAt(slotsSelected,position, '1');
        } else {
            slotsSelected = setCharAt(slotsSelected,position, '0');
        }
    }


    // set character at given index of string.
    private String setCharAt(String services, int i, char ch) {
        char[] charArray = services.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }



    // update the existing spaceship
    private void updateSpaceShipsData() {

        SpaceShip updatedSpaceShip = currentSpaceShip;
        updatedSpaceShip.setSpaceShipName(name);
        updatedSpaceShip.setDescription(description);
        updatedSpaceShip.setPrice(Float.parseFloat(price));
        updatedSpaceShip.setHaveRideSharing(currentSpaceShip.isHaveRideSharing());
        updatedSpaceShip.setNextSlotConfig(slotsSelected);


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectSlotsActivity.this);
        builder.setTitle("Update spaceship data")
                .setMessage("Are you sure you want to update the spaceship data?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                                            if (currentSpaceShip.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                                index = counter;
                                            }
                                            counter++;
                                        }
                                    }
                                }

                                // Update the spaceShip you want to delete
                                try {
                                    if (index != -1)
                                        spaceShipArrayList.set(index, updatedSpaceShip);
                                } catch (IndexOutOfBoundsException e) {
                                    Toast.makeText(SelectSlotsActivity.this, "Data not updated. Please retry",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // Set the updated spaceShips back to the company reference
                                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()) {
                                            Toast.makeText(SelectSlotsActivity.this, "SpaceShip Updated...",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(SelectSlotsActivity.this,
                                                    AllSpaceShipsListActivity.class);
                                            intent1.putExtra("loginMode", "owner");
                                            intent1.putExtra("companyID", companyId);
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent1);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }).show();

    }


    private void setCurrentSlotView(){

        for(int position=0;position<8;position++){
            if(slotsSelected.charAt(position)=='0'){
                slots[position].setBackgroundColor(Color.parseColor("#FF1744"));
            } else {
                slots[position].setBackgroundColor(Color.GREEN);
            }
        }
    }

}