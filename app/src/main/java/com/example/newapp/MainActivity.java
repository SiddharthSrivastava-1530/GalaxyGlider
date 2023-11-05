package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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



//        ArrayList<SpaceShip> spaceShips = new ArrayList<>();
//        FirebaseDatabase.getInstance().getReference("company/" + "abc/").
//                setValue(new Company("spacex","spacex@gmail.com", "do a travel with us",
//                        "data:image/png;base64,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",
//                        "12345","",true,spaceShips));


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
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
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