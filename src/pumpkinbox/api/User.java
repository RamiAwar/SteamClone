package pumpkinbox.api;

/**
 * Created by ramiawar on 4/7/17.
 */
public class User {

    public User(int userId, String username){
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private String username;
    private int userId;


}
