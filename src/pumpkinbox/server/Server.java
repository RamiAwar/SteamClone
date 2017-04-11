
/**
 * Created by ramiawar on 3/23/17.
 */
package pumpkinbox.server;

import pumpkinbox.api.CODES;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.api.User;
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
import java.util.ArrayList;
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
                            String user_details = Integer.toString(rs.getInt("id")) + "|" + rs.getString("firstname");
                            dataout.writeObject(user_details);

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

                    String[] get_content = CONTENT.split("\\|");

                    String sender = get_content[0];
                    String content = get_content[1];

                    ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + sender +"';");

                    try {
                        System.out.println("Checking if user exists ...");

                        if(rs.next()){

                            //User exists, check auth token, get username:
                            username = rs.getString("email");

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
                                    "WHERE id='" + sender+ "';";

                            if(validToken){

                                //Send OK
                                System.out.println("User exists and valid auth token. Getting friends ...");

                                if(userId != Integer.parseInt(sender)) userId = Integer.parseInt(sender);

                                boolean notThere = true;
                                for (int i = 0; i < users.size(); i++) {
                                    if(users.get(i).getUserId() == userId){
                                        notThere = false;
                                        break;
                                    }
                                }
                                if(notThere) users.add(new User(userId, username));


                                System.out.println("CONTENT: " + content);
                                //Check user friends
                                if(content.equals("friends")) {

                                    ArrayList<User> onlineFriends = new ArrayList<User>();

                                    System.out.println("Fetching friends from database...");
                                    ResultSet friends = db.executeQuery("SELECT * FROM pumpkinbox_friends_table WHERE sender_id='" +
                                            sender + "' OR receiver_id='" +
                                            sender + "';");

                                    while (friends.next()) {

                                        //retrieving which part of statement corresponds to friend id
                                        int friend_id = (friends.getInt("sender_id") == Integer.parseInt(sender))
                                                ? friends.getInt("receiver_id")
                                                : friends.getInt("sender_id");

                                        //Check if friend is online
                                        boolean isOnline = false;
                                        for (int i = 0; i < users.size(); i++) {
                                            if(users.get(i).getUserId() == friend_id) isOnline = true;
                                        }
                                        if (isOnline) {
                                            //Get friend username
                                            System.out.println("Friend online: " + friend_id);
                                            ResultSet friend = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + friend_id + "';");
                                            if(friend.next()) onlineFriends.add(new User(friend_id, friend.getString("email")));
                                        }

                                        System.out.println("[ " + friends.getInt("sender_id"));
                                        System.out.println(friends.getInt("receiver_id") + " ]");
                                    }

                                    //Return friends to client
                                    //Turn onlineFriends into a string
                                    String outputFriends = "";
                                    try {
                                        System.out.println("Forming output...");
                                        for (int i = 0; i < onlineFriends.size()-1; i++) {
                                            System.out.println(onlineFriends.get(i).getUsername());
                                            outputFriends += onlineFriends.get(i).getUsername() + "|";
                                            outputFriends += onlineFriends.get(i).getUserId() + "|";
                                        }

                                        outputFriends += onlineFriends.get(onlineFriends.size() - 1).getUsername() + "|";
                                        outputFriends += onlineFriends.get(onlineFriends.size() - 1).getUserId();

                                        System.out.println("Sending to client: " + outputFriends);
                                        dataout.writeObject(outputFriends);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


                                }

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

                case "MESSAGE":

                    break;

                case "UPDATE":

                    break;
            }
        }
    }

}
