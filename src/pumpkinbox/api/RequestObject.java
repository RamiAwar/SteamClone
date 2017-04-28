package pumpkinbox.api;

/**
 * Created by ramiawar on 4/17/17.
 */
public class RequestObject {

    private int request_id;
    private int receiver_id;
    private String auth;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    private String requester_username;

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getRequester_username() {
        return requester_username;
    }

    public void setRequester_username(String requester_username) {
        this.requester_username = requester_username;
    }

    public RequestObject(int request_id, int receiver_id, String requester_username) {
        this.requester_username = requester_username;
        this.request_id = request_id;
        this.receiver_id = receiver_id;
    }

    public RequestObject(int request_id, String requester_username) {
        this.requester_username = requester_username;
        this.request_id = request_id;
    }

    public RequestObject(int request_id, int receiver_id, String requester_username, String auth) {
        this.requester_username = requester_username;
        this.request_id = request_id;
        this.receiver_id = receiver_id;
        this.auth = auth;
    }


}
