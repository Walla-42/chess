import chess.*;
import server.ClientSession;
import server.ServerFacade;
import ui.LogoutREPL;


public class Main {
    private static ServerFacade facade;

    public static void main(String[] args) {
        int serverPort = 8080;

        System.out.println("Started HTTP server on " + serverPort);
        facade = new ServerFacade(serverPort);

        ClientSession session = new ClientSession();
        ServerFacade server = new ServerFacade(serverPort);

        LogoutREPL preloginUI = new LogoutREPL(server, session);
        preloginUI.run();
    }
}