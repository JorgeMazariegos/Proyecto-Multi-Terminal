/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import modelos.Mensaje;
import modelos.Ticket;

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
    private boolean disponible;
    private static Properties config = new Properties();
    String ip;
    int port;
    
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
            loadProperties();
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            disponible = true;
            new Thread(this::listenServer).start();
            Mensaje mensaje = new Mensaje("Conectado General");
            mensaje.setStatus(true);            
            sendMensaje(mensaje);
            new Thread(this::serverRequest).start();
            
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
        disponible = false;
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
    
    private void listenServer() {
        try {
            while (conectado) {

                Object obj = in.readObject();

                if(obj instanceof Mensaje) {
                    Mensaje mensaje = (Mensaje) obj;

                    Platform.runLater(() -> {
                        System.out.println("Mensaje del servidor: "
                                + mensaje.getMensaje());
                    });
                }

                if(obj instanceof Ticket) {
                    Ticket ticket = (Ticket) obj; 
                    procesarTicket(ticket);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Servidor desconectado");
            conectado = false;
            conectToServer.setDisable(false);
            desconectar.setDisable(true);
            serverStatus.pseudoClassStateChanged(on, false);
            serverStatus.setText("⬤ Desconectado");
        }
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
    
    private void loadProperties(){
        InputStream input;
        
        try{
            input = getClass().getResourceAsStream("/serverConfig/config.properties");
            config.load(input);
            input.close();
            this.port = Integer.parseInt(config.getProperty("port"));
            this.ip = config.getProperty("ip");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    private void serverRequest(){
        while(conectado){
            if(disponible){
                try {
                    Thread.sleep(1000);
                    Mensaje mensaje = new Mensaje("Request General");
                    sendMensaje(mensaje);
                    disponible = false;
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    private void procesarTicket(Ticket ticket) {
        System.out.println(ticket.getDPI());
    }
}
