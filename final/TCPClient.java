import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class TCPClient extends Thread {
    private String server_ip;
    protected static final int SERVERPORT = 12345;
    protected String name;
    
    private PrintWriter out;
    private BufferedReader in;

    private boolean running = false;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 2) {
            TCPClient instance = new TCPClient(args[0], args[1]);
            instance.start();
        } else {
            System.err.println("Usage: java TCPClient <server_ip> <name>");
            System.exit(1);
        }
    }

    public TCPClient(String serverip, String name) {
        this.server_ip = serverip;
        this.name = name;
    }

    public void stopClient() {
        running = false;
    }

    private String generateRequest() {
        Random r = new Random();
        String request;
        if (r.nextFloat() < 0.8) {
            request = "L;";
            int account = (int)(r.nextFloat() * 900 + 100);
            request = request + account;
        } else {
            request = "A;";
            int orig_account = (int)(r.nextFloat() * 900 + 100);
            request = request + orig_account +";";
            int dest_account = (int) (r.nextFloat() * 900 + 100);
            request = request + dest_account +";";
            int transfer_ammount = (int)(r.nextFloat() * 90 + 10);
            request = request + transfer_ammount +";";
        }

        return request;
    }

    public void run() {
        running = true;
        
        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);
            System.out.println("Connecting to " + server_ip);
            Socket s = new Socket(serverAddr, SERVERPORT);
            
            try {
                System.out.println("Press Ctrl+D to close connection.");
                out = new PrintWriter(
                    new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream())
                    ),
                    true
                );
                System.out.println("Printer. Ready");

                in = new BufferedReader(
                    new InputStreamReader(s.getInputStream())
                );
                System.out.println("Writer. Ready.");
                /*
                Runnable read_handler = () -> { // This runnable handles reading
                    try {
                        while (running) {
                            String msg;
                            if ((msg = in.readLine()) != null)
                                System.out.println(msg);
                        }
                    } catch(IOException ioe) {
                        System.err.println("Error on TCPClient.read_handler");
                        System.err.println(ioe);
                        System.exit(1);
                    } catch (Exception e) {
                        System.err.println("Error on TCPClient.read_handler");
                        System.err.println(e);
                    }
                };
                Thread read_handler_thread = new Thread(read_handler);
                read_handler_thread.start();
                */
                Runnable write_handler = () -> { // This runnable handles
                    while (running) {
                        try {
                            //String response = scanner.nextLine();
                            String response = generateRequest();
                            System.out.println(response);
                            out.println(response); // command
                            out.flush();
                        } catch (NoSuchElementException nsee) {
                            System.err.println("Closing connection");
                            scanner.close();
                            stopClient();
                        }
                    }
                    out.println("Disconnecting");
                    System.exit(0);
                };
                Thread write_handler_thread = new Thread(write_handler);
                write_handler_thread.start();
                
                //running = false;
            } catch (EOFException eofe) {
                System.err.println("Proceding to close conection.");
            }
        } catch (UnknownHostException uhe) {
            System.err.println("Host unknown.");
        } catch (ConnectException ce) {
            System.err.println("Connection refused.");
            System.err.println("Check if the server is running.");
        } catch (IOException ioe) {
            System.err.println("Error on TCPClient.");
            System.err.println(ioe);
        }
    }
}