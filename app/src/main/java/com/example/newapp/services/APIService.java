package com.example.newapp.services;

import com.example.newapp.DataModel.NotificationSender;
import com.example.newapp.utils.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",

                    // server key for using firebase messaging services
                    "Authorization:key=AAAALf1ZZZ8:APA91bEi_-boZdLSrhf8RL6tfMGGmIeD0_j63VToAoFzPZmcg9vDzxXcaUJTCdWcbIGK_ngDyLUzWtwANFbZ_ZsgYfI899O3mmc1MklTjgPDDDltwVLuVwSllVLKOu0ASGJnsAqJW1B3" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")

    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
