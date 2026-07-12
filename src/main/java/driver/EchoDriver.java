package driver;

import echo.Echo;
import echo.EchoInterface;
import java.io.Reader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EchoDriver {
  final static String programName = "REPL Driver %s.%s";
  final static String majorVersion = "0";
  final static String minorVersion = "1-Echo";
  String startUpFileName;
  public EchoDriver(String startUpFileName) {
    this.startUpFileName = startUpFileName;
  }

  public void run() {
    if (startUpFileName != null) {
      try (Reader reader = new FileReader(startUpFileName)) {
        EchoInterface initial = new Echo(reader);

        while (initial.hasNext()) {
          System.out.printf("%4d: %s\n", initial.lineNumber(), initial.next());
        }
      }
      catch (FileNotFoundException e) {
        System.err.printf("Unable to find \"%s\"\n", startUpFileName);
        System.err.printf("Terminating program.\n");
        System.exit(2);
      } catch (IOException e) {
        System.err.printf("IO Error %s\n", e.getMessage());
        System.err.printf("Terminating program.\n");
        System.exit(3);
      }
    }

    // ----------------------------------------------------------------
    // REPL begins here
    // ----------------------------------------------------------------
    System.out.printf("%s\n", String.format(programName, majorVersion, minorVersion));
    String prompt = "> ";
    Echo repl = new Echo(new InputStreamReader(System.in));
    System.out.print(prompt);
    System.out.flush();

    while (repl.hasNext()) {
      String next = repl.next();
      int line = repl.lineNumber();
      System.out.printf("%4d: %s\n", line, next);
      System.out.print(prompt);
      System.out.flush();
    }
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
    EchoDriver driver = new EchoDriver(fName);
    driver.run();
  }

}
