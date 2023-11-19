package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CompanyDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView allTextView;
    private TextView seeLicenseTextView;
    private ImageView compImageView;
    private String companyId;
    private String loginMode;
    private String companyName;
    private String companyDesc;
    private String companyImageUrl;
    private String companyLicenseUrl;
    private boolean isCompanyAuthorised;
    private TextView authorizeTextView;
    private CardView statusAuthorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        nameTextView = findViewById(R.id.name_company_details);
        descriptionTextView = findViewById(R.id.desc_company_details);
        allTextView = findViewById(R.id.seeAllSpaceShips_tv_company_details);
        compImageView = findViewById(R.id.img_company_details);
        seeLicenseTextView = findViewById(R.id.seePdf_tv_details);
        authorizeTextView = findViewById(R.id.verify_company_details_activity_tv);
        statusAuthorization = findViewById(R.id.cardView3);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        // getting data from intent.
        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");
        companyId = intent.getStringExtra("companyID");
        companyName = intent.getStringExtra("company_name");
        companyDesc = intent.getStringExtra("company_desc");
        companyImageUrl = intent.getStringExtra("company_img");
        companyLicenseUrl = intent.getStringExtra("company_license");
        isCompanyAuthorised = intent.getBooleanExtra("isAuthorised", false);

        // if not in admin mode set authorization, licenseView and statusOfAuthorization
        // functionalities as not available (setting their respective views as invisible.
        if (!(loginMode.equals("admin"))) {
            authorizeTextView.setVisibility(View.GONE);
            seeLicenseTextView.setVisibility(View.GONE);
            statusAuthorization.setVisibility(View.GONE);
        }

        setAuthorizationViews();
        setViewData();


        allTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CompanyDetailsActivity.this, AllSpaceShipsListActivity.class);
                intent1.putExtra("companyID", companyId);
                intent1.putExtra("loginMode", loginMode);
                startActivity(intent1);
            }
        });

        // opening license through intent.
        seeLicenseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.parse(companyLicenseUrl), "application/pdf");
                startActivity(intent1);
            }
        });

        authorizeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizeCompany();
            }
        });
    }


    // set respective views data for showing company details.
    private void setViewData() {

        nameTextView.setText(companyName);
        descriptionTextView.setText(companyDesc);
        Glide.with(getApplicationContext()).load(companyImageUrl).error(R.drawable.account_img)
                .placeholder(R.drawable.account_img).into(compImageView);

    }

    // onClick over authorize -- authorize and un-authorize company as per currentAuthorizationStatus.
    private void authorizeCompany() {
        if(!isCompanyAuthorised) {
            FirebaseDatabase.getInstance().getReference("company/" + companyId + "/operational").setValue(true);
            isCompanyAuthorised = true;
        }
        else {
            FirebaseDatabase.getInstance().getReference("company/" + companyId + "/operational").setValue(false);
            isCompanyAuthorised = false;
        }
        setAuthorizationViews();
    }

    // setting views that provide authorization information.
    private void setAuthorizationViews(){
        if (isCompanyAuthorised) {
            authorizeTextView.setText(R.string.unauthorize_company);
            if(loginMode.equals("user")) {
                allTextView.setVisibility(View.VISIBLE);
            }
        } else {
            authorizeTextView.setText(R.string.authorise_company);
            if(loginMode.equals("user")) {
                allTextView.setVisibility(View.GONE);
            }
        }
    }
}