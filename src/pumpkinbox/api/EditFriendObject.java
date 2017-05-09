package pumpkinbox.api;

/**
 * Created by ramiawar on 4/19/17.
 */
public class EditFriendObject {

    //parameters: friend_id, firstnamelastname, time_added

    private int friend_id;
    private String name;
    private String time_added;
    private String auth;
    private int sender_id;

    public String getAuth() {
        return auth;
    }

    public int getSenderId(){
        return sender_id;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public EditFriendObject(int sender_id, int friend_id, String name, String time_added, String auth) {
        this.friend_id = friend_id;
        this.name = name;
        this.time_added = time_added;
        this.auth = auth;
        this.sender_id = sender_id;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_added() {
        return time_added;
    }

    public void setTime_added(String time_added) {
        this.time_added = time_added;
    }
}
