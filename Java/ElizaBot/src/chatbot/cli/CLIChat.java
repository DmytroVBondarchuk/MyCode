package chatbot.cli;

import codeanticode.eliza.Eliza;

import java.util.Scanner;

/**
 * A command line Eliza chat program
 */
public class CLIChat {

    private Scanner scanner;
    private Eliza eliza;

    /**
     * Creates a new CLIChat for Eliza
     */
    public CLIChat() {
        scanner = new Scanner(System.in);
        eliza = new Eliza();
    }

    /**
     * Gets a user response
     * @return the user's response from standard in
     */
    private String getUserResponse() {
        return scanner.nextLine();
    }

    /**
     * Runs the Eliza chat bot over CLI
     */
    public void run() {

        // This call displays Eliza's greeting.
        System.out.println(eliza.getGreeting());

        while (!eliza.isFinished()) {
            String userResponse = getUserResponse();


            String elizaResponse = eliza.processInput(userResponse);

            System.out.println(elizaResponse);

        }

    }

}
