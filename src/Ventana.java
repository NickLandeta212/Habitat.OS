import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Ventana {

    // Componentes enlazados al .form (Swing UI Designer de IntelliJ)
    private JPanel mainPanel;
    private JTabbedPane tabs;
    private JPanel condPanel;
    private JPanel areaPanel;
    private JPanel reservaPanel;
    private JPanel pagoPanel;
    private JPanel multaPanel;
    private JPanel facturaPanel;
    private JPanel accionesPanel;

    private final GestorHabitatOS gestor = new GestorHabitatOS();

    public Ventana() {
        construirCondominos();
        construirAreas();
        construirReservas();
        construirPagos();
        construirMultas();
        construirFacturas();
        construirAcciones();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // ---------- Helpers genéricos ----------
    private JPanel formGrid(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 6, 6));
        for (int i = 0; i < labels.length; i++) {
            p.add(new JLabel(labels[i]));
            p.add(fields[i]);
        }
        return p;
    }

    private JPanel armarTab(JPanel form, JPanel botones, JTextArea salida) {
        JPanel contenedor = new JPanel(new BorderLayout(8, 8));
        contenedor.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contenedor.add(form, BorderLayout.NORTH);
        contenedor.add(botones, BorderLayout.CENTER);
        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        contenedor.add(new JScrollPane(salida), BorderLayout.SOUTH);
        return contenedor;
    }

    private void mostrar(JTextArea area, String texto) {
        area.setText(texto == null ? "" : texto);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(mainPanel, msg);
    }

    // ---------- Pestaña: Condóminos ----------
    private void construirCondominos() {
        JTextField tId = new JTextField();
        JTextField tCedula = new JTextField();
        JTextField tNombre = new JTextField();
        JTextField tTel = new JTextField();
        JTextField tCorreo = new JTextField();
        JTextField tDepto = new JTextField();
        JComboBox<EstadoCondomino> cbEstado = new JComboBox<>(EstadoCondomino.values());

        JPanel form = formGrid(
                new String[]{"ID:", "Cédula:", "Nombre:", "Teléfono:", "Correo:", "Departamento:", "Estado:"},
                new JComponent[]{tId, tCedula, tNombre, tTel, tCorreo, tDepto, cbEstado});

        JButton bReg = new JButton("Registrar");
        JButton bBus = new JButton("Buscar (cédula)");
        JButton bMod = new JButton("Modificar");
        JButton bIn = new JButton("Inactivar");
        JButton bList = new JButton("Listar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bReg); botones.add(bBus); botones.add(bMod); botones.add(bIn); botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {
            try {
                Condomino c = new Condomino(tId.getText().trim(), tCedula.getText().trim(),
                        tNombre.getText().trim(), tTel.getText().trim(), tCorreo.getText().trim(),
                        tDepto.getText().trim(), (EstadoCondomino) cbEstado.getSelectedItem());
                info(gestor.registrarCondomino(c) ? "Condómino registrado." : "Ya existe un condómino con esa cédula.");
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bBus.addActionListener(e -> {
            Condomino c = gestor.buscarCondominoSecuencial(tCedula.getText().trim());
            mostrar(salida, c == null ? "No encontrado." : c.toString());
        });

        bMod.addActionListener(e -> {
            boolean ok = gestor.modificarCondomino(tCedula.getText().trim(),
                    tTel.getText().trim(), tCorreo.getText().trim(),
                    (EstadoCondomino) cbEstado.getSelectedItem());
            info(ok ? "Modificado." : "No encontrado.");
        });

        bIn.addActionListener(e -> info(
                gestor.inactivarCondomino(tCedula.getText().trim()) ? "Inactivado." : "No encontrado."));

        bList.addActionListener(e -> mostrar(salida, gestor.generarReporteCondominos()));

        condPanel.setLayout(new BorderLayout());
        condPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Áreas Comunes ----------
    private void construirAreas() {
        JTextField tId = new JTextField();
        JTextField tNombre = new JTextField();
        JTextField tCap = new JTextField();
        JTextField tDesc = new JTextField();
        JCheckBox cbDisp = new JCheckBox("Disponible", true);

        JPanel form = formGrid(
                new String[]{"ID:", "Nombre:", "Capacidad:", "Descripción:", ""},
                new JComponent[]{tId, tNombre, tCap, tDesc, cbDisp});

        JButton bReg = new JButton("Registrar");
        JButton bBus = new JButton("Buscar (id)");
        JButton bList = new JButton("Listar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bReg); botones.add(bBus); botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {
            try {
                AreaComun a = new AreaComun(tId.getText().trim(), tNombre.getText().trim(),
                        Integer.parseInt(tCap.getText().trim()), tDesc.getText().trim(), cbDisp.isSelected());
                info(gestor.registrarAreaComun(a) ? "Área registrada." : "Ya existe esa área.");
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bBus.addActionListener(e -> {
            AreaComun a = gestor.buscarAreaSecuencial(tId.getText().trim());
            mostrar(salida, a == null ? "No encontrada." : a.toString());
        });

        bList.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("ÁREAS COMUNES\n\n");
            for (AreaComun a : gestor.getAreasComunes()) sb.append(a).append("\n");
            mostrar(salida, sb.toString());
        });

        areaPanel.setLayout(new BorderLayout());
        areaPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Reservas ----------
    private void construirReservas() {
        JTextField tCodigo = new JTextField();
        JTextField tFecha = new JTextField(LocalDate.now().toString());
        JTextField tHi = new JTextField("08:00");
        JTextField tHf = new JTextField("10:00");
        JTextField tIdArea = new JTextField();
        JTextField tCedula = new JTextField();

        JPanel form = formGrid(
                new String[]{"Código:", "Fecha (YYYY-MM-DD):", "Hora inicio (HH:mm):", "Hora fin (HH:mm):", "ID Área:", "Cédula condómino:"},
                new JComponent[]{tCodigo, tFecha, tHi, tHf, tIdArea, tCedula});

        JButton bSol = new JButton("Solicitar");
        JButton bProc = new JButton("Procesar siguiente");
        JButton bCanc = new JButton("Cancelar");
        JButton bList = new JButton("Listar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bSol); botones.add(bProc); botones.add(bCanc); botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bSol.addActionListener(e -> {
            try {
                Condomino c = gestor.buscarCondominoSecuencial(tCedula.getText().trim());
                AreaComun a = gestor.buscarAreaSecuencial(tIdArea.getText().trim());
                if (c == null || a == null) { info("Condómino o área no encontrados."); return; }
                Reserva r = new Reserva(tCodigo.getText().trim(),
                        LocalDate.parse(tFecha.getText().trim()),
                        LocalTime.parse(tHi.getText().trim()),
                        LocalTime.parse(tHf.getText().trim()), a, c);
                info(gestor.solicitarReserva(r) ? "Reserva solicitada." : "Reserva inválida.");
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bProc.addActionListener(e -> info(
                gestor.procesarSiguienteReserva() ? "Reserva confirmada." : "Cola vacía o reserva cancelada."));

        bCanc.addActionListener(e -> info(
                gestor.cancelarReserva(tCodigo.getText().trim()) ? "Reserva cancelada." : "No encontrada."));

        bList.addActionListener(e -> mostrar(salida, gestor.generarReporteReservas()));

        reservaPanel.setLayout(new BorderLayout());
        reservaPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Pagos ----------
    private void construirPagos() {
        JTextField tCodigo = new JTextField();
        JTextField tFecha = new JTextField(LocalDate.now().toString());
        JTextField tMonto = new JTextField();
        JTextField tConcepto = new JTextField();
        JTextField tComp = new JTextField();
        JTextField tCedula = new JTextField();
        JComboBox<EstadoPago> cbEstado = new JComboBox<>(EstadoPago.values());

        JPanel form = formGrid(
                new String[]{"Código:", "Fecha (YYYY-MM-DD):", "Monto:", "Concepto:", "Comprobante:", "Cédula condómino:", "Nuevo estado:"},
                new JComponent[]{tCodigo, tFecha, tMonto, tConcepto, tComp, tCedula, cbEstado});

        JButton bReg = new JButton("Registrar");
        JButton bAct = new JButton("Actualizar estado");
        JButton bList = new JButton("Listar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bReg); botones.add(bAct); botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {
            try {
                Condomino c = gestor.buscarCondominoSecuencial(tCedula.getText().trim());
                if (c == null) { info("Condómino no encontrado."); return; }
                Pago p = new Pago(tCodigo.getText().trim(),
                        LocalDate.parse(tFecha.getText().trim()),
                        Double.parseDouble(tMonto.getText().trim()),
                        tConcepto.getText().trim(), tComp.getText().trim(), c);
                gestor.registrarPago(p);
                info("Pago registrado.");
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bAct.addActionListener(e -> info(
                gestor.actualizarEstadoPago(tCodigo.getText().trim(), (EstadoPago) cbEstado.getSelectedItem())
                        ? "Estado actualizado." : "Pago no encontrado."));

        bList.addActionListener(e -> mostrar(salida, gestor.generarReportePagos()));

        pagoPanel.setLayout(new BorderLayout());
        pagoPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Multas ----------
    private void construirMultas() {
        JTextField tCodigo = new JTextField();
        JTextField tFecha = new JTextField(LocalDate.now().toString());
        JTextField tMotivo = new JTextField();
        JTextField tValor = new JTextField();
        JTextField tTipo = new JTextField();
        JTextField tCedula = new JTextField();

        JPanel form = formGrid(
                new String[]{"Código:", "Fecha (YYYY-MM-DD):", "Motivo:", "Valor:", "Tipo infracción:", "Cédula condómino:"},
                new JComponent[]{tCodigo, tFecha, tMotivo, tValor, tTipo, tCedula});

        JButton bReg = new JButton("Registrar");
        JButton bPag = new JButton("Pagar");
        JButton bAnu = new JButton("Anular");
        JButton bList = new JButton("Listar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bReg); botones.add(bPag); botones.add(bAnu); botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {
            try {
                Condomino c = gestor.buscarCondominoSecuencial(tCedula.getText().trim());
                if (c == null) { info("Condómino no encontrado."); return; }
                Multa m = new Multa(tCodigo.getText().trim(),
                        LocalDate.parse(tFecha.getText().trim()),
                        tMotivo.getText().trim(),
                        Double.parseDouble(tValor.getText().trim()),
                        tTipo.getText().trim(), c);
                gestor.registrarMulta(m);
                info("Multa registrada.");
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bPag.addActionListener(e -> info(
                gestor.pagarMulta(tCodigo.getText().trim()) ? "Multa pagada." : "No encontrada."));

        bAnu.addActionListener(e -> info(
                gestor.anularMulta(tCodigo.getText().trim()) ? "Multa anulada." : "No encontrada."));

        bList.addActionListener(e -> mostrar(salida, gestor.generarReporteMultas()));

        multaPanel.setLayout(new BorderLayout());
        multaPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Facturas ----------
    private void construirFacturas() {
        JTextField tNum = new JTextField();
        JTextField tCedula = new JTextField();
        JTextField tCodPago = new JTextField();
        JTextField tCodMulta = new JTextField();

        JPanel form = formGrid(
                new String[]{"Número factura:", "Cédula condómino:", "Código pago (opcional):", "Código multa (opcional):"},
                new JComponent[]{tNum, tCedula, tCodPago, tCodMulta});

        JButton bGen = new JButton("Generar");
        JButton bAnu = new JButton("Anular");
        JButton bList = new JButton("Listar");
        JButton bTotal = new JButton("Total facturado");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bGen); botones.add(bAnu); botones.add(bList); botones.add(bTotal);

        JTextArea salida = new JTextArea(14, 80);

        bGen.addActionListener(e -> {
            try {
                Condomino c = gestor.buscarCondominoSecuencial(tCedula.getText().trim());
                if (c == null) { info("Condómino no encontrado."); return; }
                Pago p = tCodPago.getText().trim().isEmpty() ? null
                        : gestor.buscarPagoSecuencial(tCodPago.getText().trim());
                Multa m = tCodMulta.getText().trim().isEmpty() ? null
                        : gestor.buscarMultaSecuencial(tCodMulta.getText().trim());
                Factura f = gestor.generarFactura(tNum.getText().trim(), c, p, m);
                mostrar(salida, f.toString());
            } catch (Exception ex) { info("Error: " + ex.getMessage()); }
        });

        bAnu.addActionListener(e -> info(
                gestor.anularFactura(tNum.getText().trim()) ? "Factura anulada." : "No encontrada."));

        bList.addActionListener(e -> mostrar(salida, gestor.generarReporteFacturas()));

        bTotal.addActionListener(e -> mostrar(salida,
                "Total general (recursivo): $" + gestor.calcularTotalFacturasRecursivo()));

        facturaPanel.setLayout(new BorderLayout());
        facturaPanel.add(armarTab(form, botones, salida));
    }

    // ---------- Pestaña: Acciones / Reportes ----------
    private void construirAcciones() {
        JButton bVer = new JButton("Ver última acción");
        JButton bQuitar = new JButton("Quitar última acción");
        JButton bGeneral = new JButton("Reporte general");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(bVer); botones.add(bQuitar); botones.add(bGeneral);

        JTextArea salida = new JTextArea(20, 80);

        bVer.addActionListener(e -> mostrar(salida, gestor.verUltimaAccion()));
        bQuitar.addActionListener(e -> mostrar(salida, gestor.quitarUltimaAccion()));
        bGeneral.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append(gestor.generarReporteCondominos()).append("\n")
              .append(gestor.generarReporteReservas()).append("\n")
              .append(gestor.generarReportePagos()).append("\n")
              .append(gestor.generarReporteMultas()).append("\n")
              .append(gestor.generarReporteFacturas());
            mostrar(salida, sb.toString());
        });

        accionesPanel.setLayout(new BorderLayout());
        accionesPanel.add(armarTab(new JPanel(), botones, salida));
    }

    // ---------- Punto de entrada ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("HABITAT.OS");
            frame.setContentPane(new Ventana().getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(960, 640);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
