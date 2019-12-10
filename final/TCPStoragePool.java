import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TCPStoragePool implements Runnable {
    /**
     * 
     * 
     * This class handles Storage
     * Runs on server machine
     * 
     * 
    */
    protected static final int STORAGE_SERVERPORT = 23456;
    private ServerSocket storage_socket; // handles Storage-Pool communication
    private LinkedList<Socket> storageconnections; // To communicate with storage
    private int poolID;
    private TCPServer server;
    private PrintWriter server_out;
    private BufferedReader server_in;
    private boolean running = false;
    private boolean fileisblocked = false;
    private Queue<String> jobs;
    private Map<String, String> table;

    public TCPStoragePool(TCPServer srvr, int id, ServerSocket ssocket) {
        server = srvr;
        poolID = id;
        storage_socket = ssocket;
    }

    public int getID() {
        return poolID;
    }

    public void pushTable(Map<String, String> subTable) {
        table = subTable;
    }
    public void sendMap2Storages() throws IOException {
        for (Socket s : storageconnections) {
            final OutputStream out_tmp = s.getOutputStream();
            final ObjectOutputStream oos = new  ObjectOutputStream(out_tmp);
            oos.writeObject(table);
            oos.close();
            out_tmp.close();
        }
    }

    public String operate(cmd) {
        for (Socket s : storageconnections) {

        }
    }

    public void run() {
        running = true;

        try {
            try {
                /** Look up for storage connections (Thread) */
                Runnable runner = () -> {
                    Socket s;
                    try {
                        while (running) {
                            s = storage_socket.accept();
                            storageconnections.add(s);
                        }
                    } catch (IOException e) {
                        System.err.println("[Runner]: Error!");
                        e.printStackTrace();
                    }
                };

                Thread t = new Thread(runner);
                t.start();

                while (running) {
                    /** Handle client requests */

                    /** Process the request... */

                    String response = "";
                    server_out.write(response); // Writing response.
                }
            } catch (IOException e) {

            }
        } catch (Exception e) {

        }
    }

	
}