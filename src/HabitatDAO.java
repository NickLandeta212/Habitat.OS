import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;

public class HabitatDAO {

    public boolean insertarCondomino(Condomino c) {
        String sql = "INSERT INTO condomino VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(c.getId()));
            ps.setString(2, c.getCedula());
            ps.setString(3, c.getNombre());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getNumeroDepartamento());
            ps.setString(7, c.getEstadoCondomino().name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar condómino: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarCondomino(String cedula, String telefono, String correo, EstadoCondomino estado) {
        String sql = "UPDATE condomino SET telefono=?, correo=?, estado=? WHERE cedula=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, telefono);
            ps.setString(2, correo);
            ps.setString(3, estado.name());
            ps.setString(4, cedula);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar condómino: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Condomino> listarCondominos() {
        ArrayList<Condomino> lista = new ArrayList<>();
        String sql = "SELECT * FROM condomino ORDER BY cedula";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Condomino(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("numero_departamento"),
                        EstadoCondomino.valueOf(rs.getString("estado"))
                ));
            }

        } catch (Exception e) {
            System.out.println("Error al listar condóminos: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertarArea(AreaComun a) {
        String sql = "INSERT INTO area_comun VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(a.getId()));
            ps.setString(2, a.getNombre());
            ps.setInt(3, a.getCapacidad());
            ps.setString(4, a.getDescripcion());
            ps.setBoolean(5, a.estaDisponible());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar área común: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<AreaComun> listarAreas() {
        ArrayList<AreaComun> lista = new ArrayList<>();
        String sql = "SELECT * FROM area_comun ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new AreaComun(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getInt("capacidad"),
                        rs.getString("descripcion"),
                        rs.getBoolean("disponible")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error al listar áreas: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertarReserva(Reserva r) {
        String sql = "INSERT INTO reserva VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(r.getCodigo()));
            ps.setDate(2, Date.valueOf(r.getFecha()));
            ps.setTime(3, Time.valueOf(r.getHoraInicio()));
            ps.setTime(4, Time.valueOf(r.getHoraFin()));
            ps.setInt(5, Integer.parseInt(r.getAreaComun().getId()));
            ps.setString(6, r.getCondomino().getCedula());
            ps.setString(7, r.getEstadoReserva().name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadoReserva(String codigo, EstadoReserva estado) {
        return actualizarEstado("reserva", "codigo", codigo, estado.name());
    }

    public ArrayList<Reserva> listarReservas(ArrayList<AreaComun> areas, ArrayList<Condomino> condominos) {
        ArrayList<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reserva ORDER BY fecha, hora_inicio";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AreaComun area = buscarAreaLista(areas, String.valueOf(rs.getInt("id_area")));
                Condomino cond = buscarCondominoLista(condominos, rs.getString("cedula_condomino"));

                if (area != null && cond != null) {
                    Reserva r = new Reserva(
                            String.valueOf(rs.getInt("codigo")),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora_inicio").toLocalTime(),
                            rs.getTime("hora_fin").toLocalTime(),
                            area,
                            cond
                    );

                    EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));

                    if (estado == EstadoReserva.CONFIRMADA) {
                        r.confirmar();
                    }

                    if (estado == EstadoReserva.CANCELADA) {
                        r.cancelar();
                    }

                    lista.add(r);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertarPago(Pago p) {
        String sql = "INSERT INTO pago VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(p.getCodigo()));
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setDouble(3, p.getMonto());
            ps.setString(4, p.getConcepto());
            ps.setString(5, p.getNroComprobante());
            ps.setString(6, p.getCondomino().getCedula());
            ps.setString(7, p.getEstadoPago().name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar pago: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadoPago(String codigo, EstadoPago estado) {
        return actualizarEstado("pago", "codigo", codigo, estado.name());
    }

    public ArrayList<Pago> listarPagos(ArrayList<Condomino> condominos) {
        ArrayList<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pago ORDER BY fecha";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Condomino cond = buscarCondominoLista(condominos, rs.getString("cedula_condomino"));

                if (cond != null) {
                    Pago p = new Pago(
                            String.valueOf(rs.getInt("codigo")),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getDouble("monto"),
                            rs.getString("concepto"),
                            rs.getString("nro_comprobante"),
                            cond
                    );

                    p.actualizarEstado(EstadoPago.valueOf(rs.getString("estado")));
                    lista.add(p);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertarMulta(Multa m) {
        String sql = "INSERT INTO multa VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(m.getCodigo()));
            ps.setDate(2, Date.valueOf(m.getFecha()));
            ps.setString(3, m.getMotivo());
            ps.setDouble(4, m.getValor());
            ps.setString(5, m.getTipoInfraccion());
            ps.setString(6, m.getCondomino().getCedula());
            ps.setString(7, m.getEstadoMulta().name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar multa: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadoMulta(String codigo, EstadoMulta estado) {
        return actualizarEstado("multa", "codigo", codigo, estado.name());
    }

    public ArrayList<Multa> listarMultas(ArrayList<Condomino> condominos) {
        ArrayList<Multa> lista = new ArrayList<>();
        String sql = "SELECT * FROM multa ORDER BY fecha";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Condomino cond = buscarCondominoLista(condominos, rs.getString("cedula_condomino"));

                if (cond != null) {
                    Multa m = new Multa(
                            String.valueOf(rs.getInt("codigo")),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("motivo"),
                            rs.getDouble("valor"),
                            rs.getString("tipo_infraccion"),
                            cond
                    );

                    EstadoMulta estado = EstadoMulta.valueOf(rs.getString("estado"));

                    if (estado == EstadoMulta.PAGADA) {
                        m.pagarMulta();
                    }

                    if (estado == EstadoMulta.ANULADA) {
                        m.anular();
                    }

                    lista.add(m);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar multas: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertarFactura(Factura f) {
        String sql = "INSERT INTO factura VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(f.getNumero()));
            ps.setDate(2, Date.valueOf(f.getFechaEmision()));
            ps.setDouble(3, f.getTotalMonto());
            ps.setString(4, f.getCondomino().getCedula());

            if (f.getPago() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, Integer.parseInt(f.getPago().getCodigo()));
            }

            if (f.getMulta() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, Integer.parseInt(f.getMulta().getCodigo()));
            }

            ps.setString(7, f.getEstadoFactura().name());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar factura: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstadoFactura(String numero, EstadoFactura estado) {
        return actualizarEstado("factura", "numero", numero, estado.name());
    }

    public ArrayList<Factura> listarFacturas(
            ArrayList<Condomino> condominos,
            ArrayList<Pago> pagos,
            ArrayList<Multa> multas
    ) {
        ArrayList<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura ORDER BY numero";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Condomino cond = buscarCondominoLista(condominos, rs.getString("cedula_condomino"));

                Pago pago = null;
                Multa multa = null;

                Object codPago = rs.getObject("codigo_pago");
                Object codMulta = rs.getObject("codigo_multa");

                if (codPago != null) {
                    pago = buscarPagoLista(pagos, String.valueOf(rs.getInt("codigo_pago")));
                }

                if (codMulta != null) {
                    multa = buscarMultaLista(multas, String.valueOf(rs.getInt("codigo_multa")));
                }

                if (cond != null) {
                    Factura f = new Factura(
                            String.valueOf(rs.getInt("numero")),
                            rs.getDate("fecha_emision").toLocalDate(),
                            cond,
                            pago,
                            multa
                    );

                    if (EstadoFactura.valueOf(rs.getString("estado")) == EstadoFactura.ANULADA) {
                        f.anular();
                    }

                    lista.add(f);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar facturas: " + e.getMessage());
        }

        return lista;
    }

    private boolean actualizarEstado(String tabla, String campoCodigo, String codigo, String estado) {
        String sql = "UPDATE " + tabla + " SET estado=? WHERE " + campoCodigo + "=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, Integer.parseInt(codigo));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    private Condomino buscarCondominoLista(ArrayList<Condomino> lista, String cedula) {
        for (Condomino c : lista) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }

        return null;
    }

    private AreaComun buscarAreaLista(ArrayList<AreaComun> lista, String id) {
        for (AreaComun a : lista) {
            if (a.getId().equals(id)) {
                return a;
            }
        }

        return null;
    }

    private Pago buscarPagoLista(ArrayList<Pago> lista, String codigo) {
        for (Pago p : lista) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }

        return null;
    }

    private Multa buscarMultaLista(ArrayList<Multa> lista, String codigo) {
        for (Multa m : lista) {
            if (m.getCodigo().equals(codigo)) {
                return m;
            }
        }

        return null;
    }
}