package co.cgclab.gpstracker.carros.controllers;

import java.util.List;

public interface IVehiculos {
    List<String> listarVehiculos(String idUsuario) throws Exception;
}
