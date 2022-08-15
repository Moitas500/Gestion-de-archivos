package gestor_archivos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class INode implements Serializable, Comparable<INode> {

    private String path = ""; //Ubicación actual
    private int type = 0; // 1-Archivo, 0-Carpeta
    private int address = -1; // direccion del bloque de archivo --- num serie
    private int length = 0; // longitud de archivo
    private String modifytime; // Momento de modificación

    private String users = ""; // Nombre del usuario
    private int right = 1; // 0-lectura, 1-lectura y escritura
    
    private String state = "close"; // Estado
    private int father = -1; // nodo padre
    private int me = -1; // nodo propio

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime() {
        Date date = new Date();
        SimpleDateFormat adf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.modifytime = adf.format(date);
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getRight() {
                if (this.right == 0) {
            return "R";
        } else{
            return "W";
        }
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }

    public int getMe() {
        return me;
    }

    public void setMe(int me) {
        this.me = me;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
        
    public String toString() {
        return this.getUsers() + "\t" + this.getLength() + "b\t"
                + this.getRight() + "\t" + this.getModifytime();
    }

    @Override
    public int compareTo(INode o) {
        return (this.modifytime.hashCode() + this.getType()) - (o.modifytime.hashCode() + o.getType());

    }
}
