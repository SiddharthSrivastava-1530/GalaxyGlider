package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Data;
import com.example.newapp.DataModel.NotificationSender;
import com.example.newapp.R;
import com.example.newapp.utils.APIService;
import com.example.newapp.utils.Client;
import com.example.newapp.utils.MyResponse;
import com.example.newapp.utils.ServiceSettingsUtil;
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

import android.widget.RatingBar;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTransactionDetailsActivity extends AppCompatActivity {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    private Bitmap bmp;
    private Bitmap scaledBitmap;
    private TextView reviews_et;
    private TextView reviews_tv;
    private TextView submitReview_tv;
    private RatingBar ratingBar;
    private TextView companyNameTextView;
    private TextView spaceShipNameTextView;
    private TextView transactionIdTextView;
    private TextView fromTextView;
    private TextView toTextView;
    private TextView distanceTextView;
    private TextView totalCostTextView;
    private TextView isTransactionComplete_tv;
    private TextView completeJourneyTextView;
    private TextView endRecurringRideTextView;
    private TextView invoiceTextView;
    private Transaction currentTransaction;
    private SpaceShip transactionSpaceShip;
    private String chosenSeatConfig;
    private String currentSeatConfiguration;
    private ArrayList<Transaction> transactionArrayList;
    private Float rating;
    private String invoiceUrl;

    private ScrollView scrollView;

    private TextView rating_and_review_tv;

    private TextView status_tv;

    private TextView invoiceInfo;

    private TextView invoiceLink;

    private TextView line_tv;

    private APIService apiService;

    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_details);

        getSupportActionBar().hide();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        token ="c_dGJ7q4TBeVhkJdHGr7U-:APA91bFd4-WX3dP1-EIF_WeZ9TtGrOf5Vvb5TkBb8uiGZo40EFC1CJhG9UBCXHjMoItUiDxvDovUyzD1NDifSJWlZb56oKfmViYtNjpXLDsRWe3XxBltDCK5uSrt-Ldjx5kWogMmT7EU";

