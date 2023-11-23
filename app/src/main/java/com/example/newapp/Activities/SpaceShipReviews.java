package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.Adapter.ReviewAdapter;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpaceShipReviews extends AppCompatActivity {

    private ArrayList<Review> reviewArrayList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ReviewAdapter reviewAdapter;
    private Spinner spinner;
    final private String filtersUsed[] = {"Sort By", "Rating", "Time"};
    private String spaceShipId;
    private String companyID;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView no_reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_reviews);

        getSupportActionBar().hide();

        spinner = findViewById(R.id.spinner_reviews);
        reviewArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_reviews);
        recyclerView = findViewById(R.id.recycler_reviews);
        swipeRefreshLayout = findViewById(R.id.swip_ref_review_list);
        no_reviews = findViewById(R.id.no_reviews);

        Intent intent = getIntent();
        spaceShipId = intent.getStringExtra("id_ss");
        companyID = intent.getStringExtra("companyID");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSpaceShipReviews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getSpaceShipReviews();


        // setting up spinner for sorting reviews.
//        setAdapter(reviewArrayList);

        ArrayAdapter arrayAdapter = new ArrayAdapter(SpaceShipReviews.this, android.R.layout.simple_spinner_item, filtersUsed);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSpaceShipReviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    // Set arraylist to given adapter.
    private void setAdapter(ArrayList<Review> arrayList) {
        if(arrayList.size()==0){
            no_reviews.setVisibility(View.VISIBLE);
        }
        else{
            no_reviews.setVisibility(View.INVISIBLE);
        }
        reviewAdapter = new ReviewAdapter(arrayList, SpaceShipReviews.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(SpaceShipReviews.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

    }


    private void getSpaceShipReviews() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions");
        databaseReference.orderByChild("spaceShipId").equalTo(spaceShipId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Transaction transaction = dataSnapshot.getValue(Transaction.class);
                            if (transaction != null && transaction.getReview().getReview()!=null && !transaction.getReview().getReview().isEmpty()) {
                                reviewArrayList.add(transaction.getReview());
                            }
                        }
                        setAdapter(reviewArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}