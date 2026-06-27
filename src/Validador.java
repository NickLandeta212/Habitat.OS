import java.time.LocalDate;
import java.time.LocalTime;

public class Validador {

    public static boolean texto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    public static boolean enteroPositivo(String valor) {
        try {
            return texto(valor) && Integer.parseInt(valor.trim()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean decimalPositivo(double valor) {
        return valor > 0 && !Double.isNaN(valor) && !Double.isInfinite(valor);
    }

    public static boolean nombre(String nombre) {
        return texto(nombre) && nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
    }

    public static boolean telefono(String telefono) {
        return texto(telefono) && telefono.trim().matches("\\d{7,10}");
    }

    public static boolean correo(String correo) {
        return texto(correo) && correo.trim().matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean cedula(String cedula) {
        if (!texto(cedula) || !cedula.trim().matches("\\d{10}")) {
            return false;
        }

        String c = cedula.trim();
        int provincia = Integer.parseInt(c.substring(0, 2));
        int tercerDigito = Character.getNumericValue(c.charAt(2));

        if (provincia < 1 || provincia > 24 || tercerDigito >= 6) {
            return false;
        }

        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(c.charAt(i));

            if (i % 2 == 0) {
                digito = digito * 2;

                if (digito > 9) {
                    digito = digito - 9;
                }
            }

            suma = suma + digito;
        }

        int verificadorCalculado = (10 - (suma % 10)) % 10;
        int verificadorReal = Character.getNumericValue(c.charAt(9));

        return verificadorCalculado == verificadorReal;
    }

    public static boolean fechaNoFutura(LocalDate fecha) {
        return fecha != null && !fecha.isAfter(LocalDate.now());
    }

    public static boolean fechaReserva(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(LocalDate.now());
    }

    public static boolean horas(LocalTime inicio, LocalTime fin) {
        return inicio != null && fin != null && inicio.isBefore(fin);
    }
}