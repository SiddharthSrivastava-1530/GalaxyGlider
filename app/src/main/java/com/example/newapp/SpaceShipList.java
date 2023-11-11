package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.SpaceShipAdapter;
import com.example.newapp.DataModel.Admin;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpaceShipList extends AppCompatActivity {

    private ArrayList<SpaceShip> spaceShipArrayList;
    private Spinner spinner;
    private SearchView searchSpaceship;
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
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPic;
    private String currentLicenseUrl;
    private boolean currentUserAuthStatus;
    private String companyId;
    private String loginMode;
    private String currentUserDescription;
    final private String filtersUsed[] = {"Sort By", "Rating" , "Price"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        spinner = findViewById(R.id.spinner1_spaceship);

        searchSpaceship = findViewById(R.id.srchCompany_spaceship);
        spaceShipArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_spaceship);
        recyclerView = findViewById(R.id.recycler_spaceship);
        floatingActionButton = findViewById(R.id.fab_spaceship);
        swipeRefreshLayout = findViewById(R.id.swip1_spaceship);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

        if (!loginMode.equals("owner")) {
            floatingActionButton.setVisibility(View.GONE);
        }

        getUserData();

        // enable adding new spaceship if owner (move to editor activity)
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUserAuthStatus) {
                    if (loginMode.equals("owner")) {
                        Intent intent1 = new Intent(SpaceShipList.this, SpaceShipEditorActivity.class);
                        intent1.putExtra("companyID", companyId);
                        startActivity(intent1);
                    }
                } else {
                    Toast.makeText(SpaceShipList.this, "You are not authorised to add spaceships. " +
                            "Please upload your license and wait for admin authorisation.", Toast.LENGTH_LONG).show();
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSpaceShips("");
                swipeRefreshLayout.setRefreshing(false);
                spinner.setSelection(0);
            }
        });

        // Open the details of specific spaceship clicked
        onSpaceShipClickListener = new SpaceShipAdapter.OnSpaceShipClickListener() {
            @Override
            public void onSpaceShipsClicked(int position) {
                Intent intent1 = new Intent(SpaceShipList.this, SpaceShipDetailsActivity.class);
                intent1.putExtra("name_ss", spaceShipArrayList.get(position).getSpaceShipName());
                intent1.putExtra("rating_ss", spaceShipArrayList.get(position).getSpaceShipRating());
                intent1.putExtra("description_ss", spaceShipArrayList.get(position).getDescription());
                intent1.putExtra("price_ss", String.valueOf(spaceShipArrayList.get(position).getPrice()));
                intent1.putExtra("speed_ss", spaceShipArrayList.get(position).getSpeed());
                intent1.putExtra("busyTime_ss", String.valueOf(spaceShipArrayList.get(position).getBusyTime()));
                intent1.putExtra("seats_ss", spaceShipArrayList.get(position).getSeatAvailability());
                intent1.putExtra("shared_ride_ss", spaceShipArrayList.get(position).isHaveRideSharing());
                intent1.putExtra("reviews_ss", spaceShipArrayList.get(position).getReviews());
                intent1.putExtra("loginMode", loginMode);
                intent1.putExtra("companyID", companyId);
                startActivity(intent1);
            }
        };

        searchSpaceship.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                getSpaceShips(newText);
                return false;
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(SpaceShipList.this, android.R.layout.simple_spinner_item, filtersUsed);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSpaceShips(searchSpaceship.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    private void getSpaceShips(String userQuery) {
        spaceShipArrayList.clear();
        try {
            int spinnerPosition = spinner.getSelectedItemPosition();
            String child = "";
            if(spinnerPosition == 1){
                child = "spaceShipRating";
            }
            if(spinnerPosition == 2){
                child = "price";
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company").child(companyId).child("spaceShips");
            Query query = databaseReference;
            if(!child.isEmpty()){
                query = databaseReference.orderByChild(child);
            }
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null) {
                                if(spaceShip.getSpaceShipName().toLowerCase().contains(userQuery.toLowerCase())){
                                    spaceShipArrayList.add(spaceShip);
                                }
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
                Intent intent1 = new Intent(SpaceShipList.this, UserProfileActivity.class);
                intent1.putExtra("update_from_allList", true);
                intent1.putExtra("sender_pic", currentUserPic);
                intent1.putExtra("sender_name", currentUserName);
                intent1.putExtra("sender_number", currentUserEmail);
                startActivity(intent1);
            } else if (loginMode.equals("owner")) {
                Intent intent1 = new Intent(SpaceShipList.this, CompanyProfileActivity.class);
                intent1.putExtra("update_from_allList", true);
                intent1.putExtra("sender_pic", currentUserPic);
                intent1.putExtra("sender_name", currentUserName);
                intent1.putExtra("sender_desc", currentUserDescription);
                intent1.putExtra("licenseUrl", currentLicenseUrl);
                intent1.putExtra("loginMode", loginMode);
                startActivity(intent1);
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
        try {
            String key = "";
            if (loginMode.equals("admin")) {
                key = "admin";
            } else if (loginMode.equals("owner")) {
                key = "company";
            } else if (loginMode.equals("user")) {
                key = "users";
            }

            // Getting data about user from database.
            FirebaseDatabase.getInstance().getReference(key + "/" +
                            FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (loginMode.equals("admin")) {
                                currentUserName = snapshot.getValue(Admin.class).getName();
                                currentUserEmail = snapshot.getValue(Admin.class).getEmail();
                            } else if (loginMode.equals("owner")) {
                                currentUserName = snapshot.getValue(Company.class).getName();
                                currentUserEmail = snapshot.getValue(Company.class).getEmail();
                                currentUserPic = snapshot.getValue(Company.class).getImageUrl();
                                currentUserDescription = snapshot.getValue(Company.class).getDescription();
                                currentLicenseUrl = snapshot.getValue(Company.class).getLicenseUrl();
                                currentUserAuthStatus = snapshot.getValue(Company.class).getOperational();
                            } else if (loginMode.equals("user")) {
                                currentUserName = snapshot.getValue(Customer.class).getName();
                                currentUserEmail = snapshot.getValue(Customer.class).getEmail();
                                currentUserPic = snapshot.getValue(Customer.class).getProfilePic();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }

}