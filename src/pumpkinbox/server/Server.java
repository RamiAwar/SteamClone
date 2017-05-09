
/**
 * Created by ramiawar on 3/23/17.
 */
package pumpkinbox.server;

import pumpkinbox.api.CODES;
import pumpkinbox.api.GameStatus;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.User;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.security.AuthToken;
import pumpkinbox.security.Hasher;
import pumpkinbox.time.Time;
import sun.reflect.annotation.ExceptionProxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private int port = 8000;    //ServerSocket port number
    private ServerSocket serverSocket;

    public BlockingQueue<MessageObject> invitations = new LinkedBlockingQueue<>();

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

                ServerThread st = new ServerThread(newConnection, invitations);  //Creating a thread for each new client
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
        public ServerThread(Socket socket, BlockingQueue<MessageObject> invitations) {

            this.socket = socket;
            this.db = DatabaseHandler.getInstance();
        }

        public Socket getSocket() {
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

                try {

                    Object s = datain.readObject(); //Read client request

                    //TODO: Send ping every 1 min to check if connection is alive?
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

            }

        }

        /*--------------- API --------------------

        Request:    VERB    SECRET    CONTENT

         -----------------------------------------*/

        public void parseRequest(String request) {

            // Extract VERB from the request line.
            String[] tokens = request.split(" ", 3);

            String VERB = tokens[0];
            String SECRET = "";
            String CONTENT = "";

            try {
                SECRET = tokens[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                CONTENT = tokens[2];
            } catch (Exception e) {
                e.printStackTrace();
            }


            switch (VERB) {


                case "LOGIN":
                    if (SECRET.equals("")) {
                        System.out.println("Invalid request");
                        break;
                    }

                    //TODO:  decrypt SECRET

                    String[] userpass = SECRET.split("\\|");
                    String username = userpass[0];
                    String password = Hasher.sha256(userpass[1]);

                    //Look up username/pass in database
                    ResultSet rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE email='" + username + "' AND password='" + password + "';");

                    try {
                        System.out.println("Checking if user exists ...");
                        if (rs.next()) {

                            //Login user:
                            //Send OK code, homepage info
                            System.out.println("User exists, sending OK code...");
                            dataout.writeObject(CODES.OK + CRLF);

                            //TODO: Generate auth token, add to db, send to user
                            String timestamp = Time.getTimeStampPlus();
                            String authToken = AuthToken.generateToken();

                            String query = "UPDATE pumpkinbox_users_table SET " +
                                    "authtoken='" + authToken + "', " +
                                    "expiration='" + timestamp + "' " +
                                    "WHERE email='" + username + "';";

                            try {
                                db.executeAction(query);
                                System.out.println("Inserting authentication token...");
                            } catch (Exception e) {
                                System.out.println("Token insertion failed");
                            }

                            dataout.writeObject(authToken);
                            String user_details = Integer.toString(rs.getInt("id")) + "|" + rs.getString("firstname");
                            dataout.writeObject(user_details);

                            // Get home page info
                            // dataout.writeObject(INFO);

                        } else {
                            System.out.println("User not found, sending NOT FOUND code...");
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case "SIGNUP":

                    if (SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println("Invalid request");
                        break;
                    }

                    // decrypt SECRET
                    userpass = SECRET.split("\\|");
                    username = userpass[0];
                    password = Hasher.sha256(userpass[1]);

                    String[] temp = request.split(" ", 3);
                    String[] userinfo = temp[2].split("\\|");
                    String firstname = userinfo[0];
                    String lastname = userinfo[1];

                    //Look up username in database
                    rs = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE email='" + username + "' ;");

                    try {
                        if (rs.next()) {
                            //User already exists
                            dataout.writeObject(CODES.ALREADY_EXISTS);
                        } else {
                            //Write out OK if execution successful
                            if (db.executeAction("INSERT INTO pumpkinbox_users_table (id, firstname, lastname, password, email) VALUES (0,'" +
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

                case "INVITE":

                    //API REQUEST FORMAT
                    //INVITE    get    sender_id|receiver_id|game

                    temp = request.split(" ", 3);
                    String[] request_info = temp[2].split("\\|");
                    int s_id = Integer.parseInt(request_info[0]);
                    int f_id = Integer.parseInt(request_info[1]);
                    String game = request_info[2];

                    MessageObject x = new MessageObject(s_id, f_id, game);

                    try {

                        if (temp[1].equals("GET")) {

                            dataout.writeObject(CODES.OK);

                            return;
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    if(temp[1].equals("PUT")){
                        if(invitations.offer(x)){
                            try {
                                dataout.writeObject(CODES.OK);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    }

                    if(temp[1].equals("FIND")){

                        while(!invitations.isEmpty()){
                            try {

                                MessageObject y = invitations.take();

                                if(y.getReceiver_id() == s_id){
                                    System.out.println("INV: " + y);
                                    //FOUND AN INVITATION
                                    dataout.writeObject(y.getSender_id() + "|" + y.getReceiver_id() + "|" + y.getContent());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        try {
                            dataout.writeObject(CODES.NOT_FOUND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    break;

                case "UPDATE":

                    if (SECRET.equals("") || CONTENT.equals("")) {
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

                    String type = get_content[0];

                    int sender_id = Integer.parseInt(get_content[1]);

                    ResultSet user = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + Integer.toString(sender_id) + "';");

                    try {

                        System.out.println("Checking if user exists ...");

                        if (user.next()) {

                            //User exists, check auth token, get username:
                            username = user.getString("email");
                            int userId = user.getInt("id");

                            //Check authentication token
                            System.out.println("User exists. Checking if token valid...");
                            String token = user.getString("authtoken");

                            System.out.println(token);

                            if (token.equals(SECRET)) {
                                System.out.println("Token exists. Checking expiration...");
                            } else {
                                System.out.println("Invalid authentication token.");
                                dataout.writeObject(CODES.INVALID_TOKEN);
                                return;
                            }

                            //Check valid timestamp
                            String expirationTime = user.getString("expiration");
                            boolean validToken = Time.checkTimestamp(expirationTime);

                            //Update token expiration time
                            String query = "UPDATE pumpkinbox_users_table SET " +
                                    "authtoken='" + SECRET + "', " +
                                    "expiration='" + Time.getTimeStampPlus() + "' " +
                                    "WHERE id='" + Integer.toString(sender_id) + "';";

                            if (validToken) {

                                switch (type) {

                                    case "REQUEST":

                                        //TODO PREVENT USER FROM SENDING REQUEST TO HIMSELF

                                        String sender_username = get_content[2];
                                        String friend_email = get_content[3];

                                        if(sender_username.equals(friend_email)){
                                            dataout.writeObject(CODES.ALREADY_EXISTS);
                                            return;
                                        }

                                        //Check if already friends
                                        ResultSet friend = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE email='" +
                                                friend_email + "';");

                                        if (friend.next()) {

                                            int friend_id = friend.getInt("id");

                                            ResultSet already = db.executeQuery("SELECT * FROM pumpkinbox_friends_table WHERE (sender_id='" +
                                                    friend_id + "' AND receiver_id='" + userId + "' ) OR (sender_id='" + userId + "' AND receiver_id='" + friend_id + "');");

                                            if (already.next()) {
                                                dataout.writeObject(CODES.ALREADY_EXISTS);
                                                return;

                                            } else {

                                                //Add request to table
                                                String friend_request = "INSERT INTO pumpkinbox_friend_requests (sender_id, receiver_id, sender_username) " +
                                                        "VALUES('" + userId + "', '" + friend_id + "', '" + sender_username + "');";

                                                if (db.executeAction(friend_request)) {

                                                    //Seems good, reply to client
                                                    dataout.writeObject(CODES.OK);
                                                    return;
                                                }
                                            }

                                        } else {
                                            //friend requested not found, notify user
                                            dataout.writeObject(CODES.NOT_FOUND);
                                            return;
                                        }
                                        break;

                                    case "ACCEPT":  //UPDATE    auth    ACCEPT|sender_id|request_id

                                        int request_id = Integer.parseInt(get_content[2]);

                                        //Go to database, get request info
                                        ResultSet accept = db.executeQuery("SELECT * FROM pumpkinbox_friend_requests WHERE id='" + request_id + "' AND receiver_id='" +
                                                Integer.toString(sender_id) + "';");
                                        if (accept.next()) {
                                            //request is legit, user is who he says, request is directed to him

                                            //FETCH INFO
                                            int requestSenderId = accept.getInt("sender_id");
                                            String requestSenderUsername = accept.getString("sender_username");

                                            //DELETE FROM TABLE
                                            if(!db.executeAction("DELETE FROM pumpkinbox_friend_requests WHERE id='" + request_id + "';")){
                                                dataout.writeObject(CODES.INSERTION_ERROR);
                                                return;
                                            }

                                            //ADD FRIENDSHIP TO FRIENDS TABLE
                                            //TABLE: id ---- sender_id ----- receiver_id ----- timestamp ----- sender_privacy ----- receiver_privacy

                                            String timestamp = Time.prettyTimeStamp();
                                            if (db.executeAction("INSERT INTO pumpkinbox_friends_table (sender_id, receiver_id, timestamp) VALUES('" +
                                                    requestSenderId + "', '" + Integer.toString(sender_id) + "', '" + timestamp + "');")) {
                                                dataout.writeObject(CODES.OK);
                                            }
                                        }else{
                                            dataout.writeObject(CODES.NOT_FOUND);
                                        }
                                        break;

                                    case "REJECT":  //UPDATE    auth    ACCEPT|sender_id|friend_id

                                        int request_id_r = Integer.parseInt(get_content[2]);

                                        //Go to database, get request info
                                        ResultSet reject = db.executeQuery("SELECT * FROM pumpkinbox_friend_requests WHERE id='" + request_id_r + "' AND receiver_id='" +
                                                Integer.toString(sender_id) + "';");
                                        if (reject.next()) {

                                            //request is legit, user is who he says, request is directed to him

                                            //FETCH INFO
                                            int requestSenderId = reject.getInt("sender_id");
                                            String requestSenderUsername = reject.getString("sender_username");

                                            //DELETE FROM TABLE
                                            if (db.executeAction("DELETE FROM pumpkinbox_friend_requests WHERE id='" + request_id_r + "';")) {
                                                dataout.writeObject(CODES.OK);
                                            }
                                        }
                                        break;

                                    case "REMOVE":  //UPDATE    auth    ACCEPT|sender_id|friend_id|game

                                        int friend_id = Integer.parseInt(get_content[2]);

                                        //DELETE FROM TABLE
                                        if (db.executeAction("DELETE FROM pumpkinbox_friends_table WHERE" +
                                                " (sender_id='" + Integer.toString(sender_id) + "' AND receiver_id='" + Integer.toString(friend_id) +"')" +
                                                " OR (sender_id='" + Integer.toString(friend_id) + "' AND receiver_id='" + Integer.toString(sender_id) + "');")) {
                                            dataout.writeObject(CODES.OK);
                                        }

                                    case "INVITE":

                                        friend_id = Integer.parseInt(get_content[2]);





                                    default:
                                        dataout.writeObject(CODES.INVALID_REQUEST);
                                        break;
                                }

                            } else {
                                System.out.println("User exists but token EXPIRED, sending invalid token code...");
                                dataout.writeObject(CODES.INVALID_TOKEN + CRLF);

                            }

                        } else {
                            System.out.println("User not found, sending NOT FOUND code...");
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;

                case "GET":

                    if (SECRET.equals("") || CONTENT.equals("")) {
                        System.out.println("Invalid request");
                        try {
                            dataout.writeObject(CODES.INVALID_REQUEST);
                        } catch (IOException e) {
                            System.out.println("Could not send invalid request to server.");
                            e.printStackTrace();
                        }
                        break;
                    }

                    String[] content = CONTENT.split("\\|");

                    type = content[0];
                    sender_id = Integer.parseInt(content[1]);


                    user = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + sender_id + "';");

                    try {
                        System.out.println("Checking if user exists ...");

                        if (user.next()) {

                            //User exists, check auth token, get username:
                            username = user.getString("email");
                            int userId = user.getInt("id");

                            //Check authentication token
                            System.out.println("User exists. Checking if token valid...");
                            String token = user.getString("authtoken");

                            System.out.println(token);

                            if (token.equals(SECRET)) {
                                System.out.println("Token exists. Checking expiration...");
                            } else {
                                System.out.println("Invalid authentication token.");
                                dataout.writeObject(CODES.INVALID_TOKEN);
                                return;
                            }

                            //Check valid timestamp
                            String expirationTime = user.getString("expiration");
                            boolean validToken = Time.checkTimestamp(expirationTime);

                            //Update token expiration time
                            String query = "UPDATE pumpkinbox_users_table SET " +
                                    "authtoken='" + SECRET + "', " +
                                    "expiration='" + Time.getTimeStampPlus() + "' " +
                                    "WHERE id='" + sender_id + "';";

                            if (validToken) {

                                switch (type) {

                                    case "REQUEST":

                                        //Check if already friends
                                        ResultSet requests = db.executeQuery("SELECT * FROM pumpkinbox_friend_requests WHERE receiver_id='" +
                                                userId + "';");

                                        String requests_message = "";

                                        while (requests.next()) {

                                            int request_id = requests.getInt("id");
                                            sender_id = requests.getInt("sender_id");
                                            int receiver_id = userId;
                                            String sender_username = requests.getString("sender_username");

                                            requests_message += request_id + "|";
                                            requests_message += sender_username + "|";

                                        }

                                        //REMOVING ADDITIONAL | in the end
                                        if (requests_message != null && requests_message.length() > 0 && requests_message.charAt(requests_message.length() - 1) == '|') {
                                            requests_message = requests_message.substring(0, requests_message.length() - 1);
                                        }

                                        dataout.writeObject(requests_message);
                                        return;

                                    case "FRIENDS":

                                        //Get user's friends names and time befriended
                                        ResultSet friends = db.executeQuery("SELECT * FROM pumpkinbox_friends_table WHERE sender_id='" + Integer.toString(sender_id) +
                                                            "' OR receiver_id='" + Integer.toString(sender_id) + "';");

                                        String friendsList = "";

                                        while(friends.next()){

                                            int sender = friends.getInt("sender_id");
                                            int receiver = friends.getInt("receiver_id");

                                            int friend_id;
                                            //Set friend id to get friend name
                                            if(sender == userId){
                                                friend_id = receiver;
                                            }else friend_id = sender;

                                            String time_added = friends.getString("timestamp");

                                            //Get friend name
                                            ResultSet res = db.executeQuery("SELECT * FROM pumpkinbox_users_table WHERE id='" + friend_id + "';");


                                            if(res.next()) {

                                                String first_name = res.getString("firstname");
                                                String last_name = res.getString("lastname");

                                                //Return
                                                //FRIEND_ID |  FIRSTNAME LASTNAME   |    TIME ADDED
                                                friendsList += friend_id + "|" + first_name + " " + last_name + "|" + time_added + "|";
                                            }

                                        }

                                        //REMOVING ADDITIONAL | in the end
                                        if (friendsList != null && friendsList.length() > 0 && friendsList.charAt(friendsList.length() - 1) == '|') {
                                            friendsList = friendsList.substring(0, friendsList.length() - 1);
                                        }
                                        dataout.writeObject(friendsList);

                                        return;

                                    case "ACTIVITY":

                                        //Get user's friends names and time befriended
                                        ResultSet activity = db.executeQuery("SELECT * FROM pumpkinbox_game_sessions WHERE sender_id='"
                                                + Integer.toString(userId) + "' OR receiver_id='"
                                                + Integer.toString(userId) + "';");

                                        String activityList = "";

                                        while(activity.next()){

                                            int sender = activity.getInt("sender_id");
                                            int receiver = activity.getInt("receiver_id");

                                            int friend_id;

                                            //Set friend id to get friend name
                                            if(sender == userId){
                                                friend_id = receiver;
                                            }else friend_id = sender;


                                            int gameStatus = activity.getInt("status");
                                            String gameStatusString = "";

                                            if(gameStatus == GameStatus.WIN){
                                                gameStatusString = "WIN";
                                            }else if(gameStatus == GameStatus.LOSS){
                                                gameStatusString = "LOSS";
                                            }else if(gameStatus == GameStatus.TIE){
                                                gameStatusString = "TIE";
                                            }

                                            String gameName = activity.getString("game");
                                            String friend_name = activity.getString("receiver_username");
                                            String experience = Integer.toString(activity.getInt("score"));
                                            experience = "+" + experience + " experience points";

                                            //RESPONSE FORMAT
                                            // FRIEND NAME | GAME NAME | GAME STATUS | EXPERIENCE

                                            activityList += friend_name + "|"
                                                    + gameName + "|"
                                                    + gameStatusString + "|"
                                                    + experience + "|";
                                        }

                                        //REMOVING ADDITIONAL | in the end
                                        if (activityList != null && activityList.length() > 0 && activityList.charAt(activityList.length() - 1) == '|') {
                                            activityList= activityList.substring(0, activityList.length() - 1);
                                        }

                                        dataout.writeObject(activityList);
                                        return;

                                    case "FRIENDACTIVITY":

                                        int friendId = Integer.parseInt(content[2]);

                                        //Get user's friends names and time befriended
                                        ResultSet friendActivity = db.executeQuery("SELECT * FROM pumpkinbox_game_sessions WHERE sender_id='"
                                                + Integer.toString(friendId) + "' OR receiver_id='"
                                                + Integer.toString(friendId) + "';");

                                        String friendActivityList = "";

                                        while(friendActivity.next()){

                                            int sender = friendActivity.getInt("sender_id");
                                            int receiver = friendActivity.getInt("receiver_id");

                                            int friend_id;

                                            //Set friend id to get friend name
                                            if(sender == userId){
                                                friend_id = receiver;
                                            }else friend_id = sender;


                                            int gameStatus = friendActivity.getInt("status");
                                            String gameStatusString = "";

                                            if(gameStatus == GameStatus.WIN){
                                                gameStatusString = "WIN";
                                            }else if(gameStatus == GameStatus.LOSS){
                                                gameStatusString = "LOSS";
                                            }else if(gameStatus == GameStatus.TIE){
                                                gameStatusString = "TIE";
                                            }

                                            String gameName = friendActivity.getString("game");
                                            String friend_name = friendActivity.getString("receiver_username");
                                            String experience = Integer.toString(friendActivity.getInt("score"));
                                            experience = "+" + experience + " experience points";

                                            //RESPONSE FORMAT
                                            // FRIEND NAME | GAME NAME | GAME STATUS | EXPERIENCE

                                            friendActivityList += friend_name + "|"
                                                    + gameName + "|"
                                                    + gameStatusString + "|"
                                                    + experience + "|";
                                        }

                                        //REMOVING ADDITIONAL | in the end
                                        if (friendActivityList != null && friendActivityList.length() > 0 && friendActivityList.charAt(friendActivityList.length() - 1) == '|') {
                                            friendActivityList= friendActivityList.substring(0, friendActivityList.length() - 1);
                                        }
                                        dataout.writeObject(friendActivityList);

                                        return;

                                    case "STATST":

                                        //Get user's score
//                                        ResultSet globalstatstic = db.executeQuery("SELECT * FROM pumpkinbox_gamestats;");
//
//                                        int wins = 0;
//                                        int ties = 0;
//
//                                        while(globalstatstic.next()){
//                                            int status = globalstatstic.getInt("status");
//                                            if(status == 0) ties++;
//                                            else wins++;
//                                        }

//                                        dataout.writeObject(Integer.toString(wins) + "|" + Integer.toString(ties));
                                        dataout.writeObject("p|p");

                                        return;

                                    case "EXPERIENCE":

                                        System.out.println("GETTING USER EXPERIENCE...");

                                        //Get user's score
                                        ResultSet gamestatsA = db.executeQuery("SELECT * FROM pumpkinbox_gamestats WHERE user1_id='"
                                                + Integer.toString(userId) + "';");

                                        int experience = 0;

                                        try {

                                            while (gamestatsA.next()) {

                                                experience += Integer.parseInt(gamestatsA.getString("user1_score"));

                                            }
                                        }catch(Exception e){}

                                        ResultSet gamestatsB = db.executeQuery("SELECT * FROM pumpkinbox_gamestats WHERE user2_id='"
                                                + Integer.toString(userId) + "';");

                                        try{
                                            while (gamestatsB.next()) {

                                                experience += Integer.parseInt(gamestatsB.getString("user2_score"));

                                            }
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }

                                        System.out.println("WRITING OUT: " + Integer.toString(experience));
                                        dataout.writeObject(Integer.toString(experience));

                                        return;

                                    case "FRIENDEXPERIENCE":

                                        friendId = Integer.parseInt(content[2]);

                                        //Get user's score
                                        ResultSet friendGamestatsA = db.executeQuery("SELECT * FROM pumpkinbox_gamestats WHERE user1_id='"
                                                + Integer.toString(friendId) + "';");

                                        int friendExperience = 0;

                                        try {

                                            while (friendGamestatsA.next()) {

                                                friendExperience += Integer.parseInt(friendGamestatsA.getString("user1_score"));

                                            }

                                            ResultSet friendGamestatsB = db.executeQuery("SELECT * FROM pumpkinbox_gamestats WHERE user2_id='"
                                                    + Integer.toString(friendId) + "';");

                                            while (friendGamestatsB.next()) {

                                                friendExperience += Integer.parseInt(friendGamestatsB.getString("user2_score"));

                                            }
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }

                                        dataout.writeObject(Integer.toString(friendExperience));

                                        return;

                                    case "PRIVACY":

                                        friendId = Integer.parseInt(content[2]);

                                        //Get user's score
                                        ResultSet privacy1 = db.executeQuery("SELECT * FROM pumpkinbox_friends_table WHERE sender_id='"
                                                + Integer.toString(friendId) + "' AND receiver_id='" + Integer.toString(userId) + "';");
                                        while(privacy1.next()){
                                            //GET PRIVACY IF FOUND
                                            int privacy = privacy1.getInt("sender_privacy");
                                            dataout.writeObject(Integer.toString(privacy));
                                            return;
                                        }

                                        ResultSet privacy2 = db.executeQuery("SELECT * FROM pumpkinbox_friends_table WHERE sender_id='"
                                                + Integer.toString(userId) + "' AND receiver_id='" + Integer.toString(friendId) + "';");
                                        while(privacy2.next()){
                                            //GET PRIVACY IF FOUND
                                            int privacy = privacy2.getInt("receiver_privacy");
                                            dataout.writeObject(Integer.toString(privacy));
                                            return;
                                        }

                                        //SHOULD NOT REACH THIS POINT
                                        dataout.writeObject(CODES.INVALID_REQUEST);
                                        return;

                                    default:

                                        dataout.writeObject(CODES.INVALID_REQUEST);
                                        return;
                                }

                            } else {
                                System.out.println("User exists but token EXPIRED, sending invalid token code...");
                                dataout.writeObject(CODES.INVALID_TOKEN + CRLF);

                            }

                        } else {
                            System.out.println("User not found, sending NOT FOUND code...");
                            dataout.writeObject(CODES.NOT_FOUND);
                            dataout.writeObject(CODES.NOT_FOUND);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
