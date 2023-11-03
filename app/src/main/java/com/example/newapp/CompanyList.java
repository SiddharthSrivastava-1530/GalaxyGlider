package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.DataModel.Company;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    CompanyAdapter.OnCompanyClickListener onCompanyClickListener;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);

        spinner = findViewById(R.id.spinner1);
        searchCompany = findViewById(R.id.srchCompany);

        companyArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recycler);
        floatingActionButton = findViewById(R.id.fab);
        swipeRefreshLayout = findViewById(R.id.swip);

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
                Toast.makeText(CompanyList.this, "hello dear", Toast.LENGTH_LONG).show();
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
                        Log.e(" -----------> " , dataSnapshot.getValue(Company.class).getName());
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

}