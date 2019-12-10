public class TCPStorage extends Thread {
    private server_ip;
    protected static final int SERVERPORT = 23456;

    private PrintWriter out;
    private BufferedReader in;

    private boolean running = false;

    private static Scanner scanner = new Scanner(System.in);

    public TCPStorage() {
        
    }
}