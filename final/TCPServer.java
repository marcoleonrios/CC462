import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
/*
* This is gonna be the balancer class
*/
public class TCPServer {
    String loadPath;
    Queue<String> jobs;

    protected static final int SERVERPORT = 12345;
    private boolean running = false;
    ServerSocket ssocket;
    private int count = 0;

    private TCPServerThread[] client;
    private TCPServerPool[] storage;

    public static void main(String args[]) {
        if (args.length == 3) {
            
        } else {
            System.err.println(
                "Usage: java TCPServer <loadpath> <nclients> <nstorage>"
            );
        }
        // magic comes here..
    }

    TCPServer(int nclient, int nstorage) {
        client = new TCPServerThread[nclient];
        storage = new TCPServerPool[nstorage];
    }

    public int distributeInfo() {
        /**
         * Distributes the table among all nodes
         */
        return 0;
    }

    public void run() {
        running = true;

        try {
            try {
                ssocket = new ServerSocket(SERVERPORT);
                System.out.println("Waiting for node connections...");

                int node = 0;

                while (node != storage.length) { // Connecting nodes
                    Socket node_socket = ssocket.accept();
                    storage[node] = new TCPServerPool(/* Params */, node_socket);
                    Thread t = new Thread(storage[node]);
                    node++;
                    t.start();
                    System.out.println("Nodes connected: " + node);
                }
                
                System.out.println("Ready to accept client connections.");

                while (running) {
                    Socker clnt = ssocket.accept();
                    client[count] = new TCPServerThread(/** params */, clnt);
                    count++;
                    Thread t = new Thread(client[count]);
                    t.start();
                    System.out.println("Clients connected: " + count);
                }
                running = false;
            } catch (Exception e) {
                //TODO: handle exception
                // Use exception to close socket connections on
                // TCPServerThread Array. Perhabs it is more useful to have
                // a LinkedList.
            }
        } catch(Exception e) {
            System.err.println("Error on TCPServer:");
            System.err.println(e);
        }
    }

    public boolean isRunning() {
        return running;
    }
}