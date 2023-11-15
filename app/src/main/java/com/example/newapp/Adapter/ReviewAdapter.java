package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{
    private ArrayList<Review> reviews;
    private Context context;

    public ReviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }


    @NonNull
    @Override
    public ReviewAdapter.ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_holder, parent, false);
        return new ReviewAdapter.ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewHolder holder, int position) {

        holder.userName.setText(reviews.get(position).getReviewingUserName());
        holder.reviewText.setText(String.valueOf(reviews.get(position).getReview()));
//        holder.ratingTextView.setText(reviews.get(position).getRating());
        float ratingVal = Float.parseFloat(reviews.get(position).getRating());
        holder.ratingBar.setRating(ratingVal);

        holder.dateTextView.setText(getDateFromTime(reviews.get(position).getTime()));

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


    class ReviewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView reviewText;
        TextView dateTextView;

        RatingBar ratingBar;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.reviewer_name_holder);
            reviewText = itemView.findViewById(R.id.reviewer_holder);
            dateTextView = itemView.findViewById(R.id.date_holder);
            ratingBar = itemView.findViewById(R.id.ratingBar_userReview_holder);

        }
    }

    private String getDateFromTime(long currentTimeInMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeInMillis);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return day + "/" +  month + "/" + year + " " + hour + ":" + minute + "hrs";
    }
}
