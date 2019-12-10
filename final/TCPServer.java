import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class TCPServer extends Thread {
    protected static final int CLIENT_PORT = 12345;
    protected static final int STORAGE_PORT = 23456;
    ServerSocket client_socket;
    ServerSocket storage_socket;

    private static final Scanner scanner = new Scanner(System.in);

    private boolean running;
    private int count = 0;

    private LinkedList<TCPClientHandler> client_handler = new LinkedList<TCPClientHandler>();
    //private LinkedList<TCPStorageHandler> storage_handler = new LinkedList<TCPStorageHandler>();

    String name;


    public static void main(String[] args) {
        TCPServer instance = new TCPServer("myServ");
        instance.start();
    }
    public TCPServer(String name) {
        /** */
        this.name = name;
    }

    protected void client_broadcast(String msg, int id) {
        if (msg != null )
            for (TCPClientHandler h : client_handler) {
                if (h.getID() != id)
                    h.sendMessage2Client(msg);
            }
    }

    protected void remove_client_handler(int id) {
        client_handler.removeIf(instance -> instance.getID() == id);
        System.out.println("Client " + id + " has gone.");
        count--;
    }

    public void run() {
        running = true;
        try {
            client_socket = new ServerSocket(CLIENT_PORT);
            storage_socket = new ServerSocket(STORAGE_PORT);
            System.out.println("Connecting...");
            
            Runnable client_seeker = () -> {
                try {
                    int id = 0;
                    while (running) {
                        Socket client = client_socket.accept();
                        TCPClientHandler instance = new TCPClientHandler(client, this, id++);
                        Thread t = new Thread(instance);
                        t.start();
                        client_handler.add(instance);
                        count++;
                        System.out.println("[Info]: " + count + " clients connected.");
                    }
                } catch (IOException ioe) {
                    System.err.println("Error on TCPServer");
                    System.err.println(ioe);
                }
            };
            Thread client_seeker_thread = new Thread(client_seeker);
            client_seeker_thread.start();

            /*
            Runnable storage_seeker = () -> {
                try {
                    while (running) {
                        Socket client = client_socket.accept();
                        TCPStorageHandler instance = new TCPStorageHandler(client, this, count);
                        Thread t = new Thread(instance);
                        t.start();
                        handler.add(instance);
                        count++;
                        System.out.println("[Info]: " + count + " storages connected.");
                    }
                } catch (IOException ioe) {

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