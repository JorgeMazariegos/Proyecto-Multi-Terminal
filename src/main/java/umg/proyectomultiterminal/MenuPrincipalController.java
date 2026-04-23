package umg.proyectomultiterminal;

import java.io.IOException;
import javafx.fxml.FXML;

public class MenuPrincipalController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
