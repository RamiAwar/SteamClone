package pumpkinbox.client;

/**
 * Created by ramiawar on 4/6/17.
 */

import pumpkinbox.api.*;
import pumpkinbox.database.DatabaseHandler;
import pumpkinbox.server.ChatServer;
import pumpkinbox.ui.notifications.Notification;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient {

    private static final String CRLF = "\r\n";

    private String hostname = "localhost";
    private int port = 9000;

    private BlockingQueue<String> messageQueue;
    private BlockingQueue<NotificationObject> notificationQueue;

    private ObjectInputStream datain;
    private ObjectOutputStream dataout;

    private int userId;
    private String authenticationToken;

    public ChatClient(BlockingQueue<String> messageQueue, BlockingQueue<NotificationObject> notificationQueue, int id, String authToken){

        this.messageQueue = messageQueue;
        this.notificationQueue = notificationQueue;
        this.userId = id;
        this.authenticationToken = authToken;
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
            new Thread(st).start();

            System.out.println("Thread started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }


//    public static void main(String args[]) throws IOException {
//
//        //Instantiate server and call acceptConnections to loop indefinitely
//
//        ChatClient client = null;
//
//        final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
//
//        client = new ChatClient(queue);
//
//        //Create thread to setup in/out, read from queue
//        client.connect();
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String input = br.readLine();
//
//        System.out.println("Main thread: Entering input loop...");
//
//        while(!input.equals("exit")){
//
//            queue.offer(input);
//
//            System.out.println(input + " added to queue");
//
//            input = br.readLine();
//        }
//    }


    class ChatClientThread implements Runnable {

        private Socket socket;
        private ObjectInputStream datain;
        private ObjectOutputStream dataout;
        private DatabaseHandler db;

        private final String CRLF = "\r\n";

        //Constructor
        public ChatClientThread(Socket socket) {
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
            System.out.println("Streams opened successfully.");

            while (true) {
                long millis = System.currentTimeMillis();

                try {
                    System.out.println("Reading from server...");

                    //TODO: Parse message
                    String input = (String) datain.readObject();
                    StringTokenizer tokens = new StringTokenizer(input);

                    String VERB = tokens.nextToken();
                    String CONTENT = "";

                    try{CONTENT = tokens.nextToken();}catch(Exception e){e.printStackTrace();}

                    if(CONTENT.equals("")){
                        System.out.println("Invalid server response! Server has gone mad!");
                    }

                    if(VERB.equals("NOTIFICATION")){

                        String[] content = CONTENT.split("\\|");
                        String senderUsername = content[0];
                        String message = content[1];

                        NotificationObject notificationObject = new NotificationObject();
                        notificationObject.setMessage(message);
                        notificationObject.setSenderUsername(senderUsername);

                        notificationQueue.offer(notificationObject);

                    }else if(VERB.equals("MESSAGE")){

                        String[] content = CONTENT.split("\\|");
                        String senderUsername = content[0];
                        String message = content[1];

                    }

                    //TODO: Send ping every 1 min to check if connection is alive?






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
