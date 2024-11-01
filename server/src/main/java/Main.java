import server.Server;

import static dataaccess.DataAccess.configureDatabase;

public class Main {
    public static void main(String[] args) {
        try {
            configureDatabase();
            var server = new Server();
            var port = server.run(8080);
            System.out.println("♕ 240 Chess Server: " + port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}