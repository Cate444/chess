package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;

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
            CREATE TABLE IF NOT EXISTS  gamesTable (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
               PRIMARY KEY (`gameID`)
            )
            """
    };

    @Override
    public void clear() {

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
