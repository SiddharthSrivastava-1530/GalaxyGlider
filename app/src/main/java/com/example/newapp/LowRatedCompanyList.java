package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

public class LowRatedCompanyList extends Fragment {

    private ArrayList<Company> companyArrayList;
    private SearchView searchCompany;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CompanyAdapter companyAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String loginMode;
    CompanyAdapter.OnCompanyClickListener onCompanyClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_low_rated_company_list,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseApp.initializeApp(getActivity());

        searchCompany = getView().findViewById(R.id.srchCompany_lowRated);

        companyArrayList = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progressbar_lowRated);
        recyclerView = getView().findViewById(R.id.recycler_lowRated);
        swipeRefreshLayout = getView().findViewById(R.id.swip_lowRated);

        Intent intent1 = getActivity().getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        Toast.makeText(getActivity(), loginMode, Toast.LENGTH_SHORT).show();

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
                Intent intent = new Intent(getActivity(), CompanyDetailsActivity.class);
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
                                Float shipRating = Float.parseFloat(spaceShip.getSpaceShipRating());
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
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<Company> arrayList) {
        companyAdapter = new CompanyAdapter(arrayList, getActivity(), onCompanyClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(companyAdapter);
        companyAdapter.notifyDataSetChanged();

    }

}