package co.cgclab.gpstracker.carros.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.cgclab.gpstracker.carros.models.CarrosModel;

public class CarrosDAO implements ICarrosDAO {
    @Override
    public void crearActualizarCarro(CarrosModel carroDTO) throws Exception {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");
            databaseReference.child(carroDTO.getPlaca() + "").setValue(carroDTO);
        } catch (Exception ex) {
            throw new Exception("Error creando vehículo "+ex.getMessage());
        }
    }

    @Override
    public void eliminarCarro(String placa) throws Exception {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos/" + placa);
            databaseReference.removeValue();
        } catch (Exception e) {
            throw new Exception("No se pudo eliminar el vehículo con placa "+placa);
        }

    }
}
