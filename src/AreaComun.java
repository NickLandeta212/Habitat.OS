public class AreaComun {

    private String id;
    private String nombre;
    private int capacidad;
    private String descripcion;
    private boolean disponible;

    public AreaComun(String id, String nombre, int capacidad, String descripcion, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nÁrea común: " + nombre +
                "\nCapacidad: " + capacidad +
                "\nDescripción: " + descripcion +
                "\nDisponible: " + disponible +
                "\n-----------------------------";
    }
}