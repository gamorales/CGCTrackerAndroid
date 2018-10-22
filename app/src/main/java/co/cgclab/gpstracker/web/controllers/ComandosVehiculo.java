package co.cgclab.gpstracker.web.controllers;

import android.util.Log;

import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandosVehiculo implements IComandosVehiculo {
    IComandosVehiculo iComandosVehiculo;

    @Override
    public Call<CommandResponse> comandoVehiculo(String imei, String comando, String value,
                                                 String longitud01, String latitud02, String longitud02) {
        Call<CommandResponse> commandResponseCall = iComandosVehiculo.comandoVehiculo(
            imei, comando, value,
            longitud01, latitud02, longitud02
        );

        commandResponseCall.enqueue(new Callback<CommandResponse>() {
            @Override
            public void onResponse(Call<CommandResponse> call, Response<CommandResponse> response) {
                try {
                    if (response.body().getSuccess()==1) {
                        Log.i("ComandosVehiculo", response.body().getData().toString());
                    } else {
                        Log.i("ComandosVehiculo", response.body().getData().toString());
                    }
                } catch (Exception ex) {
                    Log.e("ComandosVehiculo", "ERROR: "+ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CommandResponse> call, Throwable t) {

            }
        });
        return null;
    }
}
