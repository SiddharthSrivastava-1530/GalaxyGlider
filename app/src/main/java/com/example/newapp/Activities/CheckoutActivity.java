package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Company;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.example.newapp.utils.DataUpdateServiceSettingsUtil;
import com.example.newapp.utils.ServiceSettingsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private TextView bookRideButton;
    private String userEmail;
    private String userName;
    private String distance;
    private String departure;
    private String destination;
    private String companyId;
    private String chosenSeatConfig;
    private String companyName;
    private String selectedSlotNumber;
    private SpaceShip currentSpaceShip;
    private String currentSeatConfiguration;
    private ArrayList<String> spaceShipTransactionIds;
    private ArrayList<String> userTransactionIds;
    private boolean isRideRecurring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Checkout.preload(getApplicationContext());

        getSupportActionBar().hide();

        bookRideButton = findViewById(R.id.bookRideButton);

        // getting data passed by intent
        Intent intent = getIntent();
        currentSpaceShip = (SpaceShip) intent.getSerializableExtra("spaceship");
        companyId = intent.getStringExtra("companyID");
        chosenSeatConfig = intent.getStringExtra("chosen_seat_config");
        selectedSlotNumber = intent.getStringExtra("slot_number");
        distance = intent.getStringExtra("dist");
        departure = intent.getStringExtra("dept");
        destination = intent.getStringExtra("dest");
        isRideRecurring = intent.getBooleanExtra("isRecurring", false);
        Log.e("recurring ride -->", String.valueOf(isRideRecurring));

