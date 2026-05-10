/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import org.json.JSONArray;  // necesitas la librería JSON (ver paso 3)
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author josef
 */
public class InterfazPrioritariaController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private Label     lblEstadoBusqueda;
    @FXML private WebView webView;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         webView.setZoom(1.25);
    webView.setContextMenuEnabled(false);
 navegarADestino(14.6349, -90.5069, 13);
}
   
    





@FXML
private void buscarDireccion() {
    String lugar = txtBusqueda.getText().trim();

    if (lugar.isEmpty()) {
        lblEstadoBusqueda.setText("Escribe un lugar para buscar.");
        return;
    }
    lblEstadoBusqueda.setText("Buscando...");
    // Llamada en hilo separado para no congelar la UI
    Task<double[]> task = new Task<>() {
        @Override
        protected double[] call() throws Exception {
            return geocodificar(lugar);
        }
    };
    task.setOnSucceeded(e -> {
        double[] coords = task.getValue();
        if (coords != null) {
            Platform.runLater(() -> {
                lblEstadoBusqueda.setText("Mostrando: " + lugar);
                navegarADestino(coords[0], coords[1], 15);
            });
        } else {
            Platform.runLater(() ->
                lblEstadoBusqueda.setText("No se encontró el lugar.")
            );
        }
    });
    task.setOnFailed(e ->
        Platform.runLater(() ->
            lblEstadoBusqueda.setText("Error de conexión.")
        )
    );
    new Thread(task).start();
}

private double[] geocodificar(String lugar) throws Exception {
    String encoded = URLEncoder.encode(lugar, StandardCharsets.UTF_8);
    String url = "https://nominatim.openstreetmap.org/search"
               + "?q=" + encoded
               + "&format=json&limit=1";

    HttpURLConnection conn = (HttpURLConnection) new java.net.URL(url).openConnection();
    conn.setRequestMethod("GET");
    // Nominatim requiere un User-Agent
    conn.setRequestProperty("User-Agent", "WazeNavApp/1.0");
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(5000);

    BufferedReader br = new BufferedReader(
        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
    );
    StringBuilder sb = new StringBuilder();
    String linea;
    while ((linea = br.readLine()) != null) sb.append(linea);
    br.close();

    JSONArray resultados = new JSONArray(sb.toString());
    if (resultados.length() == 0) return null;

    JSONObject primero = resultados.getJSONObject(0);
    double lat = primero.getDouble("lat");
    double lon = primero.getDouble("lon");
    return new double[]{lat, lon};
}
private void navegarADestino(double lat, double lon, int zoom) {
    String html =
        "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "  <meta charset='utf-8' />" +
        "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "  <link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>" +
        "  <script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>" +
        "  <style>" +
        "      html, body, #map {" +
        "          height: 100%;" +
        "          margin: 0;" +
        "      }" +
        "  </style>" +
        "</head>" +
        "<body>" +
        "<div id='map'></div>" +
        "<script>" +
        "   var map = L.map('map').setView([" + lat + "," + lon + "], " + zoom + ");" +
        "   L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
        "       attribution: 'OpenStreetMap'" +
        "   }).addTo(map);" +
        "   L.marker([" + lat + "," + lon + "]).addTo(map);" +
        "</script>" +
        "</body>" +
        "</html>";
    webView.getEngine().loadContent(html);
}

@FXML
private void connect() {

}

@FXML
private void disconnect() {

}
}
