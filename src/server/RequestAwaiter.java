package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestAwaiter extends Thread {
    public boolean runOn=true;
    public ServerSocket serverSocket;
    public MinetestFileServer fileserver;

    public RequestAwaiter(int port, MinetestFileServer fileserver) {
        super();
        super.setName("Minetest Media Server Java Edition");
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't open ServerSocket on port "+port);
            System.exit(1);
        }
        this.fileserver = fileserver;
    }

    public void run() {
        while (runOn) {
            try {
                Socket socket=serverSocket.accept();
                Thread t=new Thread(new MinetestRequestHandler(socket, fileserver));
                t.setName("Minetest Media Server Java Edition Request Handler");
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
