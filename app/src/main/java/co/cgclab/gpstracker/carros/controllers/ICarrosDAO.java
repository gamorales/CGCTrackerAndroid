package co.cgclab.gpstracker.carros.controllers;

import co.cgclab.gpstracker.carros.models.CarrosModel;

public interface ICarrosDAO {

    void crearActualizarCarro(CarrosModel carroDTO) throws  Exception;
    void eliminarCarro(String placa) throws Exception;

}
