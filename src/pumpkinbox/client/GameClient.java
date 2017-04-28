package pumpkinbox.client;

import pumpkinbox.api.CODES;
import pumpkinbox.api.ResponseObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {

    private static final String CRLF = "\r\n";

    private static String hostname = "localhost";
    private static int port = 9000;

    public static void sendGameStatus(int sender_id, int receiver_id, String receiver_username, String game){

        //API REQUEST FORMAT
        //STATUS   sender_id       receiver_id     game        status
        //status(END)

        Socket clientSocket;

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "STATUS " + Integer.toString(sender_id) + " " + Integer.toString(receiver_id) + " " + receiver_username + " " + game;

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static String sendGameMove(int sender_id, int receiver_id, String game, String move) {

        //API REQUEST FORMAT
        //REQUEST   sender_id       receiver_id     game        move


        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try {
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "REQUEST " + Integer.toString(sender_id) + " " + Integer.toString(receiver_id) + " " + game + " " + move;

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();

            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CODES.SEND_ERROR;
    }

    public static String receiveGameMove(int sender_id, int receiver_id, String game) {

        //API REQUEST FORMAT
        //REQUEST   sender_id       receiver_id     game        move


        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try {
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "REQUEST " + Integer.toString(sender_id) + " " + Integer.toString(receiver_id) + " " + game;

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();

            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CODES.SEND_ERROR;
    }

}
