package co.cgclab.gpstracker.carros.controllers;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public interface ICarrosController {
    void crearVehiculo(String nombre, String placa, String telefono,
                       String password, String imei, String idUsuario,
                       String estado) throws Exception;

    List<String> listarVehiculos(String idUsuario) throws Exception;

    void eliminarVehiculo(String placa) throws Exception;

}
