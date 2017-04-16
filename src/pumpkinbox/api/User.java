package pumpkinbox.api;

/**
 * Created by ramiawar on 4/7/17.
 */
public class User {

    private String username;
    private int userId;
    private String firstName;
    private String profile_photo;




    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public User(int userId, String username){
        this.username = username;
        this.userId = userId;
    }

    public User(int userId, String username, String firstName){
        this.username = username;
        this.userId = userId;
        this.firstName = firstName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
