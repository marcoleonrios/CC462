
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.DataInputStream;

public class TCPClient extends Thread {
    public String server_ip;
    public static final int SERVERPORT = 12345;
    private boolean running = false;
    protected OutputStream out;
    protected DataInputStream in;
    String name;
    
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

    public void run(){
        running = true;
        
        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);
            System.out.println("Connecting to " + server_ip);
            Socket s = new Socket(serverAddr, SERVERPORT);
            
            try {
                out = s.getOutputStream();
                in = new DataInputStream(s.getInputStream());

                while (running) {
                    // TODO

                    while (running) {
                        String msg
                    }
                }
            } catch (EOFException eofe) {
                System.out.println("Task-Queue empty.");
                System.out.println("Proceding to close conection.");
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