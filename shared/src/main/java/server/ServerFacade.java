package server;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.reflect.TypeToken;
import datamodel.*;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(String username, String email, String password) throws Exception{
        var request = buildRequest("POST", "/user", new UserData(username, email, password), null);
        var response = sendRequest(request);
        return handleResponse(response, datamodel.AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public AuthData login(String username, String password) throws Exception{
        var request = buildRequest("POST", "/session", new UserData(username, null, password), null);
        var response = sendRequest(request);
        return handleResponse(response, datamodel.AuthData.class);
    }

    public Map<String, Integer> createGame(String authToken, String gameName) throws Exception{
        var request = buildRequest("POST", "/game", new GameName(gameName), authToken);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    public Map<String, Object> listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        Type responseType = new TypeToken<Map<String, List<ReturnGameData>>>() {}.getType();
        return handleResponse(response, responseType);
    }

    public void joinGame(String authToken, int id, TeamColor color) throws Exception {
        ChessGame.TeamColor teamColor = color;
        var request = buildRequest("PUT", "/game", new JoinInfo(teamColor, id), authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null){
            request.header("Authorization" , header);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw ex;
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Type responseType) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception("body exception: "+ body);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseType != null) {
            return new Gson().fromJson(response.body(), responseType);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
