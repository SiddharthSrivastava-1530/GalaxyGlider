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
    private String companyId;
    private SpaceShip currentSpaceShip;
    private boolean haveRideSharing;

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
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship_ss");
        booleanUpdate = intent.getBooleanExtra("update_spaceship", false);
        companyId = intent.getStringExtra("companyID");


        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(SpaceShipEditorActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();


        if (booleanUpdate) {
            setViewData();
            addSpaceShipTextView.setText("Update Glider");
        } else {
            currentSpaceShip = new SpaceShip();
            deleteSpaceShipTextView.setVisibility(View.GONE);
        }

        rideSharingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rideSharingTextView.getText().toString().equals("YES")) {
                    rideSharingTextView.setText("NO");
                    haveRideSharing = false;
                } else {
                    rideSharingTextView.setText("YES");
                    haveRideSharing = true;
                }
            }
        });

        addSpaceShipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData()){
                    currentSpaceShip.setHaveRideSharing(haveRideSharing);
                    Intent intent1 = new Intent(SpaceShipEditorActivity.this, SelectSlotsActivity.class);
                    intent1.putExtra("spaceship_ss", currentSpaceShip);
                    intent1.putExtra("name_ss", nameEditText.getText().toString());
                    intent1.putExtra("description_ss", descriptionEditText.getText().toString());
                    intent1.putExtra("price_ss", priceEditText.getText().toString());
                    intent1.putExtra("companyID", companyId);
                    intent1.putExtra("update_spaceship", booleanUpdate);
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



    // deleting the spaceships
    private void deleteSpaceShipsData() {

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
                                            if (currentSpaceShip.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
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
                                        Intent intent1 = new Intent(SpaceShipEditorActivity.this, AllSpaceShipsListActivity.class);
                                        intent1.putExtra("loginMode", "owner");
                                        intent1.putExtra("companyID", companyId);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent1);
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



    // check if all the data fields are filled before updating.
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



    // setting the data to views
    private void setViewData() {
        nameEditText.setText(currentSpaceShip.getSpaceShipName());
        priceEditText.setText(String.valueOf(currentSpaceShip.getPrice()));
//        speedEditText.setText(String.valueOf(speed));
//        rideSharingTextView.setText(String.valueOf(haveSharedRide));
        descriptionEditText.setText(currentSpaceShip.getDescription());
        if (currentSpaceShip.isHaveRideSharing()) {
            rideSharingTextView.setText("YES");
            haveRideSharing = true;
        }
        else {
            rideSharingTextView.setText("NO");
            haveRideSharing = false;
        }
    }


}