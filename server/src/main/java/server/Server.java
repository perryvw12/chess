package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import service.UserServices;
import spark.*;

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
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

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
        if(user == "403") {
            res.status(403);
            return new Gson().toJson("Error: already taken" );
        }
        res.status(200);
        return new Gson().toJson(user);
    }
}
