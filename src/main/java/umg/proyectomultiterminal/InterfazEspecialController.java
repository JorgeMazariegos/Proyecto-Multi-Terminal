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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebView;
import modelos.Mensaje;
import modelos.Ticket;

import org.json.JSONArray;  
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author josef
 */
public class InterfazEspecialController implements Initializable {
PseudoClass on = PseudoClass.getPseudoClass("activo");
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado;
    private static final Properties config = new Properties();
    private String ip;
    private int port;
    private Ticket ticketActual;
    private Thread contador;
    
    @FXML private ProgressBar progressOrigen, progressDestino;
    @FXML private TextField txtDPI,txtNTicket, txtNombre, txtPago;
    @FXML private Button conectToServer, desconectar, doViaje, finishViaje;
    @FXML private Label serverStatus,entregasStatus,registroStatus,vipStatus, tiempoOrigen, tiempoDestino;
    @FXML private TextField txtOrigen;
    @FXML private TextField txtDestino;
    @FXML private Label     lblEstadoBusqueda;
    @FXML private WebView webView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webView.setZoom(1.15);

    mostrarRuta(
        14.6349, -90.5069, // origen
        14.5892, -90.5518  // destino
    );
}
    //-------------------------------------------------------------------------------------------------//
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
    }
    
    @FXML
