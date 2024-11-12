package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerFacade {
    String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        var path = "/user";
        return this.makeRequest("POST", path, newUser, null, AuthData.class);
    }

    public AuthData loginUser(HashMap<String, String> loginReq) throws ServiceException {
        var path = "/session";
        return this.makeRequest("POST", path, loginReq, null, AuthData.class);
    }

    public HashMap<String, ArrayList<GameData>> listGames(HashMap<String, String> listGameReq) throws ServiceException {
        var path = "/game";
        var authToken = listGameReq.get("authorization");
        Type type = new TypeToken<HashMap<String, ArrayList<GameData>>>(){}.getType();
        return this.makeRequest("GET", path, null, authToken, HashMap.class);
    }

    public void createGame(HashMap<String, String> createGameData) throws ServiceException {
        var path = "/game";
        HashMap<String, String> createGameReq = new HashMap<>();
        createGameReq.put("gameName", createGameData.get("gameName"));
        var authToken = createGameData.get("authorization");
        this.makeRequest("POST", path, createGameReq, authToken, HashMap.class);
    }

    public void logout(String authToken) throws ServiceException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public void joinGame(String playerColor, String gameID) {
        var path = "/game";

    }

    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ServiceException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(header != null) {
                http.setRequestProperty("authorization", header);
            }
            writeBody(request, http);
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ServiceException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServiceException {
        var status = http.getResponseCode();
        if((status / 100) != 2) {
            throw new ServiceException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
