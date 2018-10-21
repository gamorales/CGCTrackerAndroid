package co.cgclab.gpstracker.camara.logica;

import android.graphics.Bitmap;

public interface IUsuarioLogic {

    void crearUsuario(String cedula,
                      String nombre,
                      String usuario,
                      String clave,
                      Bitmap imagenUsuario) throws Exception;


    void modificarUsuario(String cedula, String clave,
                          String usuario, String nombre,
                          Bitmap imagenUsuario) throws Exception;


    void eliminarUsuario(String cedulaUsuario) throws Exception;


}
