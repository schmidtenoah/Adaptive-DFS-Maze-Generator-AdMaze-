import cli.CommandLineInterface;

/**
 * Entry point for the AdDfsMaze generator application.
 *
 * All business logic is delegated to CommandLineInterface.
 * This class serves only as the application entry point.
 *
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.run(args);
    }
}