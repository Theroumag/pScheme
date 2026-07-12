package driver;

import token.Tokenizer;
import token.Token;
import java.io.Console;
import java.io.Reader;

import ast.SExp;
import ast.exception.SchemeException;
import parser.Parser;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ParserDriver {
  final static String programName = "REPL Driver %s.%s";
  final static String majorVersion = "0";
  final static String minorVersion = "2-Parser";
  private String startUpFileName;
  private boolean showPrompt;
  private String prompt;

  public ParserDriver(String startUpFileName) {
    this.startUpFileName = startUpFileName;
    showPrompt = true;
    prompt = "> ";
  }

  private void showPromptMaybe() {
    if (showPrompt) {
      System.out.print(prompt);
      System.out.flush();
    }
  }

  private void showPromptMaybe(int i) {
    if (showPrompt) {
      System.out.print("" + i + prompt);
      System.out.flush();
    }
  }

  public void run() {

    showPrompt = false;
    if (startUpFileName != null) {
      try (Reader reader = new FileReader(startUpFileName)) {
        Parser initial = new Parser(startUpFileName, 0, reader);
        while (initial.hasNext()) {
          System.out.printf("%s\n", initial.next());
        }
      } catch (SchemeException e) {
        System.err.println(e);
      } catch (FileNotFoundException e) {
        System.err.printf("Unable to find \"%s\"\n", startUpFileName);
        System.err.printf("Terminating program.\n");
        System.exit(2);
      } catch (IOException e) {
        System.err.printf("IO Error %s\n", e.getMessage());
        System.err.printf("Terminating program.\n");
        System.exit(3);
      }
    }

    showPrompt = true;

    // ----------------------------------------------------------------
    // REPL begins here
    // ----------------------------------------------------------------

    System.out.println("========================================");
    System.out.printf("%s\n", String.format(programName, majorVersion, minorVersion));
    System.out.println("========================================");

    Parser repl = new Parser("stdin", 0, new InputStreamReader(System.in));

    int count = 1;
    showPromptMaybe(count++);
    System.out.flush();
    try {
      while (repl.hasNext()) {
        SExp next = repl.next();
        System.out.printf("<%s>\n", next);
        showPromptMaybe(count++);
      }
    } catch (SchemeException e) {
      System.err.println(e);
    }
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
    ParserDriver driver = new ParserDriver(fName);
    driver.run();
  }

}
