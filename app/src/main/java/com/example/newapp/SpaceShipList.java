package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.Adapter.SpaceShipAdapter;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.SpaceShip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpaceShipList extends AppCompatActivity {

    private ArrayList<SpaceShip> spaceShipArrayList;
    private Spinner spinner;
    private SearchView searchCompany;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SpaceShipAdapter spaceShipAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    SpaceShipAdapter.OnSpaceShipClickListener onSpaceShipClickListener;
    private FloatingActionButton floatingActionButton;
    private String imageUrl;

    private String name;
    private String description;
    private String ratings;
    private String seats;
    private String price;
    private String speed;
    private String busyTime;
    private Boolean haveSharedRide;
    private String companyId;
    private String loginMode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_list);

        spinner = findViewById(R.id.spinner1_spaceship);
        searchCompany = findViewById(R.id.srchCompany_spaceship);

        spaceShipArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_spaceship);
        recyclerView = findViewById(R.id.recycler_spaceship);
        floatingActionButton = findViewById(R.id.fab_spaceship);
        swipeRefreshLayout = findViewById(R.id.swip1_spaceship);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

        if (loginMode.equals("user")) {
            floatingActionButton.setVisibility(View.GONE);
        }


        // enable adding new spaceship if owner (move to editor activity)
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginMode.equals("owner")) {
                    Intent intent1 = new Intent(SpaceShipList.this, SpaceShipEditorActivity.class);
                    intent1.putExtra("companyID",companyId);
                    startActivity(intent1);
                } else {
                    Toast.makeText(SpaceShipList.this, "You are not authorised to add spaceships", Toast.LENGTH_SHORT).show();
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSpaceShips();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getSpaceShips();

        // Open the details of specific spaceship clicked
        onSpaceShipClickListener = new SpaceShipAdapter.OnSpaceShipClickListener() {
            @Override
            public void onSpaceShipsClicked(int position) {
                Intent intent1 = new Intent(SpaceShipList.this,SpaceShipDetailsActivity.class);
                intent1.putExtra("name_ss", spaceShipArrayList.get(position).getSpaceShipName());
                intent1.putExtra("rating_ss", spaceShipArrayList.get(position).getRatings());
                intent1.putExtra("description_ss", spaceShipArrayList.get(position).getDescription());
                intent1.putExtra("price_ss", String.valueOf(spaceShipArrayList.get(position).getPrice()));
                intent1.putExtra("picUrl_ss", spaceShipArrayList.get(position).getSpaceShipImageUrl());
//                intent1.putExtra("speed_ss",spaceShipArrayList.get(position).getSp());
                intent1.putExtra("busyTime_ss",String.valueOf(spaceShipArrayList.get(position).getBusyTime()));
                intent1.putExtra("seats_ss",spaceShipArrayList.get(position).getSeatAvailability());
                intent1.putExtra("shared_ride_ss",spaceShipArrayList.get(position).isHaveRideSharing());
                intent1.putExtra("loginMode",loginMode);
                intent1.putExtra("companyID",companyId);
                startActivity(intent1);
            }
        };

    }

    private void getSpaceShips() {
        spaceShipArrayList.clear();
        try {

            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company").child(companyId).child("spaceships");

            companyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null) {
                                spaceShipArrayList.add(spaceShip);
                            }
                            setAdapter(spaceShipArrayList);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    // Inflating menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.company_list_menu, menu);
        return true;
    }


    // Setting what happens when any menu item is clicked.
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_profile) {
            // Sending data to profile activity if received.
            // Moving to respective profile activity as per loginMode.
            if (loginMode.equals("user")) {
                Intent intent = new Intent(SpaceShipList.this, UserProfileActivity.class);
                startActivity(intent);
            } else if (loginMode.equals("owner")) {
                Intent intent = new Intent(SpaceShipList.this, CompanyProfileActivity.class);
                startActivity(intent);
            } else {

            }
        }
        return super.onOptionsItemSelected(item);
    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<SpaceShip> arrayList) {
        spaceShipAdapter = new SpaceShipAdapter(arrayList, SpaceShipList.this, onSpaceShipClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(SpaceShipList.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(spaceShipAdapter);
        spaceShipAdapter.notifyDataSetChanged();

    }

    private void getUserData() {
//        try {
//
//            Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

//            // Getting data about user from database.
//            FirebaseDatabase.getInstance().getReference("users/" +
//                            FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                            senderName = snapshot.getValue(Customer.class).getName();
////                            senderEmail = snapshot.getValue(Customer.class).getEmail();
////                            senderPic = snapshot.getValue(Customer.class).getProfilePic();
////                            senderNumber = snapshot.getValue(Customer.class).getNumber();
////                            Toast.makeText(CompanyList.this, senderName, Toast.LENGTH_SHORT).show();
//                            Customer customer = snapshot.getValue(Customer.class);
//                            Log.e("---------------", customer.getEmail());
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
//                    Toast.LENGTH_SHORT).show();
//        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Create a reference to the current user's data
            DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

            // Add a ValueEventListener to retrieve the user data
            currentUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Retrieve user data from the snapshot
                    if (dataSnapshot.exists()) {
                            Customer currentCustomer = dataSnapshot.getValue(Customer.class);
//                            Toast.makeText(CompanyList.this, currentCustomer.getName(), Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}