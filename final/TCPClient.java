
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TCPClient extends Thread {
    public String server_ip;
    public static final int SERVERPORT = 12345;
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
         * L-1992 ; 1039
         * A-1012 ; 1110 ; 260 ; 579.80
         * Returns 0 if success and returns 1 if fails.
         */
        String[] cmd_list = cmd.split(";");

        if (cmd_list.length > 4 || cmd_list.length < 2) {
            System.err.println("Error: bad parameters.");
            return 1;
        } else {
            String[] operation = cmd_list[1].split("-");
            if (operation[0] != "A" || operation[0] != "B" || operation.length != 2) {
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
            Socket s = new Socket(serverAddr, SERVERPORT);
            
            try {
                out = new PrintWriter(
                    new BufferedReader(
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
                    // TODO
                    String cmd = scanner.nextLine(); // command to make an operation.
                    int status = operate(cmd);

                    if (status == 1) {
                        System.out.println("Check your request and try again.");
                    }
                }
            } catch (EOFException eofe) {
                System.err.println("Task-Queue empty.");
                System.err.println("Proceding to close conection.");
            } catch (Exception e) {
                System.err.println("Error on TCPClient:");
                e.printStackTrace();
                System.err.println("Closing conection.");
            }
            finally {
                s.close();
            }
        } catch (Exception e) {
            System.err.println("Error on TCPClient:");
            System.err.println(e);
        }
    }

}