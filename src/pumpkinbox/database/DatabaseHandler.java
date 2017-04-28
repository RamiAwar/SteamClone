package pumpkinbox.database;

import java.sql.*;

/**
 * Created by ramiawar on 3/19/17.
 */
public class DatabaseHandler {

    private static DatabaseHandler handler;

    private static final String URL = "jdbc:mysql://34.201.22.155:3306/pumpkinbox";
    private static Connection connection = null;
    private static Statement statement = null;

    private String username = "newuser";
    private String password = "password";

    //This gives access to all classes to this database handler
    public static DatabaseHandler getInstance(){
        if(handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    //Setting the constructor as private prevents other classes from creating their own database handlers.
    private DatabaseHandler(){
        createConnection();
    }

    void createConnection(){
        try {
            connection = DriverManager.getConnection(URL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query){
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public boolean executeAction(String query){
        try {
            statement = connection.createStatement();
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }



}
