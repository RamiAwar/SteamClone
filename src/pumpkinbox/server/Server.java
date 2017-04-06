
/**
 * Created by ramiawar on 3/23/17.
 */
package pumpkinbox.server;

import pumpkinbox.api.CODES;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.security.AuthToken;
import pumpkinbox.security.Hasher;
import pumpkinbox.time.Time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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

    class ServerThread implements Runnable {

        private Socket socket;
        private ObjectInputStream datain;
        private ObjectOutputStream dataout;
        private DatabaseHandler db;

        private final String CRLF = "\r\n";

        //Constructor
        public ServerThread(Socket socket) {

            this.socket = socket;
            this.db = DatabaseHandler.getInstance();
        }

        public Socket getSocket(){
            return socket;
        }

        //Thread main program
        public void run() {

            try {

                System.out.println("Getting output/input socket streams...");

                dataout = new ObjectOutputStream(socket.getOutputStream());
                datain = new ObjectInputStream(socket.getInputStream());

            } catch (Exception e) {
                System.out.println("Failed to get socket Input/Output streams ... \nClosing thread...");
                return;
            }

            //ServerThread main program
            System.out.println("Connected");

            while (true) {
                long millis = System.currentTimeMillis();

                try {

                    Object s = datain.readObject(); //Read client request

                    //TODO: Send ping every 1 min to check if connection is alive?
                    //
                    //if not
                    //remove current client from ConnectedClients list.
                    //socket.close();

                    if (!s.equals(null)) {

                        System.out.println("Server received: " + s);//for debugging
                        parseRequest((String) s);   //Decode client request

                    }

                } catch (Exception e) {
                    System.out.println("---------------Client Disconnected---------------");
                    e.printStackTrace();
                    System.out.println("-------------------------------------------------");
                    break;
                }

                try {
                    Thread.sleep(1000 - millis % 1000); //Sleep thread for 1 second to increase efficiency
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        /*--------------- API --------------------

        Request:    VERB    SECRET    CONTENT

         -----------------------------------------*/

        public void parseRequest(String request) {

            // Extract VERB from the request line.
            StringTokenizer tokens = new StringTokenizer(request);

            String VERB = tokens.nextToken();
            String SECRET = "";
            String CONTENT = "";

            try{SECRET = tokens.nextToken();}catch(Exception e){e.printStackTrace();}
            try{CONTENT = tokens.nextToken();}catch(Exception e){e.printStackTrace();}


            switch (VERB) {


                case "LOGIN":
                    if(SECRET.equals("")) {
                        System.out.println("Invalid request");
                        break;
                    }

                    //TODO:  decrypt SECRET

                    String[] userpass = SECRET.split("\\|");
                    String username = userpass[0];
                    String password = Hasher.sha256(userpass[1]);

                    //Look up username/pass in database
                    ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE email='" + username + "' AND password='" + password +"';");

                    try {
                        System.out.println("Checking if user exists ...");
                        if(rs.next()){

                            //Login user:
                            //Send OK code, homepage info
                            System.out.println("User exists, sending OK code...");
                            dataout.writeObject(CODES.OK + CRLF);

                            //TODO: Generate auth token, add to db, send to user
                            String timestamp = Time.getTimeStampPlus();
                            String authToken = AuthToken.generateToken();

                            String query = "UPDATE pumpkinbox_users_table SET "+
                                    "authtoken='" + authToken + "', " +
                                    "expiration='" + timestamp + "' " +
                                    "WHERE email='" + username + "';";

                            try{
                                db.executeAction(query);
                                System.out.println("Inserting authentication token...");
                            }catch(Exception e){
                                System.out.println("Token insertion failed");
                            }

                            dataout.writeObject(authToken);

                            dataout.writeObject(rs.getInt("id"));

                            //TODO:Get home page info
                            //dataout.writeObject(INFO);

                        }else{
                            System.out.println("User not found, sending NOT FOUND code...");
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    break;

                case "SIGNUP":

                    if(SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println("Invalid request");
                        break;
                    }

                    //TODO:decrypt SECRET

                    userpass = SECRET.split("\\|");
                    username = userpass[0];
                    password = Hasher.sha256(userpass[1]);
                    String[] userinfo = CONTENT.split("\\|");
                    String firstname = userinfo[0];
                    String lastname = userinfo[1];

                    //Look up username in database
                    rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE email='" + username + "' ;");

                    try {
                        if(rs.next()){
                            //User already exists
                            dataout.writeObject(CODES.ALREADY_EXISTS);
                        }else{
                            //Write out OK if execution successful
                            if(db.executeAction("INSERT INTO pumpkinbox_users_table (id, firstname, lastname, password, email) VALUES (0,'" +
                                    firstname + "', '" +
                                    lastname + "', '" +
                                    password + "', '" +
                                    username + "');")) dataout.writeObject(CODES.OK);
                            else dataout.writeObject(CODES.INSERTION_ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case "GET":

                    break;

                case "MESSAGE":

                    break;

                case "UPDATE":

                    break;
            }
        }
    }

}
