package pumpkinbox.server;

/**
 * Created by ramiawar on 4/6/17.
 */

/**
 * Created by ramiawar on 3/23/17.
 */

import pumpkinbox.api.CODES;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.NotificationObject;
import pumpkinbox.api.User;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.time.Time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.*;



public class ChatServer {

    private int port = 9000;    //ServerSocket port number
    private ServerSocket serverSocket;

    //This vector stores the User objects of currently connected clients
    public volatile Vector<User> onlineUsersList = new Vector<User>();


    //Empty private constructor since we do not this class to be accessible from other classes
    private ChatServer() throws ClassNotFoundException {}



    /**
     * Synchronized method to add users from threads.
     * @param user User to be added to online users list.
     */
    public synchronized void addUser(User user){
        onlineUsersList.add(user);
    }


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

            System.out.println(onlineUsersList);

            try
            {
                Socket newConnection = serverSocket.accept();
                System.out.println("Client connected.");

                ChatServerThread st = new ChatServerThread(newConnection);  //Creating a thread for each new client

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
        private String clientPort;
        private Timer timer;


        public int userId;
        public String username;


        private final String CRLF = "\r\n";

        //Constructor
        public ChatServerThread(Socket socket) {
            this.socket = socket;
            this.clientPort = String.valueOf(this.socket.getPort());
            this.db = DatabaseHandler.getInstance();
        }

        public Socket getSocket(){
            return socket;
        }


        //Thread main program
        public void run() {

            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {

                    try {

                        //Search database for unread messages
                        ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_messages WHERE receiver_id='" + userId +"' AND received='0';");

                        //Send any unread messages to client and MARK AS READ
                        while(rs.next()){

                            String message = rs.getString("content");
                            int sender_id = rs.getInt("sender_id");
                            String timestamp = rs.getString("timestamp");
                            String sender_name = "unknown error";
                            int messageId = rs.getInt("id");

                            System.out.println("MESSAGE FOUND: " + message);

                            for (int i = 0; i < onlineUsersList.size(); i++) {
                                if(onlineUsersList.get(i).getUserId() == sender_id){
                                    sender_name = onlineUsersList.get(i).getUsername();
                                    break;
                                }
                            }

                            System.out.println("Writing message to client");

                            dataout.writeObject("MESSAGE " + sender_id + "|" + timestamp + "|" + message);

                            //Update database messages as read
                            String query = "UPDATE pumpkinbox_messages SET "+
                                    "received='1' WHERE id='" + messageId + "';";
                            db.executeAction(query);

                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            timer = new Timer("Message fetcher");//create a new timer
            timer.scheduleAtFixedRate(timerTask, 30, 500);

            try {

                System.out.println("Getting output/input socket streams for " + this.clientPort);

                dataout = new ObjectOutputStream(socket.getOutputStream());
                datain = new ObjectInputStream(socket.getInputStream());

                String[] user_details = ((String) datain.readObject()).split("\\|");

                this.userId = Integer.parseInt(user_details[0]);
                this.username = user_details[1];

                //Adding newly connected user to online friends list
                addUser(new User(userId, username));

            } catch (Exception e) {
                System.out.println("Failed to get socket Input/Output streams ... \nClosing thread...");
                return;
            }

            //ServerThread main program
            System.out.println("Streams to " + this.clientPort + " opened successfully.");

            while (true) {

                try {

                    System.out.println("Waiting for object from " + this.clientPort);
                    Object s = datain.readObject(); //Read client request

                    if (!s.equals(null)) {

                        System.out.println("Server received: " + s + " from " + this.clientPort);//for debugging
                        parseRequest((String) s);   //Decode client request

                    }



                } catch (Exception e) {
                    System.out.println("---------------" + this.clientPort + " disconnected---------------");
                    e.printStackTrace();
                    break;
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

                case "GET":

                    if(SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println(this.clientPort + " - Invalid request");
                        try {
                            dataout.writeObject(CODES.INVALID_REQUEST);
                        } catch (IOException e) {
                            System.out.println(this.clientPort + " - Invalid request to server.");
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


                                //UNECCESSARY, BUT WHAT THE HECK
                                //Making sure that this user is online
                                boolean notThere = true;
                                for (int i = 0; i < onlineUsersList.size(); i++) {
                                    if(onlineUsersList.get(i).getUserId() == userId){
                                        notThere = false;
                                        break;
                                    }
                                }
                                if(notThere) addUser(new User(userId, username));


//                                System.out.println("CONTENT: " + content);
                                //Check user friends
                                if(content.equals("friends")) {//GET FRIENDS REQUESTED

                                    ArrayList<User> myOnlineFriends = new ArrayList<User>();

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
                                        for (int i = 0; i < onlineUsersList.size(); i++) {
                                            if(onlineUsersList.get(i).getUserId() == friend_id) isOnline = true;
                                        }

                                        if (isOnline) {
                                            //Get friend username
//                                            System.out.println("Friend online: " + friend_id);
                                            ResultSet friend = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + friend_id + "';");
                                            if(friend.next()) myOnlineFriends.add(new User(friend_id, friend.getString("email"), friend.getString("firstname")));
                                        }
                                    }

                                    //Return friends to client
                                    //Turn myOnlineFriends into a string
                                    String outputFriends = "";
                                    try {
                                        System.out.println("No friends online, sending to user.");
                                        if(myOnlineFriends.size() < 1) {
                                            dataout.writeObject("FRIENDS " + outputFriends);
                                            return;
                                        }


                                        //Forming a string containing all users to be sent to client
                                        for (int i = 0; i < myOnlineFriends.size()-1; i++) {
                                            System.out.println(myOnlineFriends.get(i).getUsername());
                                            outputFriends += myOnlineFriends.get(i).getUsername() + "|";
                                            outputFriends += myOnlineFriends.get(i).getUserId() + "|";
                                            outputFriends += myOnlineFriends.get(i).getFirstName() + "|";
                                        }

                                        outputFriends += myOnlineFriends.get(myOnlineFriends.size() - 1).getUsername() + "|";
                                        outputFriends += myOnlineFriends.get(myOnlineFriends.size() - 1).getUserId() + "|";
                                        outputFriends += myOnlineFriends.get(myOnlineFriends.size() - 1).getFirstName();

                                        System.out.println("Sending to client: " + outputFriends);
                                        dataout.writeObject("FRIENDS " + outputFriends);

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


                    String[] lines = request.split(" ", 3);
                    String[] message_content = lines[2].split("\\|");

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
