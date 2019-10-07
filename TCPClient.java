package parcial;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TCPClient {
    private String msg;
    public String server_ip;
    public static final int SERVERPORT = 12345;
    private OnMessageReceived msgListener = null;
    private boolean running = false;
    protected OutputStream out;
    protected InputStream in;
    
    public TCPClient(String ip, OnMessageReceived lstnr) {
        server_ip = ip;
        msgListener = lstnr;
    }

    public void sendImage(int img[][]) {
        
    }
    
    public void sendMessage(String msg) {
        if (out != null && !out.checkError()) {
            out.println(msg);
            out.flush();
        }
    }
    
    public void stopClient() {
        running = false;
    }
    
    @Override
    public void run(){
        running = true;
        
        try {
            InetAddress serverAddr = InetAddress.getByName(server_ip);
            System.out.println("Connecting to " + server_ip);
            Socket s = new Socket(serverAddr, SERVERPORT);
            
            try {
                out = s.getOutputStream();
                System.out.println(("Done"));
                in = s.getInputStream();

                while (running) {

                    byte[] insz = new byte[4];
                    in.read(insz);
                    int size = ByteBuffer.wrap(insz).asIntBuffer().get();
                    byte[] imgReceived = new byte[size];
                    in.read(imgReceived);
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgReceived));
                    int[][] matrix = LoadImage.getMatrixOfImage(img);

                    /**
                     *
                     * Here comes the convolution
                     * 
                     * */

                    img = LoadImage.getImagefromMatrix(matrix);

                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", byteOut);
                    byte[] sz = ByteBuffer.allocate(4).putInt(byteOut.size()).array();
                    out.write(sz);
                    out.write(byteOut.toByteArray());
                    out.flush();

                    msg = in.readLine();
                    
                    if (msg != null && msgListener != null) {
                        msgListener.messageReceived(msg);
                    }
                    msg = null;
                }
            } catch (Exception e) {
                System.err.println("Error on TCPClient:");
                System.err.println(e);
            }
            finally {
                s.close();
            }
        } catch (Exception e) {
            System.err.println("Error on TCPClient:");
            System.err.println(e);
        }
    }

    public interface OnMessageReceived {
        public void messageReceived(String msg);
    }
}