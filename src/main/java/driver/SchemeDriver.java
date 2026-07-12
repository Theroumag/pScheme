package driver;

import token.Tokenizer;
import util.Trace;
import token.Token;

import ast.EOFExpression;
import ast.SExp;
import ast.exception.SchemeException;
import env.Global;
import parser.Parser;

import static token.Token.Type.*;
import static util.Trace.*;

import java.io.Reader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SchemeDriver {
  final static String programName = "REPL Driver %s.%s";
  final static String majorVersion = "0";
  final static String minorVersion = "5-Scheme";
  final public static String defaultPrompt = "[%d] +-> ";

  private String startUpFileName;
  public SchemeDriver(String startUpFileName) {
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

  public void repl(boolean showPrompt, String prompt, Parser in, PrintStream out) {
    int expressionCount = 1;

    while (true) {
      try {
        // showPromptMaybe(showPrompt, String.format(prompt, expressionCount++), out);

        if (prompt != null) {
          showPromptMaybe(showPrompt, String.format(prompt, expressionCount++), out);
        } else {
          showPromptMaybe(showPrompt, null, out);
        }
        SExp expression = in.next();

        if (expression.equals(EOFExpression.eofExpression()))
          break;

        SExp result = expression.eval(Global.env());
        if (out != null)
          System.out.println(String.format("  %s", result));

      } catch (SchemeException e) {
        System.err.println(e);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(3);
      }
    }
  }

  public void run() {
    if (startUpFileName != null) {
      try (Reader reader = new FileReader(startUpFileName)) {
        Parser initial = new Parser(startUpFileName, 0, reader);

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

    Parser userInput = new Parser("stdin", 0, new InputStreamReader(System.in));
    repl(true, defaultPrompt, userInput, System.out);


    System.out.println("\n========================================");
  }

  public static void usage() {
      System.err.printf("usage: Driver [<file-name>] [DEBUG=<k>]\n");
      System.err.printf("              where\n");
      System.err.printf("              <file-name> is an optional input file name\n");
      System.err.printf("                          to be processed before main loop.\n");
      System.err.printf("              <k> is the optional debug output level.");
      System.exit(1);
  }

  public static String DEBUG = "DEBUG";
  public static void main(String[] args) {
    String inputFileName = null;
    int trace = 0;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith(DEBUG)) {
        int eq = arg.indexOf("=");
        if ((eq < 0) || (eq == arg.length() - 1)) {
          System.err.printf("Argument Error: Unable to find debug level in \"%s\"\n", arg);
          usage();
        }
        trace = Integer.parseInt(arg.substring(eq+1));
      } else if (inputFileName == null) {
        inputFileName = arg;
      } else {
        System.err.printf("Argument Error: input file nam = \"%s\" before processing \"%s\"\n", inputFileName, arg);
        usage();
      }
    }

    if (trace != 0)
      Trace.set(trace);

    SchemeDriver driver = new SchemeDriver(inputFileName);
    driver.run();
  }

}
