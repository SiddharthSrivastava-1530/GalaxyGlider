package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Company;
import com.example.newapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class InvoiceActivity extends AppCompatActivity {
    private TextView saveAndPrint;
    private long invoiceNo;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    private Bitmap bmp;
    private Bitmap scaledBitmap;
    private String userName;
    private String userEmail;
    private String source, destination;
    private String distance;
    private String spaceShipName;
    private String price;
    private String companyId;
    private String refId;
    private String companyName;
    private String spaceShipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        spaceShipName = intent.getStringExtra("name_ss");
        spaceShipId = intent.getStringExtra("id_ss");
        price = intent.getStringExtra("price_ss");
        companyId = intent.getStringExtra("companyID");
        refId = intent.getStringExtra("refId");
        source = intent.getStringExtra("source");
        destination = intent.getStringExtra("destination");
        distance = intent.getStringExtra("distance");
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        scaledBitmap = Bitmap.createScaledBitmap(bmp, 250, 60, false);
        saveAndPrint = findViewById(R.id.GenerateInvoice);

        getCompanyName();

        saveAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPdf();
                Toast.makeText(InvoiceActivity.this, "Invoice Generated and downloaded", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(InvoiceActivity.this, SpaceShipList.class);
                intent1.putExtra("loginMode", "user");
                intent1.putExtra("companyID", companyId);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });

    }


    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    // showing confirmation dialog to user onBackPress.
    private void showExitConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Exit the process!!");
        builder.setMessage("Are you sure you want to go back to spaceship Lists");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(InvoiceActivity.this, SpaceShipList.class);
                intent1.putExtra("loginMode", "user");
                intent1.putExtra("companyID", companyId);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void printPdf() {
        int y;
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(0, 0, 0));
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250, 350, 1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        canvas.drawBitmap(scaledBitmap, 0, 0, paint);

        paint.setTextSize(18f);
        paint.setTypeface(Typeface.SERIF);
        paint.setColor(Color.rgb(227, 240, 237));
        canvas.drawText("Welcome to Galaxy Glider", 12, 35, paint);

        paint.setTextSize(8.5f);
        linePaint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0, 0, 0));

        //  Details
        y = 75;
        canvas.drawText("Invoice No :  ", 20, y, paint);
        canvas.drawText("Date & Time :  ", 20, y + 12, paint);
        canvas.drawText("Status :  ", 20, y + 24, paint);
        y += 36;
        canvas.drawText("Customer Name :  ", 20, y, paint);
        canvas.drawText("Email :  ", 20, y + 12, paint);
        canvas.drawText("From :  ", 20, y + 24, paint);
        canvas.drawText("To :  ", 20, y + 36, paint);
        canvas.drawText("Total Distance :  ", 20, y + 48, paint);
        canvas.drawText("Company :  ", 20, y + 60, paint);
        canvas.drawText("SpaceShip :  ", 20, y + 72, paint);


        // get details from database/ intent
        String name = userName;
        String mail = userEmail;
        String from = source;
        String to = destination;
        long dis = Long.parseLong(distance);
        String company = companyName;
        String spaceShip = spaceShipName;


        y = 75;
        canvas.drawText(String.valueOf(invoiceNo + 1), 140, y, paint);
        canvas.drawText(datePatternFormat.format(new Date().getTime()), 140, y + 12, paint);
        canvas.drawText("Paid", 140, y + 24, paint);
        y += 36;
        canvas.drawText(name, 140, y, paint);
        canvas.drawText(mail, 140, y + 12, paint);
        canvas.drawText(from, 140, y + 24, paint);
        canvas.drawText(to, 140, y + 36, paint);
        canvas.drawText(String.valueOf(dis) + " LightYears", 140, y + 48, paint);
        canvas.drawText(company, 140, y + 60, paint);
        canvas.drawText(spaceShip, 140, y + 72, paint);

        y += 82;
        canvas.drawLine(10, y, 240, y, linePaint);
        paint.setTextSize(13f);
        canvas.drawText("Fair Calculation :", 20, y + 20, paint);
        paint.setTextSize(8.5f);
        y += 40;

        // amount calculation ---- Dynamic Fair
        double amount = 1248f;

        canvas.drawText("Basic Pay ", 20, y, paint);
        canvas.drawText("20rs/LightYear", 120, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(decimalFormat.format(amount)), 230, y, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Service Charges", 20, y + 12, paint);
        canvas.drawText("Tax 18%", 120, y + 12, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount * 18 / 100), 230, y + 12, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Additional Charges", 20, y + 24, paint);
        canvas.drawText("Space Tax 4%", 120, y + 24, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount * 4 / 100), 230, y + 24, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Trafic Cost", 20, y + 36, paint);
        canvas.drawText("Variable", 120, y + 36, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(amount * 2 / 100), 230, y + 36, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        double totalAmount = amount + amount * 18 / 100 + amount * 4 / 100 + amount * 2 / 100;
        canvas.drawText("Total", 20, y + 53, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(decimalFormat.format(totalAmount), 230, y + 53, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(10, y + 63, 240, y + 63, linePaint);

        //TOTAL
        y += 63;
        paint.setTextSize(12f);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount Paid :  " + decimalFormat.format(totalAmount), 230, y + 15, paint);
        paint.setTextAlign(Paint.Align.LEFT);


        // Creating file
        myPdfDocument.finishPage(myPage);
        String pdfName = spaceShip + name + ".pdf";

        File file = new File(getExternalFilesDir("/"), pdfName);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));

            // Upload the file to Firebase Storage
            uploadPdfToFirebaseStorage(file);

            // Uncomment the code below if you want to open a new activity after uploading
            // Intent intent = new Intent(InvoiceActivity.this, SpaceShipList.class);
            // startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();

    }

    private void uploadPdfToFirebaseStorage(File file) {
        // Get Firebase Storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to 'pdfs/pdfName'
        StorageReference pdfRef = storageRef.child("pdfs/" + file.getName());

        // Create upload task
        UploadTask uploadTask = pdfRef.putFile(Uri.fromFile(file));

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            exception.printStackTrace();
        }).addOnSuccessListener(taskSnapshot -> {
            // Handle successful uploads
            // You can get the download URL of the uploaded file
            pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");
                startActivity(intent);
                // Now you can use the downloadUrl as needed
                // For example, you might want to save this URL in your database or use it in your app
                // for further processing.
                // Note: The URL is only valid for the duration of the user session unless you save it.
            });
        });
    }


    private void getCompanyName() {

        FirebaseDatabase.getInstance().getReference("company/" + companyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        companyName = snapshot.getValue(Company.class).getName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}