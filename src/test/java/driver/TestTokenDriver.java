package driver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.InputStream;
import java.io.PrintStream;

import token.Tokenizer;
import util.Trace;

class TestTokenDriver {
  private final PrintStream defaultOut = System.out;
  private final InputStream defaultIn = System.in;
  private final ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();

  @BeforeEach
  public void setOutputStream() {
    System.setOut(new PrintStream(testOutputStream));
  }

  @AfterEach
  public void resetSystemIO() {
    System.setOut(defaultOut);
    System.setIn(defaultIn);
  }

  ByteArrayInputStream fromString(String str) {
    return new ByteArrayInputStream(str.getBytes());
  }

  void setIn(String str) {
    System.setIn(fromString(str));
  }

  String actualOutput() {
    return testOutputStream.toString();
  }

  @Test
  public void testShowPromptMaybeSuppressed() {
    // prompt suppressed
    String expectedOutput = "";
    TokenDriver driver = new TokenDriver("stdin");
    driver.showPromptMaybe(false, TokenDriver.defaultPrompt, System.out);
    assertEquals(expectedOutput, actualOutput());
  }

  @Test
  public void testShowPromptMaybeNullOutput() {
    // nowhere to show the prompt
    String expectedOutput = "";
    TokenDriver driver = new TokenDriver("stdin");
    driver.showPromptMaybe(true, TokenDriver.defaultPrompt, null);
    assertEquals(expectedOutput, actualOutput());
  }

  @Test
  public void testShowPromptMaybe() {
    // show the prompt
    String expectedOutput = TokenDriver.defaultPrompt;
    TokenDriver driver = new TokenDriver("stdin");
    driver.showPromptMaybe(true, TokenDriver.defaultPrompt, System.out);
    assertEquals(expectedOutput, actualOutput());
  }

  @Test
  public void testREPLEmpty() {
    // show the prompt
    String expectedOutput = "+->";
    TokenDriver driver = new TokenDriver("testFilename");
    String testFilename = "testFilename";
    String input = "";
    StringReader inputReader = new StringReader(input);
    Tokenizer tokenizer = new Tokenizer(testFilename, 1, inputReader);

    driver.repl(true, TokenDriver.defaultPrompt, tokenizer, System.out);

    defaultOut.println(actualOutput());
    String actual = actualOutput();
    assertTrue(actual.startsWith(expectedOutput));
  }

}
