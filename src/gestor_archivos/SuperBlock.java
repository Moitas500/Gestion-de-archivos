package gestor_archivos;

import java.io.Serializable;
import java.util.ArrayList;

public class SuperBlock implements Serializable {

    private long totalContent = 1000; // Espacio total
    private long alreadyUse = 0; // Espacio ocupado
    private long freeuse = 1000; // Espacio libre
    private ArrayList<Integer> inode_free = new ArrayList<Integer>();
    private ArrayList<Integer> inode_busy = new ArrayList<Integer>();

    public long getTotalContent() {
        return totalContent;
    }

    public long getAlreadyUse() {
        return alreadyUse;
    }

    public void setAlreadyUse(long alreadyUse) {
        this.alreadyUse = alreadyUse;
        this.freeuse = this.getTotalContent() - this.alreadyUse;
    }

    public long getFreeuse() {
        return freeuse;
    }

    public void setFreeuse(long freeuse) {
        this.freeuse = freeuse;
        this.alreadyUse = this.getTotalContent() - this.getFreeuse();
    }

    public int getInode_free() {
        if (null != this.inode_free && this.inode_free.size() > 0) {
            int tem = this.inode_free.get(0);
            if (tem > -1 && tem < 100) {
                this.inode_free.remove(0);
                this.inode_busy.add(tem);
                return tem;
            }
        }
        return -1;
    }

    public void setInode_free(int inode_free) {
        if (inode_free > -1 && inode_free < 100) {
            if (this.inode_busy.contains(inode_free)) {
                this.inode_busy.remove(inode_free);
            }
            this.inode_free.add(inode_free);
        } else {
            System.out.println("inode_free error en la operación！");
        }
    }

    public int getInode_busy() {
        if (null != this.inode_busy && this.inode_busy.size() > 0) {
            int tem = this.inode_busy.get(0);
            if (tem > -1 && tem < 100) {
                this.inode_busy.remove(0);
                this.inode_free.add(tem);
                return tem;
            }
        }
        return -1;
    }

    public void setInode_busy(int inode_busy) {
        if (inode_busy > -1 && inode_busy < 100) {
            if (this.inode_free.contains(inode_busy)) {
                this.inode_free.remove(inode_busy);
            }
            this.inode_busy.add(inode_busy);
        } else {
            System.out.println("inode_busy Error de operación！");
        }
    }
}
