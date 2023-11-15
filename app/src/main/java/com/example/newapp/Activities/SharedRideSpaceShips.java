package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private boolean currentUserAuthStatus;
    private String companyId;
    private String loginMode;
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


        Intent intent = getActivity().getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");


        if (!loginMode.equals("owner")) {
            floatingActionButton.setVisibility(View.GONE);
        }

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
                intent1.putExtra("name_ss", spaceShipArrayList.get(position).getSpaceShipName());
                intent1.putExtra("id_ss", spaceShipArrayList.get(position).getSpaceShipId());
                intent1.putExtra("rating_ss", spaceShipArrayList.get(position).getSpaceShipRating());
                intent1.putExtra("description_ss", spaceShipArrayList.get(position).getDescription());
                intent1.putExtra("price_ss", String.valueOf(spaceShipArrayList.get(position).getPrice()));
                intent1.putExtra("speed_ss", spaceShipArrayList.get(position).getSpeed());
                intent1.putExtra("busyTime_ss", String.valueOf(spaceShipArrayList.get(position).getBusyTime()));
                intent1.putExtra("seats_ss", spaceShipArrayList.get(position).getSeatsAvailable());
                intent1.putExtra("shared_ride_ss", spaceShipArrayList.get(position).isHaveRideSharing());
                intent1.putExtra("reviews_ss", spaceShipArrayList.get(position).getReviews());
                intent1.putExtra("loginMode", loginMode);
                intent1.putExtra("companyID", companyId);
                intent1.putExtra("services_ss", spaceShipArrayList.get(position).getServicesAvailable());
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
                getSpaceShipsWithSharedRide(newText);
                return false;
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, filtersUsed);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSpaceShipsWithSharedRide(searchSpaceship.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        searchSpaceship.setQuery("", false);
        getSpaceShipsWithSharedRide("");
    }


    private void getSpaceShipsWithSharedRide(String userQuery) {
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
            query.addValueEventListener(new ValueEventListener() {
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


}