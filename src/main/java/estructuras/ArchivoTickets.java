/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author AMD 5600G
 */
public class ArchivoTickets {
    private static final String FILE_NAME = "tickets.dat";

    public static void guardar(Cola cola) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE_NAME))) {

            out.writeObject(cola);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Cola cargar() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return new Cola();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(FILE_NAME))) {

            return (Cola) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Cola();
    }
     public static Cola cargar(File archivo) {

        if (archivo == null || !archivo.exists()) {
            return new Cola();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(archivo))) {

            return (Cola) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Cola();
    }
}
