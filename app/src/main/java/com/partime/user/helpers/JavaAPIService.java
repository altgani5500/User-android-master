package com.partime.user.helpers;

/**
 * Created by Vipra on 7/3/17.
 */


import com.google.gson.JsonObject;
import com.partime.user.Responses.ScheduleResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface JavaAPIService {

    @POST("v1/getScheduleData")
    Call<ScheduleResponse> getSchedule(@Header("Authorization")String header, @Header("lang")String headerLang, @Body JsonObject object);

}
