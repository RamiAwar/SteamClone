
/**
 * Created by ramiawar on 3/23/17.
 */
package pumpkinbox.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port = 8000;    //ServerSocket port number
    private ServerSocket serverSocket;

    //Empty private constructor since we do not this class to be accessible from other classes
    private Server() throws ClassNotFoundException {}

    //Accepts connections indefinitely
    public void acceptConnections() {
        try
        {
            serverSocket = new ServerSocket(port);  //Instantiating server socket
        }
        catch (IOException e)
        {
            System.err.println("ServerSocket instantiation failure");
            e.printStackTrace();
            System.exit(0);
        }

        while (true) {  //Accepting connections indefinitely
            try
            {
                Socket newConnection = serverSocket.accept();
                System.out.println("Client connected.");

                ServerThread st = new ServerThread(newConnection);  //Creating a thread for each new client
                new Thread(st).start();

            }
            catch (IOException ioe)
            {
                System.err.println("Server accept failed");
            }
        }
    }

    public static void main(String args[]) {

        //Instantiate server and call acceptConnections to loop indefinitely

        Server server = null;
        try {
            server = new Server();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        server.acceptConnections();
    }

}
