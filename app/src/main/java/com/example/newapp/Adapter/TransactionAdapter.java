package com.example.newapp.Adapter;

import android.content.Context;
import android.graphics.Color;
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
import java.util.Calendar;

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

        holder.distanceTextView.setText(transactions.get(position).getDistance()+" ly");
        String priceOfJour = String.valueOf(transactions.get(position).getTotalFare());
        double priceVal = Double.parseDouble(priceOfJour);
        holder.totalCostTextView.setText("$"+getLastTwoDigitsBeforeDecimal(priceVal));
        holder.transactionIdTextView.setText("RefId: "+transactions.get(position).getTransactionId());
        String time = getDateFromTime(transactions.get(position).getTransactionTime());
        holder.timeTextView.setText(time);
        if(transactions.get(position).isTransactionComplete()){
            holder.isTransactionComplete_tv.setText("Complete");
            holder.isTransactionComplete_tv.setTextColor(Color.GREEN);
        }
        else{
            holder.isTransactionComplete_tv.setText("Ongoing");
        }

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    class TransactionHolder extends RecyclerView.ViewHolder {

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

    private String getDateFromTime(long currentTimeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeInMillis);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = getDayOfWeekString(dayOfWeek);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        String monthStr = getMonthString(month);

        int year = calendar.get(Calendar.YEAR);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        String amPm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";

        return dayOfWeekStr + ", " + monthStr + " " + day + ", " + String.format("%02d:%02d", hour, minute) + " " + amPm;
    }

    private String getDayOfWeekString(int dayOfWeek) {
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return daysOfWeek[dayOfWeek - 1];
    }

    private String getMonthString(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }

    private String getLastTwoDigitsBeforeDecimal(double number) {
        // Convert the double to a string
        String numberString = String.valueOf(number);

        // Find the index of the decimal point
        int decimalIndex = numberString.indexOf('.');

        // Extract the substring containing the last two digits before the decimal
        String lastTwoDigits = numberString.substring(decimalIndex - 2, decimalIndex);

        // Parse the substring to an integer
        return String.valueOf(lastTwoDigits);
    }

}