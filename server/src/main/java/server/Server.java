package server;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}")); //clear
        server.post("user", this::register);
        server.post("session", this ::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::join);
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
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        // call to server to logout

        ctx.result();
    }

    private void createGame(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server to create a game

        var res = Map.of("gameID", 1234);
        ctx.result(serializer.toJson(res));
    }

    private void listGames(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server to list games

        var res = Map.of("games", List.of());
        ctx.result(serializer.toJson(res));
    }

    private void join(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server to join a game
        ctx.result();
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
