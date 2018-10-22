package co.cgclab.gpstracker.web.controllers;

import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IComandosVehiculo {
    @FormUrlEncoded
    @POST("gps_rest")
    Call<CommandResponse> comandoVehiculo(
            @Field("imei") String imei,
            @Field("comando") String comando,
            @Field("value") String value,
            @Field("longitud01") String longitud01,
            @Field("latitud02") String latitud02,
            @Field("longitud02") String longitud02
    );
}
