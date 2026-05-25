import java.time.LocalDate;

public class Multa extends Registro {

    private String motivo;
    private double valor;
    private String tipoInfraccion;
    private EstadoMulta estadoMulta;

    public Multa(String codigo, LocalDate fecha, String motivo, double valor,
                 String tipoInfraccion, Condomino condomino) {

        super(codigo, fecha, EstadoMulta.PENDIENTE.name(), condomino);

        this.motivo = motivo;
        this.valor = valor;
        this.tipoInfraccion = tipoInfraccion;
        this.estadoMulta = EstadoMulta.PENDIENTE;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getValor() {
        return valor;
    }

    public String getTipoInfraccion() {
        return tipoInfraccion;
    }

    public EstadoMulta getEstadoMulta() {
        return estadoMulta;
    }

    public void registrar() {
        this.estadoMulta = EstadoMulta.PENDIENTE;
        this.estado = EstadoMulta.PENDIENTE.name();
    }

    public void pagarMulta() {
        this.estadoMulta = EstadoMulta.PAGADA;
        this.estado = EstadoMulta.PAGADA.name();
    }

    public void anular() {
        this.estadoMulta = EstadoMulta.ANULADA;
        this.estado = EstadoMulta.ANULADA.name();
    }

    @Override
    public String toString() {
        return "Código multa: " + codigo +
                "\nFecha: " + fecha +
                "\nMotivo: " + motivo +
                "\nValor: $" + valor +
                "\nTipo infracción: " + tipoInfraccion +
                "\nCondómino: " + condomino.getNombre() +
                "\nEstado: " + estadoMulta +
                "\n-----------------------------";
    }
}