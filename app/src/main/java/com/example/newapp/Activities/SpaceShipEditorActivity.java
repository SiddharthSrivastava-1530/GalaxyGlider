package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SpaceShipEditorActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private TextView rideSharingTextView;
    private TextView addSpaceShipTextView;
    private TextView deleteSpaceShipTextView;
    private Boolean booleanUpdate;
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
    private String services;
    ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_editor);

//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

//
        nameEditText = findViewById(R.id.spaceshipName_et);
        priceEditText = findViewById(R.id.spaceship_price_et);
        rideSharingTextView = findViewById(R.id.spaceship_sharing_et);
        descriptionEditText = findViewById(R.id.spaceship_desc_et);
//        busyTimeEditText = findViewById(R.id.spaceship_busyTime_et);
        addSpaceShipTextView = findViewById(R.id.spaceShip_add);
        deleteSpaceShipTextView = findViewById(R.id.delete_ss_tv);

        Intent intent = getIntent();
        name = intent.getStringExtra("name_ss");
        rating = intent.getStringExtra("rating_ss");
        description = intent.getStringExtra("description_ss");
        price = intent.getStringExtra("price_ss");
        speed = intent.getFloatExtra("speed_ss", 0);
        busyTime = intent.getStringExtra("busyTime_ss");
        seats = intent.getStringExtra("seats_ss");
        services = intent.getStringExtra("services_ss");
        haveSharedRide = intent.getBooleanExtra("shared_ride_ss", false);
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        booleanUpdate = intent.getBooleanExtra("update_spaceship", false);
        reviews = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");


        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(SpaceShipEditorActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        if (booleanUpdate) {
            setViewData();
            addSpaceShipTextView.setText("Update Glider");
        } else {
            deleteSpaceShipTextView.setVisibility(View.GONE);
        }

        rideSharingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rideSharingTextView.getText().toString().equals("YES")) {
                    rideSharingTextView.setText("NO");
                    haveSharedRide = false;
                } else {
                    rideSharingTextView.setText("YES");
                    haveSharedRide = true;
                }
            }
        });

        addSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (booleanUpdate) {
                    if (checkData() && booleanUpdate) {
                        updateSpaceShipsData();
                    }
                } else {
                    Intent intent1 = new Intent(SpaceShipEditorActivity.this, SeatConfigurationActivity.class);
                    intent1.putExtra("name_ss", nameEditText.getText().toString());
                    intent1.putExtra("description_ss", descriptionEditText.getText().toString());
                    intent1.putExtra("price_ss", priceEditText.getText().toString());
                    intent1.putExtra("rating_ss", rating);
                    intent1.putExtra("speed_ss", speed);
                    intent1.putExtra("busyTime_ss", busyTime);
                    intent1.putExtra("seats_ss", seats);
                    intent1.putExtra("shared_ride_ss", haveSharedRide);
                    intent1.putExtra("loginMode", loginMode);
                    intent1.putExtra("companyID", companyId);
                    intent1.putExtra("update_spaceship", false);
                    intent1.putExtra("reviews_ss", reviews);
                    startActivity(intent1);
                }

            }
        });

        deleteSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkData()) {
                    deleteSpaceShipsData();
                }
            }
        });
    }


    private boolean checkData() {
        if (nameEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (priceEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter price.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (descriptionEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter description",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    // deleting the spaceships
    private void deleteSpaceShipsData() {

        SpaceShip spaceShipToDelete = new SpaceShip(name, description, "", rating
                , seats, services, haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

        AlertDialog.Builder builder = new AlertDialog.Builder(SpaceShipEditorActivity.this);
        builder.setTitle("Delete spaceship")
                .setMessage("Do you want to delete this spaceship?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                                            if (areEqualSpaceShips(spaceShip, spaceShipToDelete)) {
                                                index = counter;
                                            }
                                            counter++;
                                        }
                                    }
                                }

                                // Remove the spaceShip you want to delete
                                if (index != -1) spaceShipArrayList.remove(index);

                                // Set the updated spaceShips back to the company reference
                                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SpaceShipEditorActivity.this, "SpaceShip Deleted...",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(SpaceShipEditorActivity.this, SpaceShipList.class);
                                        intent1.putExtra("loginMode","owner");
                                        intent1.putExtra("companyID", companyId);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle any errors here
                            }
                        });

                    }
                }).show();

    }

    // update the existing spaceship
    private void updateSpaceShipsData() {

        boolean updatedRideSharing = false;
        if (rideSharingTextView.getText().toString().equals("YES")) {
            updatedRideSharing = true;
        }

        SpaceShip updatedSpaceShip = new SpaceShip(nameEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                "", rating, seats, services, updatedRideSharing,
                Long.parseLong(busyTime), Float.parseFloat(priceEditText.getText().toString()), speed, reviews);

        SpaceShip originalSpaceShip = new SpaceShip(name, description, "", rating, seats, services,
                haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SpaceShipEditorActivity.this);
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
                                            if (areEqualSpaceShips(spaceShip, originalSpaceShip)) {
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
                                    Toast.makeText(SpaceShipEditorActivity.this, "Data not updated. Please retry", Toast.LENGTH_SHORT).show();
                                }

                                // Set the updated spaceShips back to the company reference
                                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SpaceShipEditorActivity.this, "SpaceShip Updated...",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(SpaceShipEditorActivity.this, SpaceShipList.class);
                                        intent1.putExtra("loginMode","owner");
                                        intent1.putExtra("companyID", companyId);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle any errors here
                            }
                        });
                    }
                }).show();

    }


    // setting the data to views
    private void setViewData() {

        nameEditText.setText(name);
        priceEditText.setText(price);
//        speedEditText.setText(String.valueOf(speed));
//        rideSharingTextView.setText(String.valueOf(haveSharedRide));
        descriptionEditText.setText(description);
        if (haveSharedRide) rideSharingTextView.setText("YES");
        else rideSharingTextView.setText("NO");
    }

    private boolean areEqualSpaceShips(SpaceShip spaceShip1, SpaceShip spaceShip2) {
        if (!(spaceShip1.getSpaceShipName().equals(spaceShip2.getSpaceShipName()))) {
            return false;
        }
        if (!(spaceShip1.getSpaceShipId().equals(spaceShip2.getSpaceShipId()))) {
            return false;
        }
        if (!(spaceShip1.getBusyTime() == spaceShip2.getBusyTime())) {
            return false;
        }
        if (!(spaceShip1.getPrice() == spaceShip2.getPrice())) {
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