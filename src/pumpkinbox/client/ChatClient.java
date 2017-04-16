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

    private ObjectInputStream datain;
    private ObjectOutputStream dataout;

    private BlockingQueue<User> onlineFriends = new LinkedBlockingQueue<>();

    private int userId = -1;
    private String username = "";
    private String authenticationToken = "-1";



    public ChatClient(BlockingQueue<User> onlineFriends, BlockingQueue<MessageObject> sendMessageQueue, BlockingQueue<MessageObject> receiveMessageQueue, int id, String username, String authToken){

        this.sendMessageQueue = sendMessageQueue;
        this.receiveMessageQueue = receiveMessageQueue;
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
                    System.out.println("Reading from server...");

                    String input = (String) datain.readObject();
                    StringTokenizer tokens = new StringTokenizer(input);

                    System.out.println("CLIENT RECEIVED: " + input);

                    String VERB = tokens.nextToken();
                    String CONTENT = "";

                    try{CONTENT = tokens.nextToken();}catch(Exception e){e.printStackTrace();}

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

                            String[] lines = input.split(" ", 2);
                            String[] messageObject = lines[1].split("\\|");

                            int sender_id = Integer.parseInt(messageObject[0]);
                            String time_sent = messageObject[1];
                            String message = messageObject[2];

                            System.out.println("PUTTING INTO QUEUE: " + message);

                            receiveMessageQueue.offer(new MessageObject(sender_id, time_sent, message));

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
