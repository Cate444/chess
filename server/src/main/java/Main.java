import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
       Server server = null;
        try {
            server = new Server();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
    }
}