private void buscarDireccion() {
    String origenTexto = txtOrigen.getText().trim();
    String destinoTexto = txtDestino.getText().trim();
    if (origenTexto.isEmpty() || destinoTexto.isEmpty()) {
        lblEstadoBusqueda.setText("Completa origen y destino.");
        return;
    }
    lblEstadoBusqueda.setText("Buscando ruta...");
    Task<double[][]> task = new Task<>() {
        @Override
        protected double[][] call() throws Exception {
            double[] origen = geocodificar(origenTexto);
            double[] destino = geocodificar(destinoTexto);
            return new double[][]{ origen, destino };
        }
    };
    task.setOnSucceeded(e -> {
        double[][] resultado = task.getValue();
        if (resultado[0] != null && resultado[1] != null) {
            Platform.runLater(() -> {
                mostrarRuta(
                    resultado[0][0],
                    resultado[0][1],
                    resultado[1][0],
                    resultado[1][1]
                );
                lblEstadoBusqueda.setText("Ruta cargada.");
            });
        } else {
            lblEstadoBusqueda.setText("No se encontró una ubicación.");
        }
    });
    task.setOnFailed(e ->
        lblEstadoBusqueda.setText("Error de conexión.")
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
private void mostrarRuta(
        double origenLat,
        double origenLon,
        double destinoLat,
        double destinoLon
) {
    String html =
        "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "<meta charset='utf-8' />" +
        "<link rel='stylesheet' href='https://unpkg.com/leaflet/dist/leaflet.css'/>" +
        "<script src='https://unpkg.com/leaflet/dist/leaflet.js'></script>" +
        "<style>" +
        "html, body, #map {" +
        "height:100%;" +
        "margin:0;" +
        "}" +
        "</style>" +
        "</head>" +
        "<body>" +
        "<div id='map'></div>" +
        "<script>" +
        // MAPA
        "var map = L.map('map').setView([" + origenLat + "," + origenLon + "], 13);" +
        // CAPA
        "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
        "attribution:'OpenStreetMap'" +
        "}).addTo(map);" +
        // MARCADOR ORIGEN
        "var origen = L.marker([" + origenLat + "," + origenLon + "])" +
        ".addTo(map)" +
        ".bindPopup('ORIGEN');" +
        // MARCADOR DESTINO
        "var destino = L.marker([" + destinoLat + "," + destinoLon + "])" +
        ".addTo(map)" +
        ".bindPopup('DESTINO');" +
        // LINEA ENTRE AMBOS
        "var ruta = L.polyline([" +
        "[" + origenLat + "," + origenLon + "]," +
        "[" + destinoLat + "," + destinoLon + "]" +
        "], {" +
        "color:'red'," +
        "weight:4" +
        "}).addTo(map);" +
        // AJUSTAR ZOOM AUTOMATICO
        "map.fitBounds(ruta.getBounds());" +
            
            // CARRO
"var carro = L.marker([" + origenLat + "," + origenLon + "], {" +
"icon: L.divIcon({" +
"className: 'car-icon'," +
"html: '<img src=\"file:///C:/Users/josef/OneDrive/Desktop/CARPETA%20FERCHO/Proyecto-Multi-Terminal/src/main/resources/car.png\" width=\"32\" height=\"32\">'," +
"iconSize: [32,32]" +
"})" +
"}).addTo(map);" +
            
            
// ANIMACION
"var lat = " + origenLat + ";" +
"var lon = " + origenLon + ";" +
"var pasos = 200;" +
"var deltaLat = (" + destinoLat + " - " + origenLat + ") / pasos;" +
"var deltaLon = (" + destinoLon + " - " + origenLon + ") / pasos;" +
"var contador = 0;" +
"var animacion = setInterval(function() {" +
"   lat += deltaLat;" +
"   lon += deltaLon;" +
"   carro.setLatLng([lat, lon]);" +
"   contador++;" +
"   if(contador >= pasos) {" +
"       clearInterval(animacion);" +
"   }" +
"}, 100);" +
        "</script>" +
        "</body>" +
        "</html>";
    
    webView.getEngine().loadContent(html);
}
//---------------------------------------------------------------------------------------------------------------------//

@FXML
private void connect() {
try{
            loadProperties();
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            new Thread(this::listenServer).start();
            Mensaje mensaje = new Mensaje("Disponible Entrega");
            mensaje.setStatus(true);            
            sendMensaje(mensaje);
            solicitarTicket();
            
            conectToServer.setDisable(true);
            desconectar.setDisable(false);
            serverStatus.pseudoClassStateChanged(on, true);
            serverStatus.setText("⬤ Disponible");
        }catch (IOException ex) {
            System.getLogger(InterfazEspecialController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
}

@FXML
private void disconnect() {
Mensaje mensaje = new Mensaje("Desconectado Entrega");
        mensaje.setStatus(true);
        sendMensaje(mensaje);
        conectado = false;
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.getLogger(InterfazGeneralController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } finally {
            out = null;
            in = null;
            socket = null;
        }
        detenerContador();
        conectToServer.setDisable(false);
        desconectar.setDisable(true);
        serverStatus.pseudoClassStateChanged(on, false);
        serverStatus.setText("⬤ Desconectado");
}
    private void sendMensaje(Mensaje mensaje){
        if(!conectado){
            return;
        }
        try {
            out.writeObject(mensaje);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Error al enviar mensaje");
            ex.printStackTrace();
        }
    }
    private void iniciarContador(Ticket ticket){
        contador = new Thread(() -> {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    Thread.sleep(1000);                   
                    ticket.setDuracionAtencion(ticket.getDuracionAtencion()+ 1);    
                }
                        
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        contador.setDaemon(true);
        contador.start();
    }
    
    private void detenerContador(){
        if(contador != null && contador.isAlive()){
            contador.interrupt();
            contador = null;
        }
    }
    private void listenServer() {
        try {
            while (conectado) {

                Object obj = in.readObject();

                if(obj instanceof Mensaje) {
                    Mensaje mensaje = (Mensaje) obj;

                    Platform.runLater(() -> {
                        System.out.println("Mensaje del servidor: "
                                + mensaje.getMensaje());
                    });
                }

                if(obj instanceof Ticket) {
                    Ticket ticket = (Ticket) obj; 
                    Platform.runLater(() -> {
                        procesarTicket(ticket);
                    });
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Servidor desconectado");
            conectado = false;
            conectToServer.setDisable(false);
            desconectar.setDisable(true);
            serverStatus.pseudoClassStateChanged(on, false);
            serverStatus.setText("⬤ Desconectado");
        }
    }
    private void loadProperties(){
        InputStream input;
        
        try{
            input = getClass().getResourceAsStream("/serverConfig/config.properties");
            config.load(input);
            input.close();
            this.port = Integer.parseInt(config.getProperty("port"));
            this.ip = config.getProperty("ip");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    private void solicitarTicket(){
        if(conectado) {
            Mensaje mensaje = new Mensaje("Request Entrega");
            mensaje.setStatus(true);
            sendMensaje(mensaje);
        }
    }
    private void procesarTicket(Ticket ticket) {
        resetFields();
        ticketActual = ticket;
        txtDPI.setText(String.valueOf(ticket.getDPI()));
        txtNTicket.setText("#"+String.valueOf(ticket.getNumTicket()));
        txtNombre.setText(ticket.getNombre() + " " + ticket.getApellido());
        txtOrigen.setText(ticket.getOrigen());
        txtDestino.setText(ticket.getDestino());
        txtPago.setText(String.valueOf(ticket.getPrecio()));
        iniciarContador(ticketActual);
        doViaje.setDisable(false);
    }
    private void resetFields(){
        doViaje.setDisable(true);
        finishViaje.setDisable(true);
        progressDestino.setProgress(0);
        progressOrigen.setProgress(0);
        tiempoOrigen.setText("0.00 seg");
        tiempoDestino.setText("0.00 seg");
        txtDPI.setText("");
        txtNTicket.setText("");
        txtNombre.setText("");
        txtOrigen.setText("");
        txtDestino.setText("");
        txtPago.setText("");
    }
    
    }    
    

