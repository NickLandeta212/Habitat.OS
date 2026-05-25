public class Condomino {

    private String id;
    private String cedula;
    private String nombre;
    private String telefono;
    private String correo;
    private String numeroDepartamento;
    private EstadoCondomino estadoCondomino;

    public Condomino(String id, String cedula, String nombre, String telefono,
                     String correo, String numeroDepartamento, EstadoCondomino estadoCondomino) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.numeroDepartamento = numeroDepartamento;
        this.estadoCondomino = estadoCondomino;
    }

    public String getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNumeroDepartamento() {
        return numeroDepartamento;
    }

    public EstadoCondomino getEstadoCondomino() {
        return estadoCondomino;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setNumeroDepartamento(String numeroDepartamento) {
        this.numeroDepartamento = numeroDepartamento;
    }

    public void setEstadoCondomino(EstadoCondomino estadoCondomino) {
        this.estadoCondomino = estadoCondomino;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nCédula: " + cedula +
                "\nNombre: " + nombre +
                "\nTeléfono: " + telefono +
                "\nCorreo: " + correo +
                "\nDepartamento: " + numeroDepartamento +
                "\nEstado: " + estadoCondomino +
                "\n-----------------------------";
    }
}