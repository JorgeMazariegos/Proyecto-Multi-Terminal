package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
    private String ip;
    private int port;
    String[] zonas ={
        "Zona 1",
        "Zona 2",
        "Zona 3",
        "Zona 4",
        "Zona 5",
        "Zona 6",
        "Zona 7",
        "Zona 8",
        "Zona 9",
        "Zona 10",
        "Zona 11",
        "Zona 12",
        "Zona 13",
        "Zona 14",
        "Zona 15",
        "Zona 16",
        "Zona 17",
        "Zona 18",
        "Zona 21",
        "Zona 24",
        "Zona 25"
    };
    
    
    @FXML
    private ComboBox<String> cbxTipo, cbxOrigen, cbxDestino, cbxFiltro;

    @FXML
    private Button generateTicket, desconectar , conectToServer;
    
    @FXML
    private Label title, serverStatus;

    @FXML
    private TextField txtApellido, txtDPI, txtNombre, txtPrecio;   
    
    @FXML
    private void initialize(){
        cbxTipo.getItems().addAll("Viaje", "Entrega");
        cbxFiltro.getItems().addAll("Todos","Normal", "Prioridad", "Entrega");
        cbxOrigen.getItems().addAll(zonas);
        cbxDestino.getItems().addAll(zonas);
        setupDoubleFormatter();
    }
    
    @FXML
    private void generarTicket(){
        if(camposVacios()){
            return;
        }
        Ticket ticket = new Ticket();
        BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(txtPrecio.getText()));
        if(cbxTipo.getValue().equals("Viaje")){
            if(precio.compareTo(BigDecimal.valueOf(50.00)) >= 0){
                ticket.setTipo("Prioridad");
            }else{
                ticket.setTipo("Normal");
            }
        }else{
            ticket.setTipo("Entrega");
        }
        
        ticket.setNumTicket(generarNumTicket());
        ticket.setDPI(Integer.parseInt(txtDPI.getText()));
        ticket.setNombre(txtNombre.getText());
        ticket.setApellido(txtApellido.getText());
        
        ticket.setPrecio(precio);
        ticket.setOrigen(cbxOrigen.getValue());
        ticket.setDestino(cbxDestino.getValue());
        ticket.setEstado("Solicitado");
        
        sendTicket(ticket);
    }
    
    @FXML
    private void connect(){
        try{
            socket = new Socket("100.105.253.48", 1234);
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
        if(txtDPI.getText().isBlank()){
        return true;
    }

    if(txtNombre.getText().isBlank()){
        return true;
    }

    if(txtApellido.getText().isBlank()){
        return true;
    }

    if(txtPrecio.getText().isBlank()){
        return true;
    }

    if(cbxTipo.getValue() == null){
        return true;
    }

    if(cbxOrigen.getValue() == null){
        return true;
    }

    return cbxDestino.getValue() == null;
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
                System.out.println("Error : ");
                ex.printStackTrace();
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
            System.out.println("Error : " + ex.getMessage());
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
    
    private void setupDoubleFormatter() {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            // Allow: digits, optional decimal point, and up to two digits after decimal
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                try {
                    if (!newText.equals(".")) {
                        double value = Double.parseDouble(newText);
                        if (value >= 0) {
                            return change;
                        } else {
                            return null;
                        }
                    }
                    return change; 
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        txtPrecio.setTextFormatter(formatter);
    }
}
