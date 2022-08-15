package gestor_archivos;

import java.util.TreeMap;

public class Directory extends File{
    
    private int inode_address = -1; // índice del directorio
    private String name = "";
    
    private TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>(); // Estructura de árbol

    public int getInode_address() {
        return inode_address;
    }

    public void setInode_address(int inode_address) {
        this.inode_address = inode_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeMap<Integer, Integer> getTree() {
        return tree;
    }

    public void setTree(TreeMap<Integer, Integer> tree) {
        this.tree = tree;
    }
    
    public void setTree(INode iNode, int sub) {
        this.tree.put(iNode.getMe(), sub);
    }
}
