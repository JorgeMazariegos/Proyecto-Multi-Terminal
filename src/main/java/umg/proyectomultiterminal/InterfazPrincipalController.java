package umg.proyectomultiterminal;

import estructuras.ArchivoTickets;
import estructuras.Cola;
import estructuras.ColaPrioritaria;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modelos.Ticket;
import serverLogic.Server;

public class InterfazPrincipalController {
    private static InterfazPrincipalController instance;
    // ---------- Estructuras de datos para las colas ----------
    private final Cola colaGeneral = new Cola();
    private final ColaPrioritaria colaPrioritaria = new ColaPrioritaria();
    private final Cola colaEntregas = new Cola();
    private final Cola colaTicketsFinalizados = new Cola();
    
    private final Map<Ticket, Pane> ticketPanels = new HashMap<>();
    private final Map<Ticket, Thread> contadores = new HashMap<>();
    PseudoClass on = PseudoClass.getPseudoClass("activo");
    Server server = new Server();
       

    @FXML
    private Button detenerServidor;

    @FXML
    private Label serverStatus;

    @FXML
    private Button startServer;

    @FXML
    private VBox summary;

    @FXML
    private Label viajesAtendidos;
    
    @FXML
    private VBox colaEsp, colaNormal, colaVIP;
    
    @FXML 
    private Label statusRegistro, statusGeneral, statusVIP, statusEspecial;
    
    @FXML
    private Label lblColaEsp, lblColaGeneral, lblColaVip;
    
    @FXML
    private Label destinoNormal, destinoVIP, destinoEntregas;
    
    @FXML
    private Label precioNormal, precioVIP, precioEntregas, ticketCompletadoNormal, ticketCompletadoVIP, ticketCompletadoEntrega;
    
    @FXML
    private ImageView imgEntrega, imgGeneral, imgVIP;

