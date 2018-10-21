package co.cgclab.gpstracker.carros.controllers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.cgclab.gpstracker.carros.models.CarrosModel;

public class CarrosController implements ICarrosController {

    @Override
    public void crearVehiculo(String nombre, String placa, String telefono,
                              String password, String imei, String idUsuario,
                              String estado) throws Exception {

        if (nombre==null || nombre.equals("")) {
            throw new Exception("El campo nombre es obligatorio");
        }

        if (placa==null || placa.equals("")) {
            throw new Exception("El campo placa es obligatorio");
        }

        if (telefono==null || telefono.equals("")) {
            throw new Exception("El campo teléfono es obligatorio");
        }

        if (!telefono.matches("[0-9]*")) {
            throw new Exception("El teléfono debe ser numérico");
        }

        if (imei==null || imei.equals("")) {
            throw new Exception("El campo IMEI es obligatorio");
        }

        if (!imei.matches("[0-9]*")) {
            throw new Exception("El IMEI debe ser numérico");
        }

        if (password==null || password.equals("")) {
            throw new Exception("El campo password es obligatorio");
        }

        ICarrosDAO iCarrosDAO = new CarrosDAO();
        CarrosModel carrosModel = new CarrosModel();
        carrosModel.setNombre(nombre.trim());
        carrosModel.setPlaca(placa.trim());
        carrosModel.setTelefono(telefono.trim());
        carrosModel.setImei(imei.trim());
        carrosModel.setPassword(password.trim());
        carrosModel.setIdUsuario(idUsuario.trim());
        carrosModel.setActivo(estado);
        iCarrosDAO.crearActualizarCarro(carrosModel);
    }

    @Override
    public List<String> listarVehiculos(String idUsuario) throws Exception {
        final List<String> lVehiculos = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        /*
         * Con Query se hace consulta por un campo específico dentro de la tabla.
         * Si hubiera un child sería,
         * databaseReference.child("nombreChild").orderByChild("idUsuario").equalTo(userID);
         */
        Query query = databaseReference
                .orderByChild("idUsuario").equalTo(idUsuario);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lVehiculos.clear();
                    try {
                        for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            if (vehiculo.getActivo().equals("1")) {
                                lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("CarrosController", "85) ERROR: "+ex.getMessage());
                    }

                    Log.e("CarrosController", "size: "+lVehiculos.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e("CarrosController", "size2: "+lVehiculos.size());
        if (lVehiculos.size()<1) {
            throw new Exception("No se pudo cargar la lista de vehículos");
        }

        return lVehiculos;
    }

    @Override
    public void eliminarVehiculo(String placa) throws Exception {
        if (placa==null || placa.equals("")) {
            throw new Exception("El campo placa es obligatorio");
        }

        ICarrosDAO iCarrosDAO = new CarrosDAO();
        iCarrosDAO.eliminarCarro(placa);
    }
}
