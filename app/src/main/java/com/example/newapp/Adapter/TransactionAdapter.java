package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;
    private TransactionAdapter.OnTransactionClickListener onTransactionClickListener;

    public TransactionAdapter(ArrayList<Transaction> transactions, Context context, TransactionAdapter.OnTransactionClickListener onTransactionClickListener) {
        this.transactions = transactions;
        this.context = context;
        this.onTransactionClickListener = onTransactionClickListener;
    }

    public interface OnTransactionClickListener {
        void onTransactionsClicked(int position);
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_holder, parent, false);
        return new TransactionAdapter.TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionHolder holder, int position) {

        holder.spaceShipNameTextView.setText(transactions.get(position).getSpaceShipName());
        holder.companyNameTextView.setText(transactions.get(position).getCompanyName());
        holder.fromTextView.setText(transactions.get(position).getDeparture());
        holder.toTextView.setText(transactions.get(position).getDestination());
        holder.distanceTextView.setText(transactions.get(position).getDistance());
        holder.totalCostTextView.setText(String.valueOf(transactions.get(position).getTotalFare()));
        holder.userNameTextView.setText(transactions.get(position).getUserName());
        holder.userEmailTextView.setText(transactions.get(position).getUserEmail());
        holder.transactionIdTextView.setText(transactions.get(position).getTransactionId());
        holder.timeTextView.setText(String.valueOf(transactions.get(position).getTransactionTime()));
        holder.isTransactionComplete_tv.setText(String.valueOf(transactions.get(position).isTransactionComplete()));

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    class TransactionHolder extends RecyclerView.ViewHolder {


        TextView userNameTextView;
        TextView userEmailTextView;
        TextView companyNameTextView;
        TextView spaceShipNameTextView;
        TextView transactionIdTextView;
        TextView fromTextView;
        TextView toTextView;
        TextView distanceTextView;
        TextView totalCostTextView;
        TextView timeTextView;
        TextView isTransactionComplete_tv;


        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTransactionClickListener.onTransactionsClicked(getAdapterPosition());
                }
            });

            userEmailTextView = itemView.findViewById(R.id.userEmail_transaction_list);
            userNameTextView = itemView.findViewById(R.id.userName_transaction_list);
            companyNameTextView = itemView.findViewById(R.id.companyName_transaction_list);
            spaceShipNameTextView = itemView.findViewById(R.id.spaceShipName_transaction_list);
            fromTextView = itemView.findViewById(R.id.from_transaction_list);
            totalCostTextView = itemView.findViewById(R.id.price_transaction_list);
            toTextView = itemView.findViewById(R.id.to_transaction_list);
            distanceTextView = itemView.findViewById(R.id.distance_transaction_list);
            transactionIdTextView = itemView.findViewById(R.id.transactionId_transaction_list);
            timeTextView = itemView.findViewById(R.id.time_transaction_list);
            isTransactionComplete_tv = itemView.findViewById(R.id.isOngoing_transaction_list);

        }
    }

}