package com.example.newapp.Activities;

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
    private  TextView addSpaceShipTextView;
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
//        getSupportActionBar().hide();

//
        nameEditText = findViewById(R.id.spaceshipName_et);
        priceEditText = findViewById(R.id.spaceship_price_et);
        rideSharingTextView = findViewById(R.id.spaceship_sharing_et);
        descriptionEditText = findViewById(R.id.spaceship_desc_et);
////        busyTimeEditText = findViewById(R.id.spaceship_busyTime_et);
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


        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(SpaceShipEditorActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        if (booleanUpdate) {
            setViewData();
        }

        rideSharingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rideSharingTextView.getText().toString().equals("YES")){
                    rideSharingTextView.setText("NO");
                    haveSharedRide=false;
                }
                else{
                    rideSharingTextView.setText("YES");
                    haveSharedRide=true;
                }
            }
        });
        addSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                saveSpaceShipsData();
                Intent intent1 = new Intent(SpaceShipEditorActivity.this,SeatConfigurationActivity.class);
                if(booleanUpdate) {
                    intent1.putExtra("name_ss", name);
                    intent1.putExtra("description_ss", description);
                    intent1.putExtra("price_ss", price);

                } else {
                    intent1.putExtra("name_ss", nameEditText.getText().toString());
                    intent1.putExtra("description_ss", descriptionEditText.getText().toString());
                    intent1.putExtra("price_ss", priceEditText.getText().toString());
                }
                intent1.putExtra("rating_ss", rating);
                intent1.putExtra("speed_ss", speed);
                intent1.putExtra("busyTime_ss",busyTime);
                intent1.putExtra("seats_ss",seats);
                intent1.putExtra("shared_ride_ss",haveSharedRide);
                intent1.putExtra("loginMode",loginMode);
                intent1.putExtra("companyID",companyId);
                intent1.putExtra("update_spaceship",true);
                intent1.putExtra("reviews_ss", reviews);
                intent1.putExtra("update_spaceship", false);
                startActivity(intent1);
            }
        });


    }

//
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
//                saveSpaceShipsData();
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

        SpaceShip updatedSpaceShip = new SpaceShip(nameEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                "", rating, seats, haveSharedRide,
                Long.parseLong(busyTime), Float.parseFloat(priceEditText.getText().toString()), speed,reviews);

        SpaceShip originalSpaceShip = new SpaceShip(name, description, "", rating, seats,
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
                                    if(index != -1) spaceShipArrayList.set(index, updatedSpaceShip);
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


    // setting the data to views
    private void setViewData() {

        nameEditText.setText(name);
        priceEditText.setText(price);
//        speedEditText.setText(String.valueOf(speed));
//        rideSharingTextView.setText(String.valueOf(haveSharedRide));
        descriptionEditText.setText(description);
    }

    private Boolean areEqualSpaceShips(SpaceShip spaceShip1, SpaceShip spaceShip2) {
        if (!(spaceShip1.getSpaceShipName().equals(spaceShip2.getSpaceShipName()))) {
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