//        sendNotifications(token,"Low Rated Companies","There are some low rated companies.");

        companyNameTextView = findViewById(R.id.companyName_transaction_details);
        spaceShipNameTextView = findViewById(R.id.spaceShipName_transaction_details);
        fromTextView = findViewById(R.id.from_transaction_details);
        totalCostTextView = findViewById(R.id.price_transaction_details);
        toTextView = findViewById(R.id.to_transaction_details);
        distanceTextView = findViewById(R.id.distance_transaction_details);
        transactionIdTextView = findViewById(R.id.transactionId_transaction_details);
        isTransactionComplete_tv = findViewById(R.id.isOngoing_transaction_details);
        completeJourneyTextView = findViewById(R.id.complete_transaction_details);
        reviews_et = findViewById(R.id.user_review_et);
        reviews_tv = findViewById(R.id.user_review_tv);
        ratingBar = findViewById(R.id.ratingBar);
        submitReview_tv = findViewById(R.id.submit_review_tv);
        rating_and_review_tv = findViewById(R.id.rating_and_review_tv);
        scrollView = findViewById(R.id.scrollView3);
        status_tv = findViewById(R.id.status_tv);
        invoiceInfo = findViewById(R.id.textView19);
        invoiceLink = findViewById(R.id.invoice_tv);
        line_tv = findViewById(R.id.line_tv);
        endRecurringRideTextView = findViewById(R.id.recurring_ride_end_tv);
        invoiceTextView = findViewById(R.id.invoice_tv);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        scaledBitmap = Bitmap.createScaledBitmap(bmp, 250, 60, false);

        Intent intent = getIntent();
        currentTransaction = (Transaction) intent.getSerializableExtra("transaction");
        chosenSeatConfig = currentTransaction.getChosenSeatConfiguration();
        transactionArrayList = new ArrayList<>();

        setDataViews();
        attachSeatsListener();

        if(!currentTransaction.isTransactionRecurring()){
            endRecurringRideTextView.setVisibility(View.GONE);
        }

        if (currentTransaction.isTransactionComplete()) {
            completeJourneyTextView.setVisibility(View.GONE);
            status_tv.setVisibility(View.GONE);
            invoiceLink.setVisibility(View.VISIBLE);
            invoiceInfo.setVisibility(View.VISIBLE);
            line_tv.setVisibility(View.VISIBLE);

            if (currentTransaction.getReview().getTime() == 0) {
                reviews_tv.setVisibility(View.GONE);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                reviews_et.setVisibility(View.GONE);
                submitReview_tv.setVisibility(View.GONE);
                ratingBar.setFocusable(false);
                ratingBar.setIsIndicator(true);
            }
        } else {
            reviews_et.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            submitReview_tv.setVisibility(View.GONE);
            reviews_tv.setVisibility(View.GONE);
            invoiceTextView.setVisibility(View.GONE);
            rating_and_review_tv.setVisibility(View.GONE);
            line_tv.setVisibility(View.GONE);
        }

        completeJourneyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeats();
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float chosenRating, boolean fromUser) {
                rating = chosenRating;
            }
        });

        submitReview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReviews();
            }
        });

        endRecurringRideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endRecurringRide();
            }
        });

        invoiceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(currentTransaction.getInvoiceUrl()), "application/pdf");
                if(!currentTransaction.getInvoiceUrl().isEmpty()) {
                    startActivity(intent);
                } else {
                    Toast.makeText(UserTransactionDetailsActivity.this, "No invoice available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setDataViews() {

        spaceShipNameTextView.setText(currentTransaction.getSpaceShipName());
        companyNameTextView.setText(currentTransaction.getCompanyName());
        fromTextView.setText(currentTransaction.getDeparture());
        toTextView.setText(currentTransaction.getDestination());
        distanceTextView.setText(currentTransaction.getDistance()+" ly");
        totalCostTextView.setText("$"+String.valueOf(currentTransaction.getTotalFare()));
        transactionIdTextView.setText(currentTransaction.getTransactionId());
        String transaction_complete = String.valueOf(currentTransaction.isTransactionComplete());
        if(transaction_complete.equals("false")){
            isTransactionComplete_tv.setText("Ongoing");
        }
        else{
            isTransactionComplete_tv.setText("");
        }

        if (currentTransaction.getReview().getTime() > 0) {
            ratingBar.setRating(Float.parseFloat(currentTransaction.getReview().getRating()));
            reviews_tv.setText(currentTransaction.getReview().getReview());
        }

    }


    // vacate the seats and update it on database.
    private void updateSeats() {

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                .child(currentTransaction.getCompanyId()).child("spaceShips");

        // Fetch the existing spaceShips
        companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                        SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                        if (spaceShip != null) {
                            if (spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                // set updatedSeatConfiguration after seats have been vacated.
                                transactionSpaceShip = spaceShip;
                                setSeatsVacated();
                                spaceShipArrayList.add(transactionSpaceShip);
                            } else {
                                spaceShipArrayList.add(spaceShip);
                            }
                        }
                    }
                }

                // Set the updated spaceShips back to the company reference
                companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateTransactionStatus();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void updateTransactionStatus() {

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions")
                    .child(currentTransaction.getTransactionId());

            databaseReference.child("transactionComplete").setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            completeJourneyTextView.setVisibility(View.GONE);
                            printPdf();
                            ServiceSettingsUtil.stopRideService(getApplicationContext());
                            Toast.makeText(UserTransactionDetailsActivity.this, "Invoice downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(UserTransactionDetailsActivity.this, AllTransactionsList.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    private void updateReviews() {

        try {
            String reviewString = "";
            if (reviews_et != null) {
                reviewString = reviews_et.getText().toString();
            }

            Review newReview = new Review(reviewString, String.valueOf(rating), currentTransaction.getUserName(), currentTransaction.getUserEmail(),
                    System.currentTimeMillis());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions")
                    .child(currentTransaction.getTransactionId());

            databaseReference.child("review").setValue(newReview)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateSpaceShipRating();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    // get the changes seat configuration by vacating seat chosen seats
    private String getChangedSeatConfig() {
        String updatedSeatsConfiguration = currentSeatConfiguration;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '1');
            } else {
                char character = currentSeatConfiguration.charAt(position);
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, character);
            }
        }
        return updatedSeatsConfiguration;
    }


    // set character at given index in the string
    private String setCharAt(String str, int i, char ch) {
        char[] charArray = str.toCharArray();
        charArray[i] = ch;
        return new String(charArray);
    }


    // fetch the updates in seat configuration in realtime.
    private void attachSeatsListener() {

        try {
            DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                    .child(currentTransaction.getCompanyId()).child("spaceShips");

            companyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                            SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && currentTransaction.getSpaceShipId().equals(spaceShip.getSpaceShipId())) {
                                transactionSpaceShip = spaceShip;
                                getSlotConfiguration();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Slow Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }

    }


    private void getSlotConfiguration() {

        if (currentTransaction.getSlotNo().equals("0")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot1();
        } else if (currentTransaction.getSlotNo().equals("1")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot2();
        } else if (currentTransaction.getSlotNo().equals("2")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot3();
        } else if (currentTransaction.getSlotNo().equals("3")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot4();
        } else if (currentTransaction.getSlotNo().equals("4")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot5();
        } else if (currentTransaction.getSlotNo().equals("5")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot6();
        } else if (currentTransaction.getSlotNo().equals("6")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot7();
        } else if (currentTransaction.getSlotNo().equals("7")) {
            currentSeatConfiguration = transactionSpaceShip.getSlot8();
        }

    }


    private void setSeatsVacated() {

        if (currentTransaction.getSlotNo().equals("0")) {
            transactionSpaceShip.setSlot1(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("1")) {
            transactionSpaceShip.setSlot2(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("2")) {
            transactionSpaceShip.setSlot3(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("3")) {
            transactionSpaceShip.setSlot4(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("4")) {
            transactionSpaceShip.setSlot5(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("5")) {
            transactionSpaceShip.setSlot6(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("6")) {
            transactionSpaceShip.setSlot7(getChangedSeatConfig());
        } else if (currentTransaction.getSlotNo().equals("7")) {
            transactionSpaceShip.setSlot8(getChangedSeatConfig());
        }

    }


    private String updatedCompanyRating(SpaceShip currentSpaceShip) {
        float reviewCount = 0;
        if (currentSpaceShip.getTransactionIds() != null) {
            reviewCount = currentSpaceShip.getTransactionIds().size();
        }
        float currentRating = Float.parseFloat(currentSpaceShip.getSpaceShipRating());
        return String.valueOf(((currentRating * reviewCount) + rating) / (reviewCount + 1));
    }


    private void updateSpaceShipRating() {


        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId() + "/spaceShips")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                spaceShip.setSpaceShipRating(updatedCompanyRating(spaceShip));
                                Log.e("updated comp rating", updatedCompanyRating(spaceShip));
                            }
                            spaceShipArrayList.add(spaceShip);
                        }

                        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId()
                                        + "/spaceShips")
                                .setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent1 = new Intent(UserTransactionDetailsActivity.this, AllTransactionsList.class);
                                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent1);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
        String name = currentTransaction.getUserName();
        String mail = currentTransaction.getUserEmail();
        String from = currentTransaction.getDeparture();
        String to = currentTransaction.getDestination();
        long dis = Long.parseLong(currentTransaction.getDistance());
        String company = currentTransaction.getCompanyName();
        String spaceShip = currentTransaction.getSpaceShipName();


        y = 75;
        canvas.drawText(String.valueOf(currentTransaction.getTransactionId() + 1), 140, y, paint);
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
        String pdfName = name + currentTransaction.getTransactionId() + ".pdf";

        File file = new File(getExternalFilesDir("/"), pdfName);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));

            // Uploading the file to Firebase Storage
            uploadPdfToFirebaseStorage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();

    }

    private void uploadPdfToFirebaseStorage(File file) {
        // Get Firebase Storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to 'pdfs/pdfName'
        StorageReference pdfRef = storageRef.child("invoice_pdfs/" + file.getName());

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
                invoiceUrl = uri.toString();

                uploadInvoiceToDatabase();

            });
        });
    }


    private void uploadInvoiceToDatabase() {

        FirebaseDatabase.getInstance().getReference("transactions/" + currentTransaction.getTransactionId() +
                "/invoiceUrl").setValue(invoiceUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                invoiceTextView.setVisibility(View.VISIBLE);
            }
        });

    }


    private void endRecurringRide() {


        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId() + "/spaceShips")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                            if (spaceShip != null && spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                spaceShip.setNextSeatConfigurations(endingRecurringRide(spaceShip));
                            }
                            spaceShipArrayList.add(spaceShip);
                        }

                        FirebaseDatabase.getInstance().getReference("company/" + currentTransaction.getCompanyId()
                                        + "/spaceShips")
                                .setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        updateRecurringStatus();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private ArrayList<String> endingRecurringRide(SpaceShip spaceShip) {
        int slotNo = Integer.parseInt(currentTransaction.getSlotNo());
        ArrayList<String> nextSeatConfig = spaceShip.getNextSeatConfigurations();
        String seats = nextSeatConfig.get(slotNo);
        for(int position=0; position<12;position++) {
            if(currentTransaction.getChosenSeatConfiguration().charAt(position)=='1') {
                seats = setCharAt(seats, position, '1');
            }
        }
        nextSeatConfig.set(slotNo,seats);
        return nextSeatConfig;
    }


    private void updateRecurringStatus() {

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("transactions")
                    .child(currentTransaction.getTransactionId());

            databaseReference.child("transactionRecurring").setValue(false)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            endRecurringRideTextView.setVisibility(View.GONE);
                            Toast.makeText(UserTransactionDetailsActivity.this, "Recurring ride " +
                                    "seats vacated.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    //This Method Sends the notifications combining all class of
    //SendNotificationPack Package work together
    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call,
                                   Response<MyResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().success != 1) {
                        Log.e("UserTransactionDetailsActivity","Sorry admin could not be informed. Please try again later.");
                    }else {
                        Log.e("UserTransactionDetailsActivity","Admin has been informed.");
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }

}