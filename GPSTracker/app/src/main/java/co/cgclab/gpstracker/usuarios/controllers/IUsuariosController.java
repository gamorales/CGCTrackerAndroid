package co.cgclab.gpstracker.usuarios.controllers;

public interface IUsuariosController {
    void registrarUsuarioFirebase (String correo, String clave, String claveConf) throws Exception;

    void validarPassword(String usuario) throws Exception;

    void signIn(String correo, String clave) throws Exception;
}
