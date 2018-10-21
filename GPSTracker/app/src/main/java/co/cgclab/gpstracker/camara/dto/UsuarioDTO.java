package co.cgclab.gpstracker.camara.dto;

import java.util.HashMap;
import java.util.Map;

public class UsuarioDTO {

    private Long cedula;
    private String clave;
    private String nombre;
    private Long tipoUsuario;
    private String login;


    public Long getCedula() {
        return cedula;
    }

    public void setCedula(Long cedula) {
        this.cedula = cedula;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Long tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Map<String, Object> actualizarUsuario(){

        HashMap<String,Object> elementosActualizar = new HashMap<String, Object>();
        elementosActualizar.put("cedula",this.cedula);
        elementosActualizar.put("clave",this.clave);
        elementosActualizar.put("login",this.login);
        elementosActualizar.put("nombre",this.nombre);
        elementosActualizar.put("tipoUsuario",this.tipoUsuario);

        return elementosActualizar;
    }

}
