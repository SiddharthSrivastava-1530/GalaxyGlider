package com.example.newapp.Activities;

import android.app.ProgressDialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.newapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CompanyProfileActivity extends AppCompatActivity {

    private TextView update;
    private TextView name_tv;
    private TextView email_tv;
    private TextView uploadButton;
    private EditText description_et;
    private ImageView imgProfile;
    private ProgressBar progressBar;
    private String licenseUrl;
    private String userPic;
    private Boolean updateFromAllList;
    private Uri imagePath;
    private String loginMode;
    TextView downloadPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        // To prevent collapse of views when typing is ON
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();

        // associating variables with views using corresponding id(s)
        update = findViewById(R.id.update_tv_company_profile);
        imgProfile = findViewById(R.id.uploadImage_b_company_profile);
        progressBar = findViewById(R.id.progressBar_profile_company_profile);
        description_et = findViewById(R.id.description_et_profile);
        uploadButton = findViewById(R.id.uploadPDF_btn);
        downloadPdf = findViewById(R.id.license_pdf_view_);

        name_tv = findViewById(R.id.name_profile_et_company_profile);
        email_tv = findViewById(R.id.email_profile_et_company_profile);

        // getting data passed with intent
        Intent intent = getIntent();
        updateFromAllList = intent.getBooleanExtra("update_from_allList", false);
        licenseUrl = intent.getStringExtra("licenseUrl");
        loginMode = intent.getStringExtra("loginMode");

        // update the view data if already present
        if (updateFromAllList) {
            userPic = intent.getStringExtra("sender_pic");
            name_tv.setText(intent.getStringExtra("sender_name"));
            email_tv.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            description_et.setText(intent.getStringExtra("sender_desc"));
        }

        // set circularProgressDrawable in action to show image being fetched.
        CircularProgressDrawable circularProgressDrawable =
                new CircularProgressDrawable(CompanyProfileActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(getApplicationContext()).load(userPic).error(R.drawable.account_img)
                .placeholder(circularProgressDrawable)
                .into(imgProfile);

        progressBar.setVisibility(View.GONE);

        // Update the description of the owner..
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDescription();
            }
        });

        // send intent for image selection for logo (of company) update
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(CompanyProfileActivity.this)
                        .crop()
                        .compress(512)
                        .maxResultSize(512, 512)    //Final image resolution
                        .start();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFiles();
            }
        });

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(licenseUrl), "application/pdf");
                startActivity(intent);
            }
        });

    }
    private String getMimeType(Uri uri) {
        if (uri.getScheme().equals("file")) {
            // Convert file URI to content URI using FileProvider
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(uri.getPath()));
        }

        ContentResolver contentResolver = getContentResolver();
        return contentResolver.getType(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the image selected in gallery or PDF
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedData = data.getData();
            String mimeType = getMimeType(selectedData);

            try {
                if (mimeType != null && mimeType.startsWith("image/")) {
                    // Selected data is an image
                    imagePath = selectedData;
                    getImageInImageView();
                } else if (mimeType != null && mimeType.startsWith("application/pdf")) {
                    // Selected data is a PDF
                    uploadFiles(selectedData);
                } else {
                    // Handle other types of files or show an error message
                    showToast("Unsupported file type");
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle IOException
                showToast("Error processing the selected file");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //Transform Uri (path) to Bitmap and put image in imageView.
    private void getImageInImageView() throws IOException {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Surrounding the code with try catch block to handle exception (in case image not found!!!)
        imgProfile.setImageBitmap(bitmap);
        uploadImage();
    }

    private void uploadImage() {

        // Progress dialog to show the percentage of image uploaded while uploading image
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString())
                .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            /*To upload photo to firebaseStorage and generating random Unique ID so that no two images have same ID
             .putFile associates the image having unique ID to our user */

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //When uploading of photo is complete

                        // If successfully uploaded then show image uploaded
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                // if task is successful download url of image from storage

                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {  //Downloading takes time

                                    // If url downloaded successfully then call method having url as argument
                                    if (task.isSuccessful()) {
                                        uploadProfilePicture(task.getResult().toString());
                                    }
                                }
                            });
                            Toast.makeText(CompanyProfileActivity.this, "Image Uploaded",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CompanyProfileActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss(); // on completion in either case dismiss process dialog

                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        // Calculating the percentage of image uploaded
                        double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();

                        // Setting up %age of image uploaded on progress dialogue
                        progressDialog.setMessage(" Uploaded " + (int) progress + "%");
                    }
                });
    }

    //Uploading profile picture of user.
    private void uploadProfilePicture(String url) {
        /* After downloading image url from database we update it in realtime database by referencing using user UID(path).
         Path will be : user/Unique UID associated with user/profilePic */
        try {
            FirebaseDatabase.getInstance().getReference("company/" + FirebaseAuth.getInstance().getCurrentUser()
                    .getUid() + "/imageUrl").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }
        userPic = url;
    }

    // update the description to firebaseDatabase.
    private void updateDescription() {
        String description = "";
        if (description_et != null) {
            description = description_et.getText().toString();
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company/" + FirebaseAuth.getInstance().getCurrentUser()
                .getUid() + "/description");
        databaseReference.setValue(description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CompanyProfileActivity.this, "description updated...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // select pdf file for uploading as company License.
    private void selectFiles() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF files..."), 1);
    }

    // uploading company License to firebase storage.
    // Fetching licenseUrl of pdf stored in firebase and setting url over the database on company class.
    private void uploadFiles(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");

        StorageReference reference = FirebaseStorage.getInstance().getReference("License/" +
                UUID.randomUUID().toString() + ".pdf");

        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri url = uriTask.getResult();

                        FirebaseDatabase.getInstance().getReference("company/" + FirebaseAuth.getInstance().getCurrentUser()
                                .getUid() + "/licenseUrl").setValue(url.toString());

                        licenseUrl = url.toString();

                        Toast.makeText(CompanyProfileActivity.this, "License Uploaded for verification.. ",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        progressDialog.show();
                    }
                });
    }

}