package server;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear); //clear
        server.post("user", this::register);
        server.post("session", this ::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::join);
    }

    private void clear(Context ctx){
        userService.clear();
        gameService.clear();
    }

    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJason = ctx.body();
            UserData user = serializer.fromJson(reqJason, UserData.class);
            AuthData authData = userService.register(user);
            ctx.result(serializer.toJson(authData));
        } catch (Exception ex) {
            if (ex.getMessage() == "Already exists") {
                var msg = String.format("{\"message\": \"Error: already taken\"}", ex.getMessage());
                ctx.status(403).result(msg);
            } else if (ex.getMessage() == "no password") {
                var msg = String.format("{ \"message\": \"Error: bad request\" }", ex.getMessage());
                ctx.status(400).result(msg);
            } else if (ex.getMessage() == "no username") {
                var msg = String.format("{ \"message\": \"Error: bad request\" }", ex.getMessage());
                ctx.status(400).result(msg);
            }
        }
    }

    private void login(Context ctx){
        try {
            var serializer = new Gson();
            String reqJason = ctx.body();
            UserData user = serializer.fromJson(reqJason, UserData.class);
            AuthData authData = userService.login(user);
            ctx.result(serializer.toJson(authData));
        } catch (Exception ex){
            if (Objects.equals(ex.getMessage(), "user doesnt exist")) {
                var msg = String.format("{ \"message\": \"Error: unauthorizedn\" }", ex.getMessage());
                ctx.status(401).result(msg);
            } else if (Objects.equals(ex.getMessage(), "bad request")) {
                var msg = String.format("{ \"message\": \"Error: unauthorized\" }", ex.getMessage());
                ctx.status(400).result(msg);
            }
        }
    }

    private void logout(Context ctx){
        try {
            String authToken = ctx.header("Authorization");
            System.out.println(authToken);
            userService.logout(authToken);
            ctx.result();
        }catch (Exception ex){
            if (Objects.equals(ex.getMessage(), "Unauthorized")) {
                var msg = String.format(" { \"message\": \"Error: unauthorized\" }", ex.getMessage());
                ctx.status(401).result(msg);
            }
        }
    }

    private void createGame(Context ctx) throws Exception{
       try {
           var serializer = new Gson();
           String reqJason = ctx.body();
           GameName gameName = serializer.fromJson(reqJason, GameName.class);
           String authToken = ctx.header("Authorization");
           int gameID = gameService.createGame(authToken, gameName);
           var res = Map.of("gameID", gameID);
           ctx.result(serializer.toJson(res));
       } catch (Exception ex){
           if (Objects.equals(ex.getMessage(), "Unauthorized")) {
               var msg = String.format(" { \"message\": \"Error: unauthorized\" }", ex.getMessage());
               ctx.status(401).result(msg);
           }  if (Objects.equals(ex.getMessage(), "bad request")) {
               var msg = String.format("{ \"message\": \"Error: unauthorized\" }", ex.getMessage());
               ctx.status(400).result(msg);
           }
       }
    }

    private void listGames(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server to list games

        var res = Map.of("games", List.of());
        ctx.result(serializer.toJson(res));
    }

    private void join(Context ctx) {
       try {
           var serializer = new Gson();
           String reqJason = ctx.body();
           JoinInfo joinInfo = serializer.fromJson(reqJason, JoinInfo.class);
           String authToken = ctx.header("Authorization");
           gameService.joinGame(authToken, joinInfo);
           ctx.result();
       } catch (Exception ex){
           if (Objects.equals(ex.getMessage(), "Unauthorized")) {
               var msg = String.format(" { \"message\": \"Error: unauthorized\" }", ex.getMessage());
               ctx.status(401).result(msg);
           }if (Objects.equals(ex.getMessage(), "bad request")) {
               var msg = String.format("{ \"message\": \"Error: unauthorized\" }", ex.getMessage());
               ctx.status(400).result(msg);
           } if (Objects.equals(ex.getMessage(), "already taken")) {
               var msg = String.format("{ \"message\": \"Error: already taken\" }", ex.getMessage());
               ctx.status(403).result(msg);
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
