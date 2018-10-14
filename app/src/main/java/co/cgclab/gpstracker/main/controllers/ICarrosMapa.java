package co.cgclab.gpstracker.main.controllers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICarrosMapa {
    @FormUrlEncoded
    // Para evitar los carácteres que dañen la petición (tildes, ñ, saltos de línea, etc)
    @POST("directions/json")
    public Call<RespuestaPositiva> consultarDirection(
            @Field("origin") Float origen,
            @Field("destination") Float destino,
            @Field("languaje") String idioma,
            @Field("mode") String modo,
            @Field("key") String key
    );
}
