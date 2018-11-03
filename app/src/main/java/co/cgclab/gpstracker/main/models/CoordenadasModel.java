package co.cgclab.gpstracker.main.models;

public class CoordenadasModel {
    private String comando;
    private String curso;
    private String fecha;
    private String fecha_gps;
    private String idUsuario;
    private String imei;
    private Double latitud;
    private Double longitud;
    private String tipo;
    private String velocidad;

    public CoordenadasModel() {}

    public CoordenadasModel(String comando, String curso, String fecha, String fecha_gps,
                            String idUsuario, String imei, Double latitud, Double longitud,
                            String tipo, String velocidad) {
        this.comando = comando;
        this.curso = curso;
        this.fecha = fecha;
        this.fecha_gps = fecha_gps;
        this.idUsuario = idUsuario;
        this.imei = imei;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
        this.velocidad = velocidad;
    }

    public String getComando() { return comando; }

    public void setComando(String comando) { this.comando = comando; }

    public String getCurso() { return curso; }

    public void setCurso(String curso) { this.curso = curso; }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getFecha_gps() { return fecha_gps; }

    public void setFecha_gps(String fecha_gps) { this.fecha_gps = fecha_gps; }

    public String getIdUsuario() { return idUsuario; }

    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getImei() { return imei; }

    public void setImei(String imei) { this.imei = imei; }

    public Double getLatitud() { return latitud; }

    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }

    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getVelocidad() { return velocidad; }

    public void setVelocidad(String velocidad) { this.velocidad = velocidad; }
}
