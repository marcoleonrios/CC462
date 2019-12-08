import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/*
* This is gonna be the balancer class
*/
public class TCPServer {
    String loadPath;
    Queue<String> jobs;

    protected static final int CLIENT_SERVERPORT = 12345;
    protected static final int STORAGE_SERVERPORT = 23456;
    private boolean running = false;

    ServerSocket client_ssocket;
    ServerSocket pool_ssocket;

    private int count = 0; // client counter 
    private int nstorage; // number of storage nodes
    String loadpath;
    private TCPServerThread[] client;
    private TCPStoragePool[] storage;

    public static void main(String[] args) {
        if (args.length == 3) {
            
        } else {
            System.err.println(
                "Usage: java TCPServer <loadpath> <nclients> <nstorage>"
            );
        }
        // magic comes here..
    }

    TCPServer(String ldpth, int nstrg) {
        nstorage = nstrg;
        loadPath = ldpth;
    }

    public int distributeInfo() {
        /**
         * Distributes the table among all nodes
         * Status codes:
         *   0. Success
         *   1. Not success. File not found
         */

        int chunk = 1000 / nstorage;
        try {
            Scanner fileScanner = new Scanner(new File(loadPath));
            

		} catch (FileNotFoundException e) {
            System.err.println("File not found.");
            return 1;
        }
        
        return 0;
    }

    public String send2Storage(String command) {
        
        return response;
    }

    public void run() {
        running = true;

        try {
            try {
                /** look up for pool */
                pool_ssocket = new ServerSocket(STORAGE_SERVERPORT);

                for (int i = 0; i < nstorage; i++) {
                    Socket pool = pool_ssocket.accept();
                    storage[i] = new TCPStoragePool(this, i);
                    Thread t = new Thread(storage[i]);
                    t.start();
                }

                distributeInfo();
                
                client_ssocket = new ServerSocket(CLIENT_SERVERPORT);
                
                System.out.println("Ready to accept client connections.");

                while (running) {
                    Socket clnt = client_ssocket.accept();
                    client[count] = new TCPServerThread(clnt, this, count, storage);
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