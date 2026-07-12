package driver;

import static token.Token.Type.EOF;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import token.Token;
import token.Tokenizer;

public class TokenDriver {
  final public static String programName = "REPL Driver %s.%s";
  final public static String majorVersion = "0";
  final public static String minorVersion = "1-Token";
  final public static String defaultPrompt = "+-> ";
  private String startUpFileName;
  private String prompt;

  public TokenDriver(String startUpFileName) {
    this.startUpFileName = startUpFileName;
  }

  /**
   * Show the prompt unless no prompt, no output OR suppressed
   * @param showPrompt should the prompt be show?
   * @param prompt prompt to show (null is acceptable)
   * @param out where to print the prompt (null acceptable)
   */
  public void showPromptMaybe(boolean showPrompt, String prompt, PrintStream out) {
    if (showPrompt && (prompt != null) && (out != null)) {
      out.print(prompt);
      out.flush();
    }
  }

  public void repl(boolean showPrompt, String prompt, Tokenizer in, PrintStream out) {
    while (true) {
      try {
        showPromptMaybe(showPrompt, prompt, out);
        Token result = in.next();

        if (result.matches(EOF))
          break;

        if (out != null)
          System.out.println(String.format("%s", result));

      } catch (Exception ioe) {
        System.err.printf("IO Error %s\n", ioe.getMessage());
        System.err.printf("Terminating program.\n");
        ioe.printStackTrace();
        System.exit(3);
      }
    }
  }

  public void run() {
    if (startUpFileName != null) {
      try (Reader reader = new FileReader(startUpFileName)) {
        Tokenizer initial = new Tokenizer(startUpFileName, 1, reader);

        repl(false, null, initial, System.out);

      }
      catch (FileNotFoundException e) {
        System.err.printf("Unable to find \"%s\"\n", startUpFileName);
        System.err.printf("Terminating program.\n");
        System.exit(2);
      } catch (IOException ioe) {
        System.err.printf("IO Error %s\n", ioe.getMessage());
        System.err.printf("Terminating program.\n");
        ioe.printStackTrace();
        System.exit(3);
      }
    }

    // ----------------------------------------------------------------
    // REPL begins here
    // ----------------------------------------------------------------

    System.out.println("========================================");
    System.out.printf("%s\n", String.format(programName, majorVersion, minorVersion));
    System.out.println("========================================");

    Tokenizer userInput = new Tokenizer("stdin", 1, new InputStreamReader(System.in));

    repl(true, defaultPrompt, userInput, System.out);


    System.out.println("\n========================================");
  }

  public static void main(String[] args) {
    if (args.length > 1) {
      System.err.printf("usage: Driver [<file-name>]\n");
      System.err.printf("              where\n");
      System.err.printf("              <file-name> is an optional input file name\n");
      System.err.printf("                          to be processed before main loop.\n");
      System.exit(1);
    }

    String fName = null;
    if (args.length == 1)
      fName = args[0];
    TokenDriver driver = new TokenDriver(fName);
    driver.run();
  }

}
