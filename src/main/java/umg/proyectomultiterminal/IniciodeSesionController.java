/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import java.io.IOException; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author josef
 */
public class IniciodeSesionController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void loginServidor() throws IOException {
        App.setRoot("interfazPrincipal");
    }
 
    @FXML
    private void loginRegistro() throws IOException {
        App.setRoot("RegistroTicket");
    }
    
    @FXML
    private void loginPC1() throws IOException {
        App.setRoot("interfazGeneral");
    }
    
    @FXML
    private void loginPC2() throws IOException {
        App.setRoot("menuPrincipal");
    }
    
    @FXML
    private void loginPC3() throws IOException {
        App.setRoot("menuPrincipal");
    }
    
}
