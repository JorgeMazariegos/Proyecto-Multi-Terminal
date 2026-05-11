/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package umg.proyectomultiterminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import modelos.Mensaje;
import modelos.Ticket;

/**
 * FXML Controller class
 *
 * @author josef
 */
public class InterfazGeneralController implements Initializable {
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
    
    @FXML
    private Label serverStatus,entregasStatus,registroStatus,vipStatus, tiempoOrigen, tiempoDestino;

    @FXML
    private Button conectToServer, desconectar, doViaje, finishViaje;
    
    @FXML
    private ImageView car, car2;
     
    @FXML
    private ProgressBar progressOrigen, progressDestino;
    
    @FXML
    private TextField txtDPI, txtDestino, txtNTicket, txtNombre, txtOrigen, txtPago;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        car.layoutXProperty().bind(
            progressOrigen.layoutXProperty().add(
            progressOrigen.progressProperty()
            .multiply(progressOrigen.getPrefWidth() - car.getFitWidth())
            )
        );
        
        car2.layoutXProperty().bind(
            progressDestino.layoutXProperty().add(
            progressDestino.progressProperty()
            .multiply(progressDestino.getPrefWidth() - car2.getFitWidth())
            )
        );
    }    
    
    @FXML
    private void connect(){
        try{
            loadProperties();
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            new Thread(this::listenServer).start();
            Mensaje mensaje = new Mensaje("Disponible General");
            mensaje.setStatus(true);            
            sendMensaje(mensaje);
            solicitarTicket();
            
            conectToServer.setDisable(true);
            desconectar.setDisable(false);
            serverStatus.pseudoClassStateChanged(on, true);
            serverStatus.setText("⬤ Disponible");
        }catch (IOException ex) {
            System.getLogger(InterfazGeneralController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
     
     @FXML
    private void disconnect() {
        Mensaje mensaje = new Mensaje("Desconectado General");
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
    
    @FXML
    private void simulation(){
        Random random = new Random();
        int origen = random.nextInt(26) + 5;
        int destino = random.nextInt(26) + 5;
        
        animacion(origen , destino);
    }
    
    @FXML 
    private void viajeTerminado(){
        ticketActual.setUsuarioQueAtendio("General");
        ticketActual.setEstado("Finalizado");
        detenerContador();
        resetFields();
        sendTicket(ticketActual);
        solicitarTicket();
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
    
    private void sendTicket(Ticket ticket){
        if(!conectado){
            return;
        }
        try{
            out.writeObject(ticket);
            out.flush();
        }catch(IOException ex){
            System.out.println("Error al enviar ticket");
            ex.printStackTrace();
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
            Mensaje mensaje = new Mensaje("Request General");
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
    
    private void animacion(int segundosOrigen , int segundosDestino){
        Timeline timeline = new Timeline(
            new KeyFrame(
            Duration.ZERO,
            new KeyValue(progressOrigen.progressProperty(), 0)
        ),
            new KeyFrame(
            Duration.seconds(segundosOrigen),
            new KeyValue(progressOrigen.progressProperty(), 1)
        ));
                
        timeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double seconds = newTime.toMillis() / 1000.0;
            tiempoOrigen.setText(String.format("%.2f seg", seconds));

        });
        
        timeline.setOnFinished(e ->{
            animacionFinal(segundosDestino);
        });
        
        timeline.play();
    }

    private void animacionFinal(int segundosDestino) {
        Timeline timeline = new Timeline(
            new KeyFrame(
            Duration.ZERO,
            new KeyValue(progressDestino.progressProperty(), 0)
        ),
            new KeyFrame(
            Duration.seconds(segundosDestino),
            new KeyValue(progressDestino.progressProperty(), 1)
        ));
        
        timeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double seconds = newTime.toMillis() / 1000.0;
            tiempoDestino.setText(String.format("%.2f seg", seconds));

        });
        
        timeline.setOnFinished(e ->{
            finishViaje.setDisable(false);
        });
        
        timeline.play();
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
