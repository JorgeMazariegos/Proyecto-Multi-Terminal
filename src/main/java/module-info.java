module umg.proyectomultiterminal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.base;
    requires jdk.jsobject;
    requires org.json;
    requires javafx.web;
    
    opens umg.proyectomultiterminal to javafx.fxml;
    exports umg.proyectomultiterminal;
}
