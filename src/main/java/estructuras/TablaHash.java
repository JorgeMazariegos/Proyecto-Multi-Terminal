package estructuras;
import modelos.Ticket;

/**
 *
 * @author AMD 5600G
 */
public class TablaHash {
    
    //Nodo de la hash table
    private class NodoHash{
        int dpi;
        Ticket ticket;
        NodoHash next;
        
        public NodoHash(int dpi, Ticket ticket){
            this.dpi = dpi;
            this.ticket = ticket;
        }
    }
    
    private NodoHash[] bucket;
    private int numBuckets;
    private int size;

    public TablaHash(){    
        numBuckets = 10;
        size = 0;
        bucket = new NodoHash[numBuckets];     
    }

    public int size(){ 
        return size; 
    }
    
    public boolean isEmpty(){ 
        return size() == 0; 
    }
      
    // Funcion hash para encontrar el index de una llave
    private int getBucketIndex(int dpi){
        return Math.abs(dpi % numBuckets);
    }

    public Ticket remove(int dpi){
        int bucketIndex = getBucketIndex(dpi);
        NodoHash head = bucket[bucketIndex];

        NodoHash prev = null;
        while (head != null) {
            if (head.dpi == dpi)
                break;
            prev = head;
            head = head.next;
        }

        if (head == null)
            return null;

        size--;

        if (prev != null)
            prev.next = head.next;
        else
            bucket[bucketIndex] = head.next;

        return head.ticket;
    }

    public Ticket get(int dpi){
        int bucketIndex = getBucketIndex(dpi);
        NodoHash head = bucket[bucketIndex];

        while (head != null) {
            if (head.dpi == dpi)
                return head.ticket;
            head = head.next;
        }

        return null;
    }

    public void add(int dpi, Ticket valor){
        int bucketIndex = getBucketIndex(dpi);
        NodoHash head = bucket[bucketIndex];

        while (head != null) {
            if (head.dpi == dpi) {
                head.ticket = valor;
                return;
            }
            head = head.next;
        }

        size++;
        head = bucket[bucketIndex];
        NodoHash newNode
            = new NodoHash(dpi, valor);
        newNode.next = head;
        bucket[bucketIndex] = newNode;

        if ((1.0 * size) / numBuckets >= 0.7) {
            NodoHash[] temp = bucket;
            bucket = new NodoHash[2 * numBuckets];
            numBuckets = 2 * numBuckets;
            size = 0;
            for (NodoHash headNode : temp) {
                while (headNode != null) {
                    add(headNode.dpi, headNode.ticket);
                    headNode = headNode.next;
                }
            }
        }
    }
}
