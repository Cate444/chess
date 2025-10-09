package server;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));
        server.delete("session", ctx -> ctx.result("{}")); //logout
        server.get("game", ctx -> listGames(ctx));
        server.post("game", ctx -> createGame(ctx));
        server.put("game", ctx -> ctx.result("{}")); //join
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

//    private void logout(Context ctx){
//        var serializer = new Gson();
//        String reqJason = ctx.body();
//        var req = serializer.fromJson(reqJason, Map.class);
//
//    }

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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
