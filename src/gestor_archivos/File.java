package gestor_archivos;

import java.io.Serializable;

public class File implements Serializable{
    
    private int inode_address = -1; // índice del archivo
    private String name = "";
    private String content = "";

    public int getInodeAddress() {
        return inode_address;
    }

    public void setInodeAddress(int inode_add) {
        this.inode_address = inode_add;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
