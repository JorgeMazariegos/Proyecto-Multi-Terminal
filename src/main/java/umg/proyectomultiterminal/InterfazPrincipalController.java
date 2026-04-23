package umg.proyectomultiterminal;

import java.io.IOException;
import javafx.fxml.FXML;

public class InterfazPrincipalController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}