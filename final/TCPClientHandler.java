import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class TCPClientHandler implements Runnable {
    private Socket client;
    private int clientID;
    private TCPServer server;

    private PrintWriter out;
    private BufferedReader in;

    private Runnable read_handler;

    private boolean running;

    public TCPClientHandler(Socket clnt, TCPServer srvr, int id) {
        this.client = clnt;
        this.server = srvr;
        this.clientID = id;
    }


    public int getID() {
        return clientID;
    }

    public void sendMessage2Client(String msg) {
        try {
            if (out != null) {
                out.println(msg);
                out.flush();
            } else {
                System.err.println("out is null...");
            }
        } catch (Exception e) {
            System.err.println("Error on TCPClientHandler.sendMessage2Client");
            System.err.println(e);
        }
    }

    public void run() {
        running = true;
        try {
            out = new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())
                ),
                true
            );
            System.out.println("[CH"+ clientID +"]:Printer. Ready.");

            in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );
            System.out.println("[CH"+ clientID +"]:Reader. Ready.");

            read_handler = () -> {
                try {
                    while (running) {
                        String msg;
                        if ((msg = in.readLine()) != null) {
                            if (msg.equals("Disconnecting")) {
                                server.remove_client_handler(clientID);
                                running = false;
                            } else {
                                //System.out.println(msg);
                                msg = clientID +"-"+ msg;
                                server.sendCommand(msg);
                            }
                        } else {
                            server.remove_client_handler(clientID);
                            running = false;
                        }
                    }
                } catch (SocketException se) {
                    System.err.println("Connection was reseted");
                } catch (IOException ioe) {
                    System.err.println("Error on TCPClientHandler.read_handler");
                    System.err.println(ioe);
                } catch (Exception e) {
                    System.err.println("Error on TCPClientHandler.read_handler:");
                    server.remove_client_handler(clientID);
                    System.err.println(e);
                }
            };
            
            Thread read_handler_thread = new Thread(read_handler);
            read_handler_thread.start();
            
        } catch(IOException ioe) {
            System.err.println("Error on TCPClientHandler");
            System.err.println(ioe);
        }
    }
}