package dataaccess;

import datamodel.GameName;
import datamodel.JoinInfo;
import datamodel.ReturnGameData;
import org.junit.jupiter.api.function.Executable;
import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
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
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
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
    public int createGame(GameName gameName) throws Exception {
       try (Connection conn = DatabaseManager.getConnection()) {
            String createNewGame = "INSERT INTO gameTable(gameName, game) VALUES(?, ?)";
            String gameData = new Gson().toJson(new ChessGame());
            try (var preparedStatement = conn.prepareStatement(createNewGame)) {
                preparedStatement.setString(1, gameName.gameName());
                preparedStatement.setString(2, gameData);
                preparedStatement.executeUpdate();
            }

            String getGameID = "SELECT gameID FROM gameTable gameID WHERE gameName = ?";
            try (var preparedStatement2 = conn.prepareStatement(getGameID)) {
                preparedStatement2.setString(1, gameName.gameName());
                ResultSet rs = preparedStatement2.executeQuery();
                if (!rs.next()) {
                    throw new Exception("Failed to get game ID");
                }
                return rs.getInt("gameID");
            }
        } catch (Exception ex) {
            throw ex;
        }
        //return 0;
    }

    @Override
    public void join(JoinInfo joinInfo, String username) throws Exception {

    }

    @Override
    public ArrayList<ReturnGameData> listGames() throws Exception {
        return null;
    }
}
