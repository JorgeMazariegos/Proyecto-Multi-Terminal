package modelos;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author AMD 5600G
 */
public class Ticket implements Serializable {
    int numTicket;
    
    // User/request data
    private int DPI;
    private String nombre;
    private String apellido;
    
    // viaje data
    private String origen;
    private String destino;
    private String tipo;  // Normal, Prioridad, Entrega
    private BigDecimal precio;
    
    // Simulation data
    private int tiempoEnCola;
    private int duracionAtencion;
    private int duracionTotal;
    
    //Metadata        
    private LocalDateTime fechaCreacion; //fecha en la que se pidio el viaje
    private String usuarioQueAtendio;
    private String estado; //Solicitado, Cola, Finalizado

    public int getNumTicket() {
        return numTicket;
    }

    public void setNumTicket(int numTicket) {
        this.numTicket = numTicket;
    }

    public int getDPI() {
        return DPI;
    }

    public void setDPI(int DPI) {
        this.DPI = DPI;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getTiempoEnCola() {
        return tiempoEnCola;
    }

    public void setTiempoEnCola(int tiempoEnCola) {
        this.tiempoEnCola = tiempoEnCola;
    }

    public int getDuracionAtencion() {
        return duracionAtencion;
    }

    public void setDuracionAtencion(int duracionAtencion) {
        this.duracionAtencion = duracionAtencion;
    }

    public int getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(int duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioQueAtendio() {
        return usuarioQueAtendio;
    }

    public void setUsuarioQueAtendio(String usuarioQueAtendio) {
        this.usuarioQueAtendio = usuarioQueAtendio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
