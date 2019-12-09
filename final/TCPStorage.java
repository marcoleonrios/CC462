import java.io.FileWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TCPStorage {
    protected static final int STORAGE_SERVERPORT = 23456;
    private static Scanner scanner;
    private static Map<String, String> table = new HashMap<String, String>();
    private static FileWriter writer;
    private Socket socket;
    private boolean isStorage = true;
    private boolean running = false;
    private String filepath;
    private TCPStoragePool pool;

    public static void main(String args[]) {
        if (condition) {
            
        } else {
            System.err.println("Usage: java TCPStorage <pool_ip> <filepath>");
        }
    }

    public TCPStorage(Socket sckt, String flpth, TCPStoragePool pl) {
        filepath = flpth;
        pool = pl;
        System.out.println("I'm in pool " + pool.getID())
    }

    public String readAcccount(String account_id) {
        table = CSVHandle.getMapFromFile(filepath);
        String balance;
        balance = table.get(account_id);
        return balance;
    }

    public int updateAccount(String orig_id, String dest_id, String money) {
        String balance = readAcccount(orig_id);
        float actual_balance = Float.parseFloat(balance);
        float wanted_money = Float.parseFloat(money);

        if (wanted_money > actual_balance) {
            System.out.println("Balance insufficient.");
            return 1;
        }

        /** */

        return 0;
    }

    public void run() {
        
    }
}   