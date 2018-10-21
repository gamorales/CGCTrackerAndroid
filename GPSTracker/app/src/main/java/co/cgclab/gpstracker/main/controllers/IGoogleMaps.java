package co.cgclab.gpstracker.main.controllers;

import com.google.android.gms.maps.model.LatLng;

import co.cgclab.gpstracker.main.models.RutaModel;

public interface IGoogleMaps {
    RutaModel obtenerRuta(LatLng origen, LatLng destino) throws Exception;
    RutaModel obtenerIndicaciones(LatLng origen, LatLng destino) throws Exception;
}
