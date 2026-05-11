package estructuras;

import java.io.Serializable;
import modelos.Ticket;

/**
 *
 * @author AMD 5600G
 */
public class Cola implements Serializable{
    private Ticket[] cola;
    private int inicio;
    private int fin;
    private int size;
    
    private final int defaultSize = 10;
    
    public Cola(){
        cola = new Ticket[defaultSize];
        inicio = 0;
        fin = 0;
        size = 0;
    }    
    
    public void enqueue(Ticket ticket){
        if(size == cola.length){
            resize();
        }
        cola[fin] = ticket;
        fin = (fin + 1) % cola.length;
        size++;
    }
    
    public Ticket dequeue(){
        if(isEmpty()){
            //Error de cola vacia. No deberia pasar
            return null;
        }
        Ticket ticket = cola[inicio];
        cola[inicio] = null;
        inicio = (inicio + 1) % cola.length;
        size--;        
        return ticket;
    }

    private void resize() {
        Ticket[] nuevaCola = new Ticket[cola.length * 2];
        for(int i = 0; i < size; i++){
            nuevaCola[i] = cola[(inicio + i) % cola.length];
        }
        cola = nuevaCola;
        inicio = 0;
        fin = size;
    }
    
    public boolean isEmpty(){
        return size==0;
    }
    
    public int size(){
        return size;
    }
}
