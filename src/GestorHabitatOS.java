import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GestorHabitatOS {

    private ArrayList<Condomino> condominos;
    private ArrayList<AreaComun> areasComunes;
    private ArrayList<Reserva> reservas;
    private ArrayList<Pago> pagos;
    private ArrayList<Multa> multas;
    private ArrayList<Factura> facturas;

    private Queue<Reserva> colaReservasPendientes;
    private Stack<String> pilaAcciones;

    public GestorHabitatOS() {
        condominos = new ArrayList<>();
        areasComunes = new ArrayList<>();
        reservas = new ArrayList<>();
        pagos = new ArrayList<>();
        multas = new ArrayList<>();
        facturas = new ArrayList<>();

        colaReservasPendientes = new LinkedList<>();
        pilaAcciones = new Stack<>();
    }

    public boolean registrarCondomino(Condomino condomino) {
        if (buscarCondominoSecuencial(condomino.getCedula()) != null) {
            return false;
        }

        condominos.add(condomino);
        pilaAcciones.push("Se registró el condómino: " + condomino.getNombre());
        return true;
    }

    public Condomino buscarCondominoSecuencial(String cedula) {
        for (Condomino c : condominos) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }

    public boolean modificarCondomino(String cedula, String telefono, String correo, EstadoCondomino estado) {
        Condomino encontrado = buscarCondominoBinaria(cedula);

        if (encontrado != null) {
            encontrado.setTelefono(telefono);
            encontrado.setCorreo(correo);
            encontrado.setEstadoCondomino(estado);

            pilaAcciones.push("Se modificó el condómino con cédula: " + cedula);
            return true;
        }

        return false;
    }

    public boolean inactivarCondomino(String cedula) {
        Condomino encontrado = buscarCondominoSecuencial(cedula);

        if (encontrado != null) {
            encontrado.setEstadoCondomino(EstadoCondomino.INACTIVO);
            pilaAcciones.push("Se inactivó el condómino con cédula: " + cedula);
            return true;
        }

        return false;
    }

    // Insertion Sort: ordenamiento secuencial por cédula
    public void ordenarCondominosPorCedula() {
        for (int i = 1; i < condominos.size(); i++) {
            Condomino actual = condominos.get(i);
            int j = i - 1;

            while (j >= 0 && condominos.get(j).getCedula().compareTo(actual.getCedula()) > 0) {
                condominos.set(j + 1, condominos.get(j));
                j--;
            }

            condominos.set(j + 1, actual);
        }
    }

    // Búsqueda binaria recursiva
    public Condomino buscarCondominoBinaria(String cedula) {
        ordenarCondominosPorCedula();
        return buscarCondominoBinariaRecursiva(cedula, 0, condominos.size() - 1);
    }

    private Condomino buscarCondominoBinariaRecursiva(String cedula, int inicio, int fin) {
        if (inicio > fin) {
            return null;
        }

        int medio = (inicio + fin) / 2;
        Condomino actual = condominos.get(medio);

        int comparacion = actual.getCedula().compareTo(cedula);

        if (comparacion == 0) {
            return actual;
        } else if (comparacion > 0) {
            return buscarCondominoBinariaRecursiva(cedula, inicio, medio - 1);
        } else {
            return buscarCondominoBinariaRecursiva(cedula, medio + 1, fin);
        }
    }

    public boolean registrarAreaComun(AreaComun area) {
        if (buscarAreaSecuencial(area.getId()) != null) {
            return false;
        }

        areasComunes.add(area);
        pilaAcciones.push("Se registró el área común: " + area.getNombre());
        return true;
    }

    public AreaComun buscarAreaSecuencial(String id) {
        for (AreaComun area : areasComunes) {
            if (area.getId().equals(id)) {
                return area;
            }
        }
        return null;
    }

    public boolean solicitarReserva(Reserva reserva) {
        if (!reserva.esValida()) {
            return false;
        }

        reservas.add(reserva);
        colaReservasPendientes.offer(reserva);

        pilaAcciones.push("Se solicitó la reserva: " + reserva.getCodigo());
        return true;
    }

    public boolean procesarSiguienteReserva() {
        Reserva reserva = colaReservasPendientes.poll();

        if (reserva == null) {
            return false;
        }

        if (!reserva.getAreaComun().estaDisponible() || hayCruceDeHorario(reserva)) {
            reserva.cancelar();
            pilaAcciones.push("Se canceló la reserva: " + reserva.getCodigo());
            return false;
        }

        reserva.confirmar();
        pilaAcciones.push("Se confirmó la reserva: " + reserva.getCodigo());
        return true;
    }

    public Reserva buscarReservaSecuencial(String codigo) {
        for (Reserva r : reservas) {
            if (r.getCodigo().equals(codigo)) {
                return r;
            }
        }
        return null;
    }

    public boolean cancelarReserva(String codigo) {
        Reserva reserva = buscarReservaSecuencial(codigo);

        if (reserva != null) {
            reserva.cancelar();
            pilaAcciones.push("Se canceló la reserva: " + codigo);
            return true;
        }

        return false;
    }

    private boolean hayCruceDeHorario(Reserva nueva) {
        for (Reserva r : reservas) {
            if (r == nueva) {
                continue;
            }

            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) {
                continue;
            }

            boolean mismaArea = r.getAreaComun().getId().equals(nueva.getAreaComun().getId());
            boolean mismaFecha = r.getFecha().equals(nueva.getFecha());

            boolean seCruzanHoras = nueva.getHoraInicio().isBefore(r.getHoraFin())
                    && nueva.getHoraFin().isAfter(r.getHoraInicio());

            if (mismaArea && mismaFecha && seCruzanHoras) {
                return true;
            }
        }

        return false;
    }


    public boolean registrarPago(Pago pago) {
        pagos.add(pago);
        pago.registrarPago();

        pilaAcciones.push("Se registró el pago: " + pago.getCodigo());
        return true;
    }

    public Pago buscarPagoSecuencial(String codigo) {
        for (Pago p : pagos) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        return null;
    }

    public boolean actualizarEstadoPago(String codigo, EstadoPago estadoPago) {
        Pago pago = buscarPagoSecuencial(codigo);

        if (pago != null) {
            pago.actualizarEstado(estadoPago);
            pilaAcciones.push("Se actualizó el pago: " + codigo);
            return true;
        }

        return false;
    }

    public boolean registrarMulta(Multa multa) {
        multas.add(multa);
        multa.registrar();

        pilaAcciones.push("Se registró la multa: " + multa.getCodigo());
        return true;
    }

    public Multa buscarMultaSecuencial(String codigo) {
        for (Multa m : multas) {
            if (m.getCodigo().equals(codigo)) {
                return m;
            }
        }
        return null;
    }

    public boolean pagarMulta(String codigo) {
        Multa multa = buscarMultaSecuencial(codigo);

        if (multa != null) {
            multa.pagarMulta();
            pilaAcciones.push("Se pagó la multa: " + codigo);
            return true;
        }

        return false;
    }

    public boolean anularMulta(String codigo) {
        Multa multa = buscarMultaSecuencial(codigo);

        if (multa != null) {
            multa.anular();
            pilaAcciones.push("Se anuló la multa: " + codigo);
            return true;
        }

        return false;
    }

    public Factura generarFactura(String numero, Condomino condomino, Pago pago, Multa multa) {
        Factura factura = new Factura(numero, LocalDate.now(), condomino, pago, multa);
        facturas.add(factura);

        pilaAcciones.push("Se generó la factura: " + numero);
        return factura;
    }

    public Factura buscarFacturaSecuencial(String numero) {
        for (Factura f : facturas) {
            if (f.getNumero().equals(numero)) {
                return f;
            }
        }
        return null;
    }

    public boolean anularFactura(String numero) {
        Factura factura = buscarFacturaSecuencial(numero);

        if (factura != null) {
            factura.anular();
            pilaAcciones.push("Se anuló la factura: " + numero);
            return true;
        }

        return false;
    }

    // Recursividad para sumar todas las facturas
    public double calcularTotalFacturasRecursivo() {
        return sumarFacturas(0);
    }

    private double sumarFacturas(int indice) {
        if (indice >= facturas.size()) {
            return 0;
        }

        return facturas.get(indice).getTotalMonto() + sumarFacturas(indice + 1);
    }

    public String verUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return "No hay acciones registradas.";
        }

        return pilaAcciones.peek();
    }

    public String quitarUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return "No hay acciones para quitar.";
        }

        return pilaAcciones.pop();
    }

    public String generarReporteCondominos() {
        StringBuilder reporte = new StringBuilder();
        reporte.append(" REPORTE DE CONDÓMINOS \n\n");

        for (Condomino c : condominos) {
            reporte.append(c.toString()).append("\n");
        }

        return reporte.toString();
    }

    public String generarReporteReservas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE RESERVAS \n\n");

        for (Reserva r : reservas) {
            reporte.append(r.toString()).append("\n");
        }

        return reporte.toString();
    }

    public String generarReportePagos() {
        StringBuilder reporte = new StringBuilder();
        reporte.append(" REPORTE DE PAGOS\n\n");

        for (Pago p : pagos) {
            reporte.append(p.toString()).append("\n");
        }

        return reporte.toString();
    }

    public String generarReporteMultas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE MULTAS\n\n");

        for (Multa m : multas) {
            reporte.append(m.toString()).append("\n");
        }

        return reporte.toString();
    }

    public String generarReporteFacturas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE FACTURAS \n\n");

        for (Factura f : facturas) {
            reporte.append(f.toString()).append("\n");
        }

        reporte.append("Total general facturado: $")
                .append(calcularTotalFacturasRecursivo());

        return reporte.toString();
    }

    public ArrayList<Condomino> getCondominos() {
        return condominos;
    }

    public ArrayList<AreaComun> getAreasComunes() {
        return areasComunes;
    }

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    public ArrayList<Pago> getPagos() {
        return pagos;
    }

    public ArrayList<Multa> getMultas() {
        return multas;
    }

    public ArrayList<Factura> getFacturas() {
        return facturas;
    }
}