package co.cgclab.gpstracker.web.controllers;

import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IComandosVehiculo {
    @FormUrlEncoded
    @POST("gpstracker/commands.php")
    Call<CommandResponse> comandoVehiculo(
            @Field("comando") String comando,
            @Field("placa") String placa
    );
}
