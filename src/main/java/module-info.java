module umg.proyectomultiterminal {
    requires javafx.controls;
    requires javafx.fxml;

    opens umg.proyectomultiterminal to javafx.fxml;
    exports umg.proyectomultiterminal;
}
