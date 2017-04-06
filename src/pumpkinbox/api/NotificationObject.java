package pumpkinbox.api;

/**
 * Created by ramiawar on 4/6/17.
 */
public class NotificationObject {

    private String message;
    private int sender;
    private int receiver;

    public String getSenderUsername() {
        return sender_username;
    }

    public void setSenderUsername(String sender_username) {
        this.sender_username = sender_username;
    }

    private String sender_username;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }




}
