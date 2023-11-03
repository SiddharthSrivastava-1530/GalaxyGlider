package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    EditText number;
    TextView submit;

    private String username;
    private String useremail;
    private String usernumber;

    private String loginMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name =  findViewById(R.id.name_et);
        email = findViewById(R.id.email_et);
        password = findViewById(R.id.Password_et);
        number = findViewById(R.id.Number_et);
        submit = findViewById(R.id.submit_tv);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");


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
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(SignUpActivity.this,"Please enter your name",
                            Toast.LENGTH_SHORT).show();
                    name.setError("Name is required");
                    name.requestFocus();
                    return;
                }

                //Checking if the useremail is empty and showing error accordingly.
                else if(TextUtils.isEmpty(useremail)){
                    Toast.makeText(SignUpActivity.this,"Please enter your email",
                            Toast.LENGTH_SHORT).show();
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }

                //Checking if email address matches the general pattern of email addresses.
                else if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
                    Toast.makeText(SignUpActivity.this,"Please re-enter your email",
                            Toast.LENGTH_SHORT).show();
                    email.setError("Valid email is required");
                    email.requestFocus();
                    return;
                }

                //Checking if mobile number is empty and showing errors accordingly.
                else if(TextUtils.isEmpty(usernumber)){
                    Toast.makeText(SignUpActivity.this,"Please enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. is required");
                    number.requestFocus();
                    return;
                }

                //Checking if mobile number is not equal to 10 digits and showing errors accordingly.
                else if(usernumber.length()!=10){
                    Toast.makeText(SignUpActivity.this,"Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. should have 10 digits");
                    number.requestFocus();
                    return;
                }
                //Matching the entered mobile number with regular expression defined by us.
                else if(!mobileMatcher.find()){
                    Toast.makeText(SignUpActivity.this,"Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    number.setError("Mobile No. is not valid");
                    number.requestFocus();
                    return;
                }
                //Checking if the password is left empty and showing errors accordingly.
                else if(TextUtils.isEmpty(userpassword)){
                    Toast.makeText(SignUpActivity.this,"Please enter your password",
                            Toast.LENGTH_SHORT).show();
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }

                handleSignUp();
            }
        });
    }




    //Handling Signup using Email and Password.
    void handleSignUp(){

        //Creating user using FirebaseAuth with help of email and password.
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            // Setting display name for the registered user using profile change request.
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            firebaseUser.updateProfile(profileChangeRequest);


                            // Setting data into the database.
                            FirebaseDatabase.getInstance().getReference("users/"+
                                            FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(new Customer(name.getText().toString()
                                            ,number.getText().toString(),
                                            email.getText().toString(),
                                            "",loginMode))

                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                //Sending the verification email.
                                                firebaseUser.sendEmailVerification();

                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up successful. Please verify your email.",
                                                        Toast.LENGTH_LONG).show();

                                                //Navigating to login activity for user to login after verifying email.
                                                Intent intent = new Intent(SignUpActivity.this,CompanyList.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up failed. Please try again.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(SignUpActivity.this,
                                    task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
