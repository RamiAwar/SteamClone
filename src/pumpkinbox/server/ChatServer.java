package pumpkinbox.server;

/**
 * Created by ramiawar on 4/6/17.
 */

/**
 * Created by ramiawar on 3/23/17.
 */

import pumpkinbox.api.CODES;
import pumpkinbox.api.Friend;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.api.User;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.time.Time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatServer {

    private int port = 9000;    //ServerSocket port number
    private ServerSocket serverSocket;

    //This array is for storing notifications incoming from the database like messages or invitations and passing them over through the notiication queue
    public ArrayList<NotificationObject> notificationList = new ArrayList<>();

    //This queue is for saving notifications and sending them once the access token has been confirmed.
    private BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();

    //This vector stores the currently connected clients
    public Vector<ChatServerThread> connectedClients = new Vector<ChatServerThread>();

    //This vector stores the User objects of currently connected clients
    public Vector<User> users = new Vector<User>();

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

            System.out.println(users);

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
        private String ip;

        public int userId;
        public String username;


        private final String CRLF = "\r\n";

        //Constructor
        public ChatServerThread(Socket socket) {
            this.socket = socket;
            this.ip = String.valueOf(this.socket.getPort());
            this.db = DatabaseHandler.getInstance();
        }

        public Socket getSocket(){
            return socket;
        }


        //Thread main program
        public void run() {

            try {

                System.out.println("Getting output/input socket streams for " + this.ip);

                dataout = new ObjectOutputStream(socket.getOutputStream());
                datain = new ObjectInputStream(socket.getInputStream());

                userId = Integer.parseInt((String) datain.readObject());
                users.add(new User(userId, ""));

            } catch (Exception e) {
                System.out.println("Failed to get socket Input/Output streams ... \nClosing thread...");
                return;
            }

            //ServerThread main program
            System.out.println("Streams to " + this.ip + " opened successfully.");

            while (true) {

//                long millis = System.currentTimeMillis();

                try {
                    System.out.println("Waiting for object from " + this.ip);
                    Object s = datain.readObject(); //Read client request

                    //TODO: Check if connection still alive to close socket and update friends list if not

                    if (!s.equals(null)) {

                        System.out.println("Server received: " + s + " from " + this.ip);//for debugging
                        parseRequest((String) s);   //Decode client request


                        //TODO fill notif queue
                        //Check notification queue
//                        for (int i = 0; i < notificationList.size(); i++) {
//                            NotificationObject notification = notificationList.get(i);
//                            if( userId == notification.getReceiver()){
//                                String message = notification.getMessage();
//                                String sender = notification.getSenderUsername();
//                                notificationList.remove(i);
//                                notificationQueue.offer("NOTIFICATION " + sender + "|" + message);
//
//                            }
//                        }

                    }

                } catch (Exception e) {
                    connectedClients.remove(this);
                    System.out.println("---------------" + this.ip + " disconnected---------------");
                    e.printStackTrace();
                    break;
                }

//                try {
//                    Thread.sleep(100 - millis % 100); //Sleep thread for 0.1 second to increase efficiency
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
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

                case "GET":

                    if(SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println(this.ip + " - Invalid request");
                        try {
                            dataout.writeObject(CODES.INVALID_REQUEST);
                        } catch (IOException e) {
                            System.out.println(this.ip + " - Invalid request to server.");
                            e.printStackTrace();
                        }

                        break;
                    }

                    String[] get_content = CONTENT.split("\\|");

                    String sender = get_content[0];
                    String content = get_content[1];

                    ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + sender +"';");

                    try {
//                        System.out.println("Checking if user exists ...");

                        if(rs.next()){

                            //User exists, check auth token, get username:
                            username = rs.getString("email");

                            //Check authentication token
//                            System.out.println("User exists. Checking if token valid...");
                            String token = rs.getString("authtoken");

//                            System.out.println(token);

                            if(token.equals(SECRET)){
//                                System.out.println("Token exists. Checking expiration...");
                            } else{
//                                System.out.println("Invalid authentication token.");
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
//                                System.out.println("User exists and valid auth token. Getting friends ...");

                                if(userId != Integer.parseInt(sender)) userId = Integer.parseInt(sender);

                                boolean notThere = true;
                                for (int i = 0; i < users.size(); i++) {
                                    if(users.get(i).getUserId() == userId){
                                        notThere = false;
                                        break;
                                    }
                                }
                                if(notThere) users.add(new User(userId, username));


//                                System.out.println("CONTENT: " + content);
                                //Check user friends
                                if(content.equals("friends")) {

                                    ArrayList<User> onlineFriends = new ArrayList<User>();

//                                    System.out.println("Fetching friends from database...");
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
//                                            System.out.println("Friend online: " + friend_id);
                                            ResultSet friend = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + friend_id + "';");
                                            if(friend.next()) onlineFriends.add(new User(friend_id, friend.getString("email")));
                                        }

//                                        System.out.println("[ " + friends.getInt("sender_id"));
//                                        System.out.println(friends.getInt("receiver_id") + " ]");
                                    }

                                    //Return friends to client
                                    //Turn onlineFriends into a string
                                    String outputFriends = "";
                                    try {
//                                        System.out.println("Forming output...");
                                        if(onlineFriends.size() < 1) return;
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

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    break;



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
                    String contents = message_content[2];

                    //Look up username/pass in database
                    ResultSet rset = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + sender_id +"';");

                    try {
                        System.out.println("Checking if user exists ...");

                        if(rset.next()){

                            //User exists, check auth token:

                            //Check authentication token
                            System.out.println("User exists. Checking if token valid...");
                            String token = rset.getString("authtoken");

                            System.out.println(token);

                            if(token.equals(SECRET)){
                                System.out.println("Token exists. Checking expiration...");
                            } else{
                                System.out.println("Invalid authentication token.");
                                dataout.writeObject(CODES.INVALID_TOKEN);
                                return;
                            }

                            //Check valid timestamp
                            String expirationTime = rset.getString("expiration");
                            boolean validToken = Time.checkTimestamp(expirationTime);

                            //Update token expiration time
                            String query = "UPDATE pumpkinbox_users_table SET "+
                                    "authtoken='" + SECRET + "', " +
                                    "expiration='" + Time.getTimeStampPlus() + "' " +
                                    "WHERE id='" + sender_id + "';";

                            if(validToken){
                                //Send OK
                                System.out.println("User exists and valid auth token, adding message to db...");

                                if(userId != Integer.parseInt(sender_id)) userId = Integer.parseInt(sender_id);

                                //Add message to database
                                //get timestamp
                                String timestamp = Time.prettyTimeStamp();
                                if(db.executeAction("INSERT INTO pumpkinbox_messages (sender_id, receiver_id, content, timestamp) VALUES ('" +
                                        sender_id + "', '" +
                                        receiver_id + "', '" +
                                        contents + "', '" +
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
