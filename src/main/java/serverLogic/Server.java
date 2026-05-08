/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverLogic;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import modelos.Mensaje;
import modelos.Ticket;
import umg.proyectomultiterminal.InterfazPrincipalController;

/**
 *
 * @author AMD 5600G
 */
public class Server {
    InterfazPrincipalController interfaz;
    
//    private final PriorityQueue<Usuario> colaPrioritaria = new PriorityQueue<>()

    private Map<String, ClientHandler> clientes = new HashMap<>();
    
    // Control de estaciones de atención (cada estación tiene un semáforo con 1 permiso)
    private final Semaphore estacionGeneral = new Semaphore(1);
    private final Semaphore estacionPrioritaria = new Semaphore(1);
    private final Semaphore estacionEspecial = new Semaphore(1);

    // Contador para tickets
    private final AtomicInteger contadorTickets = new AtomicInteger(1);

    // Archivo para persistencia
    private static final String ARCHIVO_ATENDIDOS = "atendidos.csv";
    private final Object lockArchivo = new Object();

    // Control del servidor
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private ExecutorService clientThreadPool;

    //Config del servidor
    private static final Properties config = new Properties();
    private int port;
    
    public void start() {
        if (running) return;
        running = true;

        loadProperties();
        clientThreadPool = Executors.newFixedThreadPool(4);

        acceptThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                System.out.println("Servidor iniciado en puerto " + port);

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Nueva conexión desde " + clientSocket.getInetAddress().getHostAddress());
                        clientThreadPool.submit(new ClientHandler(clientSocket));
                    }/* catch (EOFException | SocketException e) {
                        
                        break;
                    }*/ catch (IOException e) {
                        System.out.println("Error accept(): " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("No se pudo iniciar el servidor: " + e.getMessage());
            } finally {
                stop();
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();
    }
    
    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar serverSocket: " + e.getMessage());
        }
        if (clientThreadPool != null) {
            clientThreadPool.shutdownNow();
        }
        if (acceptThread != null) {
            acceptThread.interrupt();
        }
        System.out.println("Servidor detenido.");
    }

        /**
     * Manejador de cada cliente conectado.
     * Espera un objeto Mensaje y actúa según el tipo.
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (running) {
                    Object obj = in.readObject();
                    if(obj instanceof Ticket){
                        Ticket ticket = (Ticket) obj;
                        procesarTicket(ticket);
                    }
                    if(obj instanceof Mensaje){
                        Mensaje mensaje = (Mensaje) obj;
                        
                        if(username == null && mensaje.isStatus()) {
                            String texto = mensaje.getMensaje();
                            if(texto.startsWith("Conectado ")) {
                                username = texto.replace("Conectado ", "");
                                clientes.put(username, this);
                                System.out.println(username + " registrado");
                            }
                        }                        
                        procesarMensaje(mensaje);
                    }
                }
            } catch (EOFException | SocketException e) {
                System.out.println("Cliente desconectado");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error con cliente: " + e.getMessage());
            } finally {
                clientes.remove(username);
                try { 
                    socket.close(); 
                } catch (IOException ignored) {
                    
                }
            }
        }

        private void procesarTicket(Ticket ticket) throws IOException {
            if(ticket.getEstado() == null){
                ticket.setEstado("Cola");
                interfaz = InterfazPrincipalController.getInstance();           
                Platform.runLater(() -> {
                    interfaz.agregarTicket(ticket);
                });
            }
        }
        
        private void procesarMensaje(Mensaje mensaje){ 
            interfaz = InterfazPrincipalController.getInstance();
  
            if(mensaje.isStatus()){              
                Platform.runLater(() -> {
                    interfaz.usuarioStatus(mensaje.getMensaje());
                });
            }
            
        }
        
        private void enviarACliente(String username, Object obj){
            ClientHandler cliente = clientes.get(username);
            cliente.sendObject(obj);
        }
        
        public void sendObject(Object obj) {
            try {
                out.writeObject(obj);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error enviando objeto");
            }
        }
    }

    private void loadProperties(){
        InputStream input;        
        try{
            input = getClass().getResourceAsStream("/serverConfig/config.properties");
            config.load(input);
            input.close();
            this.port = Integer.parseInt(config.getProperty("port"));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }    
}
