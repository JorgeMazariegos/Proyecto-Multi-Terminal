/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import java.time.LocalDate;
/**
 *
 * @author AMD 5600G
 */
public class Ticket {
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
}
