package co.cgclab.gpstracker.camara.accesodatos;

import android.graphics.Bitmap;

import co.cgclab.gpstracker.camara.dto.UsuarioDTO;

public interface IUsuarioDAO {

    boolean crearUsuario(UsuarioDTO usuarioDTO) throws  Exception;
    void modificarUsuario(UsuarioDTO usuarioDTO) throws Exception;
    void eliminarUsuario(Long cedulaUsuario) throws Exception;
    boolean almacenarFotoUsuario(Long cedulaUsuario, Bitmap fotoUsuario) throws Exception;
    void eliminarFotoUsuario(Long cedulaUsuario) throws Exception;
}
