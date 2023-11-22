package com.example.newapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.newapp.Activities.AllSpaceShipsListActivity;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.SpaceShip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {

    private ArrayList<SpaceShip> spaceShipArrayList;

    @Override
    public void onReceive(Context context, Intent intent) {

        spaceShipArrayList = new ArrayList<>();
        changeSlotConfigurationOnNewDay();

    }

    private void changeSlotConfigurationOnNewDay() {

        FirebaseDatabase.getInstance().getReference("company")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            boolean isCompanyUpdated = dataSnapshot.getValue(Company.class).isCurrentSlotUpdated();
                            if(!isCompanyUpdated) {
                                changeCompanySlotConfigurationOnNewDay(dataSnapshot.getValue(Company.class).getCompanyId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void changeCompanySlotConfigurationOnNewDay(String companyId) {
        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company").child(companyId).child("spaceShips");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    spaceShipArrayList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null) {
                                spaceShipArrayList.add(getChangedNewDayConfig(spaceShip));
                            }
                        }
                    }

                    databaseReference.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference("company/" + companyId)
                                    .child("isCurrentSlotUpdated").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private SpaceShip getChangedNewDayConfig(SpaceShip spaceShip) {

        String nextSlotConfig = spaceShip.getNextSlotConfig();
        if (nextSlotConfig.charAt(0) == '1') {
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(0));
        } else {
            spaceShip.setSlot1("000000000000");
        }
        if (nextSlotConfig.charAt(1) == '1') {
            spaceShip.setSlot2(spaceShip.getNextSeatConfigurations().get(1));
        } else {
            spaceShip.setSlot2("000000000000");
        }
        if (nextSlotConfig.charAt(2) == '1') {
            spaceShip.setSlot3(spaceShip.getNextSeatConfigurations().get(2));
        } else {
            spaceShip.setSlot3("000000000000");
        }
        if (nextSlotConfig.charAt(3) == '1') {
            spaceShip.setSlot4(spaceShip.getNextSeatConfigurations().get(3));
        } else {
            spaceShip.setSlot4("000000000000");
        }
        if (nextSlotConfig.charAt(4) == '1') {
            spaceShip.setSlot5(spaceShip.getNextSeatConfigurations().get(4));
        } else {
            spaceShip.setSlot5("000000000000");
        }
        if (nextSlotConfig.charAt(5) == '1') {
            spaceShip.setSlot6(spaceShip.getNextSeatConfigurations().get(5));
        } else {
            spaceShip.setSlot6("000000000000");
        }
        if (nextSlotConfig.charAt(6) == '1') {
            spaceShip.setSlot7(spaceShip.getNextSeatConfigurations().get(6));
        } else {
            spaceShip.setSlot7("000000000000");
        }
        if (nextSlotConfig.charAt(7) == '1') {
            spaceShip.setSlot8(spaceShip.getNextSeatConfigurations().get(7));
        } else {
            spaceShip.setSlot8("000000000000");
        }
        Log.e("spaceShipId --> ",spaceShip.getSpaceShipId());
        return spaceShip;
    }

}
