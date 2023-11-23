package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.newapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SharedRideSpaceShips extends Fragment {

    private ArrayList<SpaceShip> spaceShipArrayList;
    private Spinner spinner;
    private SearchView searchSpaceship;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SpaceShipAdapter spaceShipAdapter;
    SpaceShipAdapter.OnSpaceShipClickListener onSpaceShipClickListener;
    private FloatingActionButton floatingActionButton;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPic;
    private String currentLicenseUrl;
    private boolean currentUserAuthStatus;
    private String companyId;
    private String loginMode;
    private String currentUserDescription;
    private SwipeRefreshLayout swipeRefreshLayout;
    final private String filtersUsed[] = {"Sort By", "Rating", "Price"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_shared_ride_space_ships, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        spinner = getView().findViewById(R.id.spinner1_spaceship_shared_ride);

        searchSpaceship = getView().findViewById(R.id.srchCompany_spaceship_shared_ride);
        spaceShipArrayList = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progressbar_spaceship_shared_ride);
        recyclerView = getView().findViewById(R.id.recycler_spaceship_shared_ride);
        floatingActionButton = getView().findViewById(R.id.fab_spaceship_shared_ride);
        swipeRefreshLayout = getView().findViewById(R.id.swip_ref_shared_ride_list);

        Intent intent = getActivity().getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");


        if (!loginMode.equals("owner")) {
            floatingActionButton.setVisibility(View.GONE);
        }

        getUserData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSpaceShips(searchSpaceship.getQuery().toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getSpaceShips(searchSpaceship.getQuery().toString());

        // enable adding new spaceship if owner (move to editor activity)
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserAuthStatus) {
                    if (loginMode.equals("owner")) {
                        Intent intent1 = new Intent(getActivity(), SpaceShipEditorActivity.class);
                        intent1.putExtra("companyID", companyId);
                        startActivity(intent1);
                    }
                } else {
                    Toast.makeText(getActivity(), "You are not authorised to add spaceships. " +
                            "Please upload your license and wait for admin authorisation.", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Open the details of specific spaceship clicked
        onSpaceShipClickListener = new SpaceShipAdapter.OnSpaceShipClickListener() {
            @Override
            public void onSpaceShipsClicked(int position) {
                Intent intent1 = new Intent(getActivity(), SpaceShipDetailsActivity.class);
                intent1.putExtra("spaceship_ss", spaceShipArrayList.get(position));
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

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, filtersUsed);
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


    private void getSpaceShips(String userQuery) {
        spaceShipArrayList.clear();
        try {
            int spinnerPosition = spinner.getSelectedItemPosition();
            String child = "";
            if (spinnerPosition == 1) {
                child = "spaceShipRating";
            }
            if (spinnerPosition == 2) {
                child = "price";
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company").child(companyId).child("spaceShips");
            Query query = databaseReference;
            if (!child.isEmpty()) {
                query = databaseReference.orderByChild(child);
            }
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    spaceShipArrayList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.isHaveRideSharing()) {
                                if (spaceShip.getSpaceShipName().toLowerCase().contains(userQuery.toLowerCase())) {
                                    spaceShipArrayList.add(spaceShip);
                                }
                            }
                        }
                        if(spinnerPosition == 1){
                            Collections.reverse(spaceShipArrayList);
                        }
                        setAdapter(spaceShipArrayList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<SpaceShip> arrayList) {
        spaceShipAdapter = new SpaceShipAdapter(arrayList, getActivity(), onSpaceShipClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(spaceShipAdapter);
        spaceShipAdapter.notifyDataSetChanged();

    }

    // to get the current user data (name,email,profilePicUrl,etc) for respective loginMode.
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
            Toast.makeText(getActivity(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }

}