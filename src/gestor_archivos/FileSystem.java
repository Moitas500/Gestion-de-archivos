package gestor_archivos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class FileSystem {

    Scanner sc = new Scanner(System.in);

    public static SuperBlock sb = null; // Registra la informacion total del disco virtual
    public static ArrayList<Users> users;
    public static ArrayList<INode> inodes = new ArrayList<INode>(100);
    public static ArrayList<Object> blocks = new ArrayList<>(100);

    public static String name = null;
    public static String password = null;
    public static int aux;
    public static INode now_inode = null;
    public static Object now_file = null;
    private static FileSystem instance = new FileSystem();

    private FileSystem() {
    }

    // Singleton 
    public static FileSystem getInstance() {
        return instance;
    }

    public void init() {
        System.out.println("            Sistema de archivos ext2");
        System.out.println("_______________________________________________");

        if (!loadData()) {
            // Datos
            blocks = new ArrayList<Object>(100);
            for (int i = 0; i < 100; i++) {
                blocks.add(new File());
            }
            FileOperations.write("data.dat", blocks);

            // Inodos
            sb = new SuperBlock();
            for (int i = 0; i < 100; i++) {
                inodes.add(new INode());
            }
            for (int i = 0; i < 100; i++) {
                sb.setInodeFree(i);
            }
            FileOperations.write("super.dat", sb);

            users = new ArrayList<Users>();
            Users u = new Users("user", "123");
            users.add(u);
            register(u);
            FileOperations.write("users.dat", users);
        }

    }

    public void initFileExplorer() {
        init();
//        System.out.println("Inicio de sesión exitoso");
        now_inode = getInode("user" + "/");
        now_file = blocks.get(now_inode.getAddress());
    }

    public void login() {
        System.out.println("Bienvenido, ingrese su usuario y contraseña");
        System.out.print("Nombre de usuario:");
        name = sc.next();
        System.out.print("Constraseña:");
        password = sc.next();

        Users user = isInNames(name);
        if (user == null) {
            System.out.println("El usuario \"" + name + "\" no existe ¿desea agregarlo? s/n");
            if ("s".equals(sc.next())) {
                Users u = new Users(name, password);

                if (register(u)) {
                    System.out.println("Usuario registrado con éxito");
                    login();
                } else {
                    System.out.println("El proceso de registro a fallado :C");
                    System.exit(0);
                }

            } else {
                login();
            }

        } else {

            if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                System.out.println("Inicio de sesión correcto");
                now_inode = getInode(name + "/");
                now_file = blocks.get(now_inode.getAddress());

                help();
                execute();
            } else {
                System.out.println("Inicio de sesión incorrecto");
                login();
            }

        }
    }

    public void execute() {
        String commond = null;
        String cmd[] = null;

        while (true) {
            System.out.print(now_inode.getPath() + "> ");
            commond = sc.nextLine();
            if (commond.equals("")) {
                commond = sc.nextLine();
            }
            cmd = commond.trim().split(" ");

            // Listar
            if (cmd[0].trim().equals("ls")) {
                listFiles();
            } // crear archivo
            else if (cmd[0].equals("cat")) {
                createFile(cmd[1], true);
            } // Print working directory
            else if (cmd[0].trim().equals("pwd")) {
                PWD();
            } // Crear carpeta
            else if (cmd[0].trim().equals("mkdir")) {
                if (cmd.length == 1) {
                    System.out.println("No se ha introducido ningún nombre para la nueva carpeta");
                    continue;
                }
                createDirectory(cmd[1]);
            } // Eliminar archivos
            else if (cmd[0].trim().equals("rm")) {
                remove(cmd[1]);
            } // Estadisticas
            else if (cmd[0].trim().equals("stat")) {
                printFileStats(cmd[1]);
            } // Seleccionar directorio
            else if (cmd[0].trim().equals("cd")) {
                if (cmd.length == 1) {
                    System.out.println("cd Agregue .. o el nombre del subdirectorio después");
                    continue;
                }
                changeDirectory(cmd[1]);
            } // Abrir un archivo
            else if (cmd[0].trim().equals("open")) {
                if (aux > 5) {
                    System.out.println("Se han abierto 5 archivos");
                    continue;
                }
                openFile(cmd[1]);
            } // Cerrar un archivo
            else if (cmd[0].trim().equals("close")) {
                closeFile(cmd[1]);
            } // Cambiar nombre
            else if (cmd[0].trim().equals("rename")) {
                if (cmd.length < 3) {
                    System.out.println("entrada de comando incorrecta");
                } else if (rename(cmd[1], cmd[2])) {
                    System.out.println("Cambio de nombre exitoso");
                } else {
                    System.out.println("Cambio de nombre fallido");
                }
            } // leer contenido de archivo
            else if (cmd[0].trim().equals("read")) {
                readFile(cmd[1]);
            } // Editar archivo
            else if (cmd[0].trim().equals("write")) {
                editFile(cmd[1]);
            } // Guardar y salir
            else if (cmd[0].trim().equals("exit")) {
                exit();
            } // Deslogearse
            else if (cmd[0].trim().equals("logout")) {
                FileOperations.write("super.dat", sb);
                FileOperations.write("users.dat", users);
                FileOperations.write("inodes.dat", inodes);
                FileOperations.write("data.dat", blocks);
                System.out.println("Saliendo!");
                login();
            } // help
            else if (cmd[0].trim().equals("help")) {
                help();
            } else {
                System.out.println(commond);
                System.out.println("Commando no encontrado");
            }
        }
    }

    public boolean loadData() {
        sb = (SuperBlock) FileOperations.read("super.dat");
        if (sb != null) {
            inodes = (ArrayList<INode>) FileOperations.read("inodes.dat");
        }
        blocks = (ArrayList<Object>) FileOperations.read("data.dat");
        users = (ArrayList<Users>) FileOperations.read("users.dat");
        return !(sb == null || inodes == null || blocks == null || users == null);
    }

    public void listFiles() {
        int m = 0;
        if (now_file instanceof Directory) {
            Directory nowRealFile = (Directory) now_file;
            m = nowRealFile.getTree().size();
            if (m == 0) {
                System.out.println("Ninguna entrada de directorio");
            } else {
                System.out.println("Nombre del archivo\tDirección\tSL 0/SE 1\tLongitud de archivo\t");
                Set<Integer> dirInodes = nowRealFile.getTree().keySet();
                Iterator<Integer> iteratore = dirInodes.iterator();

                while (iteratore.hasNext()) {
                    Object file = blocks.get(nowRealFile.getTree().get(iteratore.next()));

                    if (file instanceof Directory) {
                        Directory realFile = (Directory) file;
                        INode realInode = inodes.get(realFile.getInodeAddress());

                        System.out.println(String.format("%-15s", realFile.getName()) + "\t\t" + "inode "
                                + realInode.getAddress() + "\t\t" + String.format("%5s", realInode.getRight())
                                + "\t\t" + String.format("%10s", realInode.getLength()) + "B\t");

                    } else {
                        File realFile = (File) file;
                        INode realInode = inodes.get(realFile.getInodeAddress());
                        System.out.println(String.format("%-15s", realFile.getName()) + "\t\t" + "inode "
                                + realInode.getAddress() + "\t\t" + String.format("%5s", realInode.getRight())
                                + "\t\t" + String.format("%10s", realInode.getLength()) + "B\t");
                    }
                }
                System.out.println("Número de archivos: " + m);
            }
        } else {
            File nowRealFile = (File) now_file;
        }
    }

    public boolean register(Users user) {
        int inodeFreeIndex = 0;
        inodeFreeIndex = this.getFreeInode();
        if (inodeFreeIndex > -1) {
            now_inode = inodes.get(inodeFreeIndex);
            now_inode.setAddress(inodeFreeIndex);
            now_inode.setModifytime();
            now_inode.setRight(1);
            now_inode.setState("close");
            now_inode.setType(0);
            now_inode.setUsers(user.getName());
            now_inode.setPath(user.getName() + "/");
            now_inode.setMe(inodeFreeIndex);

            inodes.set(inodeFreeIndex, now_inode);
            Directory block = new Directory();
            block.setName(user.getName());
            blocks.set(inodeFreeIndex, block);
            users.add(user);
            FileOperations.write("users.dat", users);
            FileOperations.write("inodes.dat", inodes);
            return true;
        }
        return false;
    }

    public void exit() {
        saveData();
        System.exit(0);
    }

    public void help() {
        System.out.println("\thelp\t\t\t\tayuda");
        System.out.println("\tls\t\t\t\tListar contenido del directorio actual");
        System.out.println("\tcd [directorio]\tEntrar en un directorio");
        System.out.println(String.format("%-29s", "\tpwd\t") + "Mostrar directorio actual");
        System.out.println("\tmkdir [directorio]\tCrear un directorio");
        System.out.println("\tcat [archivo]\tCrear un archivo");
        System.out.println("\tread [archivo]\tMostrar contenido de archivo");
        System.out.println("\twrite [archivo]\tEditar un archivo de texto existente");
        System.out.println("\trename [vNombre, nNombre]\tEditar nombre de un archivo existente");
        System.out.println("\trm [archivo]\t\tEliminar un archivo");
        System.out.println("\tstat [archivo]\tMostrar propiedades de archivo");
        System.out.println("\texit\t\t\t\tSalir");
        System.out.println("\tlogout\t\t\t\tCerrar sesión");
    }

    public void saveData() {
        FileOperations.write("super.dat", sb);
        FileOperations.write("users.dat", users);
        FileOperations.write("inodes.dat", inodes);
        FileOperations.write("data.dat", blocks);
    }

    public void createFile(String fileName, boolean writeable) {
        int index = getFreeInode();
        if (index != -1) {
            File myFile = new File();
            myFile.setName(fileName);
            INode inode = new INode();
            inode.setFather(now_inode.getMe());
            inode.setUsers(name);
            inode.setMe(index);
            inode.setModifytime();
            
            if (inode.getFather() == -1) {
                inode.setPath(name + "/");
            } else {
                inode.setPath(inodes.get(inode.getFather()).getPath() + fileName + "/");
            }
            
            inode.setRight(1); // Escritura
            inode.setState("open");
            inode.setType(1); // Documento
            inode.setAddress(index);
            inodes.set(index, inode);
            myFile.setInodeAddress(index);
            Directory realFile = (Directory) now_file;
            blocks.set(index, myFile);
            realFile.getTree().put(index, index);
            if (writeable) {
                System.out.println(fileName + " El archivo esta abierto, ingrese el conteniod, terminado con **");
                StringBuffer content = new StringBuffer();
                while (true) {
                    String tem = sc.nextLine();
                    if (tem.equals("**")) {
                        System.out.println("entrada de fin de archivo");
                        break;
                    } else {
                        content.append(tem + "\r\n");
                    }

                }
                
                myFile.setContent(content.toString());
                inodes.get(index).setLength(content.length());
                inodes.get(index).setState("close");
                System.out.println(fileName + " el archivo esta cerrado");
                sb.setAlreadyUse(content.length());
            }
            sb.setInodeBusy(index);
        } else {
            System.out.println("Inode de la aplicación fallá");
        }
    }

    public void createDirectory(String directoryName) {
        int index = getFreeInode();
        if (index != -1) {
            Directory my_file = new Directory();
            my_file.setName(directoryName);
            INode inode = new INode();
            inode.setFather(now_inode.getMe());
            inode.setUsers(name);
            inode.setMe(index);
            inode.setModifytime();
            inode.setPath(now_inode.getPath() + directoryName + "/");

            System.out.println("Nueva dirección: " + now_inode.getPath() + directoryName + "/");
            inode.setRight(1); // Escritura
            inode.setType(0); // Carpeta
            inode.setAddress(index);
            inodes.set(index, inode);
            my_file.setInodeAddress(index);
            Directory real_file = (Directory) now_file;
            blocks.set(index, my_file);
            real_file.getTree().put(index, index);
            inodes.get(index).setLength(0);
            sb.setInodeBusy(index);
        } else {
            System.out.println("inode la aplicación fallá");
        }

    }

    public void remove(String fileName) {
        Object o = this.getFileByName(fileName);

        if (null != o) {
            if (o instanceof Directory) {
                Directory o1 = (Directory) o;

                if (o1.getTree().size() == 0) {
                    int index = o1.getInodeAddress();
                    sb.setInodeFree(index);
                    // restablecer nodo
                    inodes.set(index, new INode());
                    // restablecer bloque
                    blocks.set(o1.getInodeAddress(), new File());
                    // Eliminar datos en el Arbol del directorio
                    Directory file = (Directory) now_file;
                    file.getTree().remove(index);

                    System.out.println(o1.getName() + "; El directorio fue eliminado");
                } else {
                    System.out.println(o1.getName() + " El directorio no esta vacio, no se puede borrar");
                }
            } else if (o instanceof File) {
                File o1 = (File) o;

                int index = o1.getInodeAddress();
                sb.setInodeFree(index);
                sb.setFreeuse(inodes.get(index).getLength());
                // restablecer nodo
                inodes.set(index, new INode());
                // restablecer bloque
                blocks.set(o1.getInodeAddress(), new File());
                // Eliminar datos en el Arbol del directorio
                Directory file = (Directory) now_file;
                file.getTree().remove(index);

                System.out.println(o1.getName() + "; El archivo fue eliminado");

            } else {
                System.out.println(fileName + " el archivo no existe");
            }
        }
    }

    public String printFileStats(String fileName) {
        int bandera = 0;
        String stats = "";
        Directory nowRealFile = (Directory) now_file;
        Set<Integer> dir_inodes = nowRealFile.getTree().keySet();
        Iterator<Integer> iteratore = dir_inodes.iterator();

        while (iteratore.hasNext()) {
            Object file = blocks.get(nowRealFile.getTree().get(iteratore.next()));

            if (file instanceof Directory) {
                if (((Directory) file).getName().equals(fileName)) {
                    INode search_inode = inodes.get(((Directory) file).getInodeAddress());
                    if (bandera == 0) {
                        System.out.println(String.format("%-20s", "Nombre archivo")
                                + "\tUsuario\t\tDirección\tLongitud de archivo\t   SL 0/SE 1\tEstado\tFecha de creación");
                        bandera = 1;
                    }
                    // revisar
                    stats += "Nombre archivo: " + ((Directory) file).getName() + "\n" + "Usuario: "
                            + search_inode.getUsers() + "\n" + "Dirección: " + "inode " + search_inode.getAddress()
                            + "\n" + "Longitud de archivo: " + search_inode.getLength() + "B" + "\n" + "Permisos: "
                            + search_inode.getRight() + "\n" + "Estado: " + search_inode.getState() + "\n"
                            + "Fecha de creación: " + search_inode.getModifytime();

                    System.out.println(String.format("%-20s", ((Directory) file).getName()) + "\t"
                            + search_inode.getUsers() + "\t\t" + "inode " + search_inode.getAddress() + "\t\t"
                            + String.format("%10s", search_inode.getLength() + "B") + "\t\t\t"
                            + String.format("%5s", search_inode.getRight()) + "\t" + search_inode.getState() + "\t"
                            + search_inode.getModifytime());

                }
            } else if (file instanceof File) {
                if (((File) file).getName().equals(fileName)) {
                    INode searchInode = inodes.get(((File) file).getInodeAddress());
                    if (bandera == 0) {
                        System.out.println(String.format("%-20s", "Nombre archivo")
                                + "\tUsuario\t\tDirección\tLongitud de archivo\t   SL 0/SE 1\tEstado\tFecha de creación");
                        bandera = 1;
                    }

                    stats += "Nombre archivo: " + ((File) file).getName() + "\n" + "Usuario: "
                            + searchInode.getUsers() + "\n" + "Dirección: " + "inode " + searchInode.getAddress()
                            + "\n" + "Longitud de archivo: " + searchInode.getLength() + "B" + "\n" + "Permisos: "
                            + searchInode.getRight() + "\n" + "Estado: " + searchInode.getState() + "\n"
                            + "Fecha de creación: " + searchInode.getModifytime();

                    System.out.println(String.format("%-20s", ((File) file).getName()) + "\t"
                            + searchInode.getUsers() + "\t\t" + "inode " + searchInode.getAddress() + "\t\t"
                            + String.format("%10s", searchInode.getLength() + "B") + "\t\t\t"
                            + String.format("%5s", searchInode.getRight()) + "\t" + searchInode.getState() + "\t"
                            + searchInode.getModifytime());
                }
            }
        }

        if (bandera == 0) {
            System.out.println("El archivo o directorio no existe");
        }
        return stats;
    }

    public boolean changeDirectory(String directory) {
        boolean bandera = false;
        if (".".equals(directory)) {
            System.out.println("Use .. para ir al directorio anterior");
        } else if ("..".equals(directory)) {
            if (now_inode.getFather() == -1) {
                System.out.println("El directorio actual es el directorio raiz");
            } else {
                Directory nowDirectory = (Directory) now_file;
                now_inode = inodes.get(now_inode.getFather());
                now_file = blocks.get(now_inode.getAddress());
            }
        } else {
            Directory realFile = (Directory) now_file;
            Set<Integer> dirInodes = realFile.getTree().keySet();
            Iterator<Integer> iteratore = dirInodes.iterator();
            while (iteratore.hasNext()) {
                Object file = blocks.get(realFile.getTree().get(iteratore.next()));
                if (file instanceof Directory myDirectory) {
                    if (myDirectory.getName().equals(directory)) {
                        now_file = myDirectory;
                        now_inode = inodes.get(myDirectory.getInodeAddress());
                        bandera = true;
                    }
                }
            }

            if (!bandera) {
                System.out.println("El directorio no existe");
            }
        }
        return bandera;
    }

    public void openFile(String fileName) {
        boolean bandera = false;

        for (int i = 0; i < blocks.size(); i++) {
            Object o = blocks.get(i);
            if (o instanceof File myFile) {
                if (myFile.getName().equals(fileName)) {
                    INode inode = inodes.get(myFile.getInodeAddress());
                    if (inode.getState().equals("open")) {
                        System.out.println("El archivo ha sido abierto");
                        bandera = true;
                    } else {

                        inode.setState("open");
                        System.out.println("Archivo abierto con éxito");
                        aux++;
                        bandera = true;
                    }
                }
            }
        }
        if (!bandera) {
            System.out.println("El archivo no existe");
        }

    }

    public void closeFile(String fileName) {
        boolean bandera = false;

        for (int i = 0; i < blocks.size(); i++) {
            Object o = blocks.get(i);
            if (o instanceof File myFile) {
                if (myFile.getName().equals(fileName)) {
                    INode inode = inodes.get(myFile.getInodeAddress());
                    if (inode.getState().equals("close")) {
                        System.out.println("El archivo ha sido cerrado!");
                        bandera = true;
                    } else {
                        inode.setState("close");
                        System.out.println("Archivo cerrado con éxito");
                        aux--;
                        bandera = true;
                    }
                }
            }

        }

        if (!bandera) {
            System.out.println("El archivo no existe");
        }
    }

    public void readFile(String fileName) {
        Object o = this.getFileByName(fileName);
        if (null != o) {
            if (o instanceof Directory o1) {
                System.out.println(o1.getName() + " El directorio no puede ejecutar este comando");
            } else if (o instanceof File o1) {
                System.out.println(o1.getName() + " El contenido del archivo es el siguiente: ");
                System.out.println(o1.getContent().substring(0, o1.getContent().lastIndexOf("\r\n")));
            }
        }
    }

    public void editFile(String fileName) {
        Object o = this.getFileByName(fileName);
        if (null != o) {
            if (o instanceof Directory o1) {
                System.out.println(o1.getName() + " El directorio no puede ejecutar este comando");
            } else if (o instanceof File o1) {
                // System.out.println(o1.getName());
                System.out.println("Seleccione: \n1.Continuar\n2.Reescribir");
                String select = sc.next();

                while (true) {
                    if ("1".equals(select)) {
                        System.out.println("Ingrese los datos para continuar, terminando con **");
                        StringBuffer content = new StringBuffer(
                                o1.getContent().substring(0, o1.getContent().lastIndexOf("\r\n")));
                        while (true) {
                            String tem = sc.next();
                            if (tem.equals("**")) {
                                System.out.println("entrada de fin de archivo");
                                break;
                            } else {
                                content.append(tem + "\r\n");
                            }

                        }
                        o1.setContent(content.toString());
                        System.out.println("Continuar operación de escritura exitosa");
                        break;
                    } else if ("2".equals(select)) {
                        System.out.println("Por favor reescriba el archivo, terminando con **");
                        StringBuffer content = new StringBuffer();
                        while (true) {
                            String tem = sc.next();
                            if (tem.equals("**")) {
                                System.out.println("Archivo modificado");
                                break;// entrada de fin de archivo
                            } else {
                                content.append(tem + "\r\n");
                            }

                        }
                        o1.setContent(content.toString());
                        System.out.println("Operación de reescritura exitosa");
                        break;

                    } else {
                        System.out.println("Errores de entrada, vuelva a ingresar");
                        select = sc.next();
                    }
                }
            }
        } else {
            System.out.println("Errores de entrada, vuelva a ingresar 1 o 2");

        }
    }

    public String PWD() {
        String path = now_inode.getPath();
        System.out.println(path);
        return path;
    }

    Object getFileByName(String name) {
        for (Object o : blocks) {
            if (o instanceof Directory o1) {
                if (o1.getName().equals(name)) {
                    return o1;
                }
            } else if (o instanceof File o1) {
                if (o1.getName().equals(name)) {
                    return o1;
                }
            }
        }
        return null;
    }

    private Users isInNames(String name) {
        for (Users u : users) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    private int getFreeInode() {
        return sb.getInodeFree();
    }


    private INode getInode(String path) {
        for (int i = 0; i < 100; i++) {
            if (path.equals(inodes.get(i).getPath())) {
                return inodes.get(i);
            }
        }
        return null;
    }

    boolean rename(String fileName, String newFileName) {
        Object o = getFileByName(fileName);
        if (null == o) {
            return false;
        } else {
            if (o instanceof Directory) {
                Directory oo = (Directory) o;
                oo.setName(newFileName);
                inodes.get(oo.getInodeAddress()).setPath(now_inode.getPath() + newFileName + "/");
                return true;
            } else {
                File oo = (File) o;
                oo.setName(newFileName);
                return true;
            }
        }
    }

    public static ArrayList<File> listFilesByDirectory(Directory directory) {
        ArrayList<File> fileList = new ArrayList<>();
        Set<Integer> dirInodes = directory.getTree().keySet();
        Iterator<Integer> iteratore = dirInodes.iterator();
        while (iteratore.hasNext()) {
            Object file = blocks.get(directory.getTree().get(iteratore.next()));
            if (file instanceof Directory real_file) {
                fileList.add(real_file);
            } else {
                File realFile = (File) file;
                fileList.add(realFile);
            }
        }
        return fileList;
    }

    public static String getFilePath(File file) {
        if (file instanceof Directory realFile) {
            return inodes.get(realFile.getInodeAddress()).getPath();
        } else {
            File realFile = (File) file;
            return inodes.get(realFile.getInodeAddress()).getPath();
        }
    }

    public Scanner getSc() {
        return sc;
    }

    public static SuperBlock getSb() {
        return sb;
    }

    public static ArrayList<Users> getUsers() {
        return users;
    }

    public static ArrayList<INode> getInodes() {
        return inodes;
    }

    public static ArrayList<Object> getBlocks() {
        return blocks;
    }

    public static String getName() {
        return name;
    }

    public static String getPassword() {
        return password;
    }

    public static INode getNow_inode() {
        return now_inode;
    }

    public static Object getNow_file() {
        return now_file;
    }
}
