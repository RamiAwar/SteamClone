package pumpkinbox.server;

/**
 * Created by ramiawar on 4/26/17.
 */

/**
 * Created by ramiawar on 3/23/17.
 */

import pumpkinbox.api.CODES;
import pumpkinbox.database.DatabaseHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {

    private int port = 9000;    //ServerSocket port number
    private ServerSocket serverSocket;

    private volatile ArrayList<GameRequestObject> gameRequestsList = new ArrayList<>();

    //Empty private constructor since we do not this class to be accessible from other classes
    private GameServer() throws ClassNotFoundException {}

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

                GameServerThread st = new GameServerThread(newConnection);  //Creating a thread for each new client
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
        GameServer server = null;
        try {
            server = new GameServer();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        server.acceptConnections();
    }

    class GameServerThread implements Runnable {

        private Socket socket;
        private ObjectInputStream datain;
        private ObjectOutputStream dataout;
        private DatabaseHandler db;

        private final String CRLF = "\r\n";

        //Constructor
        public GameServerThread(Socket socket) {

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

            try {

                //Receive client request
                Object s = datain.readObject();
                parseRequest((String) s);//Check game, check move, save in arraylist
                System.out.println("Game Requests List:");
                System.out.println(gameRequestsList);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (true) {

                try {

                    Object s = datain.readObject(); //Read client request

                    //TODO: Send ping every 1 min to check if connection is alive?

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

        public void parseRequest(String request) throws IOException, InterruptedException {

            // Extract VERB from the request line.
            String[] tokens = request.split(" ");

            String VERB = tokens[0];
            int sender_id = Integer.parseInt(tokens[1]);
            int receiver_id = Integer.parseInt(tokens[2]);
            String game = tokens[3];
            String move = "";
            try{move = tokens[4];}catch (Exception e){e.printStackTrace();}

            switch (VERB) {

                case "REQUEST":

                    if(move.length() > 0) {
                        //Save this player's move
                        GameRequestObject object = new GameRequestObject(sender_id, receiver_id, move, game);
                        gameRequestsList.add(object);
                    }

                    //Wait for other player's move and return
                    GameRequestObject returnObject = new GameRequestObject();
                    boolean found = false;

                    while (!found) {

                        for (int i = 0; i < gameRequestsList.size(); i++) {
                            GameRequestObject p = gameRequestsList.get(i);

                            if (p.getSender_id() == receiver_id && p.getReceiver_id() == sender_id && p.getGame().equals(game)) {
                                //We found an object belonging to this player
                                dataout.writeObject(p.getMove());
                                gameRequestsList.remove(i);
                                break;
                            }
                        }
//                        wait(100);
                    }

                    dataout.writeObject(CODES.SEND_ERROR);
                    break;

                case "STATUS":

                    String receiver_username = tokens[5];

                    // Extract VERB from the request line.
                    tokens = request.split(" ", 3);

                    VERB = tokens[0];
                    sender_id = Integer.parseInt(tokens[1]);
                    receiver_id = Integer.parseInt(tokens[2]);
                    game = tokens[3];
                    String status = tokens[4];
                    int score = 0;

                    if(status.equals("WIN")){
                        switch (game){
                            case "XO":
                                score = 10;
                                break;

                            default:
                                break;
                        }
                    }else if(status.equals("LOSS")){
                        switch (game){
                            case "XO":
                                score = 5;
                                break;

                            default:
                                break;
                        }
                    }else{
                        //TODO: HANDLE ERROR




                    }

                    if(db.executeAction("INSERT INTO pumpkinbox_game_sessions (sender_id, receiver_id, receiver_username, game, score, status) VALUES ('" +
                            sender_id + "', '" +
                            receiver_id + "', '" +
                            receiver_username + "', '" +
                            game + "', '" +
                            score + "', '" +
                            status + "');")) System.out.println("OK");

                    System.out.println("Message sent.");

            }
        }
    }
}
