package sample.db;

import java.sql.*;

public class DataSource {

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


    private Connection conn;

    private PreparedStatement insertUser;


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
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public void insertUser(String name, String email, String password) { // TODO adapt

        try{
            conn.setAutoCommit(false);
            System.out.println("auto commit set to false");

            insertUser.setString(1, name);
            insertUser.setString(2, email);
            insertUser.setString(3, password);
            int affectedRows = insertUser.executeUpdate();
            System.out.println(affectedRows);
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user");
            }

        } catch (Exception e) {
            System.out.println("Insert song exception: " + e.getMessage());
            e.printStackTrace();
            try {
                System.out.println("Performing rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Oh boy! Things are really bad! " + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Resetting default commit behavior");
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit! " + e.getMessage());
            }

        }
    }

}
