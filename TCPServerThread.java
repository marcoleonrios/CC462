import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Queue;

public class TCPServerThread implements Runnable {
    private Socket client;
    private int clientID; /* be usefull... maybe */
    private TCPServer server;
    private OutputStream out;
    private DataInputStream in;
    TCPServerThread[] friend;
    private boolean running = false;

    Queue<String> jobs;

    public TCPServerThread(Socket clnt, TCPServer srvr, int id, TCPServerThread[] frnd, Queue<String> jobs) {
        client = clnt;
        server = srvr;
        clientID = id;
        friend = frnd;
        this.jobs = jobs;
    }

    //@Override
    public void run() {
        running = true;
        
        try {
            try {
                out = client.getOutputStream(); 
                in = new DataInputStream(client.getInputStream());
               
                
                while (running) {
                    
                    /*
                    
                    someplace should be retrieved from the image queue.
                    
                    */ 
                    if(jobs.isEmpty()) {
                        System.out.println("TAREA TERMINADA");
                        running = false;
                        break;
                    }

                    String path = jobs.remove();

                    BufferedImage image = ImageIO.read(new File(server.loadPath + path));
                    
                    if (image != null) {
                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", byteOut);
                        byte[] sz = ByteBuffer.allocate(4).putInt(byteOut.size()).array();
                        out.write(sz);
                        out.write(byteOut.toByteArray());
                        out.flush();

                        System.out.println("En teoria se envió la imagen : " + path);

                        /*
                        Now server expects to recieve the processed image.
                        */
                        for (int i = 0; i <= 3; i++) {
                            byte[] insz = new byte[4];
                            in.readFully(insz);
                            int size = ByteBuffer.wrap(insz).asIntBuffer().get();
                            byte[] imageReceived = new byte[size];
                            in.readFully(imageReceived);
                            
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageReceived));
                            
                            String output_path = server.savePath + path.substring(0, path.length() - 4) + "client" + clientID + "conv" + i + ".jpg";
                            File outputfile = new File(output_path);

                            /*
                            ID should be different for every different kernel
                            */

                            ImageIO.write(img, "jpg", outputfile);
                        }
                    } else {
                        System.err.println("Some error loading image has ocurred.");
                    }
                }

                
            } catch (Exception e) {
                System.err.println("Error on TCPServerThread:");
                e.printStackTrace();
                //System.err.println(e.getStackTrace());
            } finally {
                client.close();
            }
        } catch (Exception e) {
            System.err.println("Error on TCPServerThread:");
            System.err.println(e);
        }
    }


    public void stopServer() {
        running = false;
    }

}