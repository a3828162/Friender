package com.example.myapplication.Fragments;

import com.example.myapplication.Notifications.MyResponse;
import com.example.myapplication.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAB6Ltm-I:APA91bHRaG4gIZ-kzGmIEk0mU09C3MKOlVh_gHfDWhY6KT4mU84gehbeLhh59Tnx14SjG9GoTeEMWhXf1vVp6Jjo8qGQnGJJxoVBXGfRUdlWXHFqLyejMMlUJqcvXd9OREYPpyUmoYeY"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
