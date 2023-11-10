package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.SpaceShip;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LowRatedCompanyList extends AppCompatActivity {

    private ArrayList<Company> companyArrayList;
    private Spinner spinner;
    private SearchView searchCompany;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CompanyAdapter companyAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String loginMode;
    CompanyAdapter.OnCompanyClickListener onCompanyClickListener;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPic;
    private String currentUserNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_rated_company_list);

        FirebaseApp.initializeApp(this);

        spinner = findViewById(R.id.spinner1_lowRated);
        searchCompany = findViewById(R.id.srchCompany_lowRated);

        companyArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_lowRated);
        recyclerView = findViewById(R.id.recycler_lowRated);
        swipeRefreshLayout = findViewById(R.id.swip_lowRated);

        Intent intent1 = getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

        getUserData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLowRatedCompanies();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getLowRatedCompanies();

        onCompanyClickListener = new CompanyAdapter.OnCompanyClickListener() {
            @Override
            public void onCompaniesClicked(int position) {
                Intent intent = new Intent(LowRatedCompanyList.this, CompanyDetailsActivity.class);
                intent.putExtra("loginMode", loginMode);
                intent.putExtra("companyID", companyArrayList.get(position).getCompanyId());
                intent.putExtra("company_name", companyArrayList.get(position).getName());
                intent.putExtra("company_desc", companyArrayList.get(position).getDescription());
                intent.putExtra("company_img", companyArrayList.get(position).getImageUrl());
                intent.putExtra("company_license", companyArrayList.get(position).getLicenseUrl());
                intent.putExtra("isAuthorised", companyArrayList.get(position).getOperational());
                startActivity(intent);
            }
        };

    }

    private void getLowRatedCompanies() {
        companyArrayList.clear();
        try {
            FirebaseDatabase.getInstance().getReference("company").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ArrayList<SpaceShip> spaceShipArrayList = dataSnapshot.getValue(Company.class).getSpaceShips();
                        boolean isLowRated = false;
                        if (spaceShipArrayList != null) {
                            for (SpaceShip spaceShip : spaceShipArrayList) {
                                Float shipRating = Float.parseFloat(spaceShip.getRatings());
                                if (shipRating <= 1 && shipRating > 0) {
                                    isLowRated = true;
                                }
                            }
                        }
                        if (isLowRated) {
                            companyArrayList.add(dataSnapshot.getValue(Company.class));
                        }

                    }
                    setAdapter(companyArrayList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LowRatedCompanyList.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<Company> arrayList) {
        companyAdapter = new CompanyAdapter(arrayList, LowRatedCompanyList.this, onCompanyClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(LowRatedCompanyList.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(companyAdapter);
        companyAdapter.notifyDataSetChanged();

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
                Intent intent = new Intent(LowRatedCompanyList.this, UserProfileActivity.class);
                intent.putExtra("update_from_allList", true);
                intent.putExtra("sender_pic", currentUserPic);
                intent.putExtra("sender_name", currentUserName);
                intent.putExtra("sender_number", currentUserEmail);
                startActivity(intent);
            } else if (loginMode.equals("owner")) {
                Intent intent = new Intent(LowRatedCompanyList.this, CompanyProfileActivity.class);
                intent.putExtra("update_from_allList", false);
                intent.putExtra("sender_pic", currentUserPic);
                intent.putExtra("sender_name", currentUserName);
                intent.putExtra("sender_number", currentUserNumber);
                startActivity(intent);
            } else {

            }
        }
        return super.onOptionsItemSelected(item);
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
                                currentUserNumber = snapshot.getValue(Admin.class).getNumber();
                            } else if (loginMode.equals("owner")) {
                                currentUserName = snapshot.getValue(Company.class).getName();
                                currentUserEmail = snapshot.getValue(Company.class).getEmail();
                                currentUserPic = snapshot.getValue(Company.class).getImageUrl();
                                currentUserNumber = snapshot.getValue(Company.class).getNumber();
                            } else if (loginMode.equals("user")) {
                                currentUserName = snapshot.getValue(Customer.class).getName();
                                currentUserEmail = snapshot.getValue(Customer.class).getEmail();
                                currentUserPic = snapshot.getValue(Customer.class).getProfilePic();
                                currentUserNumber = snapshot.getValue(Customer.class).getNumber();
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