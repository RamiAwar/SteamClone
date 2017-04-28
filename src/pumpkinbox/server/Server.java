
/**
 * Created by ramiawar on 3/23/17.
 */
package pumpkinbox.server;

import pumpkinbox.api.CODES;
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

                            //TODO:Get home page info
                            //dataout.writeObject(INFO);

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

                    //TODO:decrypt SECRET

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

                                    case "REJECT":  //UPDATE    auth    ACCEPT|sender_id|request_id

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

                case "MESSAGE":

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
