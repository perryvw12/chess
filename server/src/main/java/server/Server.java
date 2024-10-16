package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import service.ServiceException;
import service.UserServices;
import spark.*;

import java.util.HashMap;

public class Server {
    DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    UserServices userServices = new UserServices(dataAccess);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::deleteAll);
        Spark.exception(ServiceException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
    }

    private Object registerUser(Request req, Response res) throws DataAccessException, ServiceException {
        try {
            var newUser = new Gson().fromJson(req.body(), UserData.class);
            var user = userServices.registerUser(newUser);
            res.status(200);
            return new Gson().toJson(user);
        } catch (ServiceException ex) {
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("message", ex.getMessage());
            res.status(ex.getStatusCode());
            return new Gson().toJson(errorMap);
        }
    }

    private Object deleteAll(Request req, Response res) throws DataAccessException {
        userServices.deleteAll();
        res.status(200);
        return "";
    }
}
