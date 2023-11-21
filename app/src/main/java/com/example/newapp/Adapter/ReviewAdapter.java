package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.DataModel.Review;
import com.example.newapp.R;

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
//        holder.reviewText.setText(String.valueOf(reviews.get(position).getReview()));
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
}