//        Intent intent = new Intent(CheckoutActivity.this, UserReviewsActivity.class);
//        intent.putExtra("from", fromLocation.getText().toString());
//        intent.putExtra("to", toLocation.getText().toString());
//        intent.putExtra("date", date.getText().toString());
//        intent.putExtra("time", time.getText().toString());
//        startPayment();
        getUserData();
        getCompanyName();

        attachSpaceShipListener();

        String refId = UUID.randomUUID().toString();
        updateSeats(refId);

    }


    /* update the realtime seat availability and match it with chosenSeatConfiguration of user
     for checking seat availability */
    private void updateSeats(String refId) {

        DatabaseReference companyRef = FirebaseDatabase.getInstance().getReference("company")
                .child(companyId).child("spaceShips");


        // Fetch the existing SpaceShips
        companyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SpaceShip> spaceShipArrayList = new ArrayList<>();
                int index = -1, position = 0;
                boolean canUpdate = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                        SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                        if (spaceShip != null) {
                            spaceShipArrayList.add(spaceShip);
                            if (spaceShip.getSpaceShipId().equals(currentSpaceShip.getSpaceShipId())) {
                                currentSpaceShip = spaceShip;
                                spaceShipTransactionIds = spaceShip.getTransactionIds();
                                if (spaceShipTransactionIds == null) {
                                    spaceShipTransactionIds = new ArrayList<>();
                                }
                                index = position;
                                // fetches current seat configuration and updates current spaceship.
                                getSlotConfiguration();
                                // if seats are available update the seats in currentSpaceShip object.
                                canUpdate = canUpdateSeatsConfiguration();
                            }
                        }
                        position++;
                    }
                }

                if (index != -1 && canUpdate) {
                    setSlotConfiguration();
                    // add current transactionId to spaceShip transaction list.
                    spaceShipTransactionIds.add(refId);
                    currentSpaceShip.setTransactionIds(spaceShipTransactionIds);
                    // add spaceship to arraylist.
                    spaceShipArrayList.set(index, currentSpaceShip);
                    Toast.makeText(CheckoutActivity.this, "Seats booked...", Toast.LENGTH_SHORT).show();

                    // Set the updated spaceShips back to the company reference and sending intent to
                    // journey activity on completion of task.
                    companyRef.setValue(spaceShipArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Set this ride as an ongoing transaction object in database in separate node.
                            Review review = new Review();
                            Transaction transaction = new Transaction(userUID, userName, userEmail, refId, companyId,
                                    companyName, currentSpaceShip.getSpaceShipId(), currentSpaceShip.getSpaceShipName(),
                                    chosenSeatConfig, departure, destination, distance, selectedSlotNumber, "",
                                    System.currentTimeMillis(), currentSpaceShip.getPrice()*Float.parseFloat(distance), false,
                                    isRideRecurring, review);
                            // add transaction to database in node - 'transactions'
                            FirebaseDatabase.getInstance().getReference("transactions/" + refId)
                                    .setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                            // add userTransactionsIds arraylist to 'users' node
                            Log.e("transaction List size", String.valueOf(userTransactionIds.size()));
                            userTransactionIds.add(refId);

                            FirebaseDatabase.getInstance().getReference("users/" + userUID + "/transactionIds")
                                    .setValue(userTransactionIds).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                if (isRideRecurring) {
                                                    DataUpdateServiceSettingsUtil.startRecurringRideService(getApplicationContext(), transaction);
                                                }
                                                ServiceSettingsUtil.startRideService(getApplicationContext(), companyName, currentSpaceShip.getSpaceShipName(), departure, destination, distance);
                                                // Start the journey and move further to review activity.
                                                Intent intent1 = new Intent(CheckoutActivity.this, AllSpaceShipsListActivity.class);
                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent1.putExtra("companyID", companyId);
                                                intent1.putExtra("loginMode", "user");
                                                startActivity(intent1);
                                            }
                                        }
                                    });
                        }
                    });
                } else {
                    Toast.makeText(CheckoutActivity.this, "Chosen configuration unavailable at moment." +
                            " Please retry", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    // update the new seat configuration if its available
    // if not available return false and retain the original seat configuration.
    private boolean canUpdateSeatsConfiguration() {
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                Log.e("-----------> chosen Yes", String.valueOf(position));
                if (currentSeatConfiguration.charAt(position) == '1') {
                    currentSeatConfiguration = setCharAt(currentSeatConfiguration, position, '0');
                    Log.e("-----------> is Available", String.valueOf(position));
                } else {
                    return false;
                }
            }
        }
        if (isRideRecurring) {
            int slotNo = Integer.parseInt(selectedSlotNumber);
            String currentRecurringConfig = currentSpaceShip.getNextSeatConfigurations().get(slotNo);
            for (int position = 0; position < 12; position++) {
                if (chosenSeatConfig.charAt(position) == '1') {
                    if (currentRecurringConfig.charAt(position) == '1') {
                        currentRecurringConfig = setCharAt(currentRecurringConfig, position, '0');
                    } else {
                        return false;
                    }
                }
            }
            ArrayList<String> nextSeatConfig = currentSpaceShip.getNextSeatConfigurations();
            nextSeatConfig.set(slotNo, currentRecurringConfig);
            currentSpaceShip.setNextSeatConfigurations(nextSeatConfig);
        }
        return true;
    }

    private void getSlotConfiguration() {

        if (selectedSlotNumber.equals("0")) {
            currentSeatConfiguration = currentSpaceShip.getSlot1();
        } else if (selectedSlotNumber.equals("1")) {
            currentSeatConfiguration = currentSpaceShip.getSlot2();
        } else if (selectedSlotNumber.equals("2")) {
            currentSeatConfiguration = currentSpaceShip.getSlot3();
        } else if (selectedSlotNumber.equals("3")) {
            currentSeatConfiguration = currentSpaceShip.getSlot4();
        } else if (selectedSlotNumber.equals("4")) {
            currentSeatConfiguration = currentSpaceShip.getSlot5();
        } else if (selectedSlotNumber.equals("5")) {
            currentSeatConfiguration = currentSpaceShip.getSlot6();
        } else if (selectedSlotNumber.equals("6")) {
            currentSeatConfiguration = currentSpaceShip.getSlot7();
        } else if (selectedSlotNumber.equals("7")) {
            currentSeatConfiguration = currentSpaceShip.getSlot8();
        }

    }

    private void setSlotConfiguration() {

        if (selectedSlotNumber.equals("0")) {
            currentSpaceShip.setSlot1(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("1")) {
            currentSpaceShip.setSlot2(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("2")) {
            currentSpaceShip.setSlot3(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("3")) {
            currentSpaceShip.setSlot4(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("4")) {
            currentSpaceShip.setSlot5(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("5")) {
            currentSpaceShip.setSlot6(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("6")) {
            currentSpaceShip.setSlot7(currentSeatConfiguration);
        } else if (selectedSlotNumber.equals("7")) {
            currentSpaceShip.setSlot8(currentSeatConfiguration);
        }

    }


    // set given character at specific position of string.
    private String setCharAt(String string, int position, char ch) {
        char[] charArray = string.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }


    private void attachSpaceShipListener() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"
                + companyId + "/spaceShips");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SpaceShip spaceShip = dataSnapshot.getValue(SpaceShip.class);
                    if (spaceShip != null && spaceShip.getSpaceShipId().equals(currentSpaceShip.getSpaceShipId())) {
                        currentSpaceShip = spaceShip;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

    private void getUserData() {
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.getValue(Customer.class).getName();
                        userEmail = snapshot.getValue(Customer.class).getEmail();
                        userTransactionIds = snapshot.getValue(Customer.class).getTransactionIds();
                        if (userTransactionIds == null) {
                            userTransactionIds = new ArrayList<>();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}