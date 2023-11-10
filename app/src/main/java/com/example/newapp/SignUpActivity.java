package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Admin;
import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.SpaceShip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.SingleDateSelector;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText number;
    private TextView submit;
    private String username;
    private String useremail;
    private String usernumber;
    private String loginMode;
    private TextView loginView;
    private boolean isCorrectLoginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name_et);
        email = findViewById(R.id.email_et);
        password = findViewById(R.id.Password_et);
        number = findViewById(R.id.Number_et);
        submit = findViewById(R.id.submit_tv);
        loginView = findViewById(R.id.login_sign_up_activity_tv);

        Intent intent1 = getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        // If already logged in then open the specific activity.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            getCurrentUserLoginMode();

            Intent intent = null;
            if(isCorrectLoginMode) {
                if (loginMode.equals("owner")) {

                    String companyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    intent = new Intent(SignUpActivity.this, SpaceShipList.class);
                    intent.putExtra("companyID", companyId);

                } else {
                    intent = new Intent(SignUpActivity.this, CompanyList.class);
                }
            }
            if (intent != null) {
                intent.putExtra("loginMode", loginMode);
                startActivity(intent);
                finish();
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = name.getText().toString();
                useremail = email.getText().toString();
                String userpassword = password.getText().toString();
                usernumber = number.getText().toString();

                //Validate Mobile Number using Matcher and Pattern with the help of regular expression.

                //[6-9] means first digit can be 6,7,8,9;
                // [0-9]{9} means rest 9 digits can be from 0 to 9.
                String mobileRegex = "[6-9][0-9]{9}";

                //mobileMatcher matches the regular expression with mobile number.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(usernumber);

                //Checking if the username is empty and showing error accordingly.
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your name",
                            Toast.LENGTH_SHORT).show();
                    name.setError("Name is required");
                    name.requestFocus();
                    return;
                }

                //Checking if the userEmail is empty and showing error accordingly.
                else if (TextUtils.isEmpty(useremail)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your email",
                            Toast.LENGTH_SHORT).show();
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                //Checking if email address matches the general pattern of email addresses.
                else if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your email",
                            Toast.LENGTH_SHORT).show();
                    email.setError("Valid email is required");
                    email.requestFocus();
                    return;
                }

                //Checking if mobile number is empty and showing errors accordingly.
                else if (TextUtils.isEmpty(usernumber)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. is required");
                    number.requestFocus();
                    return;
                }

                //Checking if mobile number is not equal to 10 digits and showing errors accordingly.
                else if (usernumber.length() != 10) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. should have 10 digits");
                    number.requestFocus();
                    return;
                }
                //Matching the entered mobile number with regular expression defined by us.
                else if (!mobileMatcher.find()) {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. is not valid");
                    number.requestFocus();
                    return;
                }
                //Checking if the password is left empty and showing errors accordingly.
                else if (TextUtils.isEmpty(userpassword)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password",
                            Toast.LENGTH_SHORT).show();
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                if (loginMode.equals("user")) {
                    handleUserSignUp();
                } else if (loginMode.equals("owner")) {
                    handleCompanySignUp();
                }
            }
        });

        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SignUpActivity.this, LoginActivity.class);
                intent1.putExtra("loginMode", loginMode);
                startActivity(intent1);
            }
        });


    }

    // Handle company SignUp using email and password;
    private void handleCompanySignUp() {

        //Creating user using FirebaseAuth with help of email and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            // Setting display name for the registered user using profile change request.
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            firebaseUser.updateProfile(profileChangeRequest);

                            saveLoginMode();

                            // Setting data into the database.
                            ArrayList<SpaceShip> spaceShips = new ArrayList<>();
                            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("company/" + key)
                                    .setValue(new Company(name.getText().toString(),
                                            email.getText().toString(), number.getText().toString(),
                                            loginMode, key, "", "", "",
                                            false, spaceShips))

                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                //Sending the verification email.
                                                firebaseUser.sendEmailVerification();

                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up successful. Please verify your email.",
                                                        Toast.LENGTH_LONG).show();

                                                //Navigating to login activity for user to login after verifying email.
                                                Intent intent = new Intent(SignUpActivity.this, SpaceShipList.class);
                                                intent.putExtra("companyID", key);
                                                intent.putExtra("loginMode", loginMode);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                                finish();

                                            } else {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up failed. Please try again.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //Handling Signup using Email and Password.
    void handleUserSignUp() {

        //Creating user using FirebaseAuth with help of email and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            // Setting display name for the registered user using profile change request.
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            firebaseUser.updateProfile(profileChangeRequest);

                            saveLoginMode();

                            // Setting data into the database.
                            FirebaseDatabase.getInstance().getReference("users/" +
                                            FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new Customer(name.getText().toString(), number.getText().toString(),
                                            email.getText().toString(), "", loginMode))

                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                //Sending the verification email.
                                                firebaseUser.sendEmailVerification();

                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up successful. Please verify your email.",
                                                        Toast.LENGTH_LONG).show();

                                                //Navigating to login activity for user to login after verifying email.
                                                Intent intent = new Intent(SignUpActivity.this, CompanyList.class);
                                                intent.putExtra("loginMode", loginMode);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                                finish();

                                            } else {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up failed. Please try again.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void getCurrentUserLoginMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);

        String prevLoginMode = sharedPreferences.getString("loginMode", "");
        String prevLoginEmail = sharedPreferences.getString("email", "");
        String currentUserMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (prevLoginEmail.equals(currentUserMail) && prevLoginMode.equals(loginMode)) {
            isCorrectLoginMode = true;
        } else {
            isCorrectLoginMode = false;
        }
    }

    private void saveLoginMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMode",loginMode);
        editor.putString("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
        editor.apply();
    }

}
