package server;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}")); //clear
        server.post("user", this::register);
        server.post("session", this ::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::join); //join
    }

    private void register(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server and register

        var res = Map.of("username", req.get("username"), "authToken", "yzx");
        ctx.result(serializer.toJson(res));
    }

    private void login(Context ctx){
        var serializer = new Gson();
        String reqJason = ctx.body();
        var req = serializer.fromJson(reqJason, Map.class);

        //call to server to login

        var res = Map.of("username", req.get("username"), "authToken", "yzx");
        ctx.result(serializer.toJson(res));
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
