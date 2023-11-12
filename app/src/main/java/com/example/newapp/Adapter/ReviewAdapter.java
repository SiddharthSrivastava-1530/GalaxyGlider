package com.example.newapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.newapp.DataModel.Review;
import com.example.newapp.DataModel.SpaceShip;
import com.example.newapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{
    private ArrayList<Review> reviews;
    private Context context;
    private ReviewAdapter.OnReviewClickLiListener onReviewClickiListener;

    public ReviewAdapter(ArrayList<Review> reviews, Context context, ReviewAdapter.OnReviewClickLiListener onReviewClickiListener) {
        this.reviews = reviews;
        this.context = context;
        this.onReviewClickiListener = onReviewClickiListener;
    }

    public interface OnReviewClickLiListener {
        void onReviewsClicked(int position);
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
        int ratingVal = (int) Float.parseFloat(reviews.get(position).getRating());
        if(ratingVal==1){
            holder.oneStar.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==2){
            holder.twoStar.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==3){
            holder.threeStar.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==4){
            holder.fourStar.setVisibility(View.VISIBLE);
        }
        else if(ratingVal==5){
            holder.fiveStar.setVisibility(View.VISIBLE);
        }
        holder.dateTextView.setText(getDateFromTime(reviews.get(position).getTime()));

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


    class ReviewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView reviewText;
        TextView ratingTextView;
        TextView dateTextView;

        TextView oneStar;
        TextView twoStar;
        TextView threeStar;
        TextView fourStar;
        TextView fiveStar;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onReviewClickiListener.onReviewsClicked(getAdapterPosition());
                }
            });

            userName = itemView.findViewById(R.id.reviewer_name_holder);
            reviewText = itemView.findViewById(R.id.reviewer_holder);
            dateTextView = itemView.findViewById(R.id.date_holder);
            oneStar = itemView.findViewById(R.id.oneStar);
            twoStar = itemView.findViewById(R.id.twoStar);
            threeStar = itemView.findViewById(R.id.threeStar);
            fourStar = itemView.findViewById(R.id.fourStar);
            fiveStar = itemView.findViewById(R.id.fiveStar);

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
