import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class TCPServer extends Thread {
    protected static final int CLIENT_PORT = 12345;
    protected static final int STORAGE_PORT = 23456;
    ServerSocket client_socket;
    ServerSocket storage_socket;

    private static final Scanner scanner = new Scanner(System.in);

    private boolean running;
    private int client_count = 0;
    private int storage_count = 0;
    private int request_count = 0;
    private int nstorage;

    private LinkedList<TCPClientHandler> client_handler = new LinkedList<TCPClientHandler>();
    private ArrayList<LinkedList<TCPStorageHandler>> storage_handler = new ArrayList<LinkedList<TCPStorageHandler>>();

    String name;
    String filepath;
    TreeMap<Integer, Integer> table;


    public static void main(String[] args) {
        if (args.length == 3){
            int numberofnodes = Integer.parseInt(args[1]);
            TCPServer instance = new TCPServer(args[0], numberofnodes, args[2]);
            instance.start();
        } else {
            System.err.println(
                "Usage: java TCPServer <name> <n_storage> <filepath>"
            );
            System.exit(1);
        }
        
    }
    public TCPServer(String name, int nstrg, String fp) {
        /** */
        this.name = name;
        this.nstorage = nstrg;
        this.filepath = fp;
        this.table = CSVHandle.getMapFromFile(filepath);

        for (int i = 0; i < nstorage; i++) {
            LinkedList<TCPStorageHandler> handler = new LinkedList<TCPStorageHandler>();
            storage_handler.add(handler);
        }
    }

    protected void client_broadcast(String msg, int id) {
        if (msg != null)
            for (TCPClientHandler h : client_handler) {
                if (h.getID() != id)
                    h.sendMessage2Client(msg);
            }
    }

    protected void send2ClientWithId(String msg, int id) {
        if (msg != null)
            for (TCPClientHandler h : client_handler) {
                if (h.getID() == id)
                    h.sendMessage2Client(msg);
            }
    }

    protected void storage_broadcast(String msg, int id) {
        if (msg != null)
            for (LinkedList<TCPStorageHandler> l : storage_handler) {
                for (TCPStorageHandler h : l) {
                    if (h.getID() != id)
                        h.sendMessage2Storage(msg);
                }
            }
    }

    protected void sendMessage2RangeStorage(String msg, int index) {
        /** 
         * 
         * index represents the index of a range. Every element
         * of storage_handler ArrayList contains a range of data
         * 
        */

        if (msg != null) {
            for (TCPStorageHandler h : storage_handler.get(index)) {
                h.sendMessage2Storage(msg);
            }
        }
    }

    protected void sendCommand(String command) {
        /** 
         * 
         * header ; idAccount
         * ---
         * 2-L ; 1039
         * 2-A ; 1110 ; 260 ; 579.80
         * 
         * 
        */
        int data_sz = table.size();
        String[] cmd_list = command.split(";");
        String[] header = cmd_list[0].split("-");

        if (header[1].equals("L")) {
            int idAccount = Integer.valueOf(cmd_list[1]);
            int index = (int) Math.floor((float) nstorage * idAccount / data_sz);
            sendMessage2RangeStorage(command, index);
        } else {
            String cmd_orig, cmd_dest;
            String orig_accountId = cmd_list[1];
            int index_orig = (int) Math.floor((float) nstorage * Integer.parseInt(orig_accountId) / data_sz);
            String dest_accountId = cmd_list[2];
            int index_dest = (int) Math.floor((float) nstorage * Integer.parseInt(dest_accountId) / data_sz);
            String transfer_ammount = cmd_list[3];
            List<String> l = Arrays.asList(
                header[0] +"-A",
                orig_accountId,
                transfer_ammount
            );

            cmd_orig = String.join(";", l);
            l = Arrays.asList(
                header[0] +"-A",
                dest_accountId,
                "-"+transfer_ammount
            );
            cmd_dest = String.join(";", l);
            sendMessage2RangeStorage(cmd_orig, index_orig);
            sendMessage2RangeStorage(cmd_dest, index_dest);
        }
    }

    protected void remove_client_handler(int id) {
        client_handler.removeIf(instance -> instance.getID() == id);
        System.out.println("[Info]: Client " + id + " has left.");
        client_count--;
    }

    protected void remove_storage_handler(int id) {
        for (LinkedList<TCPStorageHandler> l : storage_handler) {
            l.removeIf(instance -> instance.getID() == id);
        }

        System.out.println("[Info]: Storage "+ id +" is down.");
        storage_count--;
    }

    public void run() {
        running = true;
        try {
            storage_socket = new ServerSocket(STORAGE_PORT);
            System.out.println("Connecting with storage...");

            
            /**
             * 
             * Need to initialize some storages first.
             *  
            */

            int iter = 2 * nstorage;
            int init_target = 0;
            int init_storage_id = 0;

            ArrayList<TreeMap<Integer, Integer>> splitted_table = CSVHandle.splitMap(table, nstorage);

            while (iter-- > 0) {
                Socket storage = storage_socket.accept();
                TCPStorageHandler instance = new TCPStorageHandler(
                    storage,
                    this, 
                    splitted_table.get(init_target), 
                    init_storage_id++
                );
                Thread t = new Thread(instance);
                t.start();
                storage_handler.get(init_target).add(instance);
                init_target = (init_target + 1) % (nstorage); // Just fow now...
                storage_count++;
                System.out.println("[Info]: "+ storage_count +" storages connected.");
            }

            client_socket = new ServerSocket(CLIENT_PORT);
            
            Runnable client_seeker = () -> {
                System.out.println("Looking for clients...");
                try {
                    int id = 0;
                    while (running) {
                        Socket client = client_socket.accept();
                        TCPClientHandler instance = new TCPClientHandler(client, this, id++);
                        Thread t = new Thread(instance);
                        t.start();
                        client_handler.add(instance);
                        client_count++;
                        System.out.println("[Info]: "+ client_count +" clients connected.");
                    }
                } catch (IOException ioe) {
                    System.err.println("Error on TCPServer.client_seeker");
                    System.err.println(ioe);
                }
            };
            Thread client_seeker_thread = new Thread(client_seeker);
            client_seeker_thread.start();
            /*
            Runnable storage_seeker = () -> {
                System.out.println("Looking for storage...");
                try {
                    int target = 0;
                    int storage_id = 0;
                    while (running) {
                        Socket storage = storage_socket.accept();
                        TCPStorageHandler instance = new TCPStorageHandler(
                            storage,
                            this,
                            storage_id++
                        );
                        Thread t = new Thread(instance);
                        t.start();
                        storage_handler.get(target).add(instance);
                        target = (target + 1) % (nstorage); // Just fow now...
                        storage_count++;
                        System.out.println("[Info]: " + storage_count + " storages connected.");
                    }
                } catch (IOException ioe) {
                    System.err.println("Error on TCPServer.storage_seeker");
                    System.err.println(ioe);
                }
            };
            Thread storage_seeker_thread = new Thread(storage_seeker);
            storage_seeker_thread.start();
            */

            while (running) {
                String response = scanner.nextLine();
                response = name + ": " + response;
                client_broadcast(response, -1);
            }
            

        } catch (IOException ioe) {
            System.err.println("Error on TCPServer. First try");
            System.err.println(ioe);
        }
    }
}