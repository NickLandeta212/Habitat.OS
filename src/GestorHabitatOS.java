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

    private boolean textoVacio(String texto) {
        if (texto == null) {
            return true;
        } else if (texto.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean identificadorValido(String identificador) {
        if (textoVacio(identificador)) {
            return false;
        } else if (identificador.trim().startsWith("-")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean registrarCondomino(Condomino condomino) {
        if (condomino == null) {
            return false;
        } else if (!identificadorValido(condomino.getCedula())) {
            return false;
        } else if (textoVacio(condomino.getNombre())) {
            return false;
        } else if (buscarCondominoSecuencial(condomino.getCedula()) != null) {
            return false;
        } else {
            condominos.add(condomino);
            pilaAcciones.push("Se registró el condómino: " + condomino.getNombre());
            return true;
        }
    }

    public Condomino buscarCondominoSecuencial(String cedula) {
        if (!identificadorValido(cedula)) {
            return null;
        }

        for (Condomino c : condominos) {
            if (c.getCedula().trim().equals(cedula.trim())) {
                return c;
            }
        }

        return null;
    }

    public boolean modificarCondomino(
            String cedula,
            String telefono,
            String correo,
            EstadoCondomino estado
    ) {
        if (!identificadorValido(cedula)) {
            return false;
        } else if (textoVacio(telefono)) {
            return false;
        } else if (textoVacio(correo)) {
            return false;
        } else if (estado == null) {
            return false;
        }

        Condomino encontrado = buscarCondominoBinaria(cedula);

        if (encontrado == null) {
            return false;
        } else {
            encontrado.setTelefono(telefono.trim());
            encontrado.setCorreo(correo.trim());
            encontrado.setEstadoCondomino(estado);
            pilaAcciones.push("Se modificó el condómino con cédula: " + cedula);
            return true;
        }
    }

    public boolean inactivarCondomino(String cedula) {
        if (!identificadorValido(cedula)) {
            return false;
        }

        Condomino encontrado = buscarCondominoSecuencial(cedula);

        if (encontrado == null) {
            return false;
        } else if (encontrado.getEstadoCondomino() == EstadoCondomino.INACTIVO) {
            return false;
        } else {
            encontrado.setEstadoCondomino(EstadoCondomino.INACTIVO);
            pilaAcciones.push("Se inactivó el condómino con cédula: " + cedula);
            return true;
        }
    }

    public void ordenarCondominosPorCedula() {
        for (int i = 1; i < condominos.size(); i++) {
            Condomino actual = condominos.get(i);
            int j = i - 1;

            while (j >= 0
                    && condominos.get(j).getCedula()
                    .compareTo(actual.getCedula()) > 0) {

                condominos.set(j + 1, condominos.get(j));
                j--;
            }

            condominos.set(j + 1, actual);
        }
    }

    public Condomino buscarCondominoBinaria(String cedula) {
        if (!identificadorValido(cedula)) {
            return null;
        }

        ordenarCondominosPorCedula();

        return buscarCondominoBinariaRecursiva(
                cedula.trim(),
                0,
                condominos.size() - 1
        );
    }

    private Condomino buscarCondominoBinariaRecursiva(
            String cedula,
            int inicio,
            int fin
    ) {
        if (inicio > fin) {
            return null;
        }

        int medio = (inicio + fin) / 2;
        Condomino actual = condominos.get(medio);

        int comparacion = actual.getCedula().compareTo(cedula);

        if (comparacion == 0) {
            return actual;
        } else if (comparacion > 0) {
            return buscarCondominoBinariaRecursiva(
                    cedula,
                    inicio,
                    medio - 1
            );
        } else {
            return buscarCondominoBinariaRecursiva(
                    cedula,
                    medio + 1,
                    fin
            );
        }
    }

    public boolean registrarAreaComun(AreaComun area) {
        if (area == null) {
            return false;
        } else if (!identificadorValido(area.getId())) {
            return false;
        } else if (textoVacio(area.getNombre())) {
            return false;
        } else if (buscarAreaSecuencial(area.getId()) != null) {
            return false;
        } else if (existeAreaConMismoNombre(area.getNombre())) {
            return false;
        } else {
            areasComunes.add(area);
            pilaAcciones.push("Se registró el área común: " + area.getNombre());
            return true;
        }
    }

    private boolean existeAreaConMismoNombre(String nombre) {
        if (textoVacio(nombre)) {
            return false;
        }

        for (AreaComun area : areasComunes) {
            if (area.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                return true;
            }
        }

        return false;
    }

    public AreaComun buscarAreaSecuencial(String id) {
        if (!identificadorValido(id)) {
            return null;
        }

        for (AreaComun area : areasComunes) {
            if (area.getId().trim().equals(id.trim())) {
                return area;
            }
        }

        return null;
    }

    public boolean solicitarReserva(Reserva reserva) {
        if (reserva == null) {
            return false;
        } else if (!identificadorValido(reserva.getCodigo())) {
            return false;
        } else if (reserva.getAreaComun() == null) {
            return false;
        } else if (!identificadorValido(reserva.getAreaComun().getId())) {
            return false;
        } else if (buscarReservaSecuencial(reserva.getCodigo()) != null) {
            return false;
        } else if (!reserva.esValida()) {
            return false;
        } else if (existeReservaExactamenteIgual(reserva)) {
            return false;
        } else {
            reservas.add(reserva);
            colaReservasPendientes.offer(reserva);
            pilaAcciones.push("Se solicitó la reserva: " + reserva.getCodigo());
            return true;
        }
    }

    private boolean existeReservaExactamenteIgual(Reserva nueva) {
        for (Reserva registrada : reservas) {
            if (registrada.getAreaComun() == null
                    || nueva.getAreaComun() == null) {
                continue;
            }

            boolean mismaArea =
                    registrada.getAreaComun().getId().trim()
                            .equals(nueva.getAreaComun().getId().trim());

            boolean mismaFecha =
                    registrada.getFecha().equals(nueva.getFecha());

            boolean mismaHoraInicio =
                    registrada.getHoraInicio().equals(nueva.getHoraInicio());

            boolean mismaHoraFin =
                    registrada.getHoraFin().equals(nueva.getHoraFin());

            boolean noEstaCancelada =
                    registrada.getEstadoReserva() != EstadoReserva.CANCELADA;

            if (mismaArea
                    && mismaFecha
                    && mismaHoraInicio
                    && mismaHoraFin
                    && noEstaCancelada) {
                return true;
            }
        }

        return false;
    }

    public boolean procesarSiguienteReserva() {
        Reserva reserva = colaReservasPendientes.poll();

        if (reserva == null) {
            return false;
        } else if (!reserva.getAreaComun().estaDisponible()) {
            reserva.cancelar();
            pilaAcciones.push(
                    "Se canceló la reserva porque el área no está disponible: "
                            + reserva.getCodigo()
            );
            return false;
        } else if (hayCruceDeHorario(reserva)) {
            reserva.cancelar();
            pilaAcciones.push(
                    "Se canceló la reserva por cruce de horario: "
                            + reserva.getCodigo()
            );
            return false;
        } else {
            reserva.confirmar();
            pilaAcciones.push("Se confirmó la reserva: " + reserva.getCodigo());
            return true;
        }
    }

    public Reserva buscarReservaSecuencial(String codigo) {
        if (!identificadorValido(codigo)) {
            return null;
        }

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().trim().equals(codigo.trim())) {
                return reserva;
            }
        }

        return null;
    }

    public boolean cancelarReserva(String codigo) {
        if (!identificadorValido(codigo)) {
            return false;
        }

        Reserva reserva = buscarReservaSecuencial(codigo);

        if (reserva == null) {
            return false;
        } else if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            return false;
        } else {
            reserva.cancelar();
            pilaAcciones.push("Se canceló la reserva: " + codigo);
            return true;
        }
    }

    private boolean hayCruceDeHorario(Reserva nueva) {
        for (Reserva registrada : reservas) {
            if (registrada == nueva) {
                continue;
            }

            if (registrada.getEstadoReserva() == EstadoReserva.CANCELADA) {
                continue;
            }

            boolean mismaArea =
                    registrada.getAreaComun().getId()
                            .equals(nueva.getAreaComun().getId());

            boolean mismaFecha =
                    registrada.getFecha().equals(nueva.getFecha());

            boolean seCruzanHoras =
                    nueva.getHoraInicio().isBefore(registrada.getHoraFin())
                            && nueva.getHoraFin().isAfter(registrada.getHoraInicio());

            if (mismaArea && mismaFecha && seCruzanHoras) {
                return true;
            }
        }

        return false;
    }

    public boolean registrarPago(Pago pago) {
        if (pago == null) {
            return false;
        } else if (!identificadorValido(pago.getCodigo())) {
            return false;
        } else if (buscarPagoSecuencial(pago.getCodigo()) != null) {
            return false;
        } else {
            pagos.add(pago);
            pago.registrarPago();
            pilaAcciones.push("Se registró el pago: " + pago.getCodigo());
            return true;
        }
    }

    public Pago buscarPagoSecuencial(String codigo) {
        if (!identificadorValido(codigo)) {
            return null;
        }

        for (Pago pago : pagos) {
            if (pago.getCodigo().trim().equals(codigo.trim())) {
                return pago;
            }
        }

        return null;
    }

    public boolean actualizarEstadoPago(
            String codigo,
            EstadoPago estadoPago
    ) {
        if (!identificadorValido(codigo)) {
            return false;
        } else if (estadoPago == null) {
            return false;
        }

        Pago pago = buscarPagoSecuencial(codigo);

        if (pago == null) {
            return false;
        } else {
            pago.actualizarEstado(estadoPago);
            pilaAcciones.push("Se actualizó el pago: " + codigo);
            return true;
        }
    }

    public boolean registrarMulta(Multa multa) {
        if (multa == null) {
            return false;
        } else if (!identificadorValido(multa.getCodigo())) {
            return false;
        } else if (buscarMultaSecuencial(multa.getCodigo()) != null) {
            return false;
        } else {
            multas.add(multa);
            multa.registrar();
            pilaAcciones.push("Se registró la multa: " + multa.getCodigo());
            return true;
        }
    }

    public Multa buscarMultaSecuencial(String codigo) {
        if (!identificadorValido(codigo)) {
            return null;
        }

        for (Multa multa : multas) {
            if (multa.getCodigo().trim().equals(codigo.trim())) {
                return multa;
            }
        }

        return null;
    }

    public boolean pagarMulta(String codigo) {
        if (!identificadorValido(codigo)) {
            return false;
        }

        Multa multa = buscarMultaSecuencial(codigo);

        if (multa == null) {
            return false;
        } else {
            multa.pagarMulta();
            pilaAcciones.push("Se pagó la multa: " + codigo);
            return true;
        }
    }

    public boolean anularMulta(String codigo) {
        if (!identificadorValido(codigo)) {
            return false;
        }

        Multa multa = buscarMultaSecuencial(codigo);

        if (multa == null) {
            return false;
        } else {
            multa.anular();
            pilaAcciones.push("Se anuló la multa: " + codigo);
            return true;
        }
    }

    public Factura generarFactura(
            String numero,
            Condomino condomino,
            Pago pago,
            Multa multa
    ) {
        if (!identificadorValido(numero)) {
            return null;
        } else if (condomino == null) {
            return null;
        } else if (pago == null && multa == null) {
            return null;
        } else if (buscarFacturaSecuencial(numero) != null) {
            return null;
        } else {
            Factura factura = new Factura(
                    numero,
                    LocalDate.now(),
                    condomino,
                    pago,
                    multa
            );

            if (factura.getTotalMonto() < 0) {
                return null;
            } else {
                facturas.add(factura);
                pilaAcciones.push("Se generó la factura: " + numero);
                return factura;
            }
        }
    }

    public Factura buscarFacturaSecuencial(String numero) {
        if (!identificadorValido(numero)) {
            return null;
        }

        for (Factura factura : facturas) {
            if (factura.getNumero().trim().equals(numero.trim())) {
                return factura;
            }
        }

        return null;
    }

    public boolean anularFactura(String numero) {
        if (!identificadorValido(numero)) {
            return false;
        }

        Factura factura = buscarFacturaSecuencial(numero);

        if (factura == null) {
            return false;
        } else {
            factura.anular();
            pilaAcciones.push("Se anuló la factura: " + numero);
            return true;
        }
    }

    public double calcularTotalFacturasRecursivo() {
        return sumarFacturas(0);
    }

    private double sumarFacturas(int indice) {
        if (indice >= facturas.size()) {
            return 0;
        } else {
            return facturas.get(indice).getTotalMonto()
                    + sumarFacturas(indice + 1);
        }
    }

    public String verUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return "No hay acciones registradas.";
        } else {
            return pilaAcciones.peek();
        }
    }

    public String quitarUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return "No hay acciones para quitar.";
        } else {
            return pilaAcciones.pop();
        }
    }

    public String generarReporteCondominos() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE CONDÓMINOS\n\n");

        if (condominos.isEmpty()) {
            reporte.append("No existen condóminos registrados.\n");
        } else {
            for (Condomino condomino : condominos) {
                reporte.append(condomino).append("\n");
            }
        }

        return reporte.toString();
    }

    public String generarReporteReservas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE RESERVAS\n\n");

        if (reservas.isEmpty()) {
            reporte.append("No existen reservas registradas.\n");
        } else {
            for (Reserva reserva : reservas) {
                reporte.append(reserva).append("\n");
            }
        }

        return reporte.toString();
    }

    public String generarReportePagos() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE PAGOS\n\n");

        if (pagos.isEmpty()) {
            reporte.append("No existen pagos registrados.\n");
        } else {
            for (Pago pago : pagos) {
                reporte.append(pago).append("\n");
            }
        }

        return reporte.toString();
    }

    public String generarReporteMultas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE MULTAS\n\n");

        if (multas.isEmpty()) {
            reporte.append("No existen multas registradas.\n");
        } else {
            for (Multa multa : multas) {
                reporte.append(multa).append("\n");
            }
        }

        return reporte.toString();
    }

    public String generarReporteFacturas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE FACTURAS\n\n");

        if (facturas.isEmpty()) {
            reporte.append("No existen facturas registradas.\n");
        } else {
            for (Factura factura : facturas) {
                reporte.append(factura).append("\n");
            }

            reporte.append("\nTotal general facturado: $")
                    .append(calcularTotalFacturasRecursivo());
        }

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