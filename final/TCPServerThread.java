import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPServerThread implements Runnable {
    /**
     * 
     * 
     * This class handles clients!!
     * Runs on server machine
     * 
     * 
     */
    private Socket client_socket; // Connects with the server
    private TCPServer server;
    private PrintWriter out;
    private BufferedReader in;
    private int id;
    private TCPStoragePool[] storage;

    private boolean running = false;
    
    public static void main(String[] args) {

    }

    public TCPServerThread(Socket clnt, TCPServer srvr, int clientID, TCPStoragePool[] strg) {
        client_socket = clnt;
        server = srvr;
        id = clientID;
        storage = strg;
    }

    public int getID() {
        return id;
    }

    public void run() {
        running = true;

        try {
            try {
                out = new PrintWriter(
                    new BufferedWriter(
                        new OutputStreamWriter(client_socket.getOutputStream())
                    ),
                    true
                );
                System.out.println("Printer. Ready");

                in = new BufferedReader(
                    new InputStreamReader(client_socket.getInputStream())
                );

                while (running) {
                    String command = in.readLine();
                    System.out.println("Request: " + command);
                    String response = server.send2Storage(command);
                    out.write(response);
                }


            } catch(Exception e) {

            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

}