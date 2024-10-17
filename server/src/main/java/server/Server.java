package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import service.UserServices;
import spark.*;

import java.util.HashMap;

public class Server {
    UserDataAccess userDataAccess = new MemoryUserDAO();
    AuthDataAccess authDataAccess = new MemoryAuthDAO();
    GameDataAccess gameDataAccess = new MemoryGameDAO();
    DataAccess dataAccess = new DataAccess(userDataAccess, authDataAccess, gameDataAccess);
    UserServices userServices = new UserServices(dataAccess);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::deleteAll);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        var user = userServices.registerUser(newUser);
        if(user == "400") {
            res.status(400);
            HashMap<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Error: bad request");
            return new Gson().toJson(responseMap);
        }
        if(user == "403") {
            res.status(403);
            HashMap<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Error: already taken");
            return new Gson().toJson(responseMap);
        }
        res.status(200);
        return new Gson().toJson(user);
    }

    private Object deleteAll(Request req, Response res) throws DataAccessException {
        userServices.deleteAll();
        res.status(200);
        return new Gson().toJson("");
    }
}
