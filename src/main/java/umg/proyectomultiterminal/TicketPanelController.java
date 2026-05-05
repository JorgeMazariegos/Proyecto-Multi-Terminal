package umg.proyectomultiterminal;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TicketPanelController {

    @FXML
    private Label espera;

    @FXML
    private ImageView imageView;

    @FXML
    private Label numTicket;
    
    public void setNumTicker(String ticket){
        numTicket.setText("Ticket #" + ticket);
    }
    
    public void setTiempoEspera(String tiempo){
        espera.setText("Espera: " + tiempo);
    }
    
    public void setImage(String tipo){
        String url;
        switch (tipo) {
            case "Prioridad":
                url = "star";
                break;
            case "Entrega":
                url = "rayo";
                break;
            default:
                url = "car";
                break;
        }
        
        Image image = new Image(getClass().getResource("/"+ url + ".png").toExternalForm());
        imageView.setImage(image);
    }
}

