package co.cgclab.gpstracker.usuarios.controllers;

import java.util.regex.Pattern;

public class UsuariosController implements IUsuariosController {
    @Override
    public void registrarUsuarioFirebase(String correo, String clave, String claveConf) throws Exception {
        Pattern regex = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
        if (!regex.matcher(correo).matches()) {
            throw new Exception("No es un correo electrónico valido");
        }

        if (correo==null || correo.equals("")) {
            throw new Exception("Debe ingresar un correo electrónico");
        }

        if (!correo.contains(("@"))) {
            throw new Exception("No es un correo electrónico valido");
        }

        if (clave==null || clave.equals("")) {
            throw new Exception("El campo password no puede estar vacio");
        }

        if (!clave.equals(claveConf)) {
            throw new Exception("Las contraseñas son diferentes");
        }

    }

    @Override
    public void validarPassword(String correo) throws Exception {
        Pattern regex = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
        if (!regex.matcher(correo).matches()) {
            throw new Exception("No es un correo electrónico valido");
        }

        if (correo==null || correo.equals("")) {
            throw new Exception("Debe ingresar un correo electrónico");
        }
    }

    @Override
    public void signIn(String correo, String clave) throws Exception {
        Pattern regex = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
        if (!regex.matcher(correo).matches()) {
            throw new Exception("No es un correo electrónico valido");
        }

        if (correo==null || correo.equals("")) {
            throw new Exception("Debe ingresar un correo electrónico");
        }

        if (clave==null || clave.equals("")) {
            throw new Exception("El campo password no puede estar vacio");
        }

    }
}
