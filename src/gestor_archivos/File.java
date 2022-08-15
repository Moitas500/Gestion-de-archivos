package gestor_archivos;

import java.io.Serializable;

public class File implements Serializable{
    
    private int inode_add = -1; // índice del archivo
    private String name = "";
    private String content = "";

    public int getInode_add() {
        return inode_add;
    }

    public void setInode_add(int inode_add) {
        this.inode_add = inode_add;
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
