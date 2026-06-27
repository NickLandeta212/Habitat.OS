import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GestorHabitatOS {

    private HabitatDAO dao = new HabitatDAO();

    private ArrayList<Condomino> condominos = new ArrayList<>();
    private ArrayList<AreaComun> areasComunes = new ArrayList<>();
    private ArrayList<Reserva> reservas = new ArrayList<>();
    private ArrayList<Pago> pagos = new ArrayList<>();
    private ArrayList<Multa> multas = new ArrayList<>();
    private ArrayList<Factura> facturas = new ArrayList<>();

    private Queue<Reserva> colaReservasPendientes = new LinkedList<>();
    private Stack<String> pilaAcciones = new Stack<>();

    private ArbolBinario<Condomino> arbolCondominos = new ArbolBinario<>(Condomino::getCedula);
    private ArbolBinario<AreaComun> arbolAreas = new ArbolBinario<>(AreaComun::getId);
    private ArbolBinario<Reserva> arbolReservas = new ArbolBinario<>(Reserva::getCodigo);
    private ArbolBinario<Pago> arbolPagos = new ArbolBinario<>(Pago::getCodigo);
    private ArbolBinario<Pago> arbolComprobantes = new ArbolBinario<>(Pago::getNroComprobante);
    private ArbolBinario<Multa> arbolMultas = new ArbolBinario<>(Multa::getCodigo);
    private ArbolBinario<Factura> arbolFacturas = new ArbolBinario<>(Factura::getNumero);

    public GestorHabitatOS() {
        cargarDatosDesdeMySQL();
    }

    public void cargarDatosDesdeMySQL() {
        condominos = dao.listarCondominos();
        areasComunes = dao.listarAreas();
        reservas = dao.listarReservas(areasComunes, condominos);
        pagos = dao.listarPagos(condominos);
        multas = dao.listarMultas(condominos);
        facturas = dao.listarFacturas(condominos, pagos, multas);

        reconstruirEstructuras();
    }

    private void reconstruirEstructuras() {
        arbolCondominos.limpiar();
        arbolAreas.limpiar();
        arbolReservas.limpiar();
        arbolPagos.limpiar();
        arbolComprobantes.limpiar();
        arbolMultas.limpiar();
        arbolFacturas.limpiar();
        colaReservasPendientes.clear();

        for (Condomino c : condominos) arbolCondominos.insertar(c);
        for (AreaComun a : areasComunes) arbolAreas.insertar(a);

        for (Reserva r : reservas) {
            arbolReservas.insertar(r);

            if (r.getEstadoReserva() == EstadoReserva.PENDIENTE) {
                colaReservasPendientes.offer(r);
            }
        }

        for (Pago p : pagos) {
            arbolPagos.insertar(p);
            arbolComprobantes.insertar(p);
        }

        for (Multa m : multas) arbolMultas.insertar(m);
        for (Factura f : facturas) arbolFacturas.insertar(f);
    }

    public boolean registrarCondomino(Condomino condomino) {
        if (condomino == null ||
                !Validador.enteroPositivo(condomino.getId()) ||
                !Validador.cedula(condomino.getCedula()) ||
                !Validador.nombre(condomino.getNombre()) ||
                !Validador.telefono(condomino.getTelefono()) ||
                !Validador.correo(condomino.getCorreo()) ||
                !Validador.texto(condomino.getNumeroDepartamento()) ||
                condomino.getEstadoCondomino() == null ||
                arbolCondominos.contiene(condomino.getCedula())) {
            return false;
        }

        if (dao.insertarCondomino(condomino)) {
            condominos.add(condomino);
            arbolCondominos.insertar(condomino);
            pilaAcciones.push("Se registró condómino en MySQL: " + condomino.getNombre());
            return true;
        }

        return false;
    }

    public Condomino buscarCondominoSecuencial(String cedula) {
        return arbolCondominos.buscar(cedula);
    }

    public Condomino buscarCondominoBinaria(String cedula) {
        return arbolCondominos.buscar(cedula);
    }

    public boolean modificarCondomino(String cedula, String telefono, String correo, EstadoCondomino estado) {
        if (!Validador.cedula(cedula) ||
                !Validador.telefono(telefono) ||
                !Validador.correo(correo) ||
                estado == null) {
            return false;
        }

        Condomino c = arbolCondominos.buscar(cedula);

        if (c == null) {
            return false;
        }

        if (dao.actualizarCondomino(cedula, telefono.trim(), correo.trim(), estado)) {
            c.setTelefono(telefono.trim());
            c.setCorreo(correo.trim());
            c.setEstadoCondomino(estado);
            pilaAcciones.push("Se modificó condómino: " + cedula);
            return true;
        }

        return false;
    }

    public boolean inactivarCondomino(String cedula) {
        Condomino c = buscarCondominoSecuencial(cedula);

        if (c == null) {
            return false;
        }

        return modificarCondomino(
                cedula,
                c.getTelefono(),
                c.getCorreo(),
                EstadoCondomino.INACTIVO
        );
    }

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

    public boolean registrarAreaComun(AreaComun area) {
        if (area == null ||
                !Validador.enteroPositivo(area.getId()) ||
                !Validador.texto(area.getNombre()) ||
                area.getCapacidad() <= 0 ||
                !Validador.texto(area.getDescripcion()) ||
                arbolAreas.contiene(area.getId())) {
            return false;
        }

        if (dao.insertarArea(area)) {
            areasComunes.add(area);
            arbolAreas.insertar(area);
            pilaAcciones.push("Se registró área común en MySQL: " + area.getNombre());
            return true;
        }

        return false;
    }

    public AreaComun buscarAreaSecuencial(String id) {
        return arbolAreas.buscar(id);
    }

    public boolean solicitarReserva(Reserva reserva) {
        if (reserva == null ||
                !Validador.enteroPositivo(reserva.getCodigo()) ||
                arbolReservas.contiene(reserva.getCodigo()) ||
                !Validador.fechaReserva(reserva.getFecha()) ||
                !Validador.horas(reserva.getHoraInicio(), reserva.getHoraFin()) ||
                reserva.getAreaComun() == null ||
                !reserva.getAreaComun().estaDisponible() ||
                reserva.getCondomino() == null ||
                reserva.getCondomino().getEstadoCondomino() != EstadoCondomino.ACTIVO ||
                existeCruceReserva(reserva)) {
            return false;
        }

        if (dao.insertarReserva(reserva)) {
            reservas.add(reserva);
            colaReservasPendientes.offer(reserva);
            arbolReservas.insertar(reserva);
            pilaAcciones.push("Se registró reserva en MySQL: " + reserva.getCodigo());
            return true;
        }

        return false;
    }

    private boolean existeCruceReserva(Reserva nueva) {
        for (Reserva r : reservas) {
            if (r.getEstadoReserva() == EstadoReserva.CANCELADA) {
                continue;
            }

            boolean mismaArea = r.getAreaComun().getId().equals(nueva.getAreaComun().getId());
            boolean mismaFecha = r.getFecha().equals(nueva.getFecha());
            boolean cruce = nueva.getHoraInicio().isBefore(r.getHoraFin())
                    && nueva.getHoraFin().isAfter(r.getHoraInicio());

            if (mismaArea && mismaFecha && cruce) {
                return true;
            }
        }

        return false;
    }

    public boolean procesarSiguienteReserva() {
        while (!colaReservasPendientes.isEmpty()) {
            Reserva r = colaReservasPendientes.poll();

            if (r.getEstadoReserva() == EstadoReserva.PENDIENTE) {
                r.confirmar();
                dao.actualizarEstadoReserva(r.getCodigo(), EstadoReserva.CONFIRMADA);
                pilaAcciones.push("Se confirmó reserva con cola FIFO: " + r.getCodigo());
                return true;
            }
        }

        return false;
    }

    public Reserva buscarReservaSecuencial(String codigo) {
        return arbolReservas.buscar(codigo);
    }

    public boolean cancelarReserva(String codigo) {
        Reserva r = arbolReservas.buscar(codigo);

        if (r == null || r.getEstadoReserva() == EstadoReserva.CANCELADA) {
            return false;
        }

        if (dao.actualizarEstadoReserva(codigo, EstadoReserva.CANCELADA)) {
            r.cancelar();
            colaReservasPendientes.remove(r);
            pilaAcciones.push("Se canceló reserva: " + codigo);
            return true;
        }

        return false;
    }

    public boolean registrarPago(Pago pago) {
        if (pago == null ||
                !Validador.enteroPositivo(pago.getCodigo()) ||
                arbolPagos.contiene(pago.getCodigo()) ||
                !Validador.fechaNoFutura(pago.getFecha()) ||
                !Validador.decimalPositivo(pago.getMonto()) ||
                !Validador.texto(pago.getConcepto()) ||
                !Validador.texto(pago.getNroComprobante()) ||
                arbolComprobantes.contiene(pago.getNroComprobante()) ||
                pago.getCondomino() == null ||
                pago.getCondomino().getEstadoCondomino() != EstadoCondomino.ACTIVO) {
            return false;
        }

        pago.registrarPago();

        if (dao.insertarPago(pago)) {
            pagos.add(pago);
            arbolPagos.insertar(pago);
            arbolComprobantes.insertar(pago);
            pilaAcciones.push("Se registró pago en MySQL: " + pago.getCodigo());
            return true;
        }

        return false;
    }

    public Pago buscarPagoSecuencial(String codigo) {
        return arbolPagos.buscar(codigo);
    }

    public boolean actualizarEstadoPago(String codigo, EstadoPago estadoPago) {
        Pago p = arbolPagos.buscar(codigo);

        if (p == null || estadoPago == null) {
            return false;
        }

        if (dao.actualizarEstadoPago(codigo, estadoPago)) {
            p.actualizarEstado(estadoPago);
            pilaAcciones.push("Se actualizó pago: " + codigo);
            return true;
        }

        return false;
    }

    public boolean registrarMulta(Multa multa) {
        if (multa == null ||
                !Validador.enteroPositivo(multa.getCodigo()) ||
                arbolMultas.contiene(multa.getCodigo()) ||
                !Validador.fechaNoFutura(multa.getFecha()) ||
                !Validador.texto(multa.getMotivo()) ||
                !Validador.decimalPositivo(multa.getValor()) ||
                !Validador.texto(multa.getTipoInfraccion()) ||
                multa.getCondomino() == null ||
                multa.getCondomino().getEstadoCondomino() != EstadoCondomino.ACTIVO) {
            return false;
        }

        if (dao.insertarMulta(multa)) {
            multas.add(multa);
            arbolMultas.insertar(multa);
            pilaAcciones.push("Se registró multa en MySQL: " + multa.getCodigo());
            return true;
        }

        return false;
    }

    public Multa buscarMultaSecuencial(String codigo) {
        return arbolMultas.buscar(codigo);
    }

    public boolean pagarMulta(String codigo) {
        Multa m = arbolMultas.buscar(codigo);

        if (m == null || m.getEstadoMulta() != EstadoMulta.PENDIENTE) {
            return false;
        }

        if (dao.actualizarEstadoMulta(codigo, EstadoMulta.PAGADA)) {
            m.pagarMulta();
            pilaAcciones.push("Se pagó multa: " + codigo);
            return true;
        }

        return false;
    }

    public boolean anularMulta(String codigo) {
        Multa m = arbolMultas.buscar(codigo);

        if (m == null || m.getEstadoMulta() == EstadoMulta.ANULADA) {
            return false;
        }

        if (dao.actualizarEstadoMulta(codigo, EstadoMulta.ANULADA)) {
            m.anular();
            pilaAcciones.push("Se anuló multa: " + codigo);
            return true;
        }

        return false;
    }

    public Factura generarFactura(String numero, Condomino condomino, Pago pago, Multa multa) {
        if (!Validador.enteroPositivo(numero) ||
                arbolFacturas.contiene(numero) ||
                condomino == null ||
                condomino.getEstadoCondomino() != EstadoCondomino.ACTIVO ||
                (pago == null && multa == null)) {
            return null;
        }

        if (pago != null && pago.getEstadoPago() != EstadoPago.PAGADO) {
            return null;
        }

        if (multa != null && multa.getEstadoMulta() == EstadoMulta.ANULADA) {
            return null;
        }

        Factura f = new Factura(numero, LocalDate.now(), condomino, pago, multa);

        if (!Validador.decimalPositivo(f.getTotalMonto())) {
            return null;
        }

        if (dao.insertarFactura(f)) {
            facturas.add(f);
            arbolFacturas.insertar(f);
            pilaAcciones.push("Se generó factura en MySQL: " + numero);
            return f;
        }

        return null;
    }

    public Factura buscarFacturaSecuencial(String numero) {
        return arbolFacturas.buscar(numero);
    }

    public boolean anularFactura(String numero) {
        Factura f = arbolFacturas.buscar(numero);

        if (f == null || f.getEstadoFactura() == EstadoFactura.ANULADA) {
            return false;
        }

        if (dao.actualizarEstadoFactura(numero, EstadoFactura.ANULADA)) {
            f.anular();
            pilaAcciones.push("Se anuló factura: " + numero);
            return true;
        }

        return false;
    }

    public double calcularTotalFacturasRecursivo() {
        return sumarFacturas(0);
    }

    private double sumarFacturas(int posicion) {
        if (posicion >= facturas.size()) {
            return 0;
        }

        Factura actual = facturas.get(posicion);
        double valor = actual.getEstadoFactura() == EstadoFactura.EMITIDA
                ? actual.getTotalMonto()
                : 0;

        return valor + sumarFacturas(posicion + 1);
    }

    public void ordenarPagosPorFechaInsertionSort() {
        for (int i = 1; i < pagos.size(); i++) {
            Pago actual = pagos.get(i);
            int j = i - 1;

            while (j >= 0 && pagos.get(j).getFecha().isAfter(actual.getFecha())) {
                pagos.set(j + 1, pagos.get(j));
                j--;
            }

            pagos.set(j + 1, actual);
        }
    }

    public String clasificarPagosPorEstado() {
        int pagados = 0;
        int pendientes = 0;
        int atrasados = 0;

        for (Pago p : pagos) {
            if (p.getEstadoPago() == EstadoPago.PAGADO) pagados++;
            if (p.getEstadoPago() == EstadoPago.PENDIENTE) pendientes++;
            if (p.getEstadoPago() == EstadoPago.ATRASADO) atrasados++;
        }

        return "Pagados: " + pagados +
                "\nPendientes: " + pendientes +
                "\nAtrasados: " + atrasados;
    }

    public String verUltimaAccion() {
        return pilaAcciones.isEmpty() ? "No hay acciones registradas." : pilaAcciones.peek();
    }

    public String quitarUltimaAccion() {
        return pilaAcciones.isEmpty() ? "No hay acciones para quitar." : pilaAcciones.pop();
    }

    private String reporte(ArrayList<?> lista, String titulo) {
        StringBuilder sb = new StringBuilder(titulo).append("\n\n");

        if (lista.isEmpty()) {
            sb.append("No existen registros.\n");
        }

        for (Object dato : lista) {
            sb.append(dato).append("\n");
        }

        return sb.toString();
    }

    public String generarReporteCondominos() {
        cargarDatosDesdeMySQL();
        return reporte(condominos, "REPORTE DE CONDÓMINOS");
    }

    public String generarReporteReservas() {
        cargarDatosDesdeMySQL();
        return reporte(reservas, "REPORTE DE RESERVAS");
    }

    public String generarReportePagos() {
        cargarDatosDesdeMySQL();
        ordenarPagosPorFechaInsertionSort();

        return reporte(pagos, "REPORTE DE PAGOS ORDENADOS POR FECHA")
                + "\n" + clasificarPagosPorEstado();
    }

    public String generarReporteMultas() {
        cargarDatosDesdeMySQL();
        return reporte(multas, "REPORTE DE MULTAS");
    }

    public String generarReporteFacturas() {
        cargarDatosDesdeMySQL();

        return reporte(facturas, "REPORTE DE FACTURAS")
                + "\nTotal facturado: $" + calcularTotalFacturasRecursivo();
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