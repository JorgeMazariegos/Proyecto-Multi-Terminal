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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final Map<String, ClientHandler> clientes = new ConcurrentHashMap<>();;

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
        if (!running){
            return;
        }
        for(ClientHandler cliente : clientes.values()){
            cliente.disconnect();
        }
        clientes.clear();
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

    /*
     * Manejador de cada cliente conectado.
     * Espera un objeto Mensaje y actúa según el tipo.
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;
        private volatile boolean disponible;

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
                            if(texto.startsWith("Disponible ")) {
                                username = texto.replace("Disponible ", "");
                                clientes.put(username, this);
                                System.out.println(username + " registrado");
                                enviarListaClientes(this);
                                Mensaje conexion = new Mensaje(username);
                                conexion.setTipo("CONECTADO");      
                                broadcast(conexion);
                            }
                        }                        
                        procesarMensaje(mensaje);
                    }
                }
            } catch (EOFException | SocketException e) {
                System.out.println("Cliente desconectado");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error con cliente: " + e.getMessage());
                e.printStackTrace();
            } finally {
                clientes.remove(username);
                Mensaje desconexion = new Mensaje(username);
                desconexion.setTipo("DESCONECTADO");
                broadcast(desconexion);             
                try {
                    socket.close(); 
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    
                }         
            }
        }

        private void procesarTicket(Ticket ticket) throws IOException {
            if(ticket.getEstado().equals("Solicitado")){
                ticket.setEstado("Cola");
                interfaz = InterfazPrincipalController.getInstance();
                boolean ticketEnviado = false;
                switch(ticket.getTipo()){
                    case "Normal":
                        ClientHandler general = clientes.get("General");
                        if(general != null && general.disponible){
                            enviarACliente("General", ticket);
                            ticketEnviado = true;
                        }
                        break;
                    case "Prioridad":
                        ClientHandler vip = clientes.get("VIP");
                        if(vip != null && vip.disponible){
                            enviarACliente("VIP", ticket);
                            ticketEnviado = true;
                        }
                        break;
                    case "Entrega":{
                        ClientHandler entrega = clientes.get("Entrega");
                        if(entrega != null && entrega.disponible){
                            enviarACliente("Entrega", ticket);
                            ticketEnviado = true;
                        }
                        break;
                    }                      
                }
                if(!ticketEnviado){
                    Platform.runLater(() -> {
                        interfaz.agregarTicket(ticket);
                    });
                }
            }
            if(ticket.getEstado().equals("Finalizado")){
                Platform.runLater(() -> {
                    interfaz.guardarTicket(ticket);
                });           
            }
        }
        
        private void procesarMensaje(Mensaje mensaje){ 
            interfaz = InterfazPrincipalController.getInstance();  
            if(mensaje.isStatus()){
                if(mensaje.getMensaje().startsWith("Request ")){
                    String cliente = mensaje.getMensaje().replace("Request ", "");
                    clientes.get(cliente).disponible = true;
                    Platform.runLater(() -> {
                        interfaz.usuarioStatus("Disponible " + cliente);
                    });
                    Ticket ticket = interfaz.enviarTicket(cliente);
                    if(ticket == null){                            
                        return;
                    } 
                    enviarACliente(cliente,ticket);  
                }else{
                    Platform.runLater(() -> {
                        interfaz.usuarioStatus(mensaje.getMensaje());
                    });
                }        
            }
        }
        
        private void enviarACliente(String username, Object obj){
            ClientHandler cliente = clientes.get(username);
            if(cliente != null && cliente.disponible){
                cliente.disponible = false;
                cliente.sendObject(obj);
            }           
        }
        
        public synchronized void sendObject(Object obj) {
            try {
                out.writeObject(obj);
                out.flush();
                out.reset();
            } catch (IOException e) {
                System.out.println("Error enviando objeto");
                e.printStackTrace();
            }
        }
        
        public void disconnect() {
            try {
                if(socket != null && !socket.isClosed()){
                    socket.close();
                }
            }catch(IOException e){
                e.printStackTrace();
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
    
    private void broadcast(Mensaje mensaje){
        for(ClientHandler cliente : clientes.values()){
            cliente.sendObject(mensaje);
        }
    }
    
    private void enviarListaClientes(ClientHandler clienteNuevo){
        for(String nombre : clientes.keySet()){
            Mensaje mensaje = new Mensaje(nombre);
            mensaje.setTipo("CLIENTE_CONECTADO");
            clienteNuevo.sendObject(mensaje);
        }
    }
    
}
