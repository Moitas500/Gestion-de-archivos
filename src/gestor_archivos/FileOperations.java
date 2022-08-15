package gestor_archivos;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileOperations {

    public static Object read(String name) {
        ObjectInputStream oin = null;
        Object obj = null;
        try {
            oin = new ObjectInputStream(new FileInputStream(name));
            obj = oin.readObject();
        } catch (EOFException el) {
        } catch (FileNotFoundException fnfe) {
            write(name, null);
            read(name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (null != oin) {
                oin.close();
            }
        } catch (IOException e) {
            System.out.println("ObjectInputStream no se pudo cerrar");
        }

        return obj;
    }

    public static void write(String name, Object o) {
        File file = new File(name);
        ObjectOutputStream oout = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("No es posible crear el archivo de datos");
            }
        }

        try {
            oout = new ObjectOutputStream(new FileOutputStream(file));
            if (null != o) {
                oout.writeObject(o);
            }
        } catch (Exception e) {
            System.out.println("Error al leer el contenido del archivo");
        } finally {
            try {
                if (null != oout) {
                    oout.close();
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar ObjectOutputStream");
            }
        }
    }
}
