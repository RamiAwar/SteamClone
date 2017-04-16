package pumpkinbox.api;

/**
 * Created by ramiawar on 4/16/17.
 */
public class MessageObject {

    private int sender_id;
    private int receiver_id;
    private String content;
    private String sender_username;
    private String time_sent;

    public String getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(String time_sent) {
        this.time_sent = time_sent;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageObject(int sender_id, int receiver_id, String content){
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
    }

    public MessageObject(String sender_username, String time_sent, String content){
        this.sender_username = sender_username;
        this.time_sent = time_sent;
        this.content = content;
    }

    public MessageObject(int sender_id, String time_sent, String content){
        this.sender_id = sender_id;
        this.time_sent = time_sent;
        this.content = content;
    }

}
