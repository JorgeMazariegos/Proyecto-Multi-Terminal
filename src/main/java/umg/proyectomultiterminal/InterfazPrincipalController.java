package umg.proyectomultiterminal;

import estructuras.Cola;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    
    private Map<Ticket, Thread> contadores = new HashMap<>();
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
        ticket.setDPI(3453456);
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
        iniciarContador(controller, ticket);
        
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
            case "Disponible":
                switch (second) {
                    case "Registro":
                        statusRegistro.setText("⬤ Disponible");
                        statusRegistro.setTextFill(Color.web("#52b047"));
                        break;
                    case "General":
                        statusGeneral.setText("⬤ Disponible");
                        statusGeneral.setTextFill(Color.web("#52b047"));
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Disponible");
                        statusVIP.setTextFill(Color.web("#52b047"));
                        break;
                    case "Especial":
                        statusEspecial.setText("⬤ Disponible");
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
                if(colaGeneral.isEmpty()){
                    return null;
                }
                Ticket ticket = colaGeneral.dequeue();
                detenerContador(ticket);
                Platform.runLater(() -> {
                    System.out.println(colaGeneral.size());
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
    
    public void iniciarContador(TicketPanelController controller , Ticket ticket){
        Thread contador = new Thread(() -> {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    Thread.sleep(1000);                   
                    ticket.setTiempoEnCola(ticket.getTiempoEnCola() + 1);
                    Platform.runLater(() -> {
                    controller.setTiempoEspera(intAStringSegundos(ticket.getTiempoEnCola()) + " min");
                    });     
                }
                        
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        contador.setDaemon(true);
        contadores.put(ticket, contador);
        contador.start();
    }
       
    public void detenerContador(Ticket ticket) {
        Thread contador = contadores.get(ticket);
        if (contador != null) {
            contador.interrupt();
            contadores.remove(ticket);
        }
    }
    
    private String intAStringSegundos(int tiempo){
        int minutos = tiempo / 60;
        int segundos = tiempo % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
}