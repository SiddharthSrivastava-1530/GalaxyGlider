package com.example.newapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class CompanyDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView allTextView;
    private ImageView compImageView;
    private String companyId;
    private String loginMode;
    private String companyName;
    private String companyDesc;
    private String companyImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        nameTextView = findViewById(R.id.name_company_details);
        descriptionTextView = findViewById(R.id.desc_company_details);
        allTextView = findViewById(R.id.seeAllSpaceShips_tv_company_details);
        compImageView = findViewById(R.id.img_company_details);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        companyName = intent.getStringExtra("company_name");
        companyDesc = intent.getStringExtra("company_desc");
        companyImageUrl = intent.getStringExtra("company_img");

        Toast.makeText(this, loginMode, Toast.LENGTH_SHORT).show();

        setViewData();


        allTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CompanyDetailsActivity.this, SpaceShipList.class);
                intent1.putExtra("companyID", companyId);
                intent1.putExtra("loginMode", loginMode);
                startActivity(intent1);
            }
        });

    }

    private void setViewData() {

        nameTextView.setText(companyName);
        descriptionTextView.setText(companyDesc);
        Glide.with(getApplicationContext()).load(companyImageUrl).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(compImageView);

    }
}