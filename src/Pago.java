import java.time.LocalDate;

public class Pago extends Registro {

    private double monto;
    private String concepto;
    private String nroComprobante;
    private EstadoPago estadoPago;

    public Pago(String codigo, LocalDate fecha, double monto, String concepto,
                String nroComprobante, Condomino condomino) {

        super(codigo, fecha, EstadoPago.PENDIENTE.name(), condomino);

        this.monto = monto;
        this.concepto = concepto;
        this.nroComprobante = nroComprobante;
        this.estadoPago = EstadoPago.PENDIENTE;
    }

    public double getMonto() {
        return monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getNroComprobante() {
        return nroComprobante;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void registrarPago() {
        this.estadoPago = EstadoPago.PAGADO;
        this.estado = EstadoPago.PAGADO.name();
    }

    public void actualizarEstado(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
        this.estado = estadoPago.name();
    }

    public void eliminar() {
        this.estadoPago = EstadoPago.PENDIENTE;
        this.estado = EstadoPago.PENDIENTE.name();
    }

    @Override
    public String toString() {
        return "Código pago: " + codigo +
                "\nFecha: " + fecha +
                "\nMonto: $" + monto +
                "\nConcepto: " + concepto +
                "\nComprobante: " + nroComprobante +
                "\nCondómino: " + condomino.getNombre() +
                "\nEstado: " + estadoPago +
                "\n-----------------------------";
    }
}