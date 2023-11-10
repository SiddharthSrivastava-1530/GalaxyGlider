package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.SpaceShip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView enterAsUser;
    TextView enterAsOwner;
    TextView enterAsAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterAsUser = findViewById(R.id.textView_as_user);
        enterAsOwner = findViewById(R.id.textView_as_owner);
        enterAsAdmin = findViewById(R.id.textView_as_admin);

        enterAsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                intent.putExtra("loginMode","user");
                startActivity(intent);
            }
        });

        enterAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("loginMode","admin");
                startActivity(intent);
            }
        });

        enterAsOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                intent.putExtra("loginMode","owner");
                startActivity(intent);
            }
        });

    }
}