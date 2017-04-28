package pumpkinbox.client;

import pumpkinbox.api.CODES;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.ui.notifications.Notification;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String CRLF = "\r\n";

    private static String hostname = "localhost";
    private static int port = 8000;

    public static ResponseObject sendLoginData(String data) {

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try {

            clientSocket = new Socket(hostname, port);

            //CARE ABOUT ORDER
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            dataout.writeObject(data);

            String response = (String) datain.readObject();

            if(response.equals(CODES.NOT_FOUND)){
                object.setStatusCode(CODES.NOT_FOUND);
                return object;
            }

            String token = (String) datain.readObject();
            String user_details = (String) datain.readObject();

            String[] details = user_details.split("\\|");


            clientSocket.close();

            object.setUserId(Integer.parseInt(details[0]));
            object.setUserName(details[1]);
            object.setResponse(response);
            object.setToken(token);
            object.setStatusCode(CODES.OK);

            return object;

        } catch (Exception e) {
            Notification notif = new Notification("Error", "An error occured. Please restart the application.", 10, "ERROR");
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }

    public static ResponseObject acceptFriendRequest(int request_id, int senderId, String auth){

        //API REQUEST FORMAT
        //UPDATE    auth    ACCEPT|sender_id|request_id

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "UPDATE " + auth + " ACCEPT|"  + Integer.toString(senderId) +  "|" +  Integer.toString(request_id);

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(response);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }

    public static ResponseObject rejectFriendRequest(int request_id, int senderId, String auth){

        //API REQUEST FORMAT
        //UPDATE    auth    REJECT|sender_id|request_id

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "UPDATE " + auth + " REJECT|"  + Integer.toString(senderId) +  "|" +  Integer.toString(request_id);

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(response);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }

    public static ResponseObject getFriendRequests(int senderId, String auth){

        //API REQUEST FORMAT
        //GET    auth    REQUESTS|myId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " REQUEST|"  + Integer.toString(senderId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            //TODO parse response into status and response

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(response);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }

    public static ResponseObject sendFriendRequestData(int senderId,String sender_username, String friend_email, String auth){

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "UPDATE " + auth + " REQUEST|" + Integer.toString(senderId) + "|" + sender_username + "|" + friend_email;

            dataout.writeObject(request);
            String response = (String) datain.readObject();
            clientSocket.close();


            //Data received, parse into response object
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(response);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;
    }

    public static ResponseObject sendSignupData(String data){
        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try {

            clientSocket = new Socket(hostname, port);

            //CARE ABOUT ORDER
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            dataout.writeObject(data);

            String response = (String) datain.readObject();

            clientSocket.close();

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(response);

            return object;

        } catch (Exception e) {
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);

        return object;

    }






}
