
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {
    public String server_ip;
    public static final int CLIENT_SERVERPORT = 12345;
    private boolean running = false;
    protected PrintWriter out;
    protected BufferedReader in;

    String name;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String args[]) {
        if (args.length == 3) {
            int THREAD_NUMBER = Integer.valueOf(args[2]);

            for (int i = 0; i < THREAD_NUMBER; i++) {
                TCPClient t = new TCPClient(args[0], args[1]);
                t.start();
            }
        } else {
            System.err.println("Usage: TCPClient <ip> <name> <threads>");
        }
    }

    public TCPClient(String ip, String name) {
        server_ip = ip;
        this.name = name;
    }
    
    public void stopClient() {
        running = false;
    }

    protected int operate(String cmd) {
        /**
         * Comunicates operations to the balancer
         * Examples:
         * L ; 934
         * A ; 110 ; 260 ; 579.80
         * Returns 0 if success and returns 1 if fails.
         */
        String[] cmd_list = cmd.split(";");

        if (cmd_list.length != 4 || cmd_list.length != 2) {
            System.err.println("Error: bad parameters.");
            return 1;
        } else {
            if (cmd_list[0] != "A" || cmd_list[0] != "L") {
                System.err.println("Error: bad operation.");
                return 1;
            } else {
                if (out != null && !out.checkError()) {
                    out.println(cmd);
                    out.flush();
                }
            }
        }

        return 0;
    }

    public void run(){
        running = true;
        
        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);
            System.out.println("Connecting to " + server_ip);
            Socket s = new Socket(serverAddr, CLIENT_SERVERPORT);
            
            try {
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

                while (running) {
                    System.out.println("Insert your command: ");
                    String cmd = scanner.nextLine(); // command to make an operation.
                    int status = operate(cmd);

                    if (status == 1) {
                        System.out.println("Check your request and try again.");
                    } else {
                        String response = in.readLine();
                        System.out.println("[Server]: " + response);
                    }
                }
                
                running = false;
                System.out.println("Bye!")
            } catch (EOFException eofe) {
                System.err.println("Task-Queue empty.");
                System.err.println("Proceding to close conection.");
            } catch (Exception e) {
                System.err.println("Error on TCPClient:");
                e.printStackTrace();
                System.err.println("Closing conection.");
            }
            finally {
                running = false;
                s.close();
            }
        } catch (Exception e) {
            System.err.println("Error on TCPClient:");
            System.err.println(e);
        }
    }

}