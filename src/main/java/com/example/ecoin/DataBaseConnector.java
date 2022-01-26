package com.example.ecoin;

import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DataBaseConnector extends ApplicationGUI {

    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/ewaluta?";
    private static final String DATABASE_USERNAME = "dominic";
    private static final String DATABASE_PASSWORD = "Q@wertyuiop";
    private static final String REGISTRATION_QUERY = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
    private static final String LOGIN_QUERY = "SELECT * FROM users WHERE email = ? and password = ?";


    // Method for registration - insert a user into database
    public void insert(String fullName, String emailId, String password) throws SQLException {


        // Establish Connection with database
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(REGISTRATION_QUERY)) {
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId));
            preparedStatement.setString(3, org.apache.commons.codec.digest.DigestUtils.sha256Hex(password));


            // Execute
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // print SQL exception info
            printSQLException(e);
        }
    }

    // Method for login - validate a user in database
    public boolean validate(String emailId, String password) throws SQLException {

        // Establish a Connection with database
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_QUERY)) {
            preparedStatement.setString(1, org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId));
            preparedStatement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(password));

            // Execute
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            // print SQL exception info
            printSQLException(e);
        }
        return false;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}