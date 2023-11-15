package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserTransactionDetailsActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView companyNameTextView;
    private TextView spaceShipNameTextView;
    private TextView transactionIdTextView;
    private TextView fromTextView;
    private TextView seatsConfig;
    private TextView toTextView;
    private TextView distanceTextView;
    private TextView totalCostTextView;
    private TextView timeTextView;
    private TextView isTransactionComplete_tv;
    private TextView completeJourneyTextView;
    private Transaction currentTransaction;
    private String seats;
    private String chosenSeatConfig;
    private ArrayList<Transaction> transactionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_details);

        userEmailTextView = findViewById(R.id.userEmail_transaction_details);
        userNameTextView = findViewById(R.id.userName_transaction_details);
        companyNameTextView = findViewById(R.id.companyName_transaction_details);
        spaceShipNameTextView = findViewById(R.id.spaceShipName_transaction_details);
        fromTextView = findViewById(R.id.from_transaction_details);
        totalCostTextView = findViewById(R.id.price_transaction_details);
        toTextView = findViewById(R.id.to_transaction_details);
        distanceTextView = findViewById(R.id.distance_transaction_details);
        transactionIdTextView = findViewById(R.id.transactionId_transaction_details);
        timeTextView = findViewById(R.id.time_transaction_details);
        isTransactionComplete_tv = findViewById(R.id.isOngoing_transaction_details);
        completeJourneyTextView = findViewById(R.id.complete_transaction_details);
        seatsConfig = findViewById(R.id.seatsChosen_transaction_details);

        Intent intent = getIntent();
        currentTransaction = (Transaction) intent.getSerializableExtra("transaction");
        chosenSeatConfig = currentTransaction.getChosenSeatConfiguration();
        transactionArrayList = new ArrayList<>();

        setDataViews();
        attachSeatsListener();

        if(currentTransaction.isTransactionComplete()) {
            completeJourneyTextView.setVisibility(View.GONE);
        }

        completeJourneyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeats();
            }
        });

    }

    private void setDataViews(){

        spaceShipNameTextView.setText(currentTransaction.getSpaceShipName());
        companyNameTextView.setText(currentTransaction.getCompanyName());
        fromTextView.setText(currentTransaction.getDeparture());
        toTextView.setText(currentTransaction.getDestination());
        distanceTextView.setText(currentTransaction.getDistance());
        totalCostTextView.setText(String.valueOf(currentTransaction.getTotalFare()));
        userNameTextView.setText(currentTransaction.getUserName());
        userEmailTextView.setText(currentTransaction.getUserEmail());
        transactionIdTextView.setText(currentTransaction.getTransactionId());
        timeTextView.setText(String.valueOf(currentTransaction.getTransactionTime()));
        isTransactionComplete_tv.setText(String.valueOf(currentTransaction.isTransactionComplete()));
        seatsConfig.setText(currentTransaction.getChosenSeatConfiguration());

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
                SpaceShip currentSpaceShip = new SpaceShip();

                int index = -1, counter = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot spaceShipSnapshot : dataSnapshot.getChildren()) {
                        SpaceShip spaceShip = spaceShipSnapshot.getValue(SpaceShip.class);
                        if (spaceShip != null) {
                            spaceShipArrayList.add(spaceShip);
                            if (spaceShip.getSpaceShipId().equals(currentTransaction.getSpaceShipId())) {
                                index = counter;
                                currentSpaceShip = spaceShip;
                            }
                            counter++;
                        }
                    }
                }

                // set updatedSeatConfiguration after seats have been vacated.
                currentSpaceShip.setSeatsAvailable(getChangedSeatConfig());

                // Update the spaceShip
                if (index != -1) {
                    spaceShipArrayList.set(index, currentSpaceShip);
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

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Query query = databaseReference;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    transactionArrayList = dataSnapshot.getValue(Customer.class).getTransactions();

                    int position = 0;
                    for(Transaction transaction : transactionArrayList){
                        if(transaction.getTransactionId().equals(currentTransaction.getTransactionId())){
                            currentTransaction.setTransactionComplete(true);
                            transactionArrayList.set(position, currentTransaction);
                        }
                        position += 1;
                    }

                    databaseReference.child("transactions").setValue(transactionArrayList)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            completeJourneyTextView.setVisibility(View.GONE);

                            Intent intent1 = new Intent(UserTransactionDetailsActivity.this,AllTransactionsList.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    // get the changes seat configuration by vacating seat chosen seats
    private String getChangedSeatConfig() {
       String updatedSeatsConfiguration = seats;
        for (int position = 0; position < 12; position++) {
            if (chosenSeatConfig.charAt(position) == '1') {
                updatedSeatsConfiguration = setCharAt(updatedSeatsConfiguration, position, '1');
            } else {
                char character = seats.charAt(position);
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
                                seats = spaceShip.getSeatsAvailable();
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


}