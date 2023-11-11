package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TotpSecret;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpaceShipEditorActivity extends AppCompatActivity implements Serializable {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private TextView rideSharingTextView;
    private EditText speedEditText;
    private EditText seatsEditText;
    private  TextView addSpaceShipTextView;
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
        setContentView(R.layout.activity_space_ship_editor);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
//        getSupportActionBar().hide();


        nameEditText = findViewById(R.id.spaceshipName_et);
        seatsEditText = findViewById(R.id.spaceship_seats_et);
        priceEditText = findViewById(R.id.spaceship_price_et);
//        rideSharingTextView = findViewById(R.id.spaceShip_rideSharing_edit);
        descriptionEditText = findViewById(R.id.spaceship_desc_et);
//        busyTimeEditText = findViewById(R.id.spaceship_busyTime_et);
        speedEditText = findViewById(R.id.spaceShip_speed_editor);
        addSpaceShipTextView = findViewById(R.id.spaceShip_add);

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

        spaceShipKey = UUID.randomUUID().toString();

        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(SpaceShipEditorActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        if (booleanUpdate) {
            setViewData();
        }

        addSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSpaceShipsData();
            }
        });


    }


    private boolean checkData() {
        if (nameEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (seatsEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter seats.",
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


    //Inflating the menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.space_ship_editor_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this is a new task, hide the "Delete" menu item.
        if (!booleanUpdate) {
            MenuItem menuItem = menu.findItem(R.id.menu_item_del);
            menuItem.setVisible(false);
        }
        return true;

    }

    //Setting what happens when any menu item is clicked.
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_save) {
            if (booleanUpdate && checkData()) {
                updateSpaceShipsData();
            } else if (checkData()) {
                saveSpaceShipsData();
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_item_del) {
            if (booleanUpdate) {
                deleteSpaceShipsData();
            } else {
                startActivity(new Intent(SpaceShipEditorActivity.this, SpaceShipList.class));
                finish();
            }
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // deleting the spaceships
    private void deleteSpaceShipsData() {

        SpaceShip spaceShipToDelete = new SpaceShip(name, description, "", rating
                , seats, haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);

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
                                if(index != -1) spaceShipArrayList.remove(index);

                                // Set the updated spaceShips back to the company reference
                                companyRef.setValue(spaceShipArrayList);

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

        SpaceShip updatedSpaceShip = new SpaceShip(nameEditText.getText().toString(), descriptionEditText.getText().toString(),
                "", rating, seatsEditText.getText().toString(), haveSharedRide,
                Long.parseLong(busyTime), Float.parseFloat(priceEditText.getText().toString()),
                Float.parseFloat(speedEditText.getText().toString()),reviews);

        SpaceShip originalSpaceShip = new SpaceShip(name, description, "", rating, seats,
                haveSharedRide, Long.parseLong(busyTime), Float.parseFloat(price), speed, reviews);


        AlertDialog.Builder builder = new AlertDialog.Builder(SpaceShipEditorActivity.this);
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
                                    spaceShipArrayList.set(index, updatedSpaceShip);
                                } catch (IndexOutOfBoundsException e){
                                    Toast.makeText(SpaceShipEditorActivity.this, "Data not updated. Please retry", Toast.LENGTH_SHORT).show();
                                }

                                // Set the updated spaceShips back to the company reference
                                companyRef.setValue(spaceShipArrayList);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle any errors here
                            }
                        });
                    }
                }).show();

    }

    // save the new spaceship data in database
    private void saveSpaceShipsData() {

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company").child(companyId);
        ArrayList<Review> reviews = new ArrayList<>();
        spaceShip = new SpaceShip(nameEditText.getText().toString(), descriptionEditText.getText().toString(),
                "", "0.0", seatsEditText.getText().toString(), haveSharedRide,
                0, Float.parseFloat(priceEditText.getText().toString()),
                Float.parseFloat(speedEditText.getText().toString()), reviews);

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


    // setting the data to views
    private void setViewData() {

        nameEditText.setText(name);
        priceEditText.setText(price);
        speedEditText.setText(String.valueOf(speed));
//        rideSharingTextView.setText(String.valueOf(haveSharedRide));
        descriptionEditText.setText(description);
        seatsEditText.setText(seats);
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