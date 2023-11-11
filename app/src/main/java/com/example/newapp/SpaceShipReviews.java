package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.newapp.Adapter.CompanyAdapter;
import com.example.newapp.Adapter.ReviewAdapter;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Review;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class SpaceShipReviews extends AppCompatActivity {

    private ArrayList<Review> reviewArrayList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ReviewAdapter reviewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    ReviewAdapter.OnReviewClickLiListener onReviewClickLiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_ship_reviews);

        reviewArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_reviews);
        recyclerView = findViewById(R.id.recycler_reviews);
        swipeRefreshLayout = findViewById(R.id.swip_reviews);

        Intent intent = getIntent();
        reviewArrayList = (ArrayList<Review>) intent.getSerializableExtra("reviews_ss");

        setAdapter(reviewArrayList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAdapter(reviewArrayList);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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