import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva extends Registro {

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private AreaComun areaComun;
    private EstadoReserva estadoReserva;

    public Reserva(String codigo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, AreaComun areaComun, Condomino condomino) {

        super(codigo, fecha, EstadoReserva.PENDIENTE.name(), condomino);

        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.areaComun = areaComun;
        this.estadoReserva = EstadoReserva.PENDIENTE;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public AreaComun getAreaComun() {
        return areaComun;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void confirmar() {
        this.estadoReserva = EstadoReserva.CONFIRMADA;
        this.estado = EstadoReserva.CONFIRMADA.name();
    }

    public void cancelar() {
        this.estadoReserva = EstadoReserva.CANCELADA;
        this.estado = EstadoReserva.CANCELADA.name();
    }

    public void modificar(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, AreaComun areaComun) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.areaComun = areaComun;
    }

    public boolean esValida() {
        return fecha != null
                && horaInicio != null
                && horaFin != null
                && areaComun != null
                && condomino != null
                && horaInicio.isBefore(horaFin);
    }

    @Override
    public String toString() {
        return "Código reserva: " + codigo +
                "\nFecha: " + fecha +
                "\nHora inicio: " + horaInicio +
                "\nHora fin: " + horaFin +
                "\nÁrea común: " + areaComun.getNombre() +
                "\nCondómino: " + condomino.getNombre() +
                "\nEstado: " + estadoReserva +
                "\n-----------------------------";
    }
}