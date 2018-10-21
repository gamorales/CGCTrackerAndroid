package co.cgclab.gpstracker.main.models;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.main.controllers.ICarrosMapa;
import co.cgclab.gpstracker.main.controllers.RespuestaNegativa;
import co.cgclab.gpstracker.main.controllers.RespuestaPositiva;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConexionRest extends AsyncTask<LatLng, Long, String> {

    private String URLBASE = "https://maps.googleapis.com/maps/api/directions/json?";

    @Override
    protected String doInBackground(LatLng... latLngs) {

        StringBuffer respuestaRest = new StringBuffer();

        try {
            StringBuffer peticion = new StringBuffer();
            peticion.append(URLBASE);
            peticion.append("origin=");
            peticion.append(latLngs[0].latitude);
            peticion.append(",");
            peticion.append(latLngs[0].longitude);
            peticion.append("&destination=");
            peticion.append(latLngs[1].latitude);
            peticion.append(",");
            peticion.append(latLngs[1].longitude);
            peticion.append("&language=es");
            peticion.append("&mode=driving");
            //peticion.append("&key=AIzaSyBsiK6VavcSeMMDZZMy7Crl-zunm9DJdKg");
            peticion.append("&key=AIzaSyCBX2IGhqGNQNy2JCAqDratpRlnXidQDIk");
            //peticion.append(this.getString(R.string.google_maps_key));

            URL url = new URL(peticion.toString());
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpsURLConnection.getInputStream())
            );
            String datosJSON;

            while((datosJSON=bufferedReader.readLine())!=null) {
                respuestaRest.append(datosJSON);
            }

            bufferedReader.close();
            httpsURLConnection.disconnect();

/*            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URLBASE+"maps/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Se instancia por referencia de la interfaz
            ICarrosMapa iCarrosMapa = retrofit.create(ICarrosMapa.class);
            Call<RespuestaPositiva> call = iCarrosMapa.consultarDirection(

            );
            //Call<RespuestaPositiva> call = iEstudiantesRetrofit.consultarEstudiantes(EnumGeneral.CLAVE_SERVICIOS.getElemento());
*/
//            return call.execute().body();
        } catch (Exception e) {
            Log.e("ConexionRest", "ERROR consulta estudiante "+e.getMessage());
        }

        return respuestaRest.toString();
    }
}
