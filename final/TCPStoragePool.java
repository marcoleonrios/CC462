import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
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
    private Socket server_socket; // handles Server-Pool communication.
    private LinkedList<Socket> storageconnections; // To communicate with storage
    private int poolID;
    private TCPServer server;
    private PrintWriter server_out;
    private BufferedReader server_in;
    private boolean running = false;
    private boolean fileisblocked = false;

    private Runnable looker;

    private Queue<String> jobs;

    public TCPStoragePool(TCPServer srvr, int id) {
        server = srvr;
        poolID = id;
    }

    public int getID() {
        return poolID;
    }

    public void run() {
        running = true;

        try {
            try {
                server_out = new PrintWriter(
                    new BufferedWriter(
                        new OutputStreamWriter(server_socket.getOutputStream())
                    ),
                    true
                );
                System.out.println("Printer. Ready");

                server_in = new BufferedReader(
                    new InputStreamReader(server_socket.getInputStream())
                );
                System.out.println("Reader. Ready.");

                /** Look up for storage connections (Thread) */

                Thread t = new Thread(looker);
                t.start();

                while (running) {
                    /** Handle client requests */
                    server_in.readLine(); // Reading a Request

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