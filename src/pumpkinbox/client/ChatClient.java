package pumpkinbox.client;

/**
 * Created by ramiawar on 4/6/17.
 */

import pumpkinbox.api.*;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.server.ChatServer;
import pumpkinbox.ui.notifications.Notification;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient {

    private static final String CRLF = "\r\n";

    private String hostname = "localhost";
    private int port = 9000;

    private Timer timer;

    private BlockingQueue<String> messageQueue;
    private BlockingQueue<NotificationObject> notificationQueue;

    private ObjectInputStream datain;
    private ObjectOutputStream dataout;

    private BlockingQueue<User> onlineFriends = new LinkedBlockingQueue<>();

    private int userId = -1;
    private String authenticationToken = "-1";

    public ChatClient(BlockingQueue<User> onlineFriends, BlockingQueue<String> messageQueue, BlockingQueue<NotificationObject> notificationQueue, int id, String authToken){

        this.messageQueue = messageQueue;
        this.notificationQueue = notificationQueue;
        this.userId = id;
        this.authenticationToken = authToken;
        this.onlineFriends = onlineFriends;
    }


//    public ChatObject sendData(String data) throws IOException, ClassNotFoundException {
//
//        System.out.println("Sending data...");
//        dataout.writeObject(data);
//
//        String response = (String) datain.readObject();
//        String token = (String) datain.readObject();
//
//        ChatObject object = new ChatObject();
//
//        object.setResponse(response);
//        object.setToken(token);
//        object.setStatusCode(CODES.OK);
//
//        if(response.equals(CODES.NOT_FOUND)) object.setStatusCode(CODES.NOT_FOUND);
//
//        return object;
//    }


    public ChatObject connect() {

        Socket clientSocket;

        ChatObject object = new ChatObject();

        try {

            System.out.println("Creating client socket...");
            clientSocket = new Socket(hostname, port);

            ChatClientThread st = new ChatClientThread(clientSocket);  //Creating a thread for each new client
            Thread th =  new Thread(st);
            th.setDaemon(true);
            th.start();

            System.out.println("Thread started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }


    class ChatClientThread implements Runnable {

        private Socket socket;
        private ObjectInputStream datain;
        private ObjectOutputStream dataout;
        private DatabaseHandler db;


        private final String CRLF = "\r\n";

        //Constructor
        private ChatClientThread(Socket socket) {
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

                datain = new ObjectInputStream(socket.getInputStream());
                dataout = new ObjectOutputStream(socket.getOutputStream());

                System.out.println("Sending user id...");
                dataout.writeObject(Integer.toString(userId));

            } catch (Exception e) {
                System.out.println("Failed to get socket Input/Output streams ... \nClosing thread...");
                return;
            }

            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {

                    //Check online friends
                    try {

                        System.out.println(userId + " - Requesting online friends...");

                        dataout.writeObject("GET " + authenticationToken + " " + userId + "|friends");
                        System.out.println("Receiving online friends...");

                        String friends = (String) datain.readObject();

                        System.out.println("Received: " + friends);

                        String[] friendsList = friends.split("\\|");
                        for (int i = 0; i < friendsList.length-1; i+=2) {
                            onlineFriends.offer(new User(Integer.parseInt(friendsList[i+1]), friendsList[i]));
                            System.out.println(friendsList[i]);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            timer = new Timer("Friends updater");//create a new timer
            timer.scheduleAtFixedRate(timerTask, 30, 1000);

            //ServerThread main program
            System.out.println("Streams opened successfully.");

//            while (true) {
//
//                try {
//                    System.out.println("Reading from server...");
//
//                    TODO: Parse message
//
//                    String input = (String) datain.readObject();
//                    StringTokenizer tokens = new StringTokenizer(input);
//
//                    String VERB = tokens.nextToken();
//                    String CONTENT = "";
//
//                    try{CONTENT = tokens.nextToken();}catch(Exception e){e.printStackTrace();}
//
//                    if(CONTENT.equals("")){
//                        System.out.println("Invalid server response! Server has gone mad!");
//                    }
////
//                    if(VERB.equals("NOTIFICATION")){
//
//                        String[] content = CONTENT.split("\\|");
//                        String senderUsername = content[0];
//                        String message = content[1];
//
//                        NotificationObject notificationObject = new NotificationObject();
//                        notificationObject.setMessage(message);
//                        notificationObject.setSenderUsername(senderUsername);
//
//                        notificationQueue.offer(notificationObject);
//
//                    }
//
//                    //TODO: Send ping every 1 min to check if connection is alive?
//
//
//                } catch (Exception e) {
//                    System.out.println("---------------Client Disconnected---------------");
//                    e.printStackTrace();
//                    System.out.println("-------------------------------------------------");
//                    break;
//                }
//
//
//            }

        }
    }




}
