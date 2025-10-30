package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.function.Executable;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUserDataAccess implements UserDataAccess{

    public SQLUserDataAccess() throws Exception{
            try (Connection conn = DatabaseManager.getConnection()) {
                for (String statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
            }
        }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  usersTable (
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
               PRIMARY KEY (`username`)
            )
            """,
            """
             CREATE TABLE IF NOT EXISTS  authTable (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
               PRIMARY KEY (`authToken`)
            )
            """
    };

    @Override
    public Executable clear() throws Exception{
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteAllUsers = "DELETE FROM usersTable";
                try (var preparedStatement = conn.prepareStatement(deleteAllUsers)) {
                    preparedStatement.executeUpdate();
                }
            String resetAutoIncrement = "ALTER TABLE usersTable AUTO_INCREMENT = 1";
                try (var preparedStatement = conn.prepareStatement(resetAutoIncrement)) {
                preparedStatement.executeUpdate();
            }
            String deleteAllAuths = "DELETE FROM authTable";
            try (var preparedStatement = conn.prepareStatement(deleteAllAuths)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new Exception("Couldn't clear users");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws Exception {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        try (Connection conn = DatabaseManager.getConnection()) {
            String insertUser = "INSERT INTO usersTable(username, email, password) " +
                    "VALUES(?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(insertUser)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.email());
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.executeUpdate();
            }
        } catch (Exception ex){
            throw new Exception("Already exists");
        }
    }

    @Override
    public String createAuthToken(UserData userData) throws Exception{
        String authToken = UUID.randomUUID().toString();
        try (Connection conn = DatabaseManager.getConnection()){
            // look up user and then compare passwords

            String lookUpUser = "SELECT * FROM usersTable WHERE username = ?";

            try (var preparedStatement = conn.prepareStatement(lookUpUser)) {
                preparedStatement.setString(1, userData.username());
                ResultSet rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("unauthorized");
                }
                String hashedPassword = rs.getString("password");
                if (!BCrypt.checkpw(userData.password(), hashedPassword)) {
                    throw new DataAccessException("unauthorized");
                }
            }


            String insertUser = "INSERT INTO authTable(username, authToken) " +
                    "VALUES(?, ?)";
            try (var preparedStatement = conn.prepareStatement(insertUser)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, authToken);
                preparedStatement.executeUpdate();
                return authToken;
            }

        } catch (Exception ex){
            throw ex;
        }
    }
    @Override
    public void logout(String authToken) throws Exception{
        try (Connection conn = DatabaseManager.getConnection()){
            String selectAuth = "SELECT * FROM authTable WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(selectAuth)) {
                preparedStatement.setString(1, authToken);
                ResultSet rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new Exception("unauthorized");
                }
                String deleteAuth = "DELETE FROM authTable WHERE authToken = ?";
                try (var preparedStatement2 = conn.prepareStatement(deleteAuth)) {
                    preparedStatement2.setString(1, authToken);
                    preparedStatement2.executeUpdate();
                }
            }
        }catch (Exception ex){
            throw ex;
        }

    }
    @Override
    public String authenticate(String authToken) throws Exception{
        try (Connection conn = DatabaseManager.getConnection()){
            String selectAuth = "SELECT * FROM authTable WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(selectAuth)) {
                preparedStatement.setString(1, authToken);
                ResultSet rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new Exception("unauthorized");
                }
            }
        }catch (Exception ex){
            throw ex;
        }
        return authToken;
    }
}
