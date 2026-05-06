/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import modelos.Mensaje;

/**
 * FXML Controller class
 *
 * @author josef
 */
public class InterfazGeneralController implements Initializable {
    PseudoClass on = PseudoClass.getPseudoClass("activo");
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado; 
    private static final Random random = new Random();
    
    @FXML
    private Label serverStatus;

    @FXML
    private Button conectToServer;

    @FXML
    private Button desconectar;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    
    @FXML
     private void connect(){
        try{
            socket = new Socket("10.63.229.45", 1234);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Mensaje mensaje = new Mensaje("Conectado General");
            mensaje.setStatus(true);
            conectado = true;
            sendMensaje(mensaje);
            conectToServer.setDisable(true);
            desconectar.setDisable(false);
            serverStatus.pseudoClassStateChanged(on, true);
            serverStatus.setText("⬤ Conectado");
        }catch (IOException ex) {
            System.getLogger(InterfazGeneralController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
     
     @FXML
    private void disconnect() {
        Mensaje mensaje = new Mensaje("Desconectado General");
        mensaje.setStatus(true);
        sendMensaje(mensaje);
        conectado = false;   // Prevent further sends immediately
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.getLogger(InterfazGeneralController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } finally {
            out = null;
            in = null;
            socket = null;
        }
        conectToServer.setDisable(false);
        desconectar.setDisable(true);
        serverStatus.pseudoClassStateChanged(on, false);
        serverStatus.setText("⬤ Desconectado");
    }
    
    private void sendMensaje(Mensaje mensaje){
        if(!conectado){
            return;
        }
        try {
            out.writeObject(mensaje);
            out.flush();
        } catch (IOException ex) {
            System.getLogger(InterfazGeneralController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
