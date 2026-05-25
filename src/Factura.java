import java.time.LocalDate;

public class Factura {

    private String numero;
    private LocalDate fechaEmision;
    private double totalMonto;
    private Condomino condomino;
    private Pago pago;
    private Multa multa;
    private EstadoFactura estadoFactura;

    public Factura(String numero, LocalDate fechaEmision, Condomino condomino, Pago pago, Multa multa) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.condomino = condomino;
        this.pago = pago;
        this.multa = multa;
        this.estadoFactura = EstadoFactura.EMITIDA;
        this.totalMonto = calcularTotal();
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public double getTotalMonto() {
        return totalMonto;
    }

    public Condomino getCondomino() {
        return condomino;
    }

    public Pago getPago() {
        return pago;
    }

    public Multa getMulta() {
        return multa;
    }

    public EstadoFactura getEstadoFactura() {
        return estadoFactura;
    }

    public double calcularTotal() {
        double total = 0;

        if (pago != null) {
            total += pago.getMonto();
        }

        if (multa != null && multa.getEstadoMulta() != EstadoMulta.ANULADA) {
            total += multa.getValor();
        }

        this.totalMonto = total;
        return total;
    }

    public void anular() {
        this.estadoFactura = EstadoFactura.ANULADA;
    }

    @Override
    public String toString() {
        return "Factura N°: " + numero +
                "\nFecha emisión: " + fechaEmision +
                "\nCondómino: " + condomino.getNombre() +
                "\nTotal: $" + totalMonto +
                "\nEstado: " + estadoFactura +
                "\n-----------------------------";
    }
}