package gestor_archivos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class INode implements Serializable, Comparable<INode> {

    private String modifytime;// tiempo de modiicación
    private int type = 0;

    public int getType() {
        return type;
    }

    @Override
    public int compareTo(INode o) {
        return (this.modifytime.hashCode() + this.getType()) - (o.modifytime.hashCode() + o.getType());

    }
}
