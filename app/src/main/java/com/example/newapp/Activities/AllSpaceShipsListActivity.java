package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.newapp.Adapter.VPAdapter;
import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AllSpaceShipsListActivity extends AppCompatActivity {

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
    private String companyId;
    private ArrayList<SpaceShip> spaceShipArrayList;
    private boolean isCurrentSlotUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_space_ships_list);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//        // Remember that you should never show the action bar if the
//        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        spaceShipArrayList = new ArrayList<>();

        tabLayout = findViewById(R.id.tablayout_all);
        viewPager = findViewById(R.id.viewpager_all);
        drawerLayout = findViewById(R.id.drawer_layout1_spaceShips_all);
        navigationView = findViewById(R.id.nav_items_spaceShips_all);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.bringToFront();
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");

        // Setting tab layout to show viewPager
        tabLayout.setupWithViewPager(viewPager);

        // Setting up VP adapter and adding fragments to be shown.
        VPAdapter vPadapter = new VPAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vPadapter.addFragment(new SharedRideSpaceShips() , "Shared Gliders");
        vPadapter.addFragment(new SpaceShipList(), "Unshared Gliders");
        viewPager.setAdapter(vPadapter);

        getUserData();
        checkIfNewDay(System.currentTimeMillis());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profile1) {
                    // Sending data to profile activity if received.
                    // Moving to respective profile activity as per loginMode.
                    if (loginMode.equals("admin")) {
                        Intent intent = new Intent(AllSpaceShipsListActivity.this, UserProfileActivity.class);
                        intent.putExtra("update_from_allList", true);
                        intent.putExtra("loginMode", loginMode);
                        intent.putExtra("sender_name", currentUserName);
                        intent.putExtra("sender_number", currentUserEmail);
                        startActivity(intent);
                    } else if (loginMode.equals("owner")) {
                        Intent intent = new Intent(AllSpaceShipsListActivity.this, CompanyProfileActivity.class);
                        intent.putExtra("update_from_allList", false);
                        intent.putExtra("sender_pic", currentUserPic);
                        intent.putExtra("sender_name", currentUserName);
                        intent.putExtra("licenseUrl", currentLicenseUrl);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(AllSpaceShipsListActivity.this, UserProfileActivity.class);
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
                    startActivity(new Intent(AllSpaceShipsListActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                if(item.getItemId() == R.id.your_rides){
                    Intent intent1 = new Intent(AllSpaceShipsListActivity.this, AllTransactionsList.class);
                    startActivity(intent1);
                }
                return false;
            }
        });


    }



    // on Back press show alert dialog box
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(loginMode.equals("owner")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Exit the app");
                builder.setMessage("Are you sure you want to exit the app.");

                // if user chooses 'yes' close the application.
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AllSpaceShipsListActivity.super.onBackPressed();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                super.onBackPressed();
            }
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
            Toast.makeText(AllSpaceShipsListActivity.this, "Slow Internet Connection",
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



    private void checkIfNewDay(long time){
        FirebaseDatabase.getInstance().getReference("company/" + companyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isCurrentSlotUpdated = snapshot.getValue(Company.class).isCurrentSlotUpdated();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Calendar currentDate = Calendar.getInstance();
        int currentDayOfYear = currentDate.get(Calendar.DAY_OF_YEAR);
        int currentYear = currentDate.get(Calendar.YEAR);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        int lastCompletedDayOfYear = sharedPreferences.getInt("lastCompletedDayOfYear", -1);
        int lastCompletedYear = sharedPreferences.getInt("lastCompletedYear", -1);

        if(lastCompletedDayOfYear !=-1 && lastCompletedYear !=-1)
        {
            if (lastCompletedYear < currentYear || lastCompletedDayOfYear < currentDayOfYear) {
                // Day has been complete saving the current date as the last completed date
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("lastCompletedDayOfYear", currentDayOfYear);
                editor.putInt("lastCompletedYear", currentYear);
                editor.putLong("time",time/6000);
                if(!isCurrentSlotUpdated) {
                    changeSlotConfigurationOnNewDay();
                }
                editor.apply();
            } else {
                // Day has not been completed
            }
        } else
        {
            // This is the first time the app is being used saving the current date as the last completed date.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time",time/6000);
            editor.putInt("lastCompletedDayOfYear", currentDayOfYear);
            editor.putInt("lastCompletedYear", currentYear);
            if(!isCurrentSlotUpdated) {
                changeSlotConfigurationOnNewDay();
            }
            editor.apply();
        }
    }



    private void changeSlotConfigurationOnNewDay() {
        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company").child(companyId).child("spaceShips");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    spaceShipArrayList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null) {
                                spaceShipArrayList.add(getChangedNewDayConfig(spaceShip));
                            }
                        }
                    }

                    databaseReference.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                Toast.makeText(AllSpaceShipsListActivity.this,"New day configuration updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AllSpaceShipsListActivity.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    private SpaceShip getChangedNewDayConfig(SpaceShip spaceShip){

        String nextSlotConfig = spaceShip.getNextSlotConfig();
        if(nextSlotConfig.charAt(0)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(0));
        } else {
            spaceShip.setSlot1("000000000000");
        }
        if(nextSlotConfig.charAt(1)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(1));
        } else {
            spaceShip.setSlot2("000000000000");
        }
        if(nextSlotConfig.charAt(2)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(2));
        } else {
            spaceShip.setSlot3("000000000000");
        }
        if(nextSlotConfig.charAt(3)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(3));
        } else {
            spaceShip.setSlot4("000000000000");
        }
        if(nextSlotConfig.charAt(4)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(4));
        } else {
            spaceShip.setSlot5("000000000000");
        }
        if(nextSlotConfig.charAt(5)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(5));
        } else {
            spaceShip.setSlot6("000000000000");
        }
        if(nextSlotConfig.charAt(6)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(6));
        } else {
            spaceShip.setSlot7("000000000000");
        }
        if(nextSlotConfig.charAt(7)=='1'){
            spaceShip.setSlot1(spaceShip.getNextSeatConfigurations().get(7));
        } else {
            spaceShip.setSlot8("000000000000");
        }
        return spaceShip;
    }


}