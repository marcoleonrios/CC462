import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.LinkedList; 
import java.util.Queue;

import java.util.Arrays;

import java.io.File;

public class TCPServer {
    String loadPath;
    String savePath;
    Queue<String> jobs;

    private int count = 0;
    public static final int SERVERPORT = 12345;
    private boolean running = false;
    TCPServerThread[] client;
    PrintWriter out;
    BufferedReader in;
    ServerSocket ssocket;

    public static void main(String args[]) {
        if (args.length == 2) {
            TCPServer instance = new TCPServer(1000);
            instance.setPaths(args[0], args[1]);
            instance.loadImagesPaths();
            System.out.println("Ready to accept conections.");
            instance.run();
        } else {
            System.err.println("Usage: java TCPServer <loadpath> <savepath>");
        }
    }

    public TCPServer(int nclient) {
        client = new TCPServerThread[nclient];
    }

    public void setPaths(String loadPath, String savePath) {
        this.loadPath = loadPath;
        this.savePath = savePath;
        
    }

    // Loads imagepaths foun in loadPath
    public void loadImagesPaths() {
        String[] pathNames;
        try{
            File f = new File(loadPath);
            pathNames = f.list();
            


            jobs = new LinkedList<String>(Arrays.asList(pathNames));
            
            for (String name : pathNames) {
                System.out.println(name);
            }
            
        } catch(Exception e) {
            System.out.println("ERROR AL DAR LA RUTA DE IMAGENES");
            System.out.println(e);
            return;
        }
    }

    public void run() {
        running = true;
        try {
            System.out.println("Connecting...");
            ssocket = new ServerSocket(SERVERPORT);

            while (!jobs.isEmpty()) {
                Socket clnt = ssocket.accept();
                client[count] = new TCPServerThread(clnt, this, count, client, jobs);
                Thread t = new Thread(client[count]);
                count++;
                t.start();
                System.out.println("Clients connected: " + count);

            }
            
            running = false;
        } catch (Exception e) {
            System.err.println("Error on TCPServer:");
            System.err.println(e);
        }
    }

    public boolean isRunning() {
        return running;
    }

}

