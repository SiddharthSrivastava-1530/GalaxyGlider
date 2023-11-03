package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
//                setValue(new Company("spacex","spacex@gmail.com","do a travel with us","","","",true,spaceShips));


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