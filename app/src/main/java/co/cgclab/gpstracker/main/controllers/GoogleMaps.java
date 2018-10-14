package co.cgclab.gpstracker.main.controllers;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import co.cgclab.gpstracker.main.models.ConexionRest;
import co.cgclab.gpstracker.main.models.RutaModel;

public class GoogleMaps implements IGoogleMaps {
    @Override
    public RutaModel obtenerRuta(LatLng origen, LatLng destino) throws Exception {
        if (origen==null) {
            throw new Exception("El origen es requerido");
        }
        if (destino==null) {
            throw new Exception("El destino es requerido");
        }

        String rutaConsultar = new ConexionRest().execute(origen, destino).get();

        if (rutaConsultar==null || rutaConsultar.equals("")) {
            throw new Exception("Error al consultar el servicio");
        }

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(rutaConsultar);
        List<LatLng> puntosPintar = null;
        RutaModel rutaModel = null;

        if (jsonElement.isJsonObject()) {
            JSONObject jsonObject = new JSONObject(rutaConsultar);
            JSONArray rutas = jsonObject.getJSONArray("routes");
            //JSONObject rutaPintar = rutas.getJSONObject(0).getJSONObject("overview_polyline");
            JSONObject rutaPintar = rutas.getJSONObject(0);
            JSONObject polyline = rutaPintar.getJSONObject("overview_polyline");
            String puntosCodificados = polyline.getString("points");
            puntosPintar = PolyUtil.decode(puntosCodificados);
        }

        rutaModel = new RutaModel();
        rutaModel.setlPuntos(puntosPintar);

        return rutaModel;
    }

    @Override
    public RutaModel obtenerIndicaciones(LatLng origen, LatLng destino) throws Exception {
        if (origen==null) {
            throw new Exception("El origen es requerido");
        }
        if (destino==null) {
            throw new Exception("El destino es requerido");
        }

        return null;
    }
}
