package pumpkinbox.server;

/**
 * Created by ramiawar on 4/6/17.
 */

/**
 * Created by ramiawar on 3/23/17.
 */

import pumpkinbox.api.CODES;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.time.Time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatServer {

    private int port = 9000;    //ServerSocket port number
    private ServerSocket serverSocket;

    public Vector<ChatServerThread> connectedClients = new Vector<ChatServerThread>();

    //Empty private constructor since we do not this class to be accessible from other classes
    private ChatServer() throws ClassNotFoundException {}

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

            System.out.println(connectedClients);

            try
            {
                Socket newConnection = serverSocket.accept();
                System.out.println("Client connected.");

                ChatServerThread st = new ChatServerThread(newConnection);  //Creating a thread for each new client
                connectedClients.add(st);

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

        ChatServer server = null;
        try {
            server = new ChatServer();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        server.acceptConnections();
    }

    class ChatServerThread implements Runnable {

        private Socket socket;
        private ObjectInputStream datain;
        private ObjectOutputStream dataout;
        private DatabaseHandler db;

        private final String CRLF = "\r\n";

        //Constructor
        public ChatServerThread(Socket socket) {
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
            System.out.println("Streams opened successfully.");

            while (true) {
                long millis = System.currentTimeMillis();

                try {
                    System.out.println("Waiting for object...");
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
                    connectedClients.remove(this);
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


                case "MESSAGE":
                    if(SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println("Invalid request");
                        try {
                            dataout.writeObject(CODES.INVALID_REQUEST);
                        } catch (IOException e) {
                            System.out.println("Could not send invalid request to server.");
                            e.printStackTrace();
                        }

                        break;
                    }


                    String[] message_content = CONTENT.split("\\|");

                    String sender_id = message_content[0];
                    String receiver_id = message_content[1];
                    String content = message_content[2];

                    //Look up username/pass in database
                    ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + sender_id +"';");

                    try {
                        System.out.println("Checking if user exists ...");

                        if(rs.next()){

                            //User exists, check auth token:

                            //Check authentication token
                            System.out.println("User exists. Checking if token valid...");

                            String token = rs.getString("authtoken");
                            System.out.println(token);
                            if(token.equals(SECRET)){
                                System.out.println("Token exists. Checking expiration...");
                            } else{
                                System.out.println("Invalid authentication token.");
                                dataout.writeObject(CODES.INVALID_TOKEN);
                                return;
                            }

                            //Check valid timestamp
                            String expirationTime = rs.getString("expiration");
                            boolean validToken = Time.checkTimestamp(expirationTime);

                            //Update token expiration time
                            String query = "UPDATE pumpkinbox_users_table SET "+
                                    "authtoken='" + SECRET + "', " +
                                    "expiration='" + Time.getTimeStampPlus() + "' " +
                                    "WHERE id='" + sender_id + "';";

                            if(validToken){
                                //Send OK
                                System.out.println("User exists and valid auth token, adding message to db...");

                                //Add message to database
                                //get timestamp
                                String timestamp = Time.getTimeStamp();
                                if(db.executeAction("INSERT INTO pumpkinbox_messages (sender_id, receiver_id, content, timestamp) VALUES ('" +
                                        sender_id + "', '" +
                                        receiver_id + "', '" +
                                        content + "', '" +
                                        timestamp + "');")) dataout.writeObject(CODES.OK);

                                System.out.println("Message sent.");

                            }else{
                                System.out.println("User exists but token EXPIRED, sending invalid token code...");
                                dataout.writeObject(CODES.INVALID_TOKEN + CRLF);

                            }

                        }else{
                            System.out.println("User not found, sending NOT FOUND code...");
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    try {
                        dataout.writeObject(CODES.INVALID_REQUEST);
                    } catch (IOException e) {
                        System.out.println("Could not send invalid request to client.");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}
