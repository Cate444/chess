package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDataAccess implements GameDataAccess {

    public SQLGameDataAccess() throws Exception{
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
            CREATE TABLE IF NOT EXISTS  gameTable (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
               PRIMARY KEY (`gameID`)
            )
            """
    };

    @Override
    public Executable clear()throws Exception{
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteAllGames = "DELETE FROM gameTable";
            try (var preparedStatement = conn.prepareStatement(deleteAllGames)) {
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    @Override
    public int createGame(GameName gameName) {
        return 0;
    }

    @Override
    public void join(JoinInfo joinInfo, String username) throws Exception {

    }

    @Override
    public ArrayList<ReturnGameData> listGames() {
        return null;
    }
}
