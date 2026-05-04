module umg.proyectomultiterminal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.base;

    opens umg.proyectomultiterminal to javafx.fxml;
    exports umg.proyectomultiterminal;
}
