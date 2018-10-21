package co.cgclab.gpstracker.carros.models;

public class CarrosModel {
    private String activo;
    private String idUsuario;
    private String nombre;
    private String password;
    private String placa;
    private String telefono;
    private String imei;

    public CarrosModel() {}

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlaca() { return placa; }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getActivo() { return activo; }

    public void setActivo(String activo) { this.activo = activo; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getImei() { return imei; }

    public void setImei(String imei) { this.imei = imei; }

}
