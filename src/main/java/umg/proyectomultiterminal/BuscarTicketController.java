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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import modelos.Ticket;
/**
 * FXML Controller class
 *
 * @author AMD 5600G
 */
public class BuscarTicketController implements Initializable {

    TablaHash tabla = new TablaHash();
    
    @FXML private ListView<String> listaViajes;
    @FXML private TextField buscarDPI;
    @FXML
    private Label txtTiempoBusqueda , txtDPI, txtNombre, txtApellido, txtOrigen, txtDestino, txtGeneral, txtPrecio, txtTiempoCola, txtTiempoAtencion, txtTiempoTotal, txtFechaAtencion, txtUsuarioAtendio;    
    
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

    @FXML
    private void buscarDPI(){
        int dpi = Integer.parseInt(buscarDPI.getText());
        long inicio = System.nanoTime();
        Ticket ticket = tabla.get(dpi);
        long fin = System.nanoTime();
        if(ticket == null){
            if (ticket == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("No se encontró un ticket con ese DPI.");
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                    getClass().getResource("/styles/alert.css").toExternalForm()
                );
                dialogPane.getStyleClass().add("custom-alert");
                alert.showAndWait();
                return;
            }
        }
        
        long total = fin - inicio;
        double totalMs = total / 1_000_000.0;
        txtTiempoBusqueda.setText(totalMs + "ms");
        mostrarDatosTicket(ticket);
    }
    
    private void cargarDPIALista(Cola cola){
        while(!cola.isEmpty()){
            Ticket ticket = cola.dequeue();
            int dpi = ticket.getDPI();
            listaViajes.getItems().add(String.valueOf(dpi));
            tabla.add(dpi, ticket);
        }
    }
    
    private void mostrarDatosTicket(Ticket ticket){
        DateTimeFormatter formato =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        txtDPI.setText(
            "[" + ticket.getDPI() + "]"
        );

        txtNombre.setText(
            "[" + ticket.getNombre() + "]"
        );

        txtApellido.setText(
            "[" + ticket.getApellido() + "]"
        );

        txtOrigen.setText(
            "[" + ticket.getOrigen() + "]"
        );

        txtDestino.setText(
            "[" + ticket.getDestino() + "]"
        );

        txtGeneral.setText(
            "[" + ticket.getTipo() + "]"
        );

        txtPrecio.setText(
            "[Q " + ticket.getPrecio() + "]"
        );

        txtTiempoCola.setText(
            "[" + ticket.getTiempoEnCola() + "s]"
        );

        txtTiempoAtencion.setText(
            "[" + ticket.getDuracionAtencion() + "s]"
        );

        txtTiempoTotal.setText(
            "[" + ticket.getDuracionTotal() + "s]"
        );

        txtFechaAtencion.setText(
            "[" + ticket.getFechaCreacion().format(formato) + "]"
        );

        txtUsuarioAtendio.setText(
            "[" + ticket.getUsuarioQueAtendio() + "]"
        );
    }
}

