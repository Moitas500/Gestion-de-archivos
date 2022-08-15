package gestor_archivos;

public class GestorArchivos {

    private FileSystem ext2;

    public GestorArchivos() {
        ext2 = FileSystem.getInstance();
        ext2.initFileExplorer();
        ext2.login();
    }

    public static void main(String[] args) {
        GestorArchivos ge = new GestorArchivos();
    }

}
