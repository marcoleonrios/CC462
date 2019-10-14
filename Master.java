
class Master {
    private TCPServer server;
    private int numClient = 4; // That's the cluster's number of nodes


    public static void main(String[] args) {
        Master m = new Master();
        m.begin();
    }

    void begin() {
        new Thread(
            new Runnable(){
            
                @Override
                public void run() {
                    server = new TCPServer(
                        new TCPServer.OnMessageReceived() {
                            @Override
                            public void messageReceived() {
                                /**
                                 * Does nothing at all...
                                 */
                            }
                        },
                        numClient
                    );
                    server.run();
                }
            }
        ).start();
    }
}