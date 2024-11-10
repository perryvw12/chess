package client;
import exception.ServiceException;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

public class ChessClientPreLogin {
    ServerFacade server;
    String authToken = null;

    public ChessClientPreLogin(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ServiceException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ServiceException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            UserData newUser = new UserData(username, password, email);
            var authData = server.registerUser(newUser);
            authToken = authData.authToken();
            return String.format("You are logged in as %s.%n", username);
        }
        throw new ServiceException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ServiceException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            HashMap<String, String> loginReq = new HashMap<>();
            loginReq.put("username", username);
            loginReq.put("password", password);
            var authData = server.loginUser(loginReq);
            authToken = authData.authToken();
            return String.format("You are logged in as %s.%n", username);
        }
        throw new ServiceException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                - help
                """;
    }

    public String isLoggedIn() {
        return authToken;
    }
}
