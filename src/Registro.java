import java.time.LocalDate;

public class Registro {

    protected String codigo;
    protected LocalDate fecha;
    protected String estado;
    protected Condomino condomino;

    public Registro(String codigo, LocalDate fecha, String estado, Condomino condomino) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.estado = estado;
        this.condomino = condomino;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public Condomino getCondomino() {
        return condomino;
    }
}