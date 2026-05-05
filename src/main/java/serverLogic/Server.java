/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverLogic;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    
    // ---------- Estructuras de datos para las colas ----------
//    private final Queue<Usuario> colaGeneral = new LinkedList<>();
//    private final PriorityQueue<Usuario> colaPrioritaria = new PriorityQueue<>(
//        Comparator.comparingInt(Usuario::getEdad).reversed()  // mayores primero
//    );

    // Para acceso concurrente a las colas
    private final Object lockColaGeneral = new Object();
    private final Object lockColaPrioritaria = new Object();
    private final Object lockColaEspecial = new Object ();

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

    // Puerto por defecto (puede ser configurable)
    private final int port = 1234;
    
    public void start() {
        if (running) return;
        running = true;

        // Pool de hilos para manejar clientes
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
                    } catch (EOFException | SocketException e) {
                        System.out.println("Cliente desconectado");
                        break;
                    } catch (IOException e) {
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
                        procesarMensaje(mensaje);
                        System.out.println(mensaje.getMensaje());
                    }
                }
            } catch (EOFException | SocketException e) {
                // Cliente desconectado normalmente
                
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error con cliente: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void procesarTicket(Ticket ticket) throws IOException {
            System.out.println(ticket.getDPI());
            interfaz = InterfazPrincipalController.getInstance();           
            Platform.runLater(() -> {
                interfaz.agregarTicker(ticket);
            });            
        }
        
        private void procesarMensaje(Mensaje mensaje){ 
            interfaz = InterfazPrincipalController.getInstance();
                        
            if(mensaje.isStatus()){              
                Platform.runLater(() -> {
                    interfaz.usuarioStatus(mensaje.getMensaje());
                });
            }
            
        }
    }
}
