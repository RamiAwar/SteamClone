package pumpkinbox.client;

import pumpkinbox.api.CODES;
import pumpkinbox.api.ResponseObject;

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
            String token = (String) datain.readObject();
            String user_details = (String) datain.readObject();

            String[] details = user_details.split("\\|");


            clientSocket.close();

            object.setUserId(Integer.parseInt(details[0]));
            object.setUserName(details[1]);
            object.setResponse(response);
            object.setToken(token);
            object.setStatusCode(CODES.OK);


            if(response.equals(CODES.NOT_FOUND)) object.setStatusCode(CODES.NOT_FOUND);

            return object;

        } catch (Exception e) {
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
