package pumpkinbox.client;

/**
 * Created by ramiawar on 4/6/17.
 */

import pumpkinbox.api.*;
import pumpkinbox.database.DatabaseHandler;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient {

    private static final String CRLF = "\r\n";

    private String hostname = "localhost";
    private int port = 9000;

    private Timer timer;
    private Timer messageSenderTimer;

    private int requestFriendsTime = 10000;

    private BlockingQueue<MessageObject> sendMessageQueue;
    private BlockingQueue<MessageObject> receiveMessageQueue;
    private BlockingQueue<MessageObject> messageNotificationQueue;
    private BlockingQueue<RequestObject> friendRequestsQueue;
    private BlockingQueue<RequestObject> friendRequestsNotificationQueue;
    private BlockingQueue<MessageObject> gameInvitationNotificationQueue;

    private ObjectInputStream datain;
    private ObjectOutputStream dataout;

    private BlockingQueue<User> onlineFriends = new LinkedBlockingQueue<>();

    private int userId = -1;
    private String username = "";
    private String authenticationToken = "-1";


    /**
     * Chat client constructor. Takes many queues as arguments, providing pipes between the chat client thread and the home controller thread.
     * @param onlineFriends This queue is used to pass on the online users that are friends.
     * @param sendMessageQueue  This queue is used to pass on the messages to be sent by the user.
     * @param receiveMessageQueue   This queue is used to pass on messages received by the user.
     * @param messageNotificationQueue  This queue is used to pass on messages received by the user, only to be consumed as notifications.
     * @param friendRequestsQueue   This queue is used to pass on friend requests to the user.
     * @param friendRequestsNotificationQueue   This queue is used to pass on friend requests to the user, that are consumed as notifications.
     * @param gameInvitationNotificationQueue   This queue is used to pass on game invitations directed to the user.
     * @param id    This is the user id connected to the server through this thread.
     * @param username  This is the username or email of the user connected to the server through this thread.
     * @param authToken This is the authentication token that the user received when logging in. This is used to authorize client requests.
     */
    public ChatClient(BlockingQueue<User> onlineFriends, BlockingQueue<MessageObject> sendMessageQueue,
                      BlockingQueue<MessageObject> receiveMessageQueue, BlockingQueue<MessageObject> messageNotificationQueue,
                      BlockingQueue<RequestObject> friendRequestsQueue, BlockingQueue<RequestObject> friendRequestsNotificationQueue,
                      BlockingQueue<MessageObject> gameInvitationNotificationQueue, int id, String username, String authToken){

        this.sendMessageQueue = sendMessageQueue;
        this.receiveMessageQueue = receiveMessageQueue;
        this.messageNotificationQueue = messageNotificationQueue;
        this.friendRequestsQueue = friendRequestsQueue;
        this.friendRequestsNotificationQueue = friendRequestsNotificationQueue;
        this.gameInvitationNotificationQueue = gameInvitationNotificationQueue;
        this.userId = id;
        this.authenticationToken = authToken;
        this.onlineFriends = onlineFriends;
        this.username = username;
    }


    public ChatObject connect() {

        Socket clientSocket;

        ChatObject object = new ChatObject();

        try {

            System.out.println("Creating client socket...");
            clientSocket = new Socket(hostname, port);

            ChatClientThread st = new ChatClientThread(clientSocket);  //Creating a thread for each new client
            Thread th =  new Thread(st);
            th.setDaemon(true);// this is in order for thread to stop when gui stops and not keep running on its own
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

                //Sending id and username to chat server thread in order to be added as an online user
                dataout.writeObject(Integer.toString(userId) + "|" + username);

            } catch (Exception e) {
                System.out.println("Failed to get socket Input/Output streams ... \nClosing thread...");
                return;
            }

            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {

                    try {

                        //Getting all online friends
//                        System.out.println(userId + " - Requesting online friends...");
                        dataout.writeObject("GET " + authenticationToken + " " + userId + "|friends");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };


            timer = new Timer("Friends updater");//create a new timer
            timer.scheduleAtFixedRate(timerTask, 30, requestFriendsTime);

            TimerTask messageSender = new TimerTask() {

                @Override
                public void run() {

                    try {

                        //Sending all queued messages to server
                        while(!sendMessageQueue.isEmpty()){

                            System.out.println("SENDING MESSAGE");

                            MessageObject message = sendMessageQueue.take();
                            dataout.writeObject("MESSAGE " + authenticationToken + " "
                                    + message.getSender_id() + "|"
                                    + message.getReceiver_id() + "|"
                                    + message.getContent());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };


            messageSenderTimer = new Timer("Message Sender");//create a new timer
            messageSenderTimer.scheduleAtFixedRate(messageSender, 30, 300);

            //ServerThread main program
            System.out.println("Streams opened successfully.");




            while (true) {

                try {

                    //ALL CLIENT RECEPTION MESSAGES ARE OF THE FORMAT:
                    //      VERB CONTENT

                    String input = (String) datain.readObject();

                    System.out.println("CLIENT RECEIVED: " + input);

                    String[] tokens = input.split(" ", 2);

                    String VERB = tokens[0];
                    String CONTENT = "";

                    try{CONTENT = tokens[1];}catch(Exception e){e.printStackTrace();}

                    if(CONTENT.equals("")){
                        System.out.println("Invalid server response! Server has gone mad!");
                    }

                    switch (VERB){

                        case "FRIENDS":

                            String[] friendsList = CONTENT.split("\\|");
                            for (int i = 0; i < friendsList.length-1; i+=3) {
                                onlineFriends.offer(new User(Integer.parseInt(friendsList[i+1]), friendsList[i], friendsList[i+2]));
                            }
                            break;

                        case "MESSAGE":

                            String[] messageObject = tokens[1].split("\\|");

                            int sender_id = Integer.parseInt(messageObject[0]);
                            String time_sent = messageObject[1];
                            String message = messageObject[2];

                            System.out.println("PUTTING INTO QUEUE: " + message);

                            receiveMessageQueue.offer(new MessageObject(sender_id, time_sent, message));
                            messageNotificationQueue.offer(new MessageObject(sender_id, time_sent, message));

                            break;

                        case "REQUEST":

                            //MESSAGE FORMAT:   REQUEST  request_id|sender_username
                            String[] requestLine = tokens[1].split("\\|");

                            int request_id = Integer.parseInt(requestLine[0]);
                            String requester_username = requestLine[1];

                            RequestObject requestObject = new RequestObject(request_id, userId, requester_username);
                            friendRequestsNotificationQueue.offer(requestObject);
                            friendRequestsQueue.offer(requestObject);

                            break;

                        default:
                            System.out.println("UNKNOWN VERB RECEIVED:");
                            System.out.println(input);

                    }




                } catch (Exception e) {
                    System.out.println("---------------Client Disconnected---------------");
                    e.printStackTrace();
                    System.out.println("-------------------------------------------------");
                    break;
                }


            }

        }
    }




}
