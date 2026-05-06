package umg.proyectomultiterminal;

import java.io.IOException;
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
    private VBox colaEsp;

    @FXML
    private VBox colaNormal;

    @FXML
    private VBox colaVIP;
    
    @FXML 
    private Label statusRegistro, statusGeneral, statusVIP, statusEspecial;
    
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
    
    public void agregarTicker(Ticket ticket){
        String tickerNum = String.valueOf(ticket.getNumTicket());
        try {
            
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/umg/proyectomultiterminal/ticketPanel.fxml"));
        Pane newPanel = loader.load();

        TicketPanelController controller = loader.getController();
        
        ticket.iniciarContador(controller);        
        controller.setNumTicker(tickerNum);
        controller.setTiempoEspera("0:00 min");
        controller.setImage(ticket.getTipo());
        
        switch (ticket.getTipo()) {
            case "Normal":
                colaNormal.getChildren().add(newPanel);
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
                        statusRegistro.setTextFill(Color.GREEN);
                        break;
                    case "General":
                        statusGeneral.setText("⬤ Conectado");
                        statusGeneral.setTextFill(Color.GREEN);
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Conectado");
                        statusVIP.setTextFill(Color.GREEN);
                        break;
                    case "Especial":
                        statusEspecial.setText("⬤ Conectado");
                        statusEspecial.setTextFill(Color.GREEN);
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
}