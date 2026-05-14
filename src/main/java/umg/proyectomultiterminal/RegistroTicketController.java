package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import modelos.Mensaje;
import modelos.Ticket;

public class RegistroTicketController {
    PseudoClass on = PseudoClass.getPseudoClass("activo");
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado; 
    private static final Random random = new Random();
    private static final Properties config = new Properties();
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
    private ComboBox<String> cbxTipo, cbxOrigen, cbxDestino;

    @FXML
    private Button desconectar , conectToServer;
    
    @FXML
    private Label serverStatus,entregasStatus,registroStatus,vipStatus;

    @FXML
    private TextField txtApellido, txtDPI, txtNombre, txtPrecio, sendMessage;   
    
    @FXML private TextFlow chatArea;
    @FXML private ScrollPane chatScroll;
    
    @FXML
    private void initialize(){
        cbxTipo.getItems().addAll("Viaje", "Entrega");
        cbxOrigen.getItems().addAll(zonas);
        cbxDestino.getItems().addAll(zonas);
        setupDoubleFormatter();
    }
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
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
        ticket.setFechaCreacion(LocalDateTime.now());
        
        sendTicket(ticket);
    }
    
    @FXML
    private void connect(){
        try{
            loadProperties();
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            new Thread(this::listenServer).start();
            Mensaje mensaje = new Mensaje("Disponible Registro");
            mensaje.setStatus(true);          
            sendMensaje(mensaje);
            conectToServer.setDisable(true);
            desconectar.setDisable(false);
            registroStatus.pseudoClassStateChanged(on, true);
            registroStatus.setText("⬤ Disponible");
        }catch (IOException ex) {
            System.getLogger(RegistroTicketController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @FXML
    private void disconnect() {
        Mensaje mensaje = new Mensaje("Desconectado Registro");
        mensaje.setStatus(true);
        sendMensaje(mensaje);
        conectado = false;
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
        serverOff();
    }
    
    @FXML
    private void enviarMensaje(KeyEvent event) {
        Mensaje mensaje = new Mensaje(sendMessage.getText());        
        mensaje.setCliente("Registro");
        if (event.getCode() == KeyCode.ENTER) {
            sendMensaje(mensaje);
            sendMessage.setText("");
        }
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
    
    private void listenServer() {
        try {
            while (conectado) {

                Object obj = in.readObject();

                if(obj instanceof Mensaje) {
                    Mensaje mensaje = (Mensaje) obj;

                    Platform.runLater(() -> {
                        procesarMensaje(mensaje);
                    });
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Servidor desconectado");
            conectado = false;
            Platform.runLater(() -> {
                conectToServer.setDisable(false);
                desconectar.setDisable(true);
                serverOff();
            });
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
    
    private void serverOff(){
        serverStatus.pseudoClassStateChanged(on, false);
        serverStatus.setText("⬤ Desconectado");
        vipStatus.pseudoClassStateChanged(on, false);
        vipStatus.setText("⬤ Desconectado");
        entregasStatus.pseudoClassStateChanged(on, false);
        entregasStatus.setText("⬤ Desconectado");
        registroStatus.pseudoClassStateChanged(on, false);
        registroStatus.setText("⬤ Desconectado");
    }

    private Label getLabel(String mensaje) {
        Label label = serverStatus;
        switch(mensaje){
            case "General":
                label = serverStatus;
                break;
            case "VIP":
                label = vipStatus;
                break;
            case "Entrega":
                label = entregasStatus;
                break;
            case "Registro":
                label = registroStatus;
                break;
        }
        return label;
    }

    private void procesarMensaje(Mensaje mensaje) {
        if(mensaje.getTipo()!=null){
            switch(mensaje.getTipo()){
                case "CONECTADO":
                    clienteConectado(mensaje);
                    break;
                case "DESCONECTADO":
                    clienteDesconectado(mensaje);
                    break;
            }
        }else{
            agregarMensaje(mensaje.getCliente(), mensaje.getMensaje());
        }
    }

    private void clienteConectado(Mensaje mensaje) {
        for(String cliente : mensaje.getClientes()){
            Label label = getLabel(cliente);
            label.pseudoClassStateChanged(on, true);
            label.setText("⬤ Disponible");
        } 
    }
    
    private void clienteDesconectado(Mensaje mensaje) {
        Label label = getLabel(mensaje.getMensaje());
        label.pseudoClassStateChanged(on, false);
        label.setText("⬤ Desconectado");    
    }
    
        private void agregarMensaje(String usuario, String mensaje) {
        String color = obtenerColorUsuario(usuario);
        String hora = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        Text timestamp = new Text("[" + hora + "] ");
        timestamp.setFill(Color.GRAY);
        Text username = new Text(usuario + ": ");
        username.setFill(Color.web(color));
        username.setStyle("-fx-font-weight: bold;");
        Text contenido = new Text(mensaje + "\n");
        contenido.setFill(Color.WHITE);
        chatArea.getChildren().addAll(
                timestamp,
                username,
                contenido
        );
        Platform.runLater(() -> {
            chatScroll.setVvalue(1.0);
        });
    }
    
    private String obtenerColorUsuario(String usuario){
        switch(usuario){
            case "Registro":
                return "#9ae96b";
            case "General":
                return "#658dd5";
            case "VIP":
                return "#FFD700";
            case "Entrega":
                return "#a37ad5";
            case "Server":
                return "#4ec9b0";
            default:
                return "#FFFFFF";
        }
    }
}
