package com.salman.myproject.rest_api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Salman on 1/1/2018.
 */

public interface KairosApi {


//    https://api.kairos.com/enroll

    @Headers({
            "Content-Type: application/json",
            "app_id: app_id",
            "app_key: app_key"
    })
    @POST("enroll")
    Call<Object> enrollUser(@Body EnrollUser User);

    @Headers({
            "Content-Type: application/json",
            "app_id: app_id",
            "app_key: app_key"
    })
    @POST("verify")
    Call<VerifyUserPost> verifyUser(@Body EnrollUser User);


    @Headers({
            "Content-Type: application/json",
            "app_id: app_id",
            "app_key: app_id"
    })
    @POST("recognize")
    Call<Recognize> recognizeUser(@Body RecognizeUser recognizeUser);
    
}
