package com.example.newapp.Activities;

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

import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText confirmPassword;
    private EditText passwordEditText;
    private TextView submit;
    private String username;
    private String useremail;
    private String loginMode;
    private TextView loginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name_et);
        email = findViewById(R.id.email_et);
        confirmPassword = findViewById(R.id.confirm_password_et);
        passwordEditText = findViewById(R.id.password_et);
        submit = findViewById(R.id.submit_tv);
        loginView = findViewById(R.id.login_sign_up_activity_tv);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        Intent intent1 = getIntent();
        loginMode = intent1.getStringExtra("loginMode");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = name.getText().toString();
                useremail = email.getText().toString();
                String userPassword = passwordEditText.getText().toString();
                String userConfirmedPassword = confirmPassword.getText().toString();

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
                else if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your password",
                            Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("Mobile No. is required");
                    passwordEditText.requestFocus();
                    return;
                }
                //Checking if the password is left empty and showing errors accordingly.
                else if (TextUtils.isEmpty(userConfirmedPassword)) {
                    Toast.makeText(SignUpActivity.this, "Please confirm your password",
                            Toast.LENGTH_SHORT).show();
                    confirmPassword.setError("Password is required");
                    confirmPassword.requestFocus();
                    return;
                }
                // Checking if password and confirmedPassword are not same
                else if (!(userConfirmedPassword.equals(userPassword))) {
                    Toast.makeText(SignUpActivity.this, "Password & confirmed password are not the same",
                            Toast.LENGTH_SHORT).show();
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
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("loginMode", loginMode);
                startActivity(intent1);
            }
        });


    }

    // Handle company SignUp using email and password;
    private void handleCompanySignUp() {

        //Creating user using FirebaseAuth with help of email and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                        passwordEditText.getText().toString())
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
                                            email.getText().toString(), loginMode, key,
                                            "", "", "",
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
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
                        passwordEditText.getText().toString())
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
                                    .setValue(new Customer(name.getText().toString(), "",
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
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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

    private void saveLoginMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMode", loginMode);
        editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        editor.apply();
    }

}
