package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import datamodel.*;
import service.GameService;
import service.UserService;

import java.util.*;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        UserDataAccess userDataAccess;
        GameDataAccess gameDataAccess;
        try {
            DatabaseManager.createDatabase();
            userDataAccess = new SQLUserDataAccess();
            gameDataAccess = new SQLGameDataAccess();
        }catch (Exception ex){
            userDataAccess = new MemoryUserDataAccess();
            gameDataAccess = new MemoryGameDataAccess();
        }

        this.userService = new UserService(userDataAccess);
        this.gameService = new GameService(gameDataAccess, userDataAccess);
        this.server = Javalin.create(config -> config.staticFiles.add("web"));


        // Route mappings
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::join);
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('KING', 'QUEEN', 'KNIGHT', 'ROOK', 'BISHOP', 'PAWN') DEFAULT 'PAWN',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private <T> T readJson(Context ctx, Class<T> clazz) {
        return gson.fromJson(ctx.body(), clazz);
    }

    private void writeJson(Context ctx, Object obj) {
        ctx.result(gson.toJson(obj));
    }

    private void sendError(Context ctx, int status, String message) {
        var res = Map.of("message", "Error: " + message);
        ctx.status(status).result(gson.toJson(res));
    }


    private void clear(Context ctx) {
        try {
            userService.clear();
            gameService.clear();
            ctx.status(200);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            sendError(ctx, 500, ex.getMessage());
        }
    }

    private void register(Context ctx) {
        try {
            UserData user = readJson(ctx, UserData.class);
            AuthData authData = userService.register(user);
            writeJson(ctx, authData);
        } catch (Exception ex) {
            switch (ex.getMessage()) {
                case "Already exists" -> sendError(ctx, 403, "already taken");
                case "no password", "no username" -> sendError(ctx, 400, "bad request");
                default -> sendError(ctx, 500, "internal server error");
            }
        }
    }

    private void login(Context ctx) {
        try {
            UserData user = readJson(ctx, UserData.class);
            AuthData authData = userService.login(user);
            writeJson(ctx, authData);
        } catch (Exception ex) {
            switch (ex.getMessage()) {
                case "unauthorized" -> sendError(ctx, 401, "unauthorized");
                case "bad request" -> sendError(ctx, 400, "bad request");
                default -> sendError(ctx, 500, "internal server error");
            }
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.status(200);
        } catch (Exception ex) {
            switch (ex.getMessage()) {
                case "unauthorized" -> sendError(ctx, 401, "unauthorized");
                default -> sendError(ctx, 500, "internal server error");
            }
        }
    }

    private void createGame(Context ctx) {
        try {
            GameName gameName = readJson(ctx, GameName.class);
            String authToken = ctx.header("Authorization");
            int gameID = gameService.createGame(authToken, gameName);
            writeJson(ctx, Map.of("gameID", gameID));
        } catch (Exception ex) {
            switch (ex.getMessage()) {
                case "unauthorized" -> sendError(ctx, 401, "unauthorized");
                case "bad request" -> sendError(ctx, 400, "bad request");
                default -> sendError(ctx, 500, "internal server error");
            }
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            ArrayList<ReturnGameData> games = gameService.listGames(authToken);
            writeJson(ctx, Map.of("games", games));
        } catch (Exception ex) {
            if ("unauthorized".equals(ex.getMessage())) {
                sendError(ctx, 401, "unauthorized");
            } else {
                sendError(ctx, 500, "internal server error");
            }
        }
    }

    private void join(Context ctx) {
        try {
            JoinInfo joinInfo = readJson(ctx, JoinInfo.class);
            String authToken = ctx.header("Authorization");
            gameService.joinGame(authToken, joinInfo);
            ctx.status(200);
        } catch (Exception ex) {
            switch (ex.getMessage()) {
                case "unauthorized" -> sendError(ctx, 401, "unauthorized");
                case "bad request" -> sendError(ctx, 400, "bad request");
                case "already taken" -> sendError(ctx, 403, "already taken");
                default -> sendError(ctx, 500, "internal server error");
            }
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
