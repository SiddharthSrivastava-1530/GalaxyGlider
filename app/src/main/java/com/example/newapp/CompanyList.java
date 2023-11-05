package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CompanyList extends AppCompatActivity {

    private ArrayList<Company> companyArrayList;
    private Spinner spinner;
    private SearchView searchCompany;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CompanyAdapter companyAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String loginMode;
    CompanyAdapter.OnCompanyClickListener onCompanyClickListener;
    private String senderName;
    private String senderEmail;
    private String senderPic;
    private String senderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);

        FirebaseApp.initializeApp(this);

        spinner = findViewById(R.id.spinner1);
        searchCompany = findViewById(R.id.srchCompany);

        companyArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swip);

        Intent intent1 = getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

//        getUserData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCompanies();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getCompanies();

        onCompanyClickListener = new CompanyAdapter.OnCompanyClickListener() {
            @Override
            public void onCompaniesClicked(int position) {
                Intent intent = new Intent(CompanyList.this, SpaceShipList.class);
                String companyId = companyArrayList.get(position).getCompanyId();
                intent.putExtra("companyID", companyId);
                intent.putExtra("loginMode", loginMode);
                startActivity(intent);
            }
        };

    }

    private void getCompanies() {
        companyArrayList.clear();
        try {
            FirebaseDatabase.getInstance().getReference("company").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        companyArrayList.add(dataSnapshot.getValue(Company.class));
                    }
                    setAdapter(companyArrayList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CompanyList.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<Company> arrayList) {
        companyAdapter = new CompanyAdapter(arrayList, CompanyList.this, onCompanyClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(CompanyList.this));
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
                Intent intent = new Intent(CompanyList.this, UserProfileActivity.class);
                startActivity(intent);
            } else if (loginMode.equals("owner")) {
                Intent intent = new Intent(CompanyList.this, CompanyProfileActivity.class);
                startActivity(intent);
            } else {

            }
        }
        return super.onOptionsItemSelected(item);
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
