/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import estructuras.ArchivoTickets;
import estructuras.Cola;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import modelos.Ticket;

/**
 * FXML Controller class
 *
 * @author AMD 5600G
 */
public class BuscarTicketController implements Initializable {

    @FXML private ListView<String> listaViajes;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
    }
    
   /*@FXML
    private void cargarArchivo() {
    Cola colaCargada = ArchivoTickets.cargar();
    if (colaCargada != null) {
        System.out.println("Archivo cargado correctamente");
    } else {
        System.out.println("No se pudo cargar el archivo");
    }
}*/
   @FXML
private void cargarArchivo() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar archivo DAT");
    fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos DAT", "*.dat")
    );
    File archivo = fileChooser.showOpenDialog(null);
    if (archivo != null) {
        Cola colaCargada = ArchivoTickets.cargar(archivo);
        if (colaCargada != null) {
            // LIMPIAR LISTVIEW
            listaViajes.getItems().clear();
            // RECORRER COLA
            while (!colaCargada.isEmpty()) {
                Ticket ticket = colaCargada.dequeue();
                String texto =
                        ticket.getNombre()
                        + " | "
                        + ticket.getOrigen()
                        + " → "
                        + ticket.getDestino();
                listaViajes.getItems().add(texto);
            }
            System.out.println("Archivo cargado correctamente");
        } else {
            System.out.println("No se pudo cargar el archivo");
        }
    } else {
        System.out.println("No se seleccionó ningún archivo");
    }
}
    }

