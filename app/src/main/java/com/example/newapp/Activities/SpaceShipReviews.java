package com.example.newapp.Activities;

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

import com.example.newapp.Adapter.ReviewAdapter;
import com.example.newapp.DataModel.Review;
import com.example.newapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpaceShipReviews extends AppCompatActivity {

    private ArrayList<Review> reviewArrayList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ReviewAdapter reviewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    ReviewAdapter.OnReviewClickLiListener onReviewClickLiListener;
    private Spinner spinner;
    final private String filtersUsed[] = {"Sort By", "Rating", "Time"};
    private ArrayList<Review> backUpReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_reviews);

        getSupportActionBar().hide();

        reviewArrayList = new ArrayList<>();
        spinner = findViewById(R.id.spinner_reviews);
        backUpReviewsList = new ArrayList<>();
        reviewArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_reviews);
        recyclerView = findViewById(R.id.recycler_reviews);
        swipeRefreshLayout = findViewById(R.id.swip_reviews);

        Intent intent = getIntent();
        reviewArrayList = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");

        if (reviewArrayList == null) {
            reviewArrayList = new ArrayList<>();
        }

//        setAdapter(reviewArrayList);
        backUpReviewsList.addAll(reviewArrayList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAdapter(reviewArrayList);
                spinner.setSelection(0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(SpaceShipReviews.this, android.R.layout.simple_spinner_item, filtersUsed);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    reviewArrayList.clear();
                    reviewArrayList.addAll(backUpReviewsList);
                    setAdapter(reviewArrayList);
                } else if (position == 1) {
                    reviewArrayList.clear();
                    reviewArrayList.addAll(backUpReviewsList);
                    Collections.sort(reviewArrayList, new Comparator<Review>() {
                        @Override
                        public int compare(Review review, Review t1) {
                            return (-1) * review.getRating().compareTo(t1.getRating());
                        }
                    });
                    setAdapter(reviewArrayList);
                } else if (position == 2) {
                    reviewArrayList.clear();
                    reviewArrayList.addAll(backUpReviewsList);
                    Collections.sort(reviewArrayList, new Comparator<Review>() {
                        @Override
                        public int compare(Review review, Review t1) {
                            if (review.getTime() == t1.getTime()) {
                                return 0;
                            } else if (review.getTime() > t1.getTime()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                    setAdapter(reviewArrayList);
                }
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

    private void setAdapter(ArrayList<Review> arrayList) {
        reviewAdapter = new ReviewAdapter(arrayList, SpaceShipReviews.this, onReviewClickLiListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(SpaceShipReviews.this));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

    }

}