    @FXML
    public void initialize(){
        instance = this;
    }
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("IniciodeSesion");
    }
    
    @FXML
    private void switchToInterfazCargar() throws IOException{
        App.setRoot("buscarTicket");
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
    
    @FXML
    private void generarTicketNormal(){
        Ticket ticket = new Ticket();
        ticket.setNumTicket(123);
        ticket.setDPI(3453456);
        ticket.setTipo("Normal");
        ticket.setEstado("Cola");
        agregarTicket(ticket);
    }
    
    @FXML
    private void guardarTickets(){
        ArchivoTickets.guardar(colaTicketsFinalizados);
    }
    
    @FXML
    private void cargarTickets(){
        Cola tickets = ArchivoTickets.cargar();
        while(!tickets.isEmpty()){
            System.out.println(tickets.dequeue().getNumTicket());
        }
    }
    
    public synchronized void agregarTicket(Ticket ticket){
        String tickerNum = String.valueOf(ticket.getNumTicket());
        try {
            
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/umg/proyectomultiterminal/ticketPanel.fxml"));
        Pane newPanel = loader.load();

        TicketPanelController controller = loader.getController();
        controller.setTiempoEspera("0:00 min");               
        controller.setNumTicker(tickerNum);        
        controller.setImage(ticket.getTipo());
        iniciarContador(controller, ticket);
        
        switch (ticket.getTipo()) {
            case "Normal":
                colaNormal.getChildren().add(newPanel);
                colaGeneral.enqueue(ticket);
                break;
            case "Prioridad":
                colaPrioritaria.insert(ticket);
                ticketPanels.put(ticket, newPanel);
                actualizarVBoxVIP();
                break;
            case "Entrega":
                colaEsp.getChildren().add(newPanel);
                colaEntregas.enqueue(ticket);
                break;
        }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualizarContadores(ticket.getTipo());
    }
        
    public static InterfazPrincipalController getInstance() {
        return instance;
    }
    
    public void usuarioStatus(String tipo){
        String[] partes = tipo.split(" ");
        String first = partes[0];
        String second = partes[1];
        
        switch(first){
            case "Disponible":
                switch (second) {
                    case "Registro":
                        statusRegistro.setText("⬤ Disponible");
                        statusRegistro.setTextFill(Color.web("#52b047"));
                        break;
                    case "General":
                        statusGeneral.setText("⬤ Disponible");
                        statusGeneral.setTextFill(Color.web("#52b047"));
                        break;
                    case "VIP":
                        statusVIP.setText("⬤ Disponible");
                        statusVIP.setTextFill(Color.web("#52b047"));
                        break;
                    case "Entrega":
                        statusEspecial.setText("⬤ Disponible");
                        statusEspecial.setTextFill(Color.web("#52b047"));
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
                    case "Entrega":
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
                    case "Entrega":
                        statusEspecial.setText("⬤ Procesando");
                        statusEspecial.setTextFill(Color.YELLOW);
                        break;
                }
                break;
        }
    }

    private void actualizarContadores(String tipo) {
        switch (tipo) {
            case "Normal":
                lblColaGeneral.setText(String.valueOf(colaGeneral.size()));
                break;
            case "Prioridad":
                lblColaVip.setText(String.valueOf(colaPrioritaria.size()));
                break;
            case "Entrega":
                lblColaEsp.setText(String.valueOf(colaEntregas.size()));
                break;
        }
    }
    
    public synchronized Ticket enviarTicket(String cola){
        Ticket ticket = null;
        switch(cola){
            case "General":
                if(colaGeneral.isEmpty()){
                    return null;
                }
                ticket = colaGeneral.dequeue();
                detenerContador(ticket);
                Platform.runLater(() -> {
                    actualizarInterfazGeneral();
                });
                break;
            case "VIP":
                if(colaPrioritaria.isEmpty()){
                    return null;
                }
                ticket = colaPrioritaria.extractMax();
                detenerContador(ticket);
                Platform.runLater(() -> {
                    actualizarInterfazVIP();
                });
                break;
            case "Entrega":
                if(colaEntregas.isEmpty()){
                    return null;
                }
                ticket = colaEntregas.dequeue();
                detenerContador(ticket);
                Platform.runLater(() -> {
                    actualizarInterfazEntregas();
                });
                break;
        }
        return ticket;
    }

    private void actualizarInterfazGeneral() {
        colaNormal.getChildren().remove(0);
        actualizarContadores("Normal");
        usuarioStatus("Procesando General");
    }
    
    private void actualizarInterfazVIP() {
        colaVIP.getChildren().remove(0);
        actualizarContadores("Prioridad");
        usuarioStatus("Procesando VIP");
    }
    
    private void actualizarInterfazEntregas() {
        colaEsp.getChildren().remove(0);
        actualizarContadores("Entrega");
        usuarioStatus("Procesando Entrega");
    }
    
    public void iniciarContador(TicketPanelController controller , Ticket ticket){
        Thread contador = new Thread(() -> {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    Thread.sleep(1000);                   
                    ticket.setTiempoEnCola(ticket.getTiempoEnCola() + 1);
                    Platform.runLater(() -> {
                    controller.setTiempoEspera(intAStringSegundos(ticket.getTiempoEnCola()) + " min");
                    });     
                }
                        
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        contador.setDaemon(true);
        contadores.put(ticket, contador);
        contador.start();
    }
       
    public void detenerContador(Ticket ticket) {
        Thread contador = contadores.get(ticket);
        if (contador != null) {
            contador.interrupt();
            contadores.remove(ticket);
        }
    }
    
    private String intAStringSegundos(int tiempo){
        int minutos = tiempo / 60;
        int segundos = tiempo % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
    
    public void guardarTicket(Ticket ticket){
        colaTicketsFinalizados.enqueue(ticket);
        int viajesTotales = Integer.parseInt(viajesAtendidos.getText());
        viajesAtendidos.setText(String.valueOf(viajesTotales + 1));
        Image image = new Image(getClass().getResource("/check.png").toExternalForm());
        switch(ticket.getTipo()){
            case "Normal":
                ticketCompletadoNormal.setText("Viaje completado");
                destinoNormal.setText("Destino:" + ticket.getDestino());
                precioNormal.setText( "💸 Tarifa:" + ticket.getPrecio());
                imgGeneral.setImage(image);
                break;
            case "Prioridad":
                ticketCompletadoVIP.setText("Viaje completado");
                destinoVIP.setText("Destino:" + ticket.getDestino());
                precioVIP.setText( "💸 Tarifa:" + ticket.getPrecio());
                imgVIP.setImage(image);
                break;
            case "Entrega":
                ticketCompletadoEntrega.setText("Viaje completado");
                destinoEntregas.setText("Destino:" + ticket.getDestino());
                precioEntregas.setText( "💸 Tarifa:" + ticket.getPrecio());
                imgEntrega.setImage(image);
                break;                
        }
    }

    private void actualizarVBoxVIP() {
        colaVIP.getChildren().clear();
        Ticket[] tickets = colaPrioritaria.toArray();
        for (int i = 0; i < tickets.length - 1; i++) {

            for (int j = i + 1; j < tickets.length; j++) {

                if (
                    tickets[j].getPrecio().compareTo(
                        tickets[i].getPrecio()
                    ) > 0
                ) {

                    Ticket temp = tickets[i];
                    tickets[i] = tickets[j];
                    tickets[j] = temp;
                }
            }
        }
        
        for (Ticket t : tickets) {
            Pane p = ticketPanels.get(t);
            if (p != null) {
                colaVIP.getChildren().add(p);
            }
        }
    }
    
}