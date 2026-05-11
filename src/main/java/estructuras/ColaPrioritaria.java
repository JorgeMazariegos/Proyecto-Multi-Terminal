package estructuras;

import modelos.Ticket;

public class ColaPrioritaria {

    private Ticket[] valor;

    private int capacity;

    private int currentHeapSize;

    public ColaPrioritaria() {
        capacity = 10;
        valor = new Ticket[capacity];
        currentHeapSize = 0;
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int left(int index) {
        return 2 * index + 1;
    }

    private int right(int index) {
        return 2 * index + 2;
    }

    private void swap(int a, int b) {
        Ticket temp = valor[a];
        valor[a] = valor[b];
        valor[b] = temp;
    }

    public boolean insert(Ticket ticket) {

        if (currentHeapSize == capacity) {
            resize();
        }

        int i = currentHeapSize;
        valor[i] = ticket;
        currentHeapSize++;

        while (
            i != 0 &&
            valor[i].getPrecio().compareTo(
                valor[parent(i)].getPrecio()
            ) > 0
        ) {

            swap(i, parent(i));
            i = parent(i);
        }

        return true;
    }

    public Ticket peek() {

        if (currentHeapSize == 0) {
            return null;
        }

        return valor[0];
    }

    //Quita el ticket con mayor prioridad
    public Ticket extractMax() {

        if (currentHeapSize <= 0) {
            return null;
        }

        if (currentHeapSize == 1) {
            currentHeapSize--;
            return valor[0];
        }

        Ticket root = valor[0];

        valor[0] = valor[currentHeapSize - 1];

        currentHeapSize--;

        maxHeapify(0);

        return root;
    }

    private void maxHeapify(int index) {

        int l = left(index);
        int r = right(index);

        int largest = index;

        if (
            l < currentHeapSize &&
            valor[l].getPrecio().compareTo(
                valor[largest].getPrecio()
            ) > 0
        ) {
            largest = l;
        }

        if (
            r < currentHeapSize &&
            valor[r].getPrecio().compareTo(
                valor[largest].getPrecio()
            ) > 0
        ) {
            largest = r;
        }
        
        if (largest != index) {

            swap(index, largest);

            maxHeapify(largest);
        }
    }

    public boolean isEmpty() {
        return currentHeapSize == 0;
    }

    public int size() {
        return currentHeapSize;
    }
    
    private void resize() {
        Ticket[] nuevo = new Ticket[capacity * 2];
        System.arraycopy(valor, 0, nuevo, 0, currentHeapSize);
        valor = nuevo;
        capacity = nuevo.length;
    }
    
    public Ticket[] toArray() {
        Ticket[] copia = new Ticket[currentHeapSize];
        System.arraycopy(valor, 0, copia, 0, currentHeapSize);
        return copia;
    }
}
