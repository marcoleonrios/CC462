import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import parcial.TCPClient.OnMessageReceived;

public class TCPServerThread extends Thread {
    private Socket client;
    private int clientID; /* be usefull... maybe */
    private TCPServer server;
    private OutputStream out;
    private InputStream in;
    private OnMessageReceived msgListener;
    private String msg;
    TCPServerThread[] friend;
    private boolean running = false;

    public TCPServerThread(Socket clnt, TCPServer srvr, int id, TCPServerThread[] frnd) {
        client = clnt;
        server = srvr;
        clientID = id;
        friend = frnd;
    }

    @Override
    public void run() {
        running = true;

        try {
            try {
                out = client.getOutputStream(); 
                msgListener = server.getMessageListener();
                in = new InputStream(client.getInputStream());

                while (running) {
                    String path = "/someplace";
                    /*
                    
                    someplace should be retrieved from the image queue.
                    
                    */ 
                    BufferedImage image = ImageIO.read(new File(path));
                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", byteOut);
                    byte[] sz = ByteBuffer.allocate(4).putInt(byteOut.size()).array();
                    out.write(sz);
                    out.write(byteOut.toByteArray());
                    out.flush();

                    /*
                    Now server expects to recieve the processed image.
                    */
                    byte[] insz = new byte[4];
                    in.read(insz);
                    int size = ByteBuffer.wrap(insz).asIntBuffer().get();
                    byte[] imageReceived = new byte[size];
                    
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageReceived));
                    
                    String output_path = path + "id";

                    /*
                    ID should be different for every different kernel
                    */

                    ImageIO.write(img, "jpg", output_path);

                    
                    if (msg != null && msgListener != null) {
                        msgListener.messageReceived(msg);
                    }

                    msg = null;
                }

                
            } catch (Exception e) {
                System.err.println("Error on TCPServerThread:");
                System.err.println(e);
            } finally {
                client.close();
            }
        } catch (Exception e) {
            System.err.println("Error on TCPServerThread:");
            System.err.println(e);
        }
    }

    public void sendMessage(String msg) {
        if (out != null && !out.checkError()) {
            out.println(msg);
            out.flush();
        }
    }

    public void stopServer() {
        running = false;
    }

}