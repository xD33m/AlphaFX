package sample.db;

import java.sql.*;

public class DataSource {
    // saved again?
    // saved ??

    private static final String DB_NAME = "users.db";

    //    private static final String CONNECTION_STRING = "jdbc:sqlite:D:\\databases\\" + DB_NAME;
//    jdbc:postgresql://ec2-54-247-123-231.eu-west-1.compute.amazonaws.com/d45cmtvctrnbfn
    private static final String CONNECTION_STRING =
            "jdbc:postgresql://ec2-54-247-123-231.eu-west-1.compute.amazonaws.com/d45cmtvctrnbfn?" +
                    "ssl=true" + "&" +
                    "sslfactory=org.postgresql.ssl.NonValidatingFactory" + "&" +
                    "password=e27656ecef593f7ec2d3d73d37180e689021fee676a3d2c14c2ea3758265084e" + "&" +
                    "user=xjgtestmyawbxw";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERS_ID = "_id";
    private static final String COLUMN_USERS_NAME = "name";
    private static final String COLUMN_USERS_EMAIL = "email";
    private static final String COLUMN_USERS_PASSWORD = "password";

    private static final String INSERT_USER = "INSERT INTO " + TABLE_USERS +
            '(' + COLUMN_USERS_NAME + ", " + COLUMN_USERS_EMAIL + ", " + COLUMN_USERS_PASSWORD +
            ") VALUES(?, ?, ?)";
    private static final String QUERY_USER = "SELECT " + COLUMN_USERS_ID + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ? "+ "AND " + COLUMN_USERS_PASSWORD + " = ?";

    private static final String QUERY_USERNAME = "SELECT " + COLUMN_USERS_ID + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ?";

    public static final String QUERY_PASSWORD = "SELECT " + COLUMN_USERS_PASSWORD + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ?";

    public static final String QUERY_DATA = "SELECT * FROM" + TABLE_USERS;


    private Connection conn;

    private PreparedStatement insertUser;
    private PreparedStatement queryUser;
    private PreparedStatement queryUserName;
    private PreparedStatement queryPassword;


    private static DataSource instance = new DataSource();

    private DataSource() {

    }

    public static DataSource getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            insertUser = conn.prepareStatement(INSERT_USER);
            queryUser = conn.prepareStatement(QUERY_USER);
            queryUserName = conn.prepareStatement(QUERY_USERNAME);
            queryPassword = conn.prepareStatement(QUERY_PASSWORD);


            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't connect to db: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (insertUser != null) {
                insertUser.close();
            }
            if(queryUser != null){
                queryUser.close();
            }
            if (queryUserName != null) {
                queryUserName.close();
            }

            if(queryPassword != null){
                queryPassword.close();
            }

            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public boolean verifyUser(String name, String password){
        try {
            queryUser.setString(1, name);
            queryUser.setString(2, password);
            ResultSet results = queryUser.executeQuery();
            return results.next();
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public String getQueryPassword(String username) {
        try {
            queryPassword.setString(1, username);
            ResultSet resultSet = queryPassword.executeQuery();
            if(resultSet.next()){
                return resultSet.getString(COLUMN_USERS_PASSWORD);
            }else {
                System.out.println("Query failed");
                return null;
            }
        }catch (SQLException e){
            System.out.println("Query failed");
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyUsername(String name) {
        try {
            queryUserName.setString(1, name);
            ResultSet results = queryUserName.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertUsers(String name, String email, String password) { // TODO adapt

        try {
            insertUser.setString(1, name);
            insertUser.setString(2, email);
            insertUser.setString(3, password);
            int affectedRows = insertUser.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user");
            }
            return true;
//                ResultSet generatedKeys = insertUser.getGeneratedKeys();
//                if (generatedKeys.next()) {
//                    return generatedKeys.getInt(1);
//                } else {
//                    throw new SQLException("Couldn't get _id for album");
//                }
        } catch (SQLException e) {
            System.out.println("Insert song exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
