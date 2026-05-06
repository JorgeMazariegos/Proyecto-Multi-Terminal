/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;
import java.io.Serializable;
import java.time.LocalDate;
import umg.proyectomultiterminal.TicketPanelController;
/**
 *
 * @author AMD 5600G
 */
public class Ticket implements Serializable {
    int numTicket;
    String DPI;
    String tipo;
    LocalDate fecha_hora_atencion;    
    String nombre;
    String apellido;
    String motivoAtencion;
    int duracionAtencion;
    int duracionTotal;
    int tiempoEnCola;
    String usuarioQueAtendio;
    String estado;
    private volatile boolean activo;
    Thread contador;

    public void iniciarContador(TicketPanelController controller){
        activo = true;
        contador = new Thread(() -> {
            try{
                while(activo){
                    Thread.sleep(1000);
                    switch(estado){
                        case "Cola":
                            tiempoEnCola++;
                            controller.setTiempoEspera(intAStringSegundos(tiempoEnCola));
                            break;
                        case "Atencion":
                            duracionAtencion++;
                            controller.setTiempoEspera(intAStringSegundos(duracionAtencion));
                            break;
                    }        
                }
                        
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });        
        contador.start();
    }
    
    public void detenerContador() {
        activo = false;
        if (contador != null) {
            contador.interrupt();
        }
    }
    
    private String intAStringSegundos(int tiempo){
        int minutos = tiempo / 60;
        int segundos = tiempo % 60;

    return String.format("%d:%02d", minutos, segundos);
    }
    
    public int getNumTicket() {
        return numTicket;
    }

    public void setNumTicket(int numTicket) {
        this.numTicket = numTicket;
    }

    public String getDPI() {
        return DPI;
    }

    public void setDPI(String DPI) {
        this.DPI = DPI;
    }

    public LocalDate getFecha_hora_atencion() {
        return fecha_hora_atencion;
    }

    public void setFecha_hora_atencion(LocalDate fecha_hora_atencion) {
        this.fecha_hora_atencion = fecha_hora_atencion;
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

    public String getMotivoAtencion() {
        return motivoAtencion;
    }

    public void setMotivoAtencion(String motivoAtencion) {
        this.motivoAtencion = motivoAtencion;
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

    public int getTiempoEnCola() {
        return tiempoEnCola;
    }

    public void setTiempoEnCola(int tiempoEnCola) {
        this.tiempoEnCola = tiempoEnCola;
    }

    public String getUsuarioQueAtendio() {
        return usuarioQueAtendio;
    }

    public void setUsuarioQueAtendio(String usuarioQueAtendio) {
        this.usuarioQueAtendio = usuarioQueAtendio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
