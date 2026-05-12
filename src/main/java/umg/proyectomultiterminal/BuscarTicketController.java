/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import estructuras.ArchivoTickets;
import estructuras.Cola;
import estructuras.TablaHash;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import modelos.Ticket;
/**
 * FXML Controller class
 *
 * @author AMD 5600G
 */
public class BuscarTicketController implements Initializable {

    TablaHash tabla = new TablaHash();
    
    @FXML private ListView<String> listaViajes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
    }
    
   @FXML
    private void cargarArchivo() {
        Cola colaCargada = ArchivoTickets.cargar();
        cargarDPIALista(colaCargada);
        if (colaCargada != null) {
            System.out.println("Archivo cargado correctamente");
        } else {
            System.out.println("No se pudo cargar el archivo");
        }
    }

    private void cargarDPIALista(Cola cola){
        while(!cola.isEmpty()){
            Ticket ticket = cola.dequeue();
            int dpi = ticket.getDPI();
            listaViajes.getItems().add(String.valueOf(dpi));
            tabla.add(dpi, ticket);
        }
    }
    
}

