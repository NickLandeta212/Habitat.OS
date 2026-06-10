
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Ventana {

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

    private JPanel formGrid(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 6, 6));

        for (int i = 0; i < labels.length; i++) {
            p.add(new JLabel(labels[i]));
            p.add(fields[i]);
        }

        return p;
    }

    private JPanel armarTab(
            JPanel form,
            JPanel botones,
            JTextArea salida
    ) {
        JPanel contenedor = new JPanel(new BorderLayout(8, 8));

        contenedor.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        contenedor.add(form, BorderLayout.NORTH);
        contenedor.add(botones, BorderLayout.CENTER);

        salida.setEditable(false);
        salida.setFont(
                new Font(Font.MONOSPACED, Font.PLAIN, 12)
        );

        contenedor.add(
                new JScrollPane(salida),
                BorderLayout.SOUTH
        );

        return contenedor;
    }

    private void mostrar(JTextArea area, String texto) {
        area.setText(texto == null ? "" : texto);
    }

    private void info(String mensaje) {
        JOptionPane.showMessageDialog(
                mainPanel,
                mensaje,
                "HABITAT.OS",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(
                mainPanel,
                mensaje,
                "Validación",
                JOptionPane.ERROR_MESSAGE
        );
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

    private boolean soloNumeros(String texto) {
        if (textoVacio(texto)) {
            return false;
        }

        String valor = texto.trim();

        for (int i = 0; i < valor.length(); i++) {
            if (!Character.isDigit(valor.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean enteroPositivo(String texto) {
        if (!soloNumeros(texto)) {
            return false;
        } else if (texto.trim().length() > 9) {
            return false;
        }

        try {
            int numero = Integer.parseInt(texto.trim());

            if (numero <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean decimalPositivo(String texto) {
        if (textoVacio(texto)) {
            return false;
        }

        try {
            double numero = Double.parseDouble(
                    texto.trim().replace(',', '.')
            );

            if (Double.isNaN(numero)) {
                return false;
            } else if (Double.isInfinite(numero)) {
                return false;
            } else if (numero <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean cedulaValida(String cedula) {
        if (!soloNumeros(cedula)) {
            return false;
        } else if (cedula.trim().length() != 10) {
            return false;
        } else {
            return true;
        }
    }

    private boolean telefonoValido(String telefono) {
        if (!soloNumeros(telefono)) {
            return false;
        } else if (telefono.trim().length() < 7) {
            return false;
        } else if (telefono.trim().length() > 10) {
            return false;
        } else {
            return true;
        }
    }

    private boolean correoValido(String correo) {
        if (textoVacio(correo)) {
            return false;
        } else if (!correo.contains("@")) {
            return false;
        } else if (correo.startsWith("@")) {
            return false;
        } else if (correo.endsWith("@")) {
            return false;
        } else {
            return true;
        }
    }

    private LocalDate obtenerFecha(String texto) {
        if (textoVacio(texto)) {
            return null;
        }

        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalTime obtenerHora(String texto) {
        if (textoVacio(texto)) {
            return null;
        }

        try {
            return LocalTime.parse(texto.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean existeIdCondomino(String id) {
        for (Condomino condomino : gestor.getCondominos()) {
            if (condomino.getId().trim().equals(id.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean existeNombreArea(String nombre) {
        for (AreaComun area : gestor.getAreasComunes()) {
            if (area.getNombre().trim()
                    .equalsIgnoreCase(nombre.trim())) {

                return true;
            }
        }

        return false;
    }

    private boolean existeReservaIgual(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            AreaComun area
    ) {
        for (Reserva reserva : gestor.getReservas()) {

            boolean mismaFecha =
                    reserva.getFecha().equals(fecha);

            boolean mismaHoraInicio =
                    reserva.getHoraInicio().equals(horaInicio);

            boolean mismaHoraFin =
                    reserva.getHoraFin().equals(horaFin);

            boolean mismaArea =
                    reserva.getAreaComun().getId()
                            .equals(area.getId());

            boolean activa =
                    reserva.getEstadoReserva()
                            != EstadoReserva.CANCELADA;

            if (mismaFecha
                    && mismaHoraInicio
                    && mismaHoraFin
                    && mismaArea
                    && activa) {

                return true;
            }
        }

        return false;
    }

    private void construirCondominos() {

        JTextField tId = new JTextField();
        JTextField tCedula = new JTextField();
        JTextField tNombre = new JTextField();
        JTextField tTel = new JTextField();
        JTextField tCorreo = new JTextField();
        JTextField tDepto = new JTextField();

        JComboBox<EstadoCondomino> cbEstado =
                new JComboBox<>(EstadoCondomino.values());

        JPanel form = formGrid(
                new String[]{
                        "ID:",
                        "Cédula:",
                        "Nombre:",
                        "Teléfono:",
                        "Correo:",
                        "Departamento:",
                        "Estado:"
                },
                new JComponent[]{
                        tId,
                        tCedula,
                        tNombre,
                        tTel,
                        tCorreo,
                        tDepto,
                        cbEstado
                }
        );

        JButton bReg = new JButton("Registrar");
        JButton bBus = new JButton("Buscar (cédula)");
        JButton bMod = new JButton("Modificar");
        JButton bIn = new JButton("Inactivar");
        JButton bList = new JButton("Listar");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bReg);
        botones.add(bBus);
        botones.add(bMod);
        botones.add(bIn);
        botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {

            String id = tId.getText().trim();
            String cedula = tCedula.getText().trim();
            String nombre = tNombre.getText().trim();
            String telefono = tTel.getText().trim();
            String correo = tCorreo.getText().trim();
            String departamento = tDepto.getText().trim();

            if (textoVacio(id)) {
                error("Debe ingresar el ID.");

            } else if (!enteroPositivo(id)) {
                error("El ID debe ser un número entero mayor que cero.");

            } else if (textoVacio(cedula)) {
                error("Debe ingresar la cédula.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else if (textoVacio(nombre)) {
                error("Debe ingresar el nombre.");

            } else if (textoVacio(telefono)) {
                error("Debe ingresar el teléfono.");

            } else if (!telefonoValido(telefono)) {
                error("El teléfono debe contener entre 7 y 10 números.");

            } else if (textoVacio(correo)) {
                error("Debe ingresar el correo.");

            } else if (!correoValido(correo)) {
                error("Debe ingresar un correo válido.");

            } else if (textoVacio(departamento)) {
                error("Debe ingresar el departamento.");

            } else if (existeIdCondomino(id)) {
                error("Ya existe un condómino con el ID " + id + ".");

            } else if (
                    gestor.buscarCondominoSecuencial(cedula) != null) {

                error("Ya existe un condómino con esa cédula.");

            } else {

                Condomino condomino = new Condomino(
                        id,
                        cedula,
                        nombre,
                        telefono,
                        correo,
                        departamento,
                        (EstadoCondomino)
                                cbEstado.getSelectedItem()
                );

                if (gestor.registrarCondomino(condomino)) {
                    info("Condómino registrado correctamente.");

                    tId.setText("");
                    tCedula.setText("");
                    tNombre.setText("");
                    tTel.setText("");
                    tCorreo.setText("");
                    tDepto.setText("");

                } else {
                    error("No se pudo registrar el condómino.");
                }
            }
        });

        bBus.addActionListener(e -> {

            String cedula = tCedula.getText().trim();

            if (textoVacio(cedula)) {
                error("Debe ingresar la cédula.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                if (condomino == null) {
                    mostrar(
                            salida,
                            "Condómino no encontrado."
                    );
                } else {
                    mostrar(
                            salida,
                            condomino.toString()
                    );
                }
            }
        });

        bMod.addActionListener(e -> {

            String cedula = tCedula.getText().trim();
            String telefono = tTel.getText().trim();
            String correo = tCorreo.getText().trim();

            if (textoVacio(cedula)) {
                error("Debe ingresar la cédula.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else if (
                    gestor.buscarCondominoSecuencial(cedula) == null) {

                error("El condómino no está registrado.");

            } else if (textoVacio(telefono)) {
                error("Debe ingresar el teléfono.");

            } else if (!telefonoValido(telefono)) {
                error("El teléfono debe contener entre 7 y 10 números.");

            } else if (textoVacio(correo)) {
                error("Debe ingresar el correo.");

            } else if (!correoValido(correo)) {
                error("Debe ingresar un correo válido.");

            } else {

                boolean modificado =
                        gestor.modificarCondomino(
                                cedula,
                                telefono,
                                correo,
                                (EstadoCondomino)
                                        cbEstado.getSelectedItem()
                        );

                if (modificado) {
                    info("Condómino modificado correctamente.");
                } else {
                    error("No se pudo modificar el condómino.");
                }
            }
        });

        bIn.addActionListener(e -> {

            String cedula = tCedula.getText().trim();

            if (textoVacio(cedula)) {
                error("Debe ingresar la cédula.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                if (condomino == null) {
                    error("El condómino no está registrado.");

                } else if (
                        condomino.getEstadoCondomino()
                                == EstadoCondomino.INACTIVO) {

                    error("El condómino ya se encuentra inactivo.");

                } else if (gestor.inactivarCondomino(cedula)) {
                    info("Condómino inactivado correctamente.");

                } else {
                    error("No se pudo inactivar el condómino.");
                }
            }
        });

        bList.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.generarReporteCondominos()
                )
        );

        condPanel.setLayout(new BorderLayout());
        condPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirAreas() {

        JTextField tId = new JTextField();
        JTextField tNombre = new JTextField();
        JTextField tCap = new JTextField();
        JTextField tDesc = new JTextField();

        JCheckBox cbDisp =
                new JCheckBox("Disponible", true);

        JPanel form = formGrid(
                new String[]{
                        "ID:",
                        "Nombre:",
                        "Capacidad:",
                        "Descripción:",
                        ""
                },
                new JComponent[]{
                        tId,
                        tNombre,
                        tCap,
                        tDesc,
                        cbDisp
                }
        );

        JButton bReg = new JButton("Registrar");
        JButton bBus = new JButton("Buscar (id)");
        JButton bList = new JButton("Listar");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bReg);
        botones.add(bBus);
        botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {

            String id = tId.getText().trim();
            String nombre = tNombre.getText().trim();
            String capacidadTexto = tCap.getText().trim();
            String descripcion = tDesc.getText().trim();

            if (textoVacio(id)) {
                error("Debe ingresar el ID del área.");

            } else if (!enteroPositivo(id)) {
                error("El ID debe ser un número entero mayor que cero.");

            } else if (textoVacio(nombre)) {
                error("Debe ingresar el nombre del área.");

            } else if (textoVacio(capacidadTexto)) {
                error("Debe ingresar la capacidad.");

            } else if (!enteroPositivo(capacidadTexto)) {
                error("La capacidad debe ser un número entero mayor que cero.");

            } else if (textoVacio(descripcion)) {
                error("Debe ingresar la descripción.");

            } else if (
                    gestor.buscarAreaSecuencial(id) != null) {

                error("Ya existe un área con el ID " + id + ".");

            } else if (existeNombreArea(nombre)) {
                error("Ya existe un área con ese nombre.");

            } else {

                int capacidad =
                        Integer.parseInt(capacidadTexto);

                AreaComun area = new AreaComun(
                        id,
                        nombre,
                        capacidad,
                        descripcion,
                        cbDisp.isSelected()
                );

                if (gestor.registrarAreaComun(area)) {
                    info("Área registrada correctamente.");

                    tId.setText("");
                    tNombre.setText("");
                    tCap.setText("");
                    tDesc.setText("");

                } else {
                    error("No se pudo registrar el área.");
                }
            }
        });

        bBus.addActionListener(e -> {

            String id = tId.getText().trim();

            if (textoVacio(id)) {
                error("Debe ingresar el ID del área.");

            } else if (!enteroPositivo(id)) {
                error("El ID debe ser un número entero mayor que cero.");

            } else {

                AreaComun area =
                        gestor.buscarAreaSecuencial(id);

                if (area == null) {
                    mostrar(
                            salida,
                            "Área común no encontrada."
                    );
                } else {
                    mostrar(
                            salida,
                            area.toString()
                    );
                }
            }
        });

        bList.addActionListener(e -> {

            StringBuilder sb =
                    new StringBuilder("ÁREAS COMUNES\n\n");

            if (gestor.getAreasComunes().isEmpty()) {
                sb.append(
                        "No existen áreas comunes registradas."
                );
            } else {
                for (AreaComun area :
                        gestor.getAreasComunes()) {

                    sb.append(area).append("\n");
                }
            }

            mostrar(salida, sb.toString());
        });

        areaPanel.setLayout(new BorderLayout());
        areaPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirReservas() {

        JTextField tCodigo = new JTextField();

        JTextField tFecha =
                new JTextField(LocalDate.now().toString());

        JTextField tHi = new JTextField("08:00");
        JTextField tHf = new JTextField("10:00");
        JTextField tIdArea = new JTextField();
        JTextField tCedula = new JTextField();

        JPanel form = formGrid(
                new String[]{
                        "Código:",
                        "Fecha (YYYY-MM-DD):",
                        "Hora inicio (HH:mm):",
                        "Hora fin (HH:mm):",
                        "ID Área:",
                        "Cédula condómino:"
                },
                new JComponent[]{
                        tCodigo,
                        tFecha,
                        tHi,
                        tHf,
                        tIdArea,
                        tCedula
                }
        );

        JButton bSol = new JButton("Solicitar");
        JButton bProc = new JButton("Procesar siguiente");
        JButton bCanc = new JButton("Cancelar");
        JButton bList = new JButton("Listar");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bSol);
        botones.add(bProc);
        botones.add(bCanc);
        botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bSol.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();
            String fechaTexto = tFecha.getText().trim();
            String horaInicioTexto = tHi.getText().trim();
            String horaFinTexto = tHf.getText().trim();
            String idArea = tIdArea.getText().trim();
            String cedula = tCedula.getText().trim();

            LocalDate fecha = obtenerFecha(fechaTexto);
            LocalTime horaInicio =
                    obtenerHora(horaInicioTexto);

            LocalTime horaFin =
                    obtenerHora(horaFinTexto);

            if (textoVacio(codigo)) {
                error("Debe ingresar el código de la reserva.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else if (
                    gestor.buscarReservaSecuencial(codigo) != null) {

                error("Ya existe una reserva con ese código.");

            } else if (textoVacio(fechaTexto)) {
                error("Debe ingresar la fecha.");

            } else if (fecha == null) {
                error("La fecha debe tener el formato YYYY-MM-DD.");

            } else if (textoVacio(horaInicioTexto)) {
                error("Debe ingresar la hora de inicio.");

            } else if (horaInicio == null) {
                error("La hora de inicio debe tener el formato HH:mm.");

            } else if (textoVacio(horaFinTexto)) {
                error("Debe ingresar la hora de finalización.");

            } else if (horaFin == null) {
                error("La hora final debe tener el formato HH:mm.");

            } else if (!horaInicio.isBefore(horaFin)) {
                error("La hora de inicio debe ser menor que la hora final.");

            } else if (textoVacio(idArea)) {
                error("Debe ingresar el ID del área.");

            } else if (!enteroPositivo(idArea)) {
                error("El ID del área debe ser un número mayor que cero.");

            } else if (textoVacio(cedula)) {
                error("Debe ingresar la cédula del condómino.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                AreaComun area =
                        gestor.buscarAreaSecuencial(idArea);

                if (condomino == null) {
                    error("El condómino no está registrado.");

                } else if (
                        condomino.getEstadoCondomino()
                                == EstadoCondomino.INACTIVO) {

                    error("No se pueden realizar reservas para un condómino inactivo.");

                } else if (area == null) {
                    error("El área común no está registrada.");

                } else if (!area.estaDisponible()) {
                    error("El área común no está disponible.");

                } else if (
                        existeReservaIgual(
                                fecha,
                                horaInicio,
                                horaFin,
                                area
                        )) {

                    error("Ya existe una reserva para esa área, fecha y horario.");

                } else {

                    Reserva reserva = new Reserva(
                            codigo,
                            fecha,
                            horaInicio,
                            horaFin,
                            area,
                            condomino
                    );

                    if (gestor.solicitarReserva(reserva)) {
                        info("Reserva solicitada correctamente.");

                        tCodigo.setText("");
                        tIdArea.setText("");
                        tCedula.setText("");

                    } else {
                        error("La reserva no cumple las condiciones requeridas.");
                    }
                }
            }
        });

        bProc.addActionListener(e -> {

            boolean procesada =
                    gestor.procesarSiguienteReserva();

            if (procesada) {
                info("La siguiente reserva fue confirmada.");
            } else {
                error("No existen reservas pendientes o la reserva fue cancelada por cruce de horario.");
            }
        });

        bCanc.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();

            if (textoVacio(codigo)) {
                error("Debe ingresar el código de la reserva.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else {

                Reserva reserva =
                        gestor.buscarReservaSecuencial(codigo);

                if (reserva == null) {
                    error("La reserva no está registrada.");

                } else if (
                        reserva.getEstadoReserva()
                                == EstadoReserva.CANCELADA) {

                    error("La reserva ya se encuentra cancelada.");

                } else if (gestor.cancelarReserva(codigo)) {
                    info("Reserva cancelada correctamente.");

                } else {
                    error("No se pudo cancelar la reserva.");
                }
            }
        });

        bList.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.generarReporteReservas()
                )
        );

        reservaPanel.setLayout(new BorderLayout());
        reservaPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirPagos() {

        JTextField tCodigo = new JTextField();

        JTextField tFecha =
                new JTextField(LocalDate.now().toString());

        JTextField tMonto = new JTextField();
        JTextField tConcepto = new JTextField();
        JTextField tComp = new JTextField();
        JTextField tCedula = new JTextField();

        JComboBox<EstadoPago> cbEstado =
                new JComboBox<>(EstadoPago.values());

        JPanel form = formGrid(
                new String[]{
                        "Código:",
                        "Fecha (YYYY-MM-DD):",
                        "Monto:",
                        "Concepto:",
                        "Comprobante:",
                        "Cédula condómino:",
                        "Nuevo estado:"
                },
                new JComponent[]{
                        tCodigo,
                        tFecha,
                        tMonto,
                        tConcepto,
                        tComp,
                        tCedula,
                        cbEstado
                }
        );

        JButton bReg = new JButton("Registrar");
        JButton bAct = new JButton("Actualizar estado");
        JButton bList = new JButton("Listar");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bReg);
        botones.add(bAct);
        botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();
            String fechaTexto = tFecha.getText().trim();
            String montoTexto = tMonto.getText().trim();
            String concepto = tConcepto.getText().trim();
            String comprobante = tComp.getText().trim();
            String cedula = tCedula.getText().trim();

            LocalDate fecha = obtenerFecha(fechaTexto);

            if (textoVacio(codigo)) {
                error("Debe ingresar el código del pago.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else if (
                    gestor.buscarPagoSecuencial(codigo) != null) {

                error("Ya existe un pago con ese código.");

            } else if (textoVacio(fechaTexto)) {
                error("Debe ingresar la fecha.");

            } else if (fecha == null) {
                error("La fecha debe tener el formato YYYY-MM-DD.");

            } else if (textoVacio(montoTexto)) {
                error("Debe ingresar el monto.");

            } else if (!decimalPositivo(montoTexto)) {
                error("El monto debe ser un número mayor que cero.");

            } else if (textoVacio(concepto)) {
                error("Debe ingresar el concepto del pago.");

            } else if (textoVacio(comprobante)) {
                error("Debe ingresar el número de comprobante.");

            } else if (textoVacio(cedula)) {
                error("Debe ingresar la cédula del condómino.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                if (condomino == null) {
                    error("El condómino no está registrado.");

                } else {

                    double monto = Double.parseDouble(
                            montoTexto.replace(',', '.')
                    );

                    Pago pago = new Pago(
                            codigo,
                            fecha,
                            monto,
                            concepto,
                            comprobante,
                            condomino
                    );

                    if (gestor.registrarPago(pago)) {
                        info("Pago registrado correctamente.");

                        tCodigo.setText("");
                        tMonto.setText("");
                        tConcepto.setText("");
                        tComp.setText("");
                        tCedula.setText("");

                    } else {
                        error("No se pudo registrar el pago.");
                    }
                }
            }
        });

        bAct.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();

            if (textoVacio(codigo)) {
                error("Debe ingresar el código del pago.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else if (
                    gestor.buscarPagoSecuencial(codigo) == null) {

                error("El pago no está registrado.");

            } else if (
                    gestor.actualizarEstadoPago(
                            codigo,
                            (EstadoPago)
                                    cbEstado.getSelectedItem()
                    )) {

                info("Estado del pago actualizado.");

            } else {
                error("No se pudo actualizar el pago.");
            }
        });

        bList.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.generarReportePagos()
                )
        );

        pagoPanel.setLayout(new BorderLayout());
        pagoPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirMultas() {

        JTextField tCodigo = new JTextField();

        JTextField tFecha =
                new JTextField(LocalDate.now().toString());

        JTextField tMotivo = new JTextField();
        JTextField tValor = new JTextField();
        JTextField tTipo = new JTextField();
        JTextField tCedula = new JTextField();

        JPanel form = formGrid(
                new String[]{
                        "Código:",
                        "Fecha (YYYY-MM-DD):",
                        "Motivo:",
                        "Valor:",
                        "Tipo infracción:",
                        "Cédula condómino:"
                },
                new JComponent[]{
                        tCodigo,
                        tFecha,
                        tMotivo,
                        tValor,
                        tTipo,
                        tCedula
                }
        );

        JButton bReg = new JButton("Registrar");
        JButton bPag = new JButton("Pagar");
        JButton bAnu = new JButton("Anular");
        JButton bList = new JButton("Listar");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bReg);
        botones.add(bPag);
        botones.add(bAnu);
        botones.add(bList);

        JTextArea salida = new JTextArea(14, 80);

        bReg.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();
            String fechaTexto = tFecha.getText().trim();
            String motivo = tMotivo.getText().trim();
            String valorTexto = tValor.getText().trim();
            String tipo = tTipo.getText().trim();
            String cedula = tCedula.getText().trim();

            LocalDate fecha = obtenerFecha(fechaTexto);

            if (textoVacio(codigo)) {
                error("Debe ingresar el código de la multa.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else if (
                    gestor.buscarMultaSecuencial(codigo) != null) {

                error("Ya existe una multa con ese código.");

            } else if (textoVacio(fechaTexto)) {
                error("Debe ingresar la fecha.");

            } else if (fecha == null) {
                error("La fecha debe tener el formato YYYY-MM-DD.");

            } else if (textoVacio(motivo)) {
                error("Debe ingresar el motivo de la multa.");

            } else if (textoVacio(valorTexto)) {
                error("Debe ingresar el valor de la multa.");

            } else if (!decimalPositivo(valorTexto)) {
                error("El valor debe ser un número mayor que cero.");

            } else if (textoVacio(tipo)) {
                error("Debe ingresar el tipo de infracción.");

            } else if (textoVacio(cedula)) {
                error("Debe ingresar la cédula del condómino.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                if (condomino == null) {
                    error("El condómino no está registrado.");

                } else {

                    double valor = Double.parseDouble(
                            valorTexto.replace(',', '.')
                    );

                    Multa multa = new Multa(
                            codigo,
                            fecha,
                            motivo,
                            valor,
                            tipo,
                            condomino
                    );

                    if (gestor.registrarMulta(multa)) {
                        info("Multa registrada correctamente.");

                        tCodigo.setText("");
                        tMotivo.setText("");
                        tValor.setText("");
                        tTipo.setText("");
                        tCedula.setText("");

                    } else {
                        error("No se pudo registrar la multa.");
                    }
                }
            }
        });

        bPag.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();

            if (textoVacio(codigo)) {
                error("Debe ingresar el código de la multa.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else {

                Multa multa =
                        gestor.buscarMultaSecuencial(codigo);

                if (multa == null) {
                    error("La multa no está registrada.");

                } else if (gestor.pagarMulta(codigo)) {
                    info("Multa pagada correctamente.");

                } else {
                    error("No se pudo pagar la multa.");
                }
            }
        });

        bAnu.addActionListener(e -> {

            String codigo = tCodigo.getText().trim();

            if (textoVacio(codigo)) {
                error("Debe ingresar el código de la multa.");

            } else if (!enteroPositivo(codigo)) {
                error("El código debe ser un número entero mayor que cero.");

            } else if (
                    gestor.buscarMultaSecuencial(codigo) == null) {

                error("La multa no está registrada.");

            } else if (gestor.anularMulta(codigo)) {
                info("Multa anulada correctamente.");

            } else {
                error("No se pudo anular la multa.");
            }
        });

        bList.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.generarReporteMultas()
                )
        );

        multaPanel.setLayout(new BorderLayout());
        multaPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirFacturas() {

        JTextField tNum = new JTextField();
        JTextField tCedula = new JTextField();
        JTextField tCodPago = new JTextField();
        JTextField tCodMulta = new JTextField();

        JPanel form = formGrid(
                new String[]{
                        "Número factura:",
                        "Cédula condómino:",
                        "Código pago (opcional):",
                        "Código multa (opcional):"
                },
                new JComponent[]{
                        tNum,
                        tCedula,
                        tCodPago,
                        tCodMulta
                }
        );

        JButton bGen = new JButton("Generar");
        JButton bAnu = new JButton("Anular");
        JButton bList = new JButton("Listar");
        JButton bTotal = new JButton("Total facturado");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bGen);
        botones.add(bAnu);
        botones.add(bList);
        botones.add(bTotal);

        JTextArea salida = new JTextArea(14, 80);

        bGen.addActionListener(e -> {

            String numero = tNum.getText().trim();
            String cedula = tCedula.getText().trim();
            String codigoPago =
                    tCodPago.getText().trim();

            String codigoMulta =
                    tCodMulta.getText().trim();

            if (textoVacio(numero)) {
                error("Debe ingresar el número de factura.");

            } else if (!enteroPositivo(numero)) {
                error("El número de factura debe ser mayor que cero.");

            } else if (
                    gestor.buscarFacturaSecuencial(numero) != null) {

                error("Ya existe una factura con ese número.");

            } else if (textoVacio(cedula)) {
                error("Debe ingresar la cédula del condómino.");

            } else if (!cedulaValida(cedula)) {
                error("La cédula debe contener exactamente 10 números.");

            } else if (
                    textoVacio(codigoPago)
                            && textoVacio(codigoMulta)) {

                error("Debe ingresar un código de pago o un código de multa.");

            } else if (
                    !textoVacio(codigoPago)
                            && !enteroPositivo(codigoPago)) {

                error("El código del pago debe ser un número mayor que cero.");

            } else if (
                    !textoVacio(codigoMulta)
                            && !enteroPositivo(codigoMulta)) {

                error("El código de la multa debe ser un número mayor que cero.");

            } else {

                Condomino condomino =
                        gestor.buscarCondominoSecuencial(cedula);

                if (condomino == null) {
                    error("El condómino no está registrado.");
                    return;
                }

                Pago pago = null;
                Multa multa = null;

                if (!textoVacio(codigoPago)) {
                    pago = gestor.buscarPagoSecuencial(
                            codigoPago
                    );

                    if (pago == null) {
                        error("El pago indicado no está registrado.");
                        return;
                    }

                    if (!pago.getCondomino().getCedula()
                            .equals(cedula)) {

                        error("El pago no pertenece al condómino seleccionado.");
                        return;
                    }
                }

                if (!textoVacio(codigoMulta)) {
                    multa = gestor.buscarMultaSecuencial(
                            codigoMulta
                    );

                    if (multa == null) {
                        error("La multa indicada no está registrada.");
                        return;
                    }

                    if (!multa.getCondomino().getCedula()
                            .equals(cedula)) {

                        error("La multa no pertenece al condómino seleccionado.");
                        return;
                    }
                }

                Factura factura =
                        gestor.generarFactura(
                                numero,
                                condomino,
                                pago,
                                multa
                        );

                if (factura == null) {
                    error("No se pudo generar la factura.");

                } else {
                    info("Factura generada correctamente.");
                    mostrar(salida, factura.toString());

                    tNum.setText("");
                    tCedula.setText("");
                    tCodPago.setText("");
                    tCodMulta.setText("");
                }
            }
        });

        bAnu.addActionListener(e -> {

            String numero = tNum.getText().trim();

            if (textoVacio(numero)) {
                error("Debe ingresar el número de factura.");

            } else if (!enteroPositivo(numero)) {
                error("El número de factura debe ser mayor que cero.");

            } else if (
                    gestor.buscarFacturaSecuencial(numero) == null) {

                error("La factura no está registrada.");

            } else if (gestor.anularFactura(numero)) {
                info("Factura anulada correctamente.");

            } else {
                error("No se pudo anular la factura.");
            }
        });

        bList.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.generarReporteFacturas()
                )
        );

        bTotal.addActionListener(e ->
                mostrar(
                        salida,
                        "Total general facturado: $"
                                + gestor
                                .calcularTotalFacturasRecursivo()
                )
        );

        facturaPanel.setLayout(new BorderLayout());
        facturaPanel.add(
                armarTab(form, botones, salida)
        );
    }

    private void construirAcciones() {

        JButton bVer =
                new JButton("Ver última acción");

        JButton bQuitar =
                new JButton("Quitar última acción");

        JButton bGeneral =
                new JButton("Reporte general");

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        botones.add(bVer);
        botones.add(bQuitar);
        botones.add(bGeneral);

        JTextArea salida = new JTextArea(20, 80);

        bVer.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.verUltimaAccion()
                )
        );

        bQuitar.addActionListener(e ->
                mostrar(
                        salida,
                        gestor.quitarUltimaAccion()
                )
        );

        bGeneral.addActionListener(e -> {

            StringBuilder sb = new StringBuilder();

            sb.append(
                    gestor.generarReporteCondominos()
            );

            sb.append("\n");

            sb.append("REPORTE DE ÁREAS COMUNES\n\n");

            if (gestor.getAreasComunes().isEmpty()) {
                sb.append(
                        "No existen áreas comunes registradas.\n"
                );
            } else {
                for (AreaComun area :
                        gestor.getAreasComunes()) {

                    sb.append(area).append("\n");
                }
            }

            sb.append("\n")
                    .append(
                            gestor.generarReporteReservas()
                    )
                    .append("\n")
                    .append(
                            gestor.generarReportePagos()
                    )
                    .append("\n")
                    .append(
                            gestor.generarReporteMultas()
                    )
                    .append("\n")
                    .append(
                            gestor.generarReporteFacturas()
                    );

            mostrar(salida, sb.toString());
        });

        accionesPanel.setLayout(new BorderLayout());
        accionesPanel.add(
                armarTab(
                        new JPanel(),
                        botones,
                        salida
                )
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame =
                    new JFrame("HABITAT.OS");

            frame.setContentPane(
                    new Ventana().getMainPanel()
            );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(960, 640);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

