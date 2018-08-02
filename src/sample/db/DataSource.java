package sample.db;
import java.io.IOException;
import java.sql.*;

public class DataSource {
    // saved again?

    private static final String DB_NAME = "users.db";

    //    private static final String CONNECTION_STRING = "jdbc:sqlite:D:\\databases\\" + DB_NAME;
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

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

    public static final String QUERY_DATA = "SELECT * FROM" + TABLE_USERS;


    private Connection conn;

    private PreparedStatement insertUser;
    private PreparedStatement queryUser;


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


    public int insertUsers(String name, String email, String password) { // TODO adapt

        try{
            queryUser.setString(1, name);
            ResultSet results = queryUser.executeQuery();
            if(results.next()){
                return results.getInt(1);
            } else {
                insertUser.setString(1, name);
                insertUser.setString(2, email);
                insertUser.setString(3, password);
                int affectedRows = insertUser.executeUpdate();
                if (affectedRows != 1) {
                    throw new SQLException("Couldn't insert user");
                }
                ResultSet generatedKeys = insertUser.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Couldn't get _id for album");
                }
            }
        } catch (SQLException e) {
            System.out.println("Insert song exception: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}
