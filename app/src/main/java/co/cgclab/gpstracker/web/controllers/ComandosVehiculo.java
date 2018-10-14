package co.cgclab.gpstracker.web.controllers;

import android.util.Log;

import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComandosVehiculo implements IComandosVehiculo {
    IComandosVehiculo iComandosVehiculo;

    @Override
    public Call<CommandResponse> comandoVehiculo(String comando, String placa){
        if (placa.equals("")) {
            //throw new Exception("Debe seleccionar un vehículo.");
        }

        if (comando.equals("")) {
            //throw new Exception("Debe seleccionar una opción.");
        }

        Call<CommandResponse> commandResponseCall = iComandosVehiculo.comandoVehiculo(
                comando,
                placa
        );

        commandResponseCall.enqueue(new Callback<CommandResponse>() {
            @Override
            public void onResponse(Call<CommandResponse> call, Response<CommandResponse> response) {
                try {
                    if (response.body().getSuccess()==1) {

                    } else {

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
