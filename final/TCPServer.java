import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.lang.Math;

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
    private int data_sz = 1000;
    String filepath;
    private TCPServerThread[] client;
    private TCPStoragePool[] storage;

    public static void main(String[] args) {
        if (args.length == 3) {

        } else {
            System.err.println("Usage: java TCPServer <loadpath> <nclients> <nstorage>");
        }
        // magic comes here..
    }

    TCPServer(String ldpth, int nstrg) {
        nstorage = nstrg;
        filepath = ldpth;
    }

    public int distributeInfo() throws IOException {
        /**
         * Distributes the table among all nodes
         * Status codes:
         *   0. Success
         *   1. Not success. File not found
         */

        int chunk = data_sz / nstorage + 1; // Look up! Maybe wrong... can't think.
       
        Map<String, String> total_table = CSVHandle.getMapFromFile(filepath);
        
        int counter = 0;
        int storage_range = 0;
        Map<String, String> subTable = new HashMap<String, String>();

        for (Map.Entry<String, String> e : total_table.entrySet()) {
            counter++;
            subTable.put(e.getKey(), e.getValue());

            if (counter == chunk) {
                storage[storage_range].pushTable(subTable);
                counter = 0;
                storage_range++;
            }
        }
        
        return 0;
    }

    public String operate(String cmd, int poolId) {
        String response = storage[poolId].operate(cmd);
    }

    public String processCommand(String command) {
        /** 
         * Se if command is posible
         * Examples:
         * L ; 934
         * A ; 110 ; 260 ; 579.80
         * Returns 0 if success and returns 1 if fails.
        */       
        String[] cmd_list = command.split(";");

        if (cmd_list[0] == "L") {
            int objectAccountID = Integer.valueOf(cmd_list[1].trim());

            int range = (int) Math.ceil(objectAccountID * nstorage / data_sz);

            return operate(command, range);
        }

        int originID = Integer.valueOf(cmd_list[1].trim());
        int destID = Integer.valueOf(cmd_list[2].trim());
        int poolIDorigin = (int) Math.ceil(originID * nstorage / data_sz);
        String readcommand_str = "L ; " + cmd_list[1].trim();
        String actual_amount_str = operate(readcommand_str, poolIDorigin);
        
        if ()
        
        return "Success\n";
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
                // TODO: handle exception
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