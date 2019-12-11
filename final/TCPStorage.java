import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

public class TCPStorage extends Thread {
    private String server_ip;
    protected static final int SERVERPORT = 23456;
    private String name;

    private PrintWriter out;
    private ObjectOutputStream oos;
    private BufferedReader in;
    private ObjectInputStream ois;

    private boolean running = false;

    private static Scanner scanner = new Scanner(System.in);

    TreeMap<Integer, Integer> table;


    public static void main(String[] args) {
        if (args.length == 2) {
            TCPStorage instance = new TCPStorage(args[0], args[1]);
            instance.start();
        } else {
            System.err.println("Usage: java TCPStorage <server_ip> <name>");
            System.exit(1);
        }
    }

    public TCPStorage(String serverip, String name) {
        this.server_ip = serverip;
        this.name = name;
    }

    public void run() {
        running = true;

        try {
            InetAddress serverAddr;
            serverAddr = InetAddress.getByName(server_ip);
            System.out.println("Connecting to "+ server_ip);
            Socket s = new Socket(serverAddr, SERVERPORT);

            System.out.println("Press Ctrl+D to close connection.");
            out = new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream())
                ),
                true
            );
            oos = new ObjectOutputStream(s.getOutputStream());
            System.out.println("Printer. Ready");

            in = new BufferedReader(
                new InputStreamReader(s.getInputStream())
            );
            ois = new ObjectInputStream(s.getInputStream());
            System.out.println("Writer. Ready.");

            table = (TreeMap<Integer, Integer>) ois.readObject(); // don't be afraid...

            Runnable read_handler = () -> {
                try {
                    while (running) {
                        String msg;
                        if ((msg = in.readLine()) != null) {
                            System.out.println(msg);
                            String[] cmd_list = msg.split(";");
                            String[] header = cmd_list[0].split("-");
                            String response;

                            if (header[1].equals("L")) {
                                int accountId = Integer.parseInt(cmd_list[1]);
                                response = ""+ table.get(accountId);
                            } else {
                                int orig_accountId = Integer.parseInt(cmd_list[1]);
                                int transfer_ammount = Integer.parseInt(cmd_list[2]);
                                int actual_orig_ammount = table.get(orig_accountId);
                                table.put(orig_accountId, actual_orig_ammount - transfer_ammount);
                                response = "Success.";
                            }

                            response = header[0] +";"+response;
                            out.println(response);
                        }
                    }
                } catch (IOException ioe) {
                    System.err.println("Error on read_handler");
                    System.err.println(ioe);
                }
            };
            Thread read_handler_thread = new Thread(read_handler);
            read_handler_thread.start();
/*
            Runnable write_handler = () -> {
                while (running) {
                    try {
                        String response = scanner.nextLine();
                        response = name +": "+ response;
                        out.println(response);
                        out.flush();
                    } catch(NoSuchElementException nsee) {
                        System.err.println("Closing connection.");
                        scanner.close();
                        running = false;
                    }
                }
                out.println("Disconnecting");
                System.exit(0);
            };

            Thread write_handler_thread = new Thread(write_handler);
            write_handler_thread.start();
*/
        } catch (UnknownHostException uhe) {
            System.err.println("Host unknown.");
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Error.");
            System.err.println(ioe);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error.");
            System.err.println(cnfe);
        }
        
    }
}