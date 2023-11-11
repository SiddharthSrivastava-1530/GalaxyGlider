package com.example.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.newapp.Adapter.VPAdapter;
import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPic;
    private String loginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        // Setting tab layout to show viewPager
        tabLayout.setupWithViewPager(viewPager);

        // Setting up VP adapter and adding fragments to be shown.
        VPAdapter vPadapter = new VPAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vPadapter.addFragment(new CompanyList(), "Authorized");
        if (loginMode.equals("user")) {
            tabLayout.setVisibility(View.GONE);
        }

        if (loginMode.equals("admin")) {
            vPadapter.addFragment(new UnauthorisedCompanyList(), "Pending");
            vPadapter.addFragment(new LowRatedCompanyList(), "Low Rated");
        }
        viewPager.setAdapter(vPadapter);

        getUserData();

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
            if (loginMode.equals("admin")) {
                Intent intent = new Intent(AllListActivity.this, UserProfileActivity.class);
                intent.putExtra("update_from_allList", true);
                intent.putExtra("loginMode", loginMode);
                intent.putExtra("sender_name", currentUserName);
                intent.putExtra("sender_number", currentUserEmail);
                startActivity(intent);
            } else if (loginMode.equals("owner")) {
                Intent intent = new Intent(AllListActivity.this, CompanyProfileActivity.class);
                intent.putExtra("update_from_allList", false);
                intent.putExtra("sender_pic", currentUserPic);
                intent.putExtra("sender_name", currentUserName);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AllListActivity.this, UserProfileActivity.class);
                intent.putExtra("update_from_allList", true);
                intent.putExtra("sender_pic", currentUserPic);
                intent.putExtra("sender_name", currentUserName);
                intent.putExtra("sender_number", currentUserEmail);
                intent.putExtra("loginMode", loginMode);
                startActivity(intent);
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
                            } else if (loginMode.equals("owner")) {
                                currentUserName = snapshot.getValue(Company.class).getName();
                                currentUserEmail = snapshot.getValue(Company.class).getEmail();
                                currentUserPic = snapshot.getValue(Company.class).getImageUrl();
                            } else if (loginMode.equals("user")) {
                                currentUserName = snapshot.getValue(Customer.class).getName();
                                currentUserEmail = snapshot.getValue(Customer.class).getEmail();
                                currentUserPic = snapshot.getValue(Customer.class).getProfilePic();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AllListActivity.this, "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }

}