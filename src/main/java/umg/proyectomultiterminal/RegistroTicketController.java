package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelos.Mensaje;
import modelos.Ticket;

public class RegistroTicketController {

    PseudoClass on = PseudoClass.getPseudoClass("activo");
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado; 
    private static final Random random = new Random();
    private static Properties config = new Properties();
    String ip;
    int port;
    
    @FXML
    private ComboBox<String> cbxFiltro;

    @FXML
    private ComboBox<String> cbxTipo;

    @FXML
    private Button generateTicket;

    @FXML
    private Label serverStatus;

    @FXML
    private Button conectToServer;

    @FXML
    private Button desconectar;
    
    @FXML
    private Label title;

    @FXML
    private TextField txtDPI;

    
    
    @FXML
    private void initialize(){
        cbxTipo.getItems().addAll("Normal", "Prioridad", "Entrega");
        cbxFiltro.getItems().addAll("Todos","Normal", "Prioridad", "Entrega");
    }
    
    @FXML
    private void generarTicket(){
        if(camposVacios()){
            return;
        }
        Ticket ticket = new Ticket();
        ticket.setNumTicket(generarNumTicket());
        ticket.setDPI(txtDPI.getText());
        ticket.setTipo(cbxTipo.getValue());
        sendTicket(ticket);
    }
    
    @FXML
    private void connect(){
        try{
            loadProperties();
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Mensaje mensaje = new Mensaje("Conectado Registro");
            mensaje.setStatus(true);
            conectado = true;
            sendMensaje(mensaje);
            conectToServer.setDisable(true);
            desconectar.setDisable(false);
            serverStatus.pseudoClassStateChanged(on, true);
            serverStatus.setText("⬤ Conectado");
        }catch (IOException ex) {
            System.getLogger(RegistroTicketController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @FXML
    private void disconnect() {
        Mensaje mensaje = new Mensaje("Desconectado Registro");
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
            System.getLogger(RegistroTicketController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
    
    //FUNCIONES DE UTILIDAD
    private static int generarNumTicket() {
        return random.nextInt(10000);
    }
    
    private boolean camposVacios(){
        if (txtDPI.getText().equals("")){
            return true;
        }
        if(cbxTipo.getValue().equals("Elegir prioridad")){
            return true;
        }
        return false;
    }
    
    private void sendTicket(Ticket ticket){
        if(!conectado){
            return;
        }
        new Thread(() ->{
            try {
                out.writeObject(ticket);
                out.flush();
            } catch (IOException ex) {
                System.getLogger(RegistroTicketController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }).start();                            
    }
    
    private void sendMensaje(Mensaje mensaje){
        if(!conectado){
            return;
        }
        try {
            out.writeObject(mensaje);
            out.flush();
        } catch (IOException ex) {
            System.getLogger(RegistroTicketController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
}
