package pumpkinbox.client;

import pumpkinbox.api.CODES;
import pumpkinbox.api.MessageObject;
import pumpkinbox.api.ResponseObject;
import pumpkinbox.ui.notifications.Notification;

import java.io.*;
import java.net.Socket;

public class Client {

    private static final String CRLF = "\r\n";

    private static String hostname = "localhost";
    private static int port = 8000;

    public static ResponseObject getFriendActivityList(int senderId, String auth, int friendId){

        //API REQUEST FORMAT
        //GET    auth    FRIENDACTIVITY|myId|friendId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " FRIENDACTIVITY|"  + Integer.toString(senderId) + "|" + Integer.toString(friendId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getFriendExperience(int senderId, String auth, int friendId){

        //API REQUEST FORMAT
        //GET    auth    FRIENDEXPERIENCE|myId|friendId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " FRIENDEXPERIENCE|"  + Integer.toString(senderId) + "|" + Integer.toString(friendId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getFriendPrivacy(int senderId, String auth, int friendId){

        //API REQUEST FORMAT
        //GET    auth    PRIVACY|myId|friendId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " PRIVACY|"  + Integer.toString(senderId) + "|" + Integer.toString(friendId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }


    public static ResponseObject getEditFriendsList(int senderId, String auth){

        //API REQUEST FORMAT
        //GET    auth    REQUESTS|myId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " FRIENDS|"  + Integer.toString(senderId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            //TODO parse response into status and response

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getActivityList(int senderId, String auth){

        //API REQUEST FORMAT
        //GET    auth    REQUESTS|myId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " ACTIVITY|"  + Integer.toString(senderId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object

            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject sendInvite(MessageObject invite){

        //API REQUEST FORMAT
        //INVITE    get    sender_id|receiver_id|game

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "INVITE " + "PUT " + Integer.toString(invite.getSender_id()) + "|"
                    + Integer.toString(invite.getReceiver_id()) + "|"
                    + invite.getContent();
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            System.out.println("RECEIVED: " + response);
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }


    public static ResponseObject checkInvite(MessageObject invite){

        //API REQUEST FORMAT
        //INVITE    get    sender_id|receiver_id|game

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "INVITE " + "GET " + Integer.toString(invite.getSender_id()) + "|"
                    + Integer.toString(invite.getReceiver_id()) + "|"
                    + invite.getContent();
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            System.out.println("RECEIVED: " + response);
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getInvite(int sender_id){

        //API REQUEST FORMAT
        //INVITE    get    sender_id|receiver_id|game

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "INVITE " + "FIND " + Integer.toString(sender_id) + "|"
                    + Integer.toString(sender_id) + "|"
                    + Integer.toString(sender_id);

            dataout.writeObject(request);

            System.out.println("WROTE OUT:" + request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            System.out.println("RECEIVED: " + response);
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getExperience(int senderId, String auth){

        //API REQUEST FORMAT
        //GET    auth    REQUESTS|myId

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " EXPERIENCE|"  + Integer.toString(senderId);
            dataout.writeObject(request);

            String response = (String) datain.readObject();
            clientSocket.close();

            //Data received, parse into response object
            System.out.println("RECEIVED: " + response);
            object.setResponse(response);
            object.setToken("");
            object.setStatusCode(CODES.OK);

            return object;


        }catch(Exception e){
            e.printStackTrace();
        }

        object.setStatusCode(CODES.SEND_ERROR);
        return object;
    }

    public static ResponseObject getStatsTicTacToe(int sender_id, String auth){

        //API REQUEST FORMAT
        //UPDATE    auth    INVITE|sender_id|friend_id|game

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{

            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "GET " + auth + " STATST|"  + Integer.toString(sender_id);

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



    public static ResponseObject sendGameInvite(int sender_id, String auth, int friend_id, String game){

        //API REQUEST FORMAT
        //UPDATE    auth    INVITE|sender_id|friend_id|game

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{

            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "UPDATE " + auth + " ACCEPT|"  + Integer.toString(sender_id) +  "|" +  Integer.toString(friend_id) + "|" + game;

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

    public static ResponseObject removeFriend(int requester_id, int friend_id, String auth){

        //API REQUEST FORMAT
        //UPDATE    auth    REMOVE|sender_id|friend_id

        Socket clientSocket;

        ResponseObject object = new ResponseObject();

        try{
            clientSocket = new Socket(hostname, port);

            //Care about order
            ObjectInputStream datain = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataout = new ObjectOutputStream(clientSocket.getOutputStream());

            String request = "UPDATE " + auth + " REMOVE|"  + Integer.toString(requester_id) +  "|" +  Integer.toString(friend_id);

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
