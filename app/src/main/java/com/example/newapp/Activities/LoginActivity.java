package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private TextView sub;
    private TextView signup;
    private String loginMode;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        email = findViewById(R.id.email_login_et);
        pass = findViewById(R.id.password_login_et);
        sub = findViewById(R.id.submit_login_tv);
        signup = findViewById(R.id.signup_tv);
        progressBar = findViewById(R.id.progressBar_login);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("loginMode");

        if (loginMode.equals("admin")) {
            signup.setVisibility(View.GONE);
        }

        //Submit button to login user
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking that text entered in the email and password is empty or not.
                if (email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter valid email and password",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                handleLogin();
            }
        });

        //Signup button navigates the user to MainActivity to register.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(new Intent(LoginActivity.this, SignUpActivity.class));
                intent1.putExtra("loginMode", loginMode);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    //Login using email and password.
    private void handleLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //Checking if the task to sign in user was successful or not.

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String currentUserEmail = firebaseUser.getEmail();
                            //If the user's email is verified then send him to allListsActivity
                            if (firebaseUser.isEmailVerified()) {
                                // path to check for user's existence in his chosen loginMode
                                String key = "users/" + firebaseUser.getUid();
                                if (loginMode.equals("admin")) {
                                    key = "admin/" + firebaseUser.getUid();
                                } else if (loginMode.equals("owner")) {
                                    key = "company/" + firebaseUser.getUid();
                                }

                                // check if user exists in same mode in database.
                                FirebaseDatabase.getInstance().getReference(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            login();
                                            finish();
                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(LoginActivity.this, "It seems you are trying to enter " +
                                                    "in wrong mode. Please enter in correct mode.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                //If the user's email is not verified then send him another email and sign out him.
                                firebaseUser.sendEmailVerification();
                                FirebaseAuth.getInstance().signOut();
                                //With the help of alertDialogBox user can directly open an app to see his emails.
                                showAlertDialogBox();
                            }
                        } else {
                            // If the task is not successful toast the exception.
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showAlertDialogBox() {
        //Building the alertDialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified")
                .setMessage("Please verify your email.You cannot use the app without email verification.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);

                        intent.addCategory(Intent.CATEGORY_APP_EMAIL); // To open an email app

                        // To open email app in new window not within our app so that on pressing back our app does not close.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }

    // save the current loginMode and currentLoginEmail of current logged in user
    // so that we can redirect without login next time on opening of app (if not logged out).
    private void saveLoginMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginMode", loginMode);
        editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        editor.apply();
    }


    private void login() {

        saveLoginMode();
        Toast.makeText(getApplicationContext(), "Logged in successful", Toast.LENGTH_SHORT).show();
        Intent intent1 = null;
        if (loginMode.equals("admin")) {
            intent1 = new Intent(LoginActivity.this, AllListActivity.class);
        } else if (loginMode.equals("owner")) {
            String companyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            intent1 = new Intent(LoginActivity.this, AllSpaceShipsListActivity.class);
            intent1.putExtra("companyID", companyId);
        } else {
            intent1 = new Intent(LoginActivity.this, AllListActivity.class);
        }
        intent1.putExtra("loginMode", loginMode);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);

    }

}