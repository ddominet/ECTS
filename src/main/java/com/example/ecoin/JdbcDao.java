package com.example.ecoin;

import java.nio.charset.StandardCharsets;
import java.sql.*;

public class JdbcDao {

    private static final String DATABASE_URL = "jdbc:mysql://10.0.20.120:3306/ewaluta?";
    private static final String DATABASE_USERNAME = "dominic";
    private static final String DATABASE_PASSWORD = "Q@wertyuiop";
    private static final String REGISTRATION_QUERY = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
    private static final String LOGIN_QUERY = "SELECT * FROM users WHERE email = ? and password = ?";

    public void insert(String fullName, String emailId, String password) throws SQLException {

        // load and register JDBC driver for MySQL
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(REGISTRATION_QUERY)) {
             preparedStatement.setString(1, fullName);
             preparedStatement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId));
             preparedStatement.setString(3, org.apache.commons.codec.digest.DigestUtils.sha256Hex(password));


            //System.out.println(preparedStatement);

            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
    }

    public boolean validate(String emailId, String password) throws SQLException {

        // Step 1: Establishing a Connection and
        // try-with-resource statement will auto close the connection.
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            //Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_QUERY)) {
            preparedStatement.setString(1, org.apache.commons.codec.digest.DigestUtils.sha256Hex(emailId));
            preparedStatement.setString(2, org.apache.commons.codec.digest.DigestUtils.sha256Hex(password));

            //System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            // print SQL exception information
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