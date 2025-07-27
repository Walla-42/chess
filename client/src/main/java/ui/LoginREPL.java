package ui;

import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("[Logged In] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "logout":
                    // put logout call here
                    System.out.println("Logging out...");
                    return;
                case "list games":
                    // put list games call here
                    break;
                case "play game":
                    // put join game call here
                    new LoginREPL(server, session).run();
                    break;
                case "create game":
                    // put create game call here
                    break;
                case "observe game":
                    break;
                default:
                    System.out.println("Unknown command.");
                    printHelp();
            }
        }
    }

    private void printHelp() {
        throw new RuntimeException("not yet implemented");
    }
}
