package com.example.newapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newapp.Activities.UserTransactionDetailsActivity;
import com.example.newapp.Activities.UserTransactionList;
import com.example.newapp.Adapter.TransactionAdapter;
import com.example.newapp.DataModel.Customer;
import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingTransactionsList extends Fragment {

    private ArrayList<Transaction> transactions;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TransactionAdapter transactionAdapter;
    TransactionAdapter.OnTransactionClickListener onTransactionClickListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ongoing_transactions_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        transactions = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progressbar_transaction_list_current);
        recyclerView = getView().findViewById(R.id.recycler_transaction_list_current);


        onTransactionClickListener = new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onTransactionsClicked(int position) {
                Intent intent1 = new Intent(getActivity(), UserTransactionDetailsActivity.class);
                intent1.putExtra("transaction", transactions.get(position));
                startActivity(intent1);
            }
        };

        getUserTransactions();


    }



    private void getUserTransactions() {

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Query query = databaseReference;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    transactions.clear();
                    ArrayList<Transaction> transactionArrayList = dataSnapshot.getValue(Customer.class).getTransactions();
                    for(Transaction transaction : transactionArrayList){
                        if(!transaction.isTransactionComplete()){
                            transactions.add(transaction);
                        }
                    }
                    setAdapter(transactions);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    // Setting up the adapter to show the list of companies in the arraylist.
    private void setAdapter(ArrayList<Transaction> arrayList) {
        transactionAdapter = new TransactionAdapter(arrayList, getActivity(), onTransactionClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(transactionAdapter);
        transactionAdapter.notifyDataSetChanged();

    }


}