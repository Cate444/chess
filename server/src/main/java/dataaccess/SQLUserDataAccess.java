package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.SQLException;

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
            System.out.println("HELLO HELLO");
            throw new Exception("Couldn't clear users");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {}
    @Override
    public UserData getUser(String username){
        return new UserData("username", "email", "password");
    }
    @Override
    public String createAuthToken(String username){
        return "authtoken";
    }
    @Override
    public void logout(String authToken) throws Exception{}
    @Override
    public String authenticate(String authToken) throws Exception{return "";}

}
