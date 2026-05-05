/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

import java.io.Serializable;

/**
 *
 * @author AMD 5600G
 */
public class Mensaje implements Serializable {
    boolean status; //Nos sirve para saber si es un mensaje sobre el status de la conexion
    String mensaje;

    public Mensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Mensaje() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }   
    
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
}
