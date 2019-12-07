import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

public class TCPServerPool implements Runnable {
    private Socket client;
    private int clientID;
    private TCPServer server;
    private OutputStream out;
    private DataInputStream in;
    TCPServerThread[] friend;
    private boolean running = false;

    Queue<String> jobs;

    public TCPServerThread() {

    }

    public void run() {
        running = true;

        try {
            try {
                out = client.getOutputStream();
                in = new DataInputStream(client.getInputStream());

                while (running) {
                    if (jobs.isEmpty()) {
                        System.out.println("Queue empty!");
                    }

                    
                }
            }
        }
    }
}