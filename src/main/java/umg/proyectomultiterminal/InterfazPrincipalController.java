package umg.proyectomultiterminal;

import estructuras.Cola;
import java.io.IOException;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modelos.Ticket;
import serverLogic.Server;

public class InterfazPrincipalController {
    private static InterfazPrincipalController instance;
    // ---------- Estructuras de datos para las colas ----------
    private final Cola colaGeneral = new Cola();
    
    PseudoClass on = PseudoClass.getPseudoClass("activo");
    Server server = new Server();
       
    @FXML
    private Pane contentArea;

    @FXML
    private Button detenerServidor;

    @FXML
    private Pane lastTicket;

    @FXML
    private Button logOff;

    @FXML
    private Label serverStatus;

    @FXML
    private Button startServer;

    @FXML
    private VBox summary;

    @FXML
    private Label title;
    
    @FXML
    private VBox colaEsp, colaNormal, colaVIP;
    
    @FXML 
    private Label statusRegistro, statusGeneral, statusVIP, statusEspecial;
    
    @FXML
    private Label lblColaEsp, lblColaGeneral, lblColaVip;
    
    @FXML
    public void initialize(){
        instance = this;
    }
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
    }
    
    @FXML
    private void startServer(){
        server.start();
        startServer.setDisable(true);
        detenerServidor.setDisable(false);
        serverStatus.pseudoClassStateChanged(on, true);
        serverStatus.setText("⬤ Encendido");
    }
    
    @FXML
    private void stopServer(){
        server.stop();
        startServer.setDisable(false);
        detenerServidor.setDisable(true);
        serverStatus.pseudoClassStateChanged(on, false);
        serverStatus.setText("⬤ Apagado");
    }
    
    @FXML
    private void generarTicketNormal(){
        Ticket ticket = new Ticket();
        ticket.setNumTicket(123);
        ticket.setDPI("8917263");
        ticket.setTipo("Normal");
        ticket.setEstado("Cola");
        agregarTicket(ticket);
    }
    
    public void agregarTicket(Ticket ticket){
        String tickerNum = String.valueOf(ticket.getNumTicket());
        try {
            
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/umg/proyectomultiterminal/ticketPanel.fxml"));
        Pane newPanel = loader.load();

        TicketPanelController controller = loader.getController();
        controller.setTiempoEspera("0:00 min");               
        controller.setNumTicker(tickerNum);        
        controller.setImage(ticket.getTipo());
        ticket.iniciarContador(controller);
        
        switch (ticket.getTipo()) {
            case "Normal":
                colaNormal.getChildren().add(newPanel);
                colaGeneral.enqueue(ticket);
                break;
            case "Prioridad":
                colaVIP.getChildren().add(newPanel);
                break;
            default:
                colaEsp.getChildren().add(newPanel);
                break;
        }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualizarContadores(ticket.getTipo());
    }
        
    public static InterfazPrincipalController getInstance() {
        return instance;
    }
    
    public void usuarioStatus(String tipo){
        String[] partes = tipo.split(" ");
        String first = partes[0];
        String second = partes[1];
        
        switch(first){
            case "Conectado":
                switch (second) {
                    case "Registro":
                        statusRegistro.setText("⬤ Conectado");
                        statusRegistro.setTextFill(Color.web("#52b047"));
                        break;
                    case "General":
                        statusGeneral.setText("⬤ Conectado");
                        statusGeneral.setTextFill(Color.web("#52b047"));
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Conectado");
                        statusVIP.setTextFill(Color.web("#52b047"));
                        break;
                    case "Especial":
                        statusEspecial.setText("⬤ Conectado");
                        statusEspecial.setTextFill(Color.web("#52b047"));
                        break;
                }
                break;
            case "Desconectado":
                switch (second) {
                    case "Registro":
                        statusRegistro.setText("⬤ Desconectado");
                        statusRegistro.setTextFill(Color.RED);
                        break;
                    case "General":
                        statusGeneral.setText("⬤ Desconectado");
                        statusGeneral.setTextFill(Color.RED);
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Desconectado");
                        statusVIP.setTextFill(Color.RED);
                        break;
                    case "Especial":
                        statusEspecial.setText("⬤ Desconectado");
                        statusEspecial.setTextFill(Color.RED);
                        break;
                }
                break;    
            case "Procesando":
                switch (second) {
                    case "General":
                        statusGeneral.setText("⬤ Procesando");
                        statusGeneral.setTextFill(Color.YELLOW);
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Procesando");
                        statusVIP.setTextFill(Color.YELLOW);
                        break;
                    case "Especial":
                        statusEspecial.setText("⬤ Procesando");
                        statusEspecial.setTextFill(Color.YELLOW);
                        break;
                }
                break;
        }
    }

    private void actualizarContadores(String tipo) {
        switch (tipo) {
            case "Normal":
                lblColaGeneral.setText(String.valueOf(colaGeneral.size()));
                break;
            case "Prioridad":
                //colaVIP.getChildren().add(newPanel);
                break;
            default:
                //colaEsp.getChildren().add(newPanel);
                break;
        }
    }
    
    public Ticket enviarTicket(String cola){
        switch(cola){
            case "General":
                Ticket ticket = colaGeneral.dequeue();
                ticket.detenerContador();
                Platform.runLater(() -> {
                    actualizarInterfazGeneral();
                });
                return ticket;
        }
        return null;
    }

    private void actualizarInterfazGeneral() {
        colaNormal.getChildren().remove(0);
        actualizarContadores("Normal");
        usuarioStatus("Procesando General");
    }
}