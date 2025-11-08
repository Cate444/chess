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
            for (String statement : createGameTable) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new Exception(String.format("Unable to configure user database: %s", ex.getMessage()));
        }
    }

    private final String[] createGameTable = {
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
    public void clear()throws Exception{
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteAllGames = "DELETE FROM gameTable";
            try (var preparedStatement = conn.prepareStatement(deleteAllGames)) {
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new Exception(e.getMessage());
        }
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
    }

    @Override
    public void join(JoinInfo joinInfo, String authToken) throws Exception {
        if(joinInfo.playerColor() == null){
            throw new Exception("bad request");
        }
       try(Connection conn = DatabaseManager.getConnection()){
           String getUser = "SELECT username FROM authTable WHERE authToken = ?";
           try (var preparedStatementUE = conn.prepareStatement(getUser)) {
               preparedStatementUE.setString(1, authToken);
               ResultSet rs = preparedStatementUE.executeQuery();
               if (!rs.next()) {
                   throw new Exception("bad request");
               }
               String username = rs.getString("username");

               String checkGameExists = "SELECT * FROM gameTable WHERE gameID = ?";
               try (var preparedStatementGE = conn.prepareStatement(checkGameExists)) {
                   preparedStatementGE.setInt(1, joinInfo.gameID());
                   rs = preparedStatementGE.executeQuery();
                   if (!rs.next()) {
                       throw new Exception("bad request");
                   }
               }

               String checkAvailability = "SELECT whiteUsername, blackUsername FROM gameTable WHERE gameID = ?";
               try (var preparedStatementAvailability = conn.prepareStatement(checkAvailability)) {
                   preparedStatementAvailability.setInt(1, joinInfo.gameID());
                   rs = preparedStatementAvailability.executeQuery();
                   if (!rs.next()) {
                       throw new DataAccessException("Bad Request");
                   }
                   if (rs.getString("whiteUsername") != null && joinInfo.playerColor() == ChessGame.TeamColor.WHITE) {
                       throw new DataAccessException("already taken");
                   }
                   if (rs.getString("blackUsername") != null && joinInfo.playerColor() == ChessGame.TeamColor.BLACK) {
                       throw new DataAccessException("already taken");
                   }
               }
               if (joinInfo.playerColor() == ChessGame.TeamColor.WHITE) {
                   String updateGame = "UPDATE gameTable SET whiteUsername = ? WHERE gameID = ?";
                   try (var preparedStatementWhitePlayer = conn.prepareStatement(updateGame)) {
                       preparedStatementWhitePlayer.setString(1, username);
                       preparedStatementWhitePlayer.setInt(2, joinInfo.gameID());
                       preparedStatementWhitePlayer.executeUpdate();
                   }
               } else if (joinInfo.playerColor() == ChessGame.TeamColor.BLACK) {
                   String updateGame = "UPDATE gameTable SET blackUsername = ? WHERE gameID = ?";
                   try (var preparedStatementWhitePlayer = conn.prepareStatement(updateGame)) {
                       preparedStatementWhitePlayer.setString(1, username);
                       preparedStatementWhitePlayer.setInt(2, joinInfo.gameID());
                       preparedStatementWhitePlayer.executeUpdate();
                   }
               }
           }
       } catch (Exception ex){
           throw ex;
       }

    }

    @Override
    public ArrayList<ReturnGameData> listGames() throws Exception {
        ArrayList<ReturnGameData> returnList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String getGames = "SELECT gameID, whiteUsername, blackUsername, gameName FROM gameTable";
            try (var preparedStatement = conn.prepareStatement(getGames)) {
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    int gameID = rs.getInt("gameID");
                    String gameName = rs.getString("gameName");
                    String whiteUsername;
                    String blackUsername;

                    if (rs.getString("whiteUsername") != null){
                        whiteUsername = rs.getString("whiteUsername");
                    } else {
                        whiteUsername = null;
                    }
                    if (rs.getString("blackUsername") != null){
                        blackUsername = rs.getString("blackUsername");
                    } else {
                        blackUsername = null;
                    }

                    returnList.add(new ReturnGameData(gameID,whiteUsername, blackUsername,gameName));
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return returnList;
    }
}
