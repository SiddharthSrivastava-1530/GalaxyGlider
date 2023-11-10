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
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.DataModel.Admin;
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
    private TextView moveToLowRated;
    private TextView moveToPendingReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);

        FirebaseApp.initializeApp(this);

        searchCompany = findViewById(R.id.srchCompany);

        companyArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swip);
        moveToLowRated = findViewById(R.id.move_to_low_rated_companies_tv);
        moveToPendingReq = findViewById(R.id.move_to_pending_req_tv);

        Intent intent1 = getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

        getUserData();

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
                Intent intent = new Intent(CompanyList.this, CompanyDetailsActivity.class);
                intent.putExtra("loginMode", loginMode);
                intent.putExtra("companyID", companyArrayList.get(position).getCompanyId());
                intent.putExtra("company_name", companyArrayList.get(position).getName());
                intent.putExtra("company_desc", companyArrayList.get(position).getDescription());
                intent.putExtra("company_img", companyArrayList.get(position).getImageUrl());
                intent.putExtra("company_license", companyArrayList.get(position).getLicenseUrl());
                intent.putExtra("isAuthorised",companyArrayList.get(position).getOperational());
                startActivity(intent);
            }
        };

        moveToPendingReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CompanyList.this,UnauthorisedCompanyList.class);
                intent1.putExtra("loginMode",loginMode);
                startActivity(intent1);
            }
        });

        moveToLowRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CompanyList.this,LowRatedCompanyList.class);
                intent1.putExtra("loginMode",loginMode);
                startActivity(intent1);
            }
        });

        searchCompany.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                companyAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void getCompanies() {
        companyArrayList.clear();
        try {
            FirebaseDatabase.getInstance().getReference("company").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Company company = dataSnapshot.getValue(Company.class);
                        if(company!=null && company.getOperational()){
                            companyArrayList.add(company);
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
                intent.putExtra("update_from_allList", true);
                intent.putExtra("sender_pic", currentUserPic);
                intent.putExtra("sender_name", currentUserName);
                intent.putExtra("sender_number", currentUserEmail);
                startActivity(intent);
            } else if (loginMode.equals("owner")) {
                Intent intent = new Intent(CompanyList.this, CompanyProfileActivity.class);
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
