package com.example.newapp.DataModel;

public class Review {

    private String review;
    private String rating;
    private String reviewingUserName;
    private String reviewingUserEmail;
    private long time;

    public Review(){

    }

    public Review(String review, String rating, String reviewingUserName, String reviewingUserEmail, long time) {
        this.review = review;
        this.rating = rating;
        this.reviewingUserName = reviewingUserName;
        this.reviewingUserEmail = reviewingUserEmail;
        this.time = time;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewingUserName() {
        return reviewingUserName;
    }

    public void setReviewingUserName(String reviewingUserName) {
        this.reviewingUserName = reviewingUserName;
    }

    public String getReviewingUserEmail() {
        return reviewingUserEmail;
    }

    public void setReviewingUserEmail(String reviewingUserEmail) {
        this.reviewingUserEmail = reviewingUserEmail;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
