package gestor_archivos;

import java.util.TreeMap;

public class Directory extends File{
    
    private int inodeAddress = -1; // índice del directorio
    private String name = "";
    
    private TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>(); // Estructura de árbol

    public int getInodeAddress() {
        return inodeAddress;
    }

    public void setInodeAddress(int inode_address) {
        this.inodeAddress = inode_address;
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
