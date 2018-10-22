package co.cgclab.gpstracker.web.controllers;

import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IComandosVehiculo {
    @GET("gps_rest/{imei}/{comando}/{value}/{longitud01}/{latitud02}/{longitud02}")
    Call<CommandResponse> comandoVehiculo(
            @Path("imei") String imei,
            @Path("comando") String comando,
            @Path("value") String value,
            @Path("longitud01") String longitud01,
            @Path("latitud02") String latitud02,
            @Path("longitud02") String longitud02
    );
}
