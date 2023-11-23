package com.example.newapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.newapp.DataModel.Transaction;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class RecurringRideReceiver extends BroadcastReceiver {

    private Transaction transaction;

    @Override
    public void onReceive(Context context, Intent intent) {

        transaction = (Transaction) intent.getSerializableExtra("current_recurring_tr");
        createNewRecurringTransaction();

    }

    private void createNewRecurringTransaction() {
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTransactionComplete(false);
        FirebaseDatabase.getInstance().getReference("transactions/" + transaction.getTransactionId())
                .setValue(transaction);
    }

}
