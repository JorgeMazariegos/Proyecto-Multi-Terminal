package umg.proyectomultiterminal;

import java.io.IOException;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import serverLogic.Server;

public class InterfazPrincipalController {

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
    public void initialize(){
        
    }
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("menuPrincipal");
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
}