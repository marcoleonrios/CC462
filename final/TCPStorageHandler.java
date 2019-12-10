import java.io.PrintWriter;

public class TCPStorageHandler implements Runnable {
    private Socket storage;
    private int storageID;
    private TCPServer server;

    private PrintWriter out;
    private BufferedReader in;

    private Runnable read_handler;

    private boolean running;

    public TCPStorage() {

    }

    public int getID() {
        return storageID;
    }

    public void run() {
        
    }
}