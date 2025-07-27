package ui;


import server.ClientSession;
import server.ServerFacade;

public class LoginREPL {
    private ServerFacade server;
    private ClientSession session;

    public LoginREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;
    }

    public void run() {
        throw new RuntimeException("not yet implemented");
    }
}
