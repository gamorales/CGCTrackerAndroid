package co.cgclab.gpstracker.main.models;

public class CoordenadasModel {
    private String fecha;
    private String idUsuario;
    private String imei;
    private Double latitud;
    private Double longitud;

    public CoordenadasModel() {}

    public CoordenadasModel(String fecha, String idUsuario, String imei,
                            Double latitud, Double longitud) {
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.imei = imei;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getFecha() { return fecha; }

    public String getIdUsuario() { return idUsuario; }

    public String getImei() { return imei; }

    public Double getLatitud() { return latitud; }

    public Double getLongitud() { return longitud; }

}
