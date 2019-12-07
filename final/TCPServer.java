import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Queue;
/*
* This is gonna be the balancer class
*/
class TCPServer {
    String loadPath;
    Queue<String> jobs;

    protected static final int SERVERPORT = 12345;
    private boolean running = false;
    ServerSocket ssocket;

    private TCPServerThread[] client;
    private TCPServerPool[] storage;

    public static void main(String args[]) {
        if (args.length == 1) {
            
        } else {
            System.err.println("Usage: java TCPServer <loadpath>");
        }
        // magic comes here..
    }

    TCPServer() {
        //implement
    }

    public int distributeInfo() {
        
    }
}