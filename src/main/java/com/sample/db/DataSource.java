package com.sample.db;

import java.sql.*;

public class DataSource {

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
    private static final String COLUMN_USERS_TOKEN = "token";

    private static final String INSERT_USER = "INSERT INTO " + TABLE_USERS +
            '(' + COLUMN_USERS_NAME + ", " + COLUMN_USERS_EMAIL + ", " + COLUMN_USERS_PASSWORD +
            ") VALUES(?, ?, ?)";

    private static final String QUERY_USER = "SELECT " + COLUMN_USERS_ID + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ? "+ "AND " + COLUMN_USERS_PASSWORD + " = ?";

    private static final String QUERY_USERNAME = "SELECT " + COLUMN_USERS_ID + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ?";

    private static final String QUERY_PASSWORD = "SELECT " + COLUMN_USERS_PASSWORD + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ?";

    private static final String QUERY_USERTOKEN = "SELECT " + COLUMN_USERS_TOKEN + " FROM " +
            TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = ?";

    private static final String INSERT_TOKEN = "UPDATE " + TABLE_USERS + " SET " + COLUMN_USERS_TOKEN + " = ? WHERE " +
            COLUMN_USERS_NAME + " = ?";

    private Connection conn;

    private PreparedStatement insertUser;
    private PreparedStatement queryUser;
    private PreparedStatement queryUserName;
    private PreparedStatement queryPassword;
    private PreparedStatement insertToken;
    private PreparedStatement queryToken;

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
            insertToken = conn.prepareStatement(INSERT_TOKEN);
            queryToken = conn.prepareStatement(QUERY_USERTOKEN);

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
            if (insertToken != null) {
                insertToken.close();
            }
            if (queryToken != null) {
                queryToken.close();
            }

            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public String getPassword(String username) {
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

    public String getUserToken(String username) {
        try {
            queryToken.setString(1, username);
            System.out.println(username + ".");
            ResultSet results = queryToken.executeQuery();
            if (results.next()) {
                System.out.println(results.getString(COLUMN_USERS_TOKEN));
                return results.getString(COLUMN_USERS_TOKEN);
            } else {
                System.out.println("Query failed");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Query failed");
            e.printStackTrace();
            return null;
        }
    }

    public void insertUsers(String name, String email, String password) {
        try {
            insertUser.setString(1, name);
            insertUser.setString(2, email);
            insertUser.setString(3, password);
            int affectedRows = insertUser.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user");
            }
        } catch (SQLException e) {
            System.out.println("Insert song exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean insertToken(String token, String username) {
        try {
            insertToken.setString(1, token);
            insertToken.setString(2, username);
            int affectedRows = insertToken.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user");
            }
            System.out.println("Token: " + token + " added to username: " + username);
            return true;
        } catch (SQLException e) {
            System.out.println("Insert token exception " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
