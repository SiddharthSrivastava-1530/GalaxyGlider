package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.newapp.R;
import com.google.android.material.navigation.NavigationView;

public class RideBillsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_bills);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_items);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_drawer, R.string.close_nav_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.bringToFront();
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profile1) {
                    Toast.makeText(RideBillsActivity.this, "home", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.contact1) {
                    Toast.makeText(RideBillsActivity.this, "contact", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.your_rides) {
                    Toast.makeText(RideBillsActivity.this, "galler", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.share1) {
                    Toast.makeText(RideBillsActivity.this, "profile", Toast.LENGTH_SHORT).show();
                }
                if(item.getItemId() == R.id.rate_us){
                    Toast.makeText(RideBillsActivity.this, "rate us", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.logout1) {
                    Toast.makeText(RideBillsActivity.this, "share", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.setting1) {
                    Toast.makeText(RideBillsActivity.this, "share", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}