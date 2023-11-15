package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.newapp.Adapter.ReviewAdapter;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    ReviewAdapter.OnReviewClickLiListener onReviewClickLiListener;
    private Spinner spinner;
    final private String filtersUsed[] = {"Sort By", "Rating", "Time"};
    private String spaceShipId;
    private String companyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_reviews);

        getSupportActionBar().hide();

        spinner = findViewById(R.id.spinner_reviews);
        reviewArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_reviews);
        recyclerView = findViewById(R.id.recycler_reviews);

        Intent intent = getIntent();
        spaceShipId = intent.getStringExtra("id_ss");
        companyID = intent.getStringExtra("companyID");


        // setting up spinner for sorting reviews.
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


        onReviewClickLiListener = new ReviewAdapter.OnReviewClickLiListener() {
            @Override
            public void onReviewsClicked(int position) {

            }
        };


    }


    // Set arraylist to given adapter.
    private void setAdapter(ArrayList<Review> arrayList) {
        reviewAdapter = new ReviewAdapter(arrayList, SpaceShipReviews.this, onReviewClickLiListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(SpaceShipReviews.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

    }


    // fetches the update in spaceShipReviews in realtime and sort it as per spinner selection.
    private void getSpaceShipReviews() {

        FirebaseDatabase.getInstance().getReference("company/" + companyID + "/spaceShips")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        reviewArrayList.clear();
                        // get reviewsArrayList from database.
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.getSpaceShipId().equals(spaceShipId)) {
                                reviewArrayList = spaceShip.getReviews();
                            }
                        }

                        // sort the list as per spinner selection
                        if (reviewArrayList != null) {
                            if (spinner.getSelectedItemPosition() == 1) {
                                Collections.sort(reviewArrayList, new Comparator<Review>() {
                                    @Override
                                    public int compare(Review review, Review t1) {
                                        return (-1) * review.getRating().compareTo(t1.getRating());
                                    }
                                });

                            } else if (spinner.getSelectedItemPosition() == 2) {
                                Collections.reverse(reviewArrayList);
                            }
                            // set the sorted list to adapter
                            setAdapter(reviewArrayList);

                        } else {
                            Toast.makeText(SpaceShipReviews.this, "No reviews given yet...",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}