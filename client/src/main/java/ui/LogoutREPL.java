package ui;

import server.ClientSession;
import server.ServerFacade;

import java.util.Scanner;

public class LogoutREPL {
    private final ServerFacade server;
    private final ClientSession session;

    public LogoutREPL(ServerFacade server, ClientSession session) {
        this.server = server;
        this.session = session;
    }


    public void run() {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("[Prelogin] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "login":
                    // put login call here
                    new LoginREPL(server, session).run();
                    break;
                case "register":
                    // put register call here
                    new LoginREPL(server, session).run();
                    break;
                case "quit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    public void printHelp() {
        throw new RuntimeException("not yet implemented");
    }
}
