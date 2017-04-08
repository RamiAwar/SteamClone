package pumpkinbox.api;

/**
 * Created by ramiawar on 4/3/17.
 */
public class ResponseObject {

    private String response;
    private String token;
    private String statusCode;
    private int userId;

    public String getUserName() {
        return name;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    private String name;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getUserId(){return userId;}

    public void setUserId(int id){this.userId = id;}

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }





}
