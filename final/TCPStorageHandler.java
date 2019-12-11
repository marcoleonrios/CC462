import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.TreeMap;

public class TCPStorageHandler implements Runnable {
    private Socket storage;
    private int storageID;
    private TCPServer server;

    private PrintWriter out;
    private ObjectOutputStream oos;
    private BufferedReader in;
    private ObjectInputStream ois;

    private Runnable read_handler;

    private boolean running;

    TreeMap<Integer, Integer> table;

    public TCPStorageHandler(Socket strg, TCPServer srvr, TreeMap<Integer, Integer> tbl, int id) {
        this.storage = strg;
        this.server = srvr;
        this.table = tbl;
        this.storageID = id;
    }

    public int getID() {
        return storageID;
    }

    public int getTableSum() {
        int sum;
        sum = table.values().stream().reduce(0, Integer::sum);

        return sum;
    }

    protected void sendMessage2Storage(String msg) {
        try {
            if (out != null) {
                out.println(msg);
                out.flush();
            } else {
                System.err.println("out is null...");
            }
        } catch (Exception e) {
            System.err.println("Error on TCPStorageHandler.sendMessage2Storage");
            System.err.println(e);
        }
    }

    public void run() {
        System.out.println("TCPStorage running...");
        running = true;
        try {
            out = new PrintWriter(
                new BufferedWriter(
                    new OutputStreamWriter(storage.getOutputStream())
                ),
                true
            );

            oos = new ObjectOutputStream(storage.getOutputStream());

            System.out.println("[CS"+ storageID +"]:Printer. Ready.");

            in = new BufferedReader(
                new InputStreamReader(storage.getInputStream())
            );
            ois = new ObjectInputStream(storage.getInputStream());

            System.out.println("[CS"+ storageID +"]:Reader. Ready.");

            oos.writeObject(table);
            oos.flush();

            read_handler = () -> {
                try {
                    while (running) {
                        String msg;
                        if ((msg = in.readLine()) != null) {
                            if (msg.equals("Disconnecting")) {
                                server.remove_storage_handler(storageID);
                                running = false;
                            } else {
                                System.out.println(msg);
                                String[] msg_list = msg.split(";");
                                int target_id = Integer.parseInt(msg_list[0]);
                                server.send2ClientWithId(msg_list[1], target_id);
                            }
                        } else {
                            server.remove_storage_handler(storageID);
                            running = false;
                        }
                    }
                } catch (SocketException se) {
                    System.err.println("Connection was reseted");
                } catch (IOException ioe) {
                    System.err.println("Error on TCPClientHandler.read_handler");
                    System.err.println(ioe);
                } catch (Exception e) {
                    System.err.println("Error on TCPClient:");
                    System.err.println(e);
                }
            };
            Thread read_handler_thread = new Thread(read_handler);
            read_handler_thread.start();

        } catch (IOException e) {
            System.err.println("Error on TCPStorageHadler (1): ");
            e.printStackTrace();
        }
    }
}