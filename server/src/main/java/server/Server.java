package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import exception.ServiceException;
import model.GameData;
import model.UserData;
import server.websocket.WebsocketHandler;
import service.*;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    DataAccess dataAccess = new DataAccess(DataAccess.Implementation.SQL);
    RegistrationService registrationService = new RegistrationService(dataAccess);
    ClearService clearService = new ClearService(dataAccess);
    LogoutService logoutService = new LogoutService(dataAccess);
    LoginService loginService = new LoginService(dataAccess);
    NewGameService newGameService = new NewGameService(dataAccess);
    ListGameService listGameService = new ListGameService(dataAccess);
    JoinGameService joinGameService = new JoinGameService(dataAccess);
    WebsocketHandler webSocketHandler = new WebsocketHandler(dataAccess);


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/db", this::deleteAll);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object exceptionHandler(ServiceException ex, Response res) {
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        res.status(ex.getStatusCode());
        return new Gson().toJson(errorMap);
    }

    private Object registerUser(Request req, Response res) throws DataAccessException, ServiceException {
        try {
            var newUser = new Gson().fromJson(req.body(), UserData.class);
            var user = registrationService.registerUser(newUser);
            res.status(200);
            return new Gson().toJson(user);
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }

    private Object login(Request req, Response res) throws DataAccessException {
        try {
            var loginReq = new Gson().fromJson(req.body(), UserData.class);
            var authData = loginService.loginUser(loginReq.username(), loginReq.password());
            return new Gson().toJson(authData);
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }

    private Object deleteAll(Request req, Response res) throws DataAccessException, ServiceException {
        clearService.deleteAll();
        res.status(200);
        return "";
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        try {
            var authToken = req.headers("authorization");
            logoutService.logoutUser(authToken);
            return "";
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        try {
            var authToken = req.headers("authorization");
            JsonObject jsonObject = JsonParser.parseString(req.body()).getAsJsonObject();
            String gameName = jsonObject.get("gameName").getAsString();
            HashMap<String, Integer> createGameResult = newGameService.createGame(authToken, gameName);
            return new Gson().toJson(createGameResult);
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }

    private Object listGame(Request req, Response res) throws DataAccessException {
        try {
            var authToken = req.headers("authorization");
            ArrayList<GameData> gameList = listGameService.listGames(authToken);
            HashMap<String, ArrayList<GameData>> games = new HashMap<>();
            games.put("games", gameList);
            return new Gson().toJson(games);
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        try {
            var authToken = req.headers("authorization");
            JsonObject jsonObject = JsonParser.parseString(req.body()).getAsJsonObject();
            if(!jsonObject.has("gameID") | !jsonObject.has("playerColor")) {
                throw new ServiceException(400, "Error: bad request");
            }
            int gameID = jsonObject.get("gameID").getAsInt();
            String playerColor = jsonObject.get("playerColor").getAsString();
            joinGameService.joinGame(authToken, playerColor, gameID);
            return "";
        } catch (ServiceException ex) {
            return exceptionHandler(ex, res);
        }
    }
}
