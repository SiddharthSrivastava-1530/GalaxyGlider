package com.example.newapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.newapp.Adapter.VPAdapter;
import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Token;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class AllListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPic;
    private String currentLicenseUrl;
    private String loginMode;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getSupportActionBar().hide();

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        drawerLayout = findViewById(R.id.drawer_layout1_companies);
        navigationView = findViewById(R.id.nav_items_companies);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.bringToFront();
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");

        // Setting tab layout to show viewPager
        tabLayout.setupWithViewPager(viewPager);

        // Setting up VP adapter and adding fragments to be shown.
        VPAdapter vPadapter = new VPAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vPadapter.addFragment(new CompanyList(), "Companies");


        if (loginMode.equals("admin")) {
            vPadapter.addFragment(new UnauthorisedCompanyList(), "Pending");
            vPadapter.addFragment(new LowRatedCompanyList(), "Low Rated");
        }
        viewPager.setAdapter(vPadapter);

        getUserData();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profile1) {
                    // Sending data to profile activity if received.
                    // Moving to respective profile activity as per loginMode.
                    if (loginMode.equals("admin")) {
                        Intent intent = new Intent(AllListActivity.this, UserProfileActivity.class);
                        intent.putExtra("update_from_allList", true);
                        intent.putExtra("loginMode", loginMode);
                        intent.putExtra("sender_pic", currentUserPic);
                        intent.putExtra("sender_name", currentUserName);
                        intent.putExtra("sender_number", currentUserEmail);
                        startActivity(intent);
                    } else if (loginMode.equals("owner")) {
                        Intent intent = new Intent(AllListActivity.this, CompanyProfileActivity.class);
                        intent.putExtra("update_from_allList", false);
                        intent.putExtra("sender_pic", currentUserPic);
                        intent.putExtra("sender_name", currentUserName);
                        intent.putExtra("licenseUrl", currentLicenseUrl);
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
                if (item.getItemId() == R.id.logout1) {
                    eraseLoginMode();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AllListActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                if(item.getItemId() == R.id.your_rides){
                    if(loginMode.equals("user")){
                        Intent intent1 = new Intent(AllListActivity.this, AllTransactionsList.class);
                        startActivity(intent1);
                    }
                }
                if(item.getItemId() == R.id.share1){
                    // Creating a share intent
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my awesome app!");
                    // shareIntent.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                }
                return false;
            }
        });

        if(loginMode.equals("admin")) {
            UpdateToken();
        }

    }

    //Updating the user tokens to send notifications
    private void UpdateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            return;
                        }
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        //Adding the token to the database so that it can be retrieved to send notifications to the user.
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }

    // on Back press show alert dialog box
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Exit the app");
            builder.setMessage("Are you sure you want to exit the app.");

            // if user chooses 'yes' close the application.
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AllListActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // to get the current user data (name,email,profilePicUrl,etc) for respective loginMode.
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
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (loginMode.equals("admin")) {
                                currentUserName = snapshot.getValue(Admin.class).getName();
                                currentUserEmail = snapshot.getValue(Admin.class).getEmail();
                                currentUserPic = snapshot.getValue(Company.class).getImageUrl();
                            } else if (loginMode.equals("owner")) {
                                currentUserName = snapshot.getValue(Company.class).getName();
                                currentUserEmail = snapshot.getValue(Company.class).getEmail();
                                currentUserPic = snapshot.getValue(Company.class).getImageUrl();
                                currentLicenseUrl = snapshot.getValue(Company.class).getLicenseUrl();
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

    // erasing loginMode and email current login stored in SharedPreferences on logout click.
    private void eraseLoginMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMode", "");
        editor.putString("email", "");
        editor.apply();
    